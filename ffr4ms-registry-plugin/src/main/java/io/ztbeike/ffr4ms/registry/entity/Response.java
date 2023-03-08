package io.ztbeike.ffr4ms.registry.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {

    /**
     * 服务实例列表
     */
    private Map<String, List<Service>> services;

    /**
     * 网关实例列表
     */
    private Map<String, List<Service>> gateways;

    /**
     * 服务实例组(cluster)名称
     */
    private Set<String> groups;

}
