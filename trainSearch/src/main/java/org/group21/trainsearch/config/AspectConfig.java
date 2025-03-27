package org.group21.trainsearch.config;

import org.group21.config.LibraryConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAspectJAutoProxy
@Import(LibraryConfig.class)
public class AspectConfig {
}
