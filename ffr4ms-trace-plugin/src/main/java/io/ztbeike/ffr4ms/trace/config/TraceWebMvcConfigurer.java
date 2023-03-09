package io.ztbeike.ffr4ms.trace.config;

import io.ztbeike.ffr4ms.trace.interceptor.TraceWebHandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class TraceWebMvcConfigurer implements WebMvcConfigurer {

    private TraceWebHandlerInterceptor interceptor;

    public TraceWebMvcConfigurer(TraceWebHandlerInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor);
    }
}
