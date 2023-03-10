package io.ztbeike.ffr4ms.gateway.cache;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;

@Slf4j
public abstract class DefaultGatewayCache<T> implements GatewayCache<T>{

    @Getter
    private Cache cache;

    public DefaultGatewayCache(String cacheName, CacheManager cacheManager) {
        this.cache = cacheManager.getCache(cacheName);
    }

    @Override
    public boolean set(String key, T object) {
        cache.put(key, object);
        if (log.isDebugEnabled()) {
            log.debug("cache data, key: {}, value: {}", key, object);
        }
        return true;
    }

    @Override
    @Nullable
    public T get(String key) {
        Cache.ValueWrapper wrapper = cache.get(key);
        return wrapper != null ? (T) wrapper.get() : null;
    }

    @Override
    public void remove(String key) {
        this.cache.evict(key);
        if (log.isDebugEnabled()) {
            log.debug("remove cache, key: {}", key);
        }
    }
}
