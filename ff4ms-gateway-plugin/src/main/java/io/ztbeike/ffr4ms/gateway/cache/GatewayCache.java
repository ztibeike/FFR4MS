package io.ztbeike.ffr4ms.gateway.cache;

import io.ztbeike.ffr4ms.gateway.model.CacheModel;

public interface GatewayCache<T extends CacheModel> {

    boolean set(String key, T object);

    T get(String key);

    void remove(String key);

}
