package org.group21.trainoperator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TrainOperatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainOperatorApplication.class, args);
	}

}
