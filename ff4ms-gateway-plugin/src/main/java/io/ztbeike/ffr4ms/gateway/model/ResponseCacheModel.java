package io.ztbeike.ffr4ms.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 缓存微服务实例响应
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCacheModel {

    /**
     * 链路追踪id
     */
    private String traceId;

    /**
     * 请求uri
     */
    private String requestUri;

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


    private Map<String, String> headers;

    public boolean validForCache() {
        return !StringUtils.isEmpty(this.traceId)
                && !StringUtils.isEmpty(this.serviceName)
                && !StringUtils.isEmpty(this.requestUri);
    }

    public boolean valid() {
        // responseCode body headers可为空, 不可为null
        return validForCache() && responseCode != null && body != null && headers != null;
    }

}
