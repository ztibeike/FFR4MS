package io.ztbeike.ffr4ms.gateway.context;

import com.netflix.util.Pair;
import com.netflix.zuul.context.RequestContext;
import io.ztbeike.ffr4ms.common.constant.TraceConstant;
import io.ztbeike.ffr4ms.common.util.TraceUtil;
import io.ztbeike.ffr4ms.gateway.cache.GatewayCache;
import io.ztbeike.ffr4ms.gateway.cache.GatewayCacheFactory;
import io.ztbeike.ffr4ms.gateway.cache.support.CacheConstants;
import io.ztbeike.ffr4ms.gateway.model.ResponseCacheModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.cloud.netflix.ribbon.RibbonHttpResponse;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 缓存网关上下文中的响应结果
 */
@Slf4j
public class ResponseCacheContext {

    private final GatewayCache<ResponseCacheModel> cache;

    private final RequestContext context;

    public ResponseCacheContext(GatewayCacheFactory factory) {
        this.cache = factory.getCache(CacheConstants.RESPONSE_CACHE_NAME, ResponseCacheModel.class);
        this.context = GatewayRequestContext.getContext();
    }

    /**
     * 缓存当前上下文中的响应
     */
    public void set() {
        if (!shouldCache()) {
            return;
        }
        String traceId = GatewayRequestContext.getRequestHeader(TraceConstant.TRACE_ID_HEADER);

        // 获取网关转发请求的响应
        Object zuulResponse = this.context.get("zuulResponse");
        if (zuulResponse == null) {
            log.error("No zuul response in current context for traceId: {}", traceId);
            return;
        }
        RibbonHttpResponse response = (RibbonHttpResponse) zuulResponse;

        // 获取响应体
        String body = "";
        try {
            body = IOUtils.toString(response.getBody(), this.context.getResponse().getCharacterEncoding());
            // 获取response body后需要将body重新放进context中
            this.context.setResponseBody(body);
        } catch (IOException e) {
            log.error("Failed to get response body in current context for traceId: {}", traceId);
        }

        // 获取响应头
        HttpHeaders headers = new HttpHeaders();
        if (!CollectionUtils.isEmpty(response.getHeaders())) {
            headers.putAll(response.getHeaders());
        }

        ResponseCacheModel model = ResponseCacheModel.builder()
                .responseCode(context.getResponse().getStatus())
                .headers(headers)
                .body(body)
                .build();
        this.setCommonInfoFromContext(model);
        // 缓存响应
        this.cache.set(this.buildCacheKey(model), model);
    }

    /**
     * 从缓存中获取当前上下文的响应
     * @return 缓存的响应信息
     */
    public ResponseCacheModel get() {
        ResponseCacheModel model = new ResponseCacheModel();
        this.setCommonInfoFromContext(model);
        return this.cache.get(this.buildCacheKey(model));
    }

    /**
     * 从缓存中删除当前上下文中的响应信息
     */
    public void remove() {
        ResponseCacheModel model = new ResponseCacheModel();
        this.setCommonInfoFromContext(model);
        this.cache.remove(this.buildCacheKey(model));
    }

    /**
     * 抽取公共方法: 从上下文中获取服务名称, 请求URI和traceId
     * @param model 需要被设置相关属性的响应实体
     */
    private void setCommonInfoFromContext(ResponseCacheModel model) {
        // 获取服务实例名称
        Object serviceId = this.context.get(FilterConstants.SERVICE_ID_KEY);
        String serviceName = serviceId == null ? null : serviceId.toString();

        // 获取请求URI
        Object uri = this.context.get(FilterConstants.REQUEST_URI_KEY);
        String requestURI = uri == null ? null : uri.toString();

        String traceId = GatewayRequestContext.getRequestHeader(TraceConstant.TRACE_ID_HEADER);
        model.setServiceName(serviceName);
        model.setTraceId(traceId);
        model.setRequestURI(requestURI);
    }

    /**
     * 根据缓存实体构建缓存key
     * @param model 被缓存实体
     * @return 缓存key
     */
    private String buildCacheKey(ResponseCacheModel model) {
        StringJoiner joiner = new StringJoiner(":");
        joiner.add(model.getTraceId()).add(model.getServiceName()).add(model.getRequestURI());
        return joiner.toString();
    }

    /**
     * 判断是否需要被缓存
     * @return 判断结果
     */
    private boolean shouldCache() {
        Map<String, String> headers = GatewayRequestContext.getRequestHeaders();
        // 判断请求头中是否包含trace信息
        boolean containTrace = TraceUtil.containTraceInfo(headers);
        if (!containTrace) {
            if (log.isDebugEnabled()) {
                log.debug("Response in current context is not cached, because the request does not contain trace information");
            }
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
        if (log.isDebugEnabled()) {
            log.debug("Response in current context is not cached, because downstream response is already cached by it's gateway");
        }
        return false;
    }

}

