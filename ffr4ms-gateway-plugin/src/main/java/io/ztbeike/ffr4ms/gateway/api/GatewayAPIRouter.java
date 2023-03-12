package io.ztbeike.ffr4ms.gateway.api;

import io.ztbeike.ffr4ms.gateway.api.dto.FrecoveryResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * API路由
 */
public class GatewayAPIRouter {

    private final Map<String, GatewayAPIHandler> router;

    /**
     * API路由的前缀
     */
    private static final String gatewayApiPrefix = "/frecovery";


    /**
     * @return API路由的前缀
     */
    public static String getGatewayApiPrefix() {
        return gatewayApiPrefix;
    }

    public GatewayAPIRouter() {
        this.router = new HashMap<>();
        // 默认路由
        this.router.put(gatewayApiPrefix, new GatewayAPIHandler() {
            @Override
            public FrecoveryResponse run() {
                return FrecoveryResponse.bad().setMessage("No such API");
            }
        });
    }

    /**
     * 添加路由
     * @param uri 路由uri
     * @param handler 路由handler
     */
    public void addHandler(String uri, GatewayAPIHandler handler) {
        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }
        this.router.put(gatewayApiPrefix + uri, handler);
    }

    /**
     * 获取路由
     * @param uri 路由uri
     * @return 路由handler
     */
    public GatewayAPIHandler getHandler(String uri) {
        if (!this.router.containsKey(uri)) {
            uri = gatewayApiPrefix;
        }
        return this.router.get(uri);
    }

}
