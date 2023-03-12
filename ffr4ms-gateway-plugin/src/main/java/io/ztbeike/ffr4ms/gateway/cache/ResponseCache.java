package io.ztbeike.ffr4ms.gateway.cache;

import io.ztbeike.ffr4ms.gateway.model.ResponseCacheModel;
import org.springframework.cache.CacheManager;

public class ResponseCache extends DefaultGatewayCache<ResponseCacheModel> {

    private static final String CACHE_NAME = "RESPONSE";

    public ResponseCache(CacheManager cacheManager) {
        super(CACHE_NAME, cacheManager);
    }
}
