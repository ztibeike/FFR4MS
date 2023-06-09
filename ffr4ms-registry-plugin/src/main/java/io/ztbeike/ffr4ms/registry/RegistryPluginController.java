package io.ztbeike.ffr4ms.registry;

import io.ztbeike.ffr4ms.common.constant.APIConstant;
import io.ztbeike.ffr4ms.registry.entity.MSConfig;
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
@RequestMapping(value = APIConstant.API_URL_COMMON_PREFIX)
@Lazy
public class RegistryPluginController {

    private RegistryPluginService service = new RegistryPluginService();

    @GetMapping(value = "/conf")
    public MSConfig getConf() {
        return service.getApps();
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

}
