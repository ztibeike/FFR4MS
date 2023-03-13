package io.ztbeike.ffr4ms.gateway.autoconfigure;

import io.ztbeike.ffr4ms.gateway.ribbon.RequestReplayLoadBalanceRule;
import io.ztbeike.ffr4ms.gateway.ribbon.RequestReplayRoutingFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.netflix.ribbon.support.RibbonRequestCustomizer;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Configuration
public class GatewayRibbonConfiguration {


    @Bean
    public RequestReplayRoutingFilter requestReplayRoutingFilter(ProxyRequestHelper helper, RibbonCommandFactory<?> factory,
                                                                 List<RibbonRequestCustomizer> customizers, @Qualifier("routingExecutorService") ExecutorService executorService ) {
        return new RequestReplayRoutingFilter(helper, factory, customizers, executorService);
    }

    @Bean
    public RequestReplayLoadBalanceRule requestReplayLoadBalanceRule() {
        return new RequestReplayLoadBalanceRule();
    }

}
