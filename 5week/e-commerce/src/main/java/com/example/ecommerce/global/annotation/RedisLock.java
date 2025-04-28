package com.example.ecommerce.global.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {
    /**
     * 락을 걸 Redis key (SpEL 사용 가능: e.g. "'lock:user:' + #userId")
     */
    String key();

    /** 락 획득을 시도하면서 최대 기다릴 시간 */
    long waitTime() default 5;

    /**
     * 락 최대 유지 시간 (lock 획득 후 자동 해제까지)
     */
    long leaseTime() default 5;

    /**
     * leaseTime 의 단위
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

}
