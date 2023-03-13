package io.ztbeike.ffr4ms.gateway.autoconfigure;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.ztbeike.ffr4ms.gateway.cache.GatewayCacheFactory;
import io.ztbeike.ffr4ms.gateway.context.ResponseCacheContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 配置自动装配
 */
@Configuration
public class EnableGatewayPluginAutoConfiguration {

    @Bean("routingExecutorService")
    @ConditionalOnMissingBean(name = "routingExecutorService")
    public ExecutorService routingExecutorService() {
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2,
                Integer.MAX_VALUE, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        return TtlExecutors.getTtlExecutorService(executorService);
    }

    @Bean
    @ConditionalOnMissingBean(Caffeine.class)
    public Caffeine<Object, Object> caffeine() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000000)
                .expireAfterWrite(120, TimeUnit.SECONDS);
    }

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }

    @Bean
    public GatewayCacheFactory gatewayCacheFactory(CacheManager cacheManager) {
        return new GatewayCacheFactory(cacheManager);
    }

    @Bean
    public ResponseCacheContext responseCacheContext(GatewayCacheFactory gatewayCacheFactory) {
        return new ResponseCacheContext(gatewayCacheFactory);
    }

}
