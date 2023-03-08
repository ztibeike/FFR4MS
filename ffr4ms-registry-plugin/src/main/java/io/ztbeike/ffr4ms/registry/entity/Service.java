package io.ztbeike.ffr4ms.registry.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Service {

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
    private String port;

    /**
     * 服务实例地址, 即IP:Port
     */
    private String address;
}