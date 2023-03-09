package io.ztbeike.ffr4ms.trace.interceptor;

import io.ztbeike.ffr4ms.common.constant.TraceConstant;
import io.ztbeike.ffr4ms.trace.context.TraceContext;
import io.ztbeike.ffr4ms.trace.context.TraceContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TraceWebHandlerInterceptor implements HandlerInterceptor {

    private TraceContextHolder holder = TraceContextHolder.getInstance();

    /**
     * 从接收到的请求中获取traceId保存至线程内部
     * @param request
     * @param response
     * @param handler
     * @return true
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        TraceContext context = buildTraceContext(request);
        holder.set(context);
        response.addHeader(TraceConstant.TRACE_ID_HEADER, context.getTraceId());
        return true;
    }

    /**
     * 清除线程内保存的链路信息
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        holder.remove();
    }

    private TraceContext buildTraceContext(HttpServletRequest request) {
        String traceId = request.getHeader(TraceConstant.TRACE_ID_HEADER);
        return new TraceContext(traceId);
    }

}
