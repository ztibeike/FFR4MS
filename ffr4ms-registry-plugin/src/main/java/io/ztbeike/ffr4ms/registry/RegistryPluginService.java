package io.ztbeike.ffr4ms.registry;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.eureka.EurekaServerContextHolder;
import com.netflix.eureka.registry.InstanceRegistry;
import io.ztbeike.ffr4ms.common.constant.GatewayConstant;
import io.ztbeike.ffr4ms.registry.entity.MSConfig;
import io.ztbeike.ffr4ms.registry.entity.MSInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 封装对注册中心的各种操作
 * 目前主要功能是获取服务实例列表
 */
@Slf4j
public class RegistryPluginService {

    /**
     * Eureka注册中心对象
     */
    private final InstanceRegistry registry = EurekaServerContextHolder.getInstance().getServerContext().getRegistry();

    /**
     * 获取Eureka注册的服务列表
     * @return 特定格式的服务列表
     */
    public MSConfig getApps() {
        List<Application> sortedApplications = this.registry.getSortedApplications();
        MSConfig config = new MSConfig();
        Map<String, List<MSInstance>> services = new HashMap<>();
        Map<String, List<MSInstance>> gateways = new HashMap<>();
        for (Application application : sortedApplications) {
            String appName = application.getName();
            List<InstanceInfo> instances = application.getInstances();
            if (instances.isEmpty()) {
                continue;
            }
            // 获取application的类型: metadata中包含gateway字段则为网关
            AppType appType = AppType.SERVICE;
            Map<String, String> metadata = instances.get(0).getMetadata();
            if (metadata.containsKey(GatewayConstant.GATEWAY_METADATA_KEY)) {
                appType = AppType.GATEWAY;
            }
            List<MSInstance> msInstances = new ArrayList<>();
            for (InstanceInfo instance: instances) {
                MSInstance msInstance = new MSInstance();
                msInstance.setName(instance.getAppName());
                msInstance.setIp(instance.getIPAddr());
                msInstance.setPort(instance.getPort());
                msInstance.setAddress(instance.getInstanceId());
                if (appType.equals(AppType.GATEWAY)) {
                    msInstance.setMetadata(instance.getMetadata());
                    appName = instance.getMetadata().get(GatewayConstant.GATEWAY_METADATA_KEY).toUpperCase() + GatewayConstant.GATEWAY_APP_NAME_SUFFIX;
                    msInstance.setName(appName);
                }
                msInstances.add(msInstance);
            }
            if (appType.equals(AppType.SERVICE)) {
                services.put(appName, msInstances);
            } else {
                gateways.put(appName, msInstances);
            }
        }
        config.setServices(services);
        config.setGateways(gateways);
        config.setGroups(services.keySet());

        return config;
    }

    enum AppType {
        SERVICE, GATEWAY
    }

}
