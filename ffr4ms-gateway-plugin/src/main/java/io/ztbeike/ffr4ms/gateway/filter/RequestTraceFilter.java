package io.ztbeike.ffr4ms.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.ztbeike.ffr4ms.common.constant.TraceConstant;
import io.ztbeike.ffr4ms.common.util.IdUtil;
import io.ztbeike.ffr4ms.gateway.context.GatewayRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.util.StringUtils;

/**
 * 给请求注入traceId
 */
@Slf4j
public class RequestTraceFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = GatewayRequestContext.getContext();
        String traceId = GatewayRequestContext.getRequestHeader(TraceConstant.TRACE_ID_HEADER);
        if (StringUtils.isEmpty(traceId)) {
            traceId = IdUtil.getUUID();
            if (log.isDebugEnabled()) {
                log.debug("Generated trace id: {}", traceId);
            }
        }
        context.addZuulRequestHeader(TraceConstant.TRACE_ID_HEADER, traceId);
        return null;
    }
}
