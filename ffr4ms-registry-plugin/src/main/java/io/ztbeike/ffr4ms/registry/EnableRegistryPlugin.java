package io.ztbeike.ffr4ms.registry;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 定义注解导入自定义Controller
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(RegistryPluginController.class)
public @interface EnableRegistryPlugin {
}
