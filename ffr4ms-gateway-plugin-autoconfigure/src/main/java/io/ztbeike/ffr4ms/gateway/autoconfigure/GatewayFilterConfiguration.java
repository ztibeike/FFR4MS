package io.ztbeike.ffr4ms.gateway.autoconfigure;

import io.ztbeike.ffr4ms.gateway.api.GatewayAPIRouter;
import io.ztbeike.ffr4ms.gateway.context.ResponseCacheContext;
import io.ztbeike.ffr4ms.gateway.filter.RequestRetryProxyFilter;
import io.ztbeike.ffr4ms.gateway.filter.RequestTraceFilter;
import io.ztbeike.ffr4ms.gateway.filter.ResponseCacheFilter;
import io.ztbeike.ffr4ms.gateway.filter.ZuulApiHandleFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayFilterConfiguration {

    @Autowired
    private ResponseCacheContext context;

    @Autowired
    private GatewayAPIRouter router;

    @Bean
    public RequestRetryProxyFilter requestRetryProxyFilter() {
        return new RequestRetryProxyFilter(context);
    }

    @Bean
    public RequestTraceFilter requestTraceFilter() {
        return new RequestTraceFilter();
    }

    @Bean
    public ResponseCacheFilter responseCacheFilter() {
        return new ResponseCacheFilter(context);
    }

    @Bean
    public ZuulApiHandleFilter zuulApiHandleFilter() {
        return new ZuulApiHandleFilter(router);
    }

}
