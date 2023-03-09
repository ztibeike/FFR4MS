package io.ztbeike.ffr4ms.registry.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 微服务系统配置
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MSConfig {

    /**
     * 服务实例列表
     */
    private Map<String, List<MSInstance>> services;

    /**
     * 网关实例列表
     */
    private Map<String, List<MSInstance>> gateways;

    /**
     * 服务实例组(cluster)名称
     */
    private Set<String> groups;

}
