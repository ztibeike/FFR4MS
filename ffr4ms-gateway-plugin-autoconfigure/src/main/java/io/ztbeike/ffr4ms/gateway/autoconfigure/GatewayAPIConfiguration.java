package io.ztbeike.ffr4ms.gateway.autoconfigure;

import io.ztbeike.ffr4ms.gateway.api.GatewayAPIRouter;
import io.ztbeike.ffr4ms.gateway.api.handler.ReplaceInstanceAPIHandler;
import io.ztbeike.ffr4ms.gateway.ribbon.RequestReplayLoadBalanceRule;
import io.ztbeike.ffr4ms.gateway.ribbon.RequestReplayRoutingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayAPIConfiguration {

    @Bean
    public ReplaceInstanceAPIHandler replaceInstanceAPIHandler(RequestReplayRoutingFilter routingFilter, RequestReplayLoadBalanceRule loadBalanceRule) {
        return new ReplaceInstanceAPIHandler(routingFilter, loadBalanceRule);
    }

    @Bean
    public GatewayAPIRouter gatewayAPIRouter(ReplaceInstanceAPIHandler apiHandler) {
        GatewayAPIRouter router = new GatewayAPIRouter();
        router.addHandler("/replace", apiHandler);
        return router;
    }

}
