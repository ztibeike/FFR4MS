package io.ztbeike.ffr4ms.gateway.ribbon;

import lombok.*;
import org.springframework.http.client.ClientHttpResponse;

import java.util.concurrent.Future;

/**
 * 网关路由信息实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "future")
@Builder
public class RouteExecuteInfo {
    private Future<ClientHttpResponse> future;

    private String host;

    private Integer port;

    private String serviceName;

}
