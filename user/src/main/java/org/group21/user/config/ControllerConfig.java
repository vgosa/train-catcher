package org.group21.user.config;

import org.group21.error.RestControllerExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RestControllerExceptionHandler.class})
public class ControllerConfig {
}
