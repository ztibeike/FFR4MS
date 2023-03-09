package io.ztbeike.ffr4ms.trace.interceptor;

import io.ztbeike.ffr4ms.common.constant.TraceConstant;
import io.ztbeike.ffr4ms.trace.context.TraceContext;
import io.ztbeike.ffr4ms.trace.context.TraceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
public class TraceRequestInterceptor implements ClientHttpRequestInterceptor {

    private String serviceName;

    private String instanceId;

    public TraceRequestInterceptor(String serviceName, String instanceId) {
        this.serviceName = serviceName;
        this.instanceId = instanceId;
    }

    /**
     * 拦截http请求, 注入链路跟踪信息
     * @param request
     * @param bytes
     * @param execution
     * @return
     * @throws IOException
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        TraceContext context = TraceContextHolder.getInstance().get();
        if (context != null && !StringUtils.isEmpty(context.getTraceId())) {
            headers.add(TraceConstant.TRACE_ID_HEADER, context.getTraceId());
        }
        headers.add(TraceConstant.SERVICE_NAME_HEADER, serviceName);
        headers.add(TraceConstant.TRACE_SERVICE_INSTANCE_HEADER, instanceId);
        return execution.execute(request, bytes);
    }

}
