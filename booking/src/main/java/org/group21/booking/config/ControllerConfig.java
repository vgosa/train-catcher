package org.group21.booking.config;

import org.group21.error.RestControllerExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RestControllerExceptionHandler.class})
public class ControllerConfig {
}
