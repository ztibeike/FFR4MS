package io.ztbeike.ffr4ms.registry.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 微服务实例
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MSInstance {

    /**
     * 服务实例名称, 即实例组名称
     */
    private String name;

    /**
     * 服务实例IP
     */
    private String ip;

    /**
     * 服务实例端口
     */
    private Integer port;

    /**
     * 服务实例地址, 即IP:Port
     */
    private String address;

    /**
     * 服务实例元信息
     */
    private Map<String, String> metadata;
}