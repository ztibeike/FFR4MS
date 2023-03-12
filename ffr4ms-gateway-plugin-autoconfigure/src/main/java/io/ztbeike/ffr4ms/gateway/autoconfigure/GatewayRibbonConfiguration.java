package io.ztbeike.ffr4ms.gateway.autoconfigure;

import io.ztbeike.ffr4ms.gateway.ribbon.RequestReplayLoadBalanceRule;
import io.ztbeike.ffr4ms.gateway.ribbon.RequestReplayRoutingFilter;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    @Qualifier("routingExecutorService")
    private ExecutorService executorService;

    @Autowired
    private ProxyRequestHelper helper;

    @Autowired
    private RibbonCommandFactory<?> ribbonCommandFactory;

    @Autowired
    private List<RibbonRequestCustomizer> requestCustomizers;

    @Bean
    public RequestReplayRoutingFilter requestReplayRoutingFilter() {
        return new RequestReplayRoutingFilter(helper, ribbonCommandFactory, requestCustomizers, executorService);
    }

    @Bean
    public RequestReplayLoadBalanceRule requestReplayLoadBalanceRule() {
        return new RequestReplayLoadBalanceRule();
    }

}
