package io.ztbeike.ffr4ms.gateway.api;

import io.ztbeike.ffr4ms.gateway.api.dto.FrecoveryResponse;

/**
 * handler抽象类
 */
public interface GatewayAPIHandler {


    FrecoveryResponse run();

}
