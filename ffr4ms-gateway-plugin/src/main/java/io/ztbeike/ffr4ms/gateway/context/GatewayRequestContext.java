package io.ztbeike.ffr4ms.gateway.context;

import com.netflix.zuul.context.RequestContext;
import lombok.experimental.UtilityClass;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;

/**
 * 获取网关转发的请求上下文
 */
@UtilityClass
public class GatewayRequestContext {

    /**
     * @return 网关请求转发的上下文
     */
    public RequestContext getContext() {
        return RequestContext.getCurrentContext();
    }

    /**
     * @return 被转发的请求
     */
    public HttpServletRequest getRequest() {
        return getContext().getRequest();
    }

    /**
     * 获取请求头
     * @param headerKey 请求头key
     * @return 请求头value
     */
    public String getRequestHeader(String headerKey) {
        Map<String, String> headers = getRequestHeaders();
        String value = headers.get(headerKey);
        return value == null ? headers.get(headerKey.toLowerCase()) : value;
    }

    /**
     * @return 合并被转发请求和真实发送请求的请求头
     */
    public Map<String, String> getRequestHeaders() {
        // 获取真实发送的请求头
        Map<String, String> headers = getContext().getZuulRequestHeaders();
        HttpServletRequest request = getRequest();
        // 获取被转发请求的请求头
        Enumeration<String> headerNames = request.getHeaderNames();
        // 合并两种请求头
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            if (!headers.containsKey(key)) {
                headers.put(key, request.getHeader(key));
            }
        }
        return headers;
    }

}
