package io.ztbeike.ffr4ms.common.constant;

public class GatewayConstant {

    /**
     * 标识在注册中心中该实例为网关
     */
    public static final String GATEWAY_METADATA_KEY = "gateway";

    /**
     * 标识网关管理的实例组名称
     */
    public static final String GATEWAY_APP_NAME_SUFFIX = "-GATEWAY";


    /**
     * 优先实例被优先选取次数的阈值
     */
    public static final int INSTANCE_PRIOR_TTL = 10;

}
