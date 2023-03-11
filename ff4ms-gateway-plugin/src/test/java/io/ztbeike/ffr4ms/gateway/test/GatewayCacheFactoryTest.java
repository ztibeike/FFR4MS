package io.ztbeike.ffr4ms.gateway.test;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.ztbeike.ffr4ms.gateway.cache.GatewayCache;
import io.ztbeike.ffr4ms.gateway.cache.GatewayCacheFactory;
import io.ztbeike.ffr4ms.gateway.cache.support.CacheConstants;
import io.ztbeike.ffr4ms.gateway.model.ResponseCacheModel;
import org.junit.Test;
import org.springframework.cache.caffeine.CaffeineCacheManager;

public class GatewayCacheFactoryTest {

    @Test
    public void testGetCache() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000);
        cacheManager.setCaffeine(caffeine);
        GatewayCacheFactory factory = new GatewayCacheFactory(cacheManager);
        GatewayCache<ResponseCacheModel> cache = factory.getCache(CacheConstants.RESPONSE_CACHE_NAME, ResponseCacheModel.class);
        cache.set("test", ResponseCacheModel.builder().traceId("123").serviceName("234").requestURI("123123").build());
        ResponseCacheModel model = cache.get("test");
        System.out.println(model.toString());
        cache.remove("test");
    }

}
