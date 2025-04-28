package com.example.ecommerce.global.aop;

import com.example.ecommerce.global.annotation.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Aspect
@Component
@Slf4j
public class RedisLockAspect {


    private final RedissonClient redissonClient;
    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    public RedisLockAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }


    @Around("@annotation(com.example.ecommerce.global.annotation.RedisLock)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        RedisLock ann = sig.getMethod().getAnnotation(RedisLock.class);

        StandardEvaluationContext ctx =
                new MethodBasedEvaluationContext(pjp.getTarget(), sig.getMethod(), pjp.getArgs(), nameDiscoverer);
        String lockKey = parser.parseExpression(ann.key()).getValue(ctx, String.class);

        RLock lock = redissonClient.getLock(lockKey);
        log.info("🔒 락 시도 - key: {}", lockKey);

        // 수정된 부분: 최대 waitTime 만큼 대기하면서 락 획득 시도
        boolean acquired = lock.tryLock(
                ann.waitTime(),
                ann.leaseTime(),
                ann.timeUnit()
        );
        if (!acquired) {
            log.warn("⚠️ 락 실패 - key: {}", lockKey);
            throw new IllegalStateException("Redisson 락 획득 실패: " + lockKey);
        }
        log.info("✅ 락 획득 성공 - key: {}", lockKey);

        try {
            return pjp.proceed();
        } finally {
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                // 트랜잭션 커밋 후 락 해제
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        if (lock.isHeldByCurrentThread()) {
                            lock.unlock();
                            log.info("🔓 트랜잭션 커밋 후 락 해제 - key: {}", lockKey);
                        }
                    }
                });
            } else {
                // 트랜잭션 없으면 바로 락 해제
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    log.info("🔓 트랜잭션 없이 락 즉시 해제 - key: {}", lockKey);
                }
            }
        }
    }
}
