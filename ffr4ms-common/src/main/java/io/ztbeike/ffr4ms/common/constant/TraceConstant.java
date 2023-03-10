package io.ztbeike.ffr4ms.common.constant;

public class TraceConstant {

    /**
     * 链路追踪唯一id
     */
    public static final String TRACE_ID_HEADER = "trace-id";

    /**
     * 实例id, 格式为: {serviceName}-ip-port
     */
    public static final String TRACE_SERVICE_INSTANCE_HEADER = "service-instance-id";

    /**
     * 实例所属服务名称
     */
    public static final String SERVICE_NAME_HEADER = "service-name";

    /**
     * 标识该响应被网关拦截和从缓存中返回
     */
    public static final String CACHED_RESPONSE_FLAG_HEADER = "cached-response";

}
