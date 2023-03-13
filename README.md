# FFR4MS

### 介绍
论文《基于消息重播的微服务系统正向故障恢复技术》(在投) -- 正向恢复部分

**主要业务流程:** 微服务系统部署时为每一个微服务实例组(集群)部署一个网关Fr-Zuul, 网关将请求负载均衡至该微服务实例组, 并缓存返回的响应. 
Fr-Docker(非本仓库内容)监控微服务系统中各个微服务实例的运行状态和容器性能状况, 通过处理监控数据快速定位到故障的微服务实例, 
通知该实例所属网关对该微服务实例进行隔离, 并重播该实例正在执行的所有请求, 下游网关如果已缓存重播请求链路中的响应, 则会拦截链路中重复的请求,
并返回缓存的响应.

### 系统架构

系统架构图: TODO

- Fr-Docker(非本仓库内容): 扩展 Docker 容器, 通过gopacket监控容器间的通信消息, 调用Docker API监控各个容器的资源占用情况，采用一种统计分析的算法来处理监控信息，定位故障的微服务实例容器；使用 Gin 框架和MongoDB开发项目界面的后端;
- Fr-Eureka(ffr4ms-registry-plugin): 对注册中心Eureka进行扩展，增加一个Web API，用于获取指定格式的微服务实例列表，以区分网关和微服务实例，配合Fr-Docker完成其功能.
- Fr-Zuul(ffr4ms-gateway-plugin): 对网关Zuul进行扩展，主要功能有: 1) 对请求注入链路跟踪信息; 2) 扩展 Ribbon 负载均衡算法, 隔离故障微服务实例, 重播执行失败的请求;3) 使用 Caffeine 框架缓存当前实例组执行完成的响应结果, 拦截上游重播的请求并返回缓存结果, 减少重复执行的开销;
- Fr-Trace(ffr4ms-trace-plugin): 扩展RestTemplate, 传递链路跟踪信息, 并注入当前微服务实例的相关信息;
- Fr-Dashboard(非本仓库内容): 项目的前端界面, 使用 Vue、ElementUI、Echarts 框架开发, 以图表方式视化展示微服务系统的运行状态和故障信息。


### 使用方式
#### Fr-Eureka(ffr4ms-registry-plugin)
1. 在Eureka注册中心中引入依赖
```xml
<dependency>
    <groupId>io.ztbeike.ffr4ms</groupId>
    <artifactId>ffr4ms-registry-plugin</artifactId>
    <version>${ffr4ms.version}</version>
</dependency>
```
2. 使用@EnableRegistryPlugin注解启用功能
```java
@SpringBootApplication
@EnableEurekaServer
@EnableRegistryPlugin
public class EurekaRegistryMain {
    public static void main(String[] args) {
        SpringApplication.run(EurekaRegistryMain.class, args);
    }
}
```
3. 访问API接口获取系统配置
```http
GET http://{registry-ip}:{registry-port}/frecovery/conf
```

#### Fr-Trace(ffr4ms-trace-plugin)
1. 在业务微服务中引入依赖
```xml
<dependency>
    <groupId>io.ztbeike.ffr4ms</groupId>
    <artifactId>ffr4ms-trace-plugin</artifactId>
    <version>${ffr4ms.version}</version>
</dependency>
```
2. 使用@EnableTracePlugin注解启用功能, 开启RestTemplate负载均衡功能
```java
@SpringBootApplication
@EnableEurekaClient
@EnableTracePlugin
public class ServiceAApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceAApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
```

#### Fr-Zuul(ffr4ms-gateway-plugin)
1. 在Zuul网关中引入依赖
```xml
<dependency>
    <groupId>io.ztbeike.ffr4ms</groupId>
    <artifactId>ffr4ms-gateway-plugin-starter</artifactId>
    <version>${ffr4ms.version}</version>
</dependency>
```

2. 使用@EnableGatewayPlugin注解启用功能
```java
@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
@EnableGatewayPlugin
public class ZuulGatewayMain {
    public static void main(String[] args) {
        SpringApplication.run(ZuulGatewayMain.class, args);
    }
}
```

3. 配置网关所属微服务实例组
```yml
# application.yml
# ...
eureka:
  instance:
    # ...
    metadata-map:
      gateway: service-a # 配置网关所属微服务实例组
# ...
```

4. 访问API接口隔离某个微服务实例和设置某个微服务实例优先
```http
POST http://{gateway-ip}:{gateway:port}/frecovery/replace
Content-Type: application/json;charset=utf-8
Accept: application/json

{
    "serviceName": "service-a",
    "downInstanceHost": "192.168.22.100",
    "downInstancePort": 8060,
    "replaceInstanceHost": "192.168.22.101",
    "replaceInstancePort": 8060
}
```

### Demo

FFR4MS-Demo项目链接: [Gitee](https://gitee.com/zengtao321/ffr4ms-demo) [GitHub](https://github.com/ztibeike/ffr4ms-demo) 