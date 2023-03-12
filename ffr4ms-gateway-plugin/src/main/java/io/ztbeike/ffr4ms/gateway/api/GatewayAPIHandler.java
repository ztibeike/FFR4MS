package io.ztbeike.ffr4ms.gateway.api;

import io.ztbeike.ffr4ms.gateway.api.dto.FrecoveryResponse;

/**
 * handler抽象类
 */
public abstract class GatewayAPIHandler {


    public abstract FrecoveryResponse run();

}
