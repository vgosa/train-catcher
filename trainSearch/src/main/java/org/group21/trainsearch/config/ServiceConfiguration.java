package org.group21.trainsearch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class ServiceConfiguration {

    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${restTemplate.connectTimeout}")
    private int connectTimeout;

    @Value("${restTemplate.readTimeout}")
    private int readTimeout;

    /** @noinspection SpringJavaInjectionPointsAutowiringInspection*/
    public ServiceConfiguration(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    @Bean("loadBalanced")
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate() {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(connectTimeout))
                .setReadTimeout(Duration.ofSeconds(readTimeout))
                .build();
    }
}
