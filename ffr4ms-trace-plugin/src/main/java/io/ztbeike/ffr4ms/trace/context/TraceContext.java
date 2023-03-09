package io.ztbeike.ffr4ms.trace.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 上下文链路信息
 * 用于链路追踪id的传递
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraceContext {

    /**
     * 链路追踪id
     */
    private String traceId;

}
