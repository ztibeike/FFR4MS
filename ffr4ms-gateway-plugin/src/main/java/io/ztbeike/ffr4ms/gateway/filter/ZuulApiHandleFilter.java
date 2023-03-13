package io.ztbeike.ffr4ms.gateway.filter;

import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.ztbeike.ffr4ms.common.constant.APIConstant;
import io.ztbeike.ffr4ms.gateway.api.GatewayAPIHandler;
import io.ztbeike.ffr4ms.gateway.api.GatewayAPIRouter;
import io.ztbeike.ffr4ms.gateway.api.dto.FrecoveryResponse;
import io.ztbeike.ffr4ms.gateway.context.GatewayRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * 外部访问API, 配合Fr-Docker完成其功能
 */
@Slf4j
public class ZuulApiHandleFilter extends ZuulFilter {

    private GatewayAPIRouter router;

    public ZuulApiHandleFilter(GatewayAPIRouter router) {
        this.router = router;
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return -10;
    }

    @Override
    public boolean shouldFilter() {
        return GatewayRequestContext.getRequest().getRequestURI().startsWith(APIConstant.API_URL_COMMON_PREFIX);
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = GatewayRequestContext.getContext();
        // 设置网关不转发本请求
        context.setSendZuulResponse(false);
        context.setResponseStatusCode(HttpStatus.OK.value());
        String requestURI = GatewayRequestContext.getRequest().getRequestURI();
        // 根据uri获取对应的handler
        GatewayAPIHandler handler = router.getHandler(requestURI);
        FrecoveryResponse response = handler.run();

        try {
            // 返回执行结果
            context.getResponse().getWriter().write(JSONObject.toJSONString(response));
        } catch (IOException | JSONException e) {
            log.error("Error occurs while write response to context");
            throw new ZuulException(e, 0, "ZuulApiHandleFilter");
        }
        return null;
    }
}
