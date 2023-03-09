package io.ztbeike.ffr4ms.trace.annotation;

import io.ztbeike.ffr4ms.trace.config.TraceConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(TraceConfiguration.class)
public @interface EnableTracePlugin {
}
