package io.ztbeike.ffr4ms.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import io.ztbeike.ffr4ms.gateway.context.ResponseCacheContext;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * 缓存网关上下文中的响应
 */
public class ResponseCacheFilter extends ZuulFilter {

    private ResponseCacheContext context;

    public ResponseCacheFilter(ResponseCacheContext context) {
        this.context = context;
    }

    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
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
        context.set();
        return null;
    }
}
