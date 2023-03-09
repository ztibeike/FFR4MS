package io.ztbeike.ffr4ms.trace.config;

import io.ztbeike.ffr4ms.common.util.NetUtil;
import io.ztbeike.ffr4ms.common.util.TraceUtil;
import io.ztbeike.ffr4ms.trace.interceptor.TraceRequestInterceptor;
import io.ztbeike.ffr4ms.trace.interceptor.TraceWebHandlerInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Configuration
public class TraceConfiguration {

    @Value("${spring.cloud.client.ip-address}")
    private String ipAddress;

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${server.port}")
    private String port;

    @Bean
    public TraceWebHandlerInterceptor traceWebHandlerInterceptor() {
        return new TraceWebHandlerInterceptor();
    }

    @Bean
    public TraceRequestInterceptor traceRequestInterceptor() {
        if (StringUtils.isEmpty(this.ipAddress)) {
            this.ipAddress = NetUtil.getLocalAddress();
        }
        Assert.hasText(this.ipAddress, "Configuration error, IP Address not found");
        Assert.hasText(this.serviceName, "Configuration error, key \"spring.application.name\" not found");
        Assert.hasText(this.port, "Configuration error, key \"server.port\" not found");
        String instanceId = TraceUtil.makeInstanceId(this.serviceName, this.ipAddress, Integer.parseInt(this.port));
        return new TraceRequestInterceptor(serviceName, instanceId);
    }

    @Bean
    public RestTemplateBeanPostProcessor restTemplateBeanPostProcessor() {
        return new RestTemplateBeanPostProcessor(traceRequestInterceptor());
    }

    @Bean
    public TraceWebMvcConfigurer traceWebMvcConfigurer() {
        return new TraceWebMvcConfigurer(traceWebHandlerInterceptor());
    }

}
