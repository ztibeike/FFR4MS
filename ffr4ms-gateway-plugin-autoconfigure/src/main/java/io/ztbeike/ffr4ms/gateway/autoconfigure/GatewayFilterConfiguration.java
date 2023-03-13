package io.ztbeike.ffr4ms.gateway.autoconfigure;

import io.ztbeike.ffr4ms.gateway.api.GatewayAPIRouter;
import io.ztbeike.ffr4ms.gateway.context.ResponseCacheContext;
import io.ztbeike.ffr4ms.gateway.filter.RequestRetryProxyFilter;
import io.ztbeike.ffr4ms.gateway.filter.RequestTraceFilter;
import io.ztbeike.ffr4ms.gateway.filter.ResponseCacheFilter;
import io.ztbeike.ffr4ms.gateway.filter.ZuulApiHandleFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayFilterConfiguration {

    @Bean
    public RequestRetryProxyFilter requestRetryProxyFilter(ResponseCacheContext context) {
        return new RequestRetryProxyFilter(context);
    }

    @Bean
    public RequestTraceFilter requestTraceFilter() {
        return new RequestTraceFilter();
    }

    @Bean
    public ResponseCacheFilter responseCacheFilter(ResponseCacheContext context) {
        return new ResponseCacheFilter(context);
    }

    @Bean
    public ZuulApiHandleFilter zuulApiHandleFilter(GatewayAPIRouter router) {
        return new ZuulApiHandleFilter(router);
    }

}
