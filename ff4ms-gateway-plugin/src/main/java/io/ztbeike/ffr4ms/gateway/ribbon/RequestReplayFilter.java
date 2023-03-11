package io.ztbeike.ffr4ms.gateway.ribbon;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import io.ztbeike.ffr4ms.gateway.context.GatewayRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.ribbon.support.RibbonCommandContext;
import org.springframework.cloud.netflix.ribbon.support.RibbonRequestCustomizer;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonRoutingFilter;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 重播失败请求
 */
@Slf4j
public class RequestReplayFilter extends RibbonRoutingFilter {


    private final ExecutorService executorService;

    /**
     * 维护正在执行的网关路由请求
     * key: ServiceName(Id)
     * value: 路由至当前Service的正在执行的请求列表
     */
    private final Map<String, List<RouteExecuteInfo>> executingRoutes;

    private static final ThreadLocal<RouteExecuteInfo> ROUTE_EXECUTE_THREAD_LOCAL = new TransmittableThreadLocal<>();

    public RequestReplayFilter(ProxyRequestHelper helper, RibbonCommandFactory<?> ribbonCommandFactory,
                               List<RibbonRequestCustomizer> requestCustomizers, ExecutorService executorService) {
        super(helper, ribbonCommandFactory, requestCustomizers);
        this.executorService = executorService;
        // 在当前场景下每个K-V对只需要put一次, ConcurrentHashMap不能保证线程安全, 使用普通HashMap
        this.executingRoutes = new HashMap<>();
    }

    @Override
    protected ClientHttpResponse forward(RibbonCommandContext context) throws Exception {
        // 参考父类实现
        Map<String, Object> info = this.helper.debug(context.getMethod(), context.getUri(), context.getHeaders(), context.getParams(), context.getRequestEntity());
        String serviceId = context.getServiceId();
        RouteExecuteInfo executeInfo = RouteExecuteInfo.builder().serviceName(serviceId).build();
        if (!this.executingRoutes.containsKey(serviceId)) {
            synchronized (this.executingRoutes) {
                // 采用类似单例模式的二次判断, 保证不会覆盖已被其他线程初始化的K-V对
                if (!this.executingRoutes.containsKey(serviceId)) {
                    // 多线程的add和remove, 使用线程安全的LinkedList
                    List<RouteExecuteInfo> list = Collections.synchronizedList(new LinkedList<>());
                    this.executingRoutes.put(serviceId, list);
                }
            }
        }
        try {
            this.executingRoutes.get(serviceId).add(executeInfo);
            ROUTE_EXECUTE_THREAD_LOCAL.set(executeInfo);
            // 正常转发请求
            Future<ClientHttpResponse> future = this.executorService.submit(() -> this.ribbonCommandFactory.create(context).execute());
            executeInfo.setFuture(future);
            ClientHttpResponse response = null;
            try {
                response = future.get();
                // CancellationException发生在接收到Fr-Docker故障微服务实例的通知后, 取消该实例所属的Future
            } catch (CancellationException | InterruptedException | ExecutionException e) {
                // 重播请求
                RibbonCommandContext retryContext = this.buildCommandContext(GatewayRequestContext.getContext());
                response = this.ribbonCommandFactory.create(retryContext).execute();
            }
            // 参考父类实现
            this.helper.appendDebug(info, response.getRawStatusCode(), response.getHeaders());
            return response;
        } catch (HystrixRuntimeException e) {
            return this.handleException(info, e);
        } finally {
            // executeInfo执行完毕, 从列表中移除
            executeInfo = ROUTE_EXECUTE_THREAD_LOCAL.get();
            if (executeInfo != null && !StringUtils.isEmpty(executeInfo.getHost())) {
                List<RouteExecuteInfo> list = this.executingRoutes.get(serviceId);
                // 线程安全remove
                list.remove(executeInfo);
            }
        }
    }

    /**
     * 取消转发给微服务实例正在执行的请求
     * @param serviceId 微服务名称
     * @param host 实例IP
     * @param port 实例端口
     */
    public void cancelServiceFuture(String serviceId, String host, Integer port) {
        List<RouteExecuteInfo> executeInfoList = this.executingRoutes.get(serviceId);
        if (!CollectionUtils.isEmpty(executeInfoList)) {
            // 锁住executeInfoList, 保证线程安全
            synchronized (executeInfoList) {
                RouteExecuteInfo executeInfo = new RouteExecuteInfo(null, host, port, serviceId);
                executeInfoList.forEach(info -> {
                    if (executeInfo.equals(info)) {
                        try {
                            info.getFuture().cancel(true);
                        } catch (Exception e) {
                            log.error("Failed to cancel future of executeInfo: {}", info);
                        }
                    }
                });
            }
        }
    }

    public static RouteExecuteInfo getContextExecuteInfo() {
        return ROUTE_EXECUTE_THREAD_LOCAL.get();
    }

}
