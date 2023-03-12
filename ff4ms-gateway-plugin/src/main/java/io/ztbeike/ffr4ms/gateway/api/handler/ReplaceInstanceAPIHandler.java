package io.ztbeike.ffr4ms.gateway.api.handler;

import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import io.ztbeike.ffr4ms.gateway.api.dto.FrecoveryResponse;
import io.ztbeike.ffr4ms.gateway.api.GatewayAPIHandler;
import io.ztbeike.ffr4ms.gateway.api.dto.ReplaceInstanceDTO;
import io.ztbeike.ffr4ms.gateway.context.GatewayRequestContext;
import io.ztbeike.ffr4ms.gateway.ribbon.RequestReplayLoadBalanceRule;
import io.ztbeike.ffr4ms.gateway.ribbon.RequestReplayRoutingFilter;
import io.ztbeike.ffr4ms.gateway.ribbon.ServiceInstance;
import io.ztbeike.ffr4ms.gateway.ribbon.ServiceInstanceStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 标记实例优先与故障
 */
@Slf4j
public class ReplaceInstanceAPIHandler extends GatewayAPIHandler {

    private final RequestReplayRoutingFilter routingFilter;

    private final RequestReplayLoadBalanceRule loadBalanceRule;

    public ReplaceInstanceAPIHandler(RequestReplayRoutingFilter routingFilter, RequestReplayLoadBalanceRule loadBalanceRule) {
        this.routingFilter = routingFilter;
        this.loadBalanceRule = loadBalanceRule;
    }

    @Override
    public FrecoveryResponse run() {
        FrecoveryResponse response = null;
        try {
            // 获取请求体
            HttpServletRequest request = GatewayRequestContext.getRequest();
            ServletInputStream inputStream = request.getInputStream();
            byte[] buff = new byte[request.getContentLength()];
            IOUtils.read(inputStream, buff, 0, request.getContentLength());
            ReplaceInstanceDTO dto = JSONObject.parseObject(new String(buff, StandardCharsets.UTF_8), ReplaceInstanceDTO.class);
            if (StringUtils.isEmpty(dto.getServiceName())) {
                throw new JSONException("Service name can not be null");
            }
            // 标记优先实例
            if (dto.validForReplaceInstance()) {
                ServiceInstance instance = new ServiceInstance(dto.getServiceName(), dto.getReplaceInstanceHost(),
                        dto.getReplaceInstancePort(), ServiceInstanceStatus.PRIOR);
                this.loadBalanceRule.markServiceInstanceStatus(instance, ServiceInstanceStatus.PRIOR);
            }
            // 标记故障实例
            if (dto.validForDownInstance()) {
                ServiceInstance instance = new ServiceInstance(dto.getServiceName(), dto.getDownInstanceHost(),
                        dto.getDownInstancePort(), ServiceInstanceStatus.FAULT);
                this.loadBalanceRule.markServiceInstanceStatus(instance, ServiceInstanceStatus.FAULT);
                this.routingFilter.cancelServiceFuture(dto.getServiceName(), dto.getDownInstanceHost(),
                        dto.getDownInstancePort());
            }
            response = FrecoveryResponse.ok();
        } catch (IOException | JSONException e) {
            response = FrecoveryResponse.bad();
            log.error("Bad request for replacing instance: {}", e.getMessage());
        } catch (Exception e) {
            response = FrecoveryResponse.error();
            log.error("Internal server error while replacing instance: {}", e.getMessage());
        }
        return response;
    }
}
