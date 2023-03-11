package io.ztbeike.ffr4ms.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.ztbeike.ffr4ms.common.constant.TraceConstant;
import io.ztbeike.ffr4ms.common.util.TraceUtil;
import io.ztbeike.ffr4ms.gateway.context.GatewayRequestContext;
import io.ztbeike.ffr4ms.gateway.context.ResponseCacheContext;
import io.ztbeike.ffr4ms.gateway.model.ResponseCacheModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 拦截上游重播的请求, 从缓存中提取并返回响应结果
 */
@Slf4j
public class RequestRetryProxyFilter extends ZuulFilter {

    private final ResponseCacheContext context;

    private final Map<String, String> IGNORE_SET_HEADER;


    public RequestRetryProxyFilter(ResponseCacheContext context) {
        this.context = context;
        this.IGNORE_SET_HEADER = new HashMap<>(2);
        this.IGNORE_SET_HEADER.put("connection", "keep-alive");
        this.IGNORE_SET_HEADER.put("transfer-encoding", "chunked");
    }

    @Override
    public String filterType() {
        return FilterConstants.ROUTE_TYPE;
    }

    @Override
    public int filterOrder() {
        return -1;
    }

    @Override
    public boolean shouldFilter() {
        Map<String, String> headers = GatewayRequestContext.getRequestHeaders();
        boolean containTrace = TraceUtil.containTraceInfo(headers);
        if (!containTrace) {
            if (log.isDebugEnabled()) {
                log.debug("Request is not proxied, because the request does not contain trace information");
            }
            return false;
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        ResponseCacheModel model = context.get();
        if (model != null) {
            if (log.isDebugEnabled()) {
                log.debug("Request for traceId {} is proxied, hit response cache", model.getTraceId());
            }
            RequestContext requestContext = GatewayRequestContext.getContext();
            // 设置网关不转发该请求
            requestContext.setSendZuulResponse(false);
            // 设置响应内容
            requestContext.setResponseStatusCode(model.getResponseCode());
            requestContext.setResponseBody(model.getBody());
            if (!CollectionUtils.isEmpty(model.getHeaders())) {
                HttpHeaders headers = model.getHeaders();
                headers.forEach((key, value) -> {
                    value.forEach(val -> {
                        if (StringUtils.isEmpty(val)) {
                            return;
                        }
                        if (this.IGNORE_SET_HEADER.containsKey(key.toLowerCase())
                                && val.equalsIgnoreCase(this.IGNORE_SET_HEADER.get(key.toLowerCase()))) {
                            return;
                        }
                        requestContext.addZuulResponseHeader(key, val);
                    });
                });
            }
            // 标记该响应是缓存响应
            requestContext.addZuulResponseHeader(TraceConstant.CACHED_RESPONSE_FLAG_HEADER, Boolean.TRUE.toString());
        }
        return null;
    }
}
