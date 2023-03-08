package io.ztbeike.ffr4ms.registry;

import com.netflix.discovery.shared.Application;
import com.netflix.eureka.EurekaServerContextHolder;
import com.netflix.eureka.registry.InstanceRegistry;
import io.ztbeike.ffr4ms.registry.entity.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 封装对注册中心的各种操作
 * 目前主要功能是获取服务实例列表
 */
@Slf4j
public class EurekaPluginService {

    /**
     * Eureka注册中心对象
     */
    private final InstanceRegistry registry = EurekaServerContextHolder.getInstance().getServerContext().getRegistry();

    /**
     * 获取Eureka注册的服务列表
     * @return 特定格式的服务列表
     */
    public Response getApps() {
        List<Application> sortedApplications = this.registry.getSortedApplications();
        ArrayList<Map<String, Object>> apps = new ArrayList<>();
        // TODO

        log.info(sortedApplications.toString());
        return new Response();
    }

}
