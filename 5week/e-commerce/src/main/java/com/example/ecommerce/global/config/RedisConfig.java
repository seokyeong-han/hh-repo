package com.example.ecommerce.global.config;

import com.example.ecommerce.domain.product.model.Product;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@ConfigurationProperties(prefix = "spring.cache.redis")
public class RedisConfig {
    private Duration defaultTtl; //프로퍼티에 설정된 기본 값
    private Map<String, Duration> cacheTtls = new HashMap<>();

    // 세터/게터 반드시 필요
    public void setDefaultTtl(Duration defaultTtl) {
        this.defaultTtl = defaultTtl;
    }
    public void setCacheTtls(Map<String, Duration> cacheTtls) {
        this.cacheTtls = cacheTtls;
    }

    @Bean //redis cache bean등록을 위한 config
    //@Primary  // 기본 RedisCacheManager 대신 이 빈을 쓰라고 표시
    public RedisCacheManager cacheManager(RedisConnectionFactory cf, ObjectMapper objectMapper) {
        /*
        // ❶ ObjectMapper 에 타입메타정보 활성화 (역직렬화 시 LinkedHashMap 방지)
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);

        // ❷ JSON 직렬화기 설정
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);
        var pair =
                RedisSerializationContext.SerializationPair
                        .fromSerializer(jsonSerializer);

        // ❸ 기본 캐시 설정 (JSON + TTL)
        RedisCacheConfiguration defaultConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeValuesWith(pair)
                        .entryTtl(defaultTtl);

        // ❹ 개별 캐시별 TTL 설정
        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        cacheTtls.forEach((name, ttl) -> {
            configs.put(name,
                    defaultConfig.entryTtl(ttl));
        });

        return RedisCacheManager.builder(cf)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configs)
                .build();

         */
        // ① 타입 전용 JSON 직렬라이저: 생성자에 objectMapper 를 주입
        Jackson2JsonRedisSerializer<Product> productSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Product.class);

        var pair = RedisSerializationContext.SerializationPair.fromSerializer(productSerializer);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(pair)
                .entryTtl(defaultTtl);

        // ④ 캐시별 TTL 설정
        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        cacheTtls.forEach((cacheName, ttl) -> {
            configs.put(cacheName,
                    RedisCacheConfiguration.defaultCacheConfig()
                            .serializeValuesWith(pair)
                            .entryTtl(ttl));
        });

        return RedisCacheManager.builder(cf)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configs)
                .build();
    }

}
