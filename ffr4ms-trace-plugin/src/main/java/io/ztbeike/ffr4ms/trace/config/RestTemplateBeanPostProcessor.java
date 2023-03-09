package io.ztbeike.ffr4ms.trace.config;

import io.ztbeike.ffr4ms.trace.interceptor.TraceRequestInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class RestTemplateBeanPostProcessor implements BeanPostProcessor {


    private TraceRequestInterceptor interceptor;

    public RestTemplateBeanPostProcessor(TraceRequestInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RestTemplate) {
            RestTemplate restTemplate = (RestTemplate) bean;
            List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
            if (interceptors == null) {
                interceptors = new ArrayList<>();
            }
            interceptors.add(interceptor);
            restTemplate.setInterceptors(interceptors);
        }
        return bean;
    }
}
