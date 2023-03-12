package io.ztbeike.ffr4ms.gateway.ribbon;

import lombok.*;

/**
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class ServiceInstance {

    /**
     * 实例名称
     */
    @NonNull
    private String serviceName;

    /**
     * 实例IP
     */
    private String host;

    /**
     * 实例端口
     */
    private Integer port;

    /**
     * 实例状态
     */
    private ServiceInstanceStatus status;

}
