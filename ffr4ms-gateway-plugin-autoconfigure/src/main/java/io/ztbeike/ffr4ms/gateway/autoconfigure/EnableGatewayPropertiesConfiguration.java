package io.ztbeike.ffr4ms.gateway.autoconfigure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class EnableGatewayPropertiesConfiguration {
}
