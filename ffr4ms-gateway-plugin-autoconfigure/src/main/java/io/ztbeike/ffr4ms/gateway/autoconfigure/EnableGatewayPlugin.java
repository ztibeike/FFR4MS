package io.ztbeike.ffr4ms.gateway.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        EnableGatewayPropertiesConfiguration.class,
        GatewayAPIConfiguration.class,
        GatewayFilterConfiguration.class,
        GatewayRibbonConfiguration.class
})
public @interface EnableGatewayPlugin {
}
