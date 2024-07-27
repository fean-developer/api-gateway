package br.com.fean.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.reactive.ReactiveManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;


@EnableDiscoveryClient
@ComponentScan(basePackages = {"br.com.fean.apigateway.infrastructure.filters", "br.com.fean.apigateway.infrastructure.provider"})
@SpringBootApplication(exclude = {ReactiveManagementWebSecurityAutoConfiguration.class,org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration.class})
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
