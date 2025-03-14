package org.group21.trainsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TrainSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainSearchApplication.class, args);
	}

}
