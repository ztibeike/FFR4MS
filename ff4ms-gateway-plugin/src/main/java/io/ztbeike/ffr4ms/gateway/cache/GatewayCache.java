package io.ztbeike.ffr4ms.gateway.cache;

public interface GatewayCache<T> {

    boolean set(String key, T object);

    T get(String key);

    void remove(String key);

}
