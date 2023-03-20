package io.ztbeike.ffr4ms.gateway.ribbon;

import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.Server;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息重播负载均衡规则, 标记微服务实例状态
 */
public class RequestReplayLoadBalanceRule extends RandomRule {

    /**
     * 维护服务名称与实例列表的对应关系,
     * 只有当服务状态被标记为故障或优先时才会添加到列表中,
     * 详见{@link #markServiceInstanceStatus(ServiceInstance, ServiceInstanceStatus)}
     */
    private final Map<String, ServiceInstanceList> serviceMap = new HashMap<>();

    /**
     * 负载均衡算法选择服务实例
     */
    @Override
    public Server choose(ILoadBalancer lb, Object key) {
        Server server = null;
        if (lb instanceof BaseLoadBalancer) {
            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) lb;
            String serviceName = loadBalancer.getName();
            // 首先从优先实例列表中选择
            server = priorChoose(serviceName, lb);
            // 如果没有优先实例, 则从所有实例中随机选取
            if (server == null) {
                server = randomChoose(lb.getReachableServers(), serviceName);
            }
        }
        if (server != null) {
            // 将实例信息注入线程上下文
            RouteExecuteInfo executeInfo = RequestReplayRoutingFilter.getContextExecuteInfo();
            executeInfo.setHost(server.getHost());
            executeInfo.setPort(server.getPort());
        }
        return server;
    }

    /**
     * 选择优先的实例
     * @param serviceName 服务名称
     * @param lb 负载均衡器
     * @return 目标实例
     */
    private Server priorChoose(String serviceName, ILoadBalancer lb) {
        ServiceInstanceList serviceInstanceList = this.serviceMap.get(serviceName);
        if (serviceInstanceList == null || !CollectionUtils.isEmpty(serviceInstanceList.priorInstances)) {
            return null;
        }
        List<ServiceInstance> priorInstances = new ArrayList<>(this.serviceMap.get(serviceName).priorInstances.size());
        synchronized (this.serviceMap.get(serviceName).priorInstances) {
            priorInstances.addAll(this.serviceMap.get(serviceName).priorInstances);
        }
        if (priorInstances.isEmpty()) {
            return null;
        }
        // 获取负载均衡器可用的实例列表
        List<Server> servers = lb.getReachableServers();
        ServiceInstance instance = ServiceInstance.builder().serviceName(serviceName).status(ServiceInstanceStatus.PRIOR).build();
        // 查找当前微服务可用的优先实例
        servers = servers.stream().filter(server -> {
            instance.setHost(server.getHost());
            instance.setPort(server.getPort());
            return priorInstances.contains(instance);
        }).collect(Collectors.toList());
        // 从可用优先实例中随机选取实例
        Server server = randomChoose(servers, serviceName);
        if (server != null) {
            // 找到被选取的实例
            ServiceInstance temp = new ServiceInstance(serviceName, server.getHost(), server.getPort(), ServiceInstanceStatus.PRIOR, 0);
            ServiceInstance priorInstance = priorInstances.stream().filter(instance1 -> instance1.equals(temp)).collect(Collectors.toList()).get(0);
            // 使用CAS将该实例的优先选取次数减1
            int count = priorInstance.decreasePriorCount();
            // 如果被优先选取的次数大于阈值, 则移除优先列表, 避免所有请求都转发至优先实例
            if (count <= 0) {
                synchronized (this.serviceMap.get(serviceName).priorInstances) {
                    this.serviceMap.get(serviceName).priorInstances.remove(priorInstance);
                }
            }
        }
        return server;
    }

    /**
     * 随机选择实例
     * @param servers 可用实例列表
     * @param serviceName 服务名称
     * @return 目标实例
     */
    private Server randomChoose(List<Server> servers, String serviceName) {
        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }
        // 排除故障实例
        if (this.serviceMap.containsKey(serviceName)) {
            synchronized (this.serviceMap.get(serviceName).faultInstances) {
                List<ServiceInstance> faultInstances = this.serviceMap.get(serviceName).faultInstances;
                if (!CollectionUtils.isEmpty(faultInstances)) {
                    ServiceInstance instance = ServiceInstance.builder().serviceName(serviceName).status(ServiceInstanceStatus.FAULT).build();
                    servers = servers.stream().filter(server -> {
                        instance.setHost(server.getHost());
                        instance.setPort(server.getPort());
                        return !faultInstances.contains(instance);
                    }).collect(Collectors.toList());
                }
            }
        }
        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }
        // 随机选择
        Server server = servers.get(new Random().nextInt(servers.size()));
        return server.isAlive() ? server : null;
    }

    /**
     * 标记服务实例状态
     * @param instance 服务实例
     * @param status 状态
     */
    public void markServiceInstanceStatus(ServiceInstance instance, ServiceInstanceStatus status) {
        String serviceName = instance.getServiceName();
        if (!this.serviceMap.containsKey(serviceName)) {
            // 初始化
            synchronized (this.serviceMap) {
                if (!this.serviceMap.containsKey(serviceName)) {
                    ServiceInstanceList list = ServiceInstanceList.builder()
                            .serviceName(serviceName)
                            .priorInstances(new ArrayList<>())
                            .faultInstances(new ArrayList<>()).build();
                    this.serviceMap.put(serviceName, list);
                }
            }
        }
        ServiceInstanceList serviceInstanceList = this.serviceMap.get(serviceName);
        instance.setStatus(status);
        // 策略模式标记状态
        status.markStatus(serviceInstanceList, instance);
    }

    @AllArgsConstructor
    @Getter
    @Builder
    public static class ServiceInstanceList {

        /**
         * 服务实例名称
         */
        private final String serviceName;


        /**
         * 优先选择实例列表
         */
        private final List<ServiceInstance> priorInstances;

        /**
         * 故障实例列表
         */
        private final List<ServiceInstance> faultInstances;

    }

}
