package io.ztbeike.ffr4ms.gateway.context;

import com.netflix.util.Pair;
import com.netflix.zuul.context.RequestContext;
import io.ztbeike.ffr4ms.common.constant.TraceConstant;
import io.ztbeike.ffr4ms.gateway.cache.GatewayCache;
import io.ztbeike.ffr4ms.gateway.model.ResponseCacheModel;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 缓存网关上下文中的响应结果
 */
public class ResponseCacheContext {

    private GatewayCache<ResponseCacheModel> cache;

    public ResponseCacheContext(GatewayCache<ResponseCacheModel> cache) {
        this.cache = cache;
    }

    public void set() {
        if (!shouldCache()) {
            return;
        }
        RequestContext context = GatewayRequestContext.getContext();
        HttpServletResponse response = context.getResponse();
        Object zuulResponse = context.get("zuulResponse");
        // TODO
    }

    private String buildCacheKey(ResponseCacheModel model) {
        if (!model.validForCache()) {
            return null;
        }
        StringJoiner joiner = new StringJoiner(":");
        joiner.add(model.getTraceId()).add(model.getServiceName()).add(model.getRequestUri());
        return joiner.toString();
    }

    private boolean shouldCache() {
        Map<String, String> headers = GatewayRequestContext.getRequestHeaders();
        // 判断请求头中是否包含trace信息
        boolean containTrace = !StringUtils.isEmpty(headers.get(TraceConstant.TRACE_ID_HEADER))
                && !StringUtils.isEmpty(headers.get(TraceConstant.SERVICE_NAME_HEADER))
                && !StringUtils.isEmpty(headers.get(TraceConstant.TRACE_SERVICE_INSTANCE_HEADER));
        if (!containTrace) {
            return false;
        }
        // 如果请求头中包含trace信息, 则判断下游的响应是否是网关缓存的
        List<Pair<String, String>> responseHeaders = GatewayRequestContext.getContext().getZuulResponseHeaders();
        if (CollectionUtils.isEmpty(responseHeaders)) {
            for (Pair<String, String> responseHeader : responseHeaders) {
                if (TraceConstant.CACHED_RESPONSE_FLAG_HEADER.equals(responseHeader.first())
                        && Boolean.TRUE.toString().equalsIgnoreCase(responseHeader.second())) {
                    return true;
                }
            }
        }
        return false;
    }

}

