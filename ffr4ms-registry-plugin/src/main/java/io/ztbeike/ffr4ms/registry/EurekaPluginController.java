package io.ztbeike.ffr4ms.registry;

import io.ztbeike.ffr4ms.registry.entity.Response;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 扩展Eureka的web接口
 * 使用@Lazy懒加载, 避免因Eureka Server未完成BootStrap
 * 导致EurekaPluginService空指针异常
 */
@RestController
@Lazy
public class EurekaPluginController {

    private EurekaPluginService service = new EurekaPluginService();

    @GetMapping(value = "/conf")
    public Response getConf() {
        return service.getApps();
    }

    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }

}
