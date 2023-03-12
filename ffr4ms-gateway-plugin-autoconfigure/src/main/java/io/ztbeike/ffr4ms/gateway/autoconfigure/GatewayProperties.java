package io.ztbeike.ffr4ms.gateway.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * 覆盖eureka的${eureka.instance} properties中的metadata-map
 * 校验网关启动时是否指定所属的微服务实例组
 */
@Data
@Validated
@ConfigurationProperties(prefix = "eureka.instance.metadata-map")
public class GatewayProperties {

    @NotEmpty(message = "Gateway requires to be specified with service group")
    private String gateway;

}
