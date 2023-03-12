package io.ztbeike.ffr4ms.gateway.autoconfigure;

import io.ztbeike.ffr4ms.gateway.api.GatewayAPIRouter;
import io.ztbeike.ffr4ms.gateway.api.handler.ReplaceInstanceAPIHandler;
import io.ztbeike.ffr4ms.gateway.ribbon.RequestReplayLoadBalanceRule;
import io.ztbeike.ffr4ms.gateway.ribbon.RequestReplayRoutingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayAPIConfiguration {

    @Autowired
    private RequestReplayRoutingFilter routingFilter;

    @Autowired
    private RequestReplayLoadBalanceRule loadBalanceRule;

    @Bean
    public ReplaceInstanceAPIHandler replaceInstanceAPIHandler() {
        return new ReplaceInstanceAPIHandler(routingFilter, loadBalanceRule);
    }

    @Bean
    public GatewayAPIRouter gatewayAPIRouter() {
        GatewayAPIRouter router = new GatewayAPIRouter();
        router.addHandler("/replace", replaceInstanceAPIHandler());
        return router;
    }

}
