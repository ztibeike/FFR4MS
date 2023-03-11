package io.ztbeike.ffr4ms.gateway.cache;

import io.ztbeike.ffr4ms.gateway.model.CacheModel;
import org.springframework.cache.CacheManager;

import java.util.HashMap;
import java.util.Map;

public class GatewayCacheFactory {

    /**
     * 保存单例cache
     */
    private final Map<String, GatewayCache<? extends CacheModel>> singletonCacheMap;

    private final CacheManager cacheManager;

    public GatewayCacheFactory(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.singletonCacheMap = new HashMap<>();
    }

    public <T extends CacheModel> GatewayCache<T> getCache(String cacheName, Class<T> modelClass) {
        if (!this.singletonCacheMap.containsKey(cacheName)) {
            synchronized (this.singletonCacheMap) {
                if (!this.singletonCacheMap.containsKey(cacheName)) {
                    GatewayCache<T> cache = new DefaultGatewayCache<>(cacheName, cacheManager);
                    this.singletonCacheMap.put(cacheName, cache);
                }
            }
        }
        return (GatewayCache<T>) this.singletonCacheMap.get(cacheName);
    }

}
