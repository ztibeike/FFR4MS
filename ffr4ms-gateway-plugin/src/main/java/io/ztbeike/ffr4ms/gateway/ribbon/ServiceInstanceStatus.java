package io.ztbeike.ffr4ms.gateway.ribbon;

import java.util.List;

/**
 * 枚举+策略模式: 状态枚举和标记状态的策略
 */
public enum ServiceInstanceStatus {

    /**
     * 优先选择
     */
    PRIOR(1) {
        @Override
        public void markStatus(RequestReplayLoadBalanceRule.ServiceInstanceList serviceInstanceList, ServiceInstance instance) {
            // 标记服务实例优先
            synchronized (serviceInstanceList.getPriorInstances()) {
                List<ServiceInstance> priorInstances = serviceInstanceList.getPriorInstances();
                instance.setStatus(ServiceInstanceStatus.PRIOR);
                if (!priorInstances.contains(instance)) {
                    priorInstances.add(instance);
                }
            }
            // 从故障列表中移除服务实例
            ServiceInstance temp = new ServiceInstance(instance.getServiceName(), instance.getHost(), instance.getPort(), ServiceInstanceStatus.FAULT, 0);
            synchronized (serviceInstanceList.getFaultInstances()) {
                List<ServiceInstance> faultInstances = serviceInstanceList.getFaultInstances();
                faultInstances.remove(temp);
            }
        }
    },

    /**
     * 故障隔离
     */
    FAULT(2) {
        @Override
        public void markStatus(RequestReplayLoadBalanceRule.ServiceInstanceList serviceInstanceList, ServiceInstance instance) {
            // 标记实例故障
            synchronized (serviceInstanceList.getFaultInstances()) {
                List<ServiceInstance> faultInstances = serviceInstanceList.getFaultInstances();
                instance.setStatus(ServiceInstanceStatus.FAULT);
                if (!faultInstances.contains(instance)) {
                    faultInstances.add(instance);
                }
            }
            // 从优先列表中移除
            ServiceInstance temp = new ServiceInstance(instance.getServiceName(), instance.getHost(), instance.getPort(), ServiceInstanceStatus.PRIOR, 0);
            synchronized (serviceInstanceList.getPriorInstances()) {
                List<ServiceInstance> priorInstances = serviceInstanceList.getPriorInstances();
                priorInstances.remove(temp);
            }
        }
    },

    /**
     * 正常状态
     */
    NORMAL(3) {
        @Override
        public void markStatus(RequestReplayLoadBalanceRule.ServiceInstanceList serviceInstanceList, ServiceInstance instance) {
            // 从故障列表中移除
            ServiceInstance temp = new ServiceInstance(instance.getServiceName(), instance.getHost(), instance.getPort(), ServiceInstanceStatus.FAULT, 0);
            synchronized (serviceInstanceList.getFaultInstances()) {
                List<ServiceInstance> faultInstances = serviceInstanceList.getFaultInstances();
                faultInstances.remove(temp);
            }
        }
    };

    private int code;

    private ServiceInstanceStatus(int code) {
        this.code = code;
    }

    public abstract void markStatus(RequestReplayLoadBalanceRule.ServiceInstanceList serviceInstanceList, ServiceInstance instance);

}
