package com.spunit.common.web;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnWebApplication
@Import({
        CommonInfoController.class,
        CommonInfoMvcController.class,
        WebClientConfig.class,
        CorrelationIdAutoConfiguration.class
})
public class CommonWebAutoConfiguration {
}
