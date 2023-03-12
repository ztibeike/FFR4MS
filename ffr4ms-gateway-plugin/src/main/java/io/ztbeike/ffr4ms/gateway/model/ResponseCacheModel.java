package io.ztbeike.ffr4ms.gateway.model;

import lombok.*;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

/**
 * 缓存微服务实例响应
 */
@Data
@EqualsAndHashCode(exclude = "headers", callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCacheModel extends CacheModel {

    /**
     * 链路追踪id
     */
    private String traceId;

    /**
     * 请求uri
     */
    private String requestURI;

    /**
     * 服务实例名称
     */
    private String serviceName;

    /**
     * 响应码
     */
    private Integer responseCode;

    /**
     * 响应体
     */
    private String body;

    /**
     * 响应头
     */
    private HttpHeaders headers;

    @Override
    public boolean validForCache() {
        return !StringUtils.isEmpty(this.traceId)
                && !StringUtils.isEmpty(this.serviceName)
                && !StringUtils.isEmpty(this.requestURI);
    }

    public boolean valid() {
        // responseCode body headers可为空, 不可为null
        return validForCache() && responseCode != null && body != null && headers != null;
    }

}
