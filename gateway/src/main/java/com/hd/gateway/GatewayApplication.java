package com.hd.gateway;

//import com.netflix.loadbalancer.IRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @Author: liwei
 */
@EnableFeignClients(basePackages = "com.hd.gateway")
@EnableHystrix
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

	public static void main(String[] args) {
		//System.setProperty("spring.cloud.gateway.httpclient.response-timeout", "10s");
		ConfigurableApplicationContext run = SpringApplication.run(GatewayApplication.class, args);
		applicationContext = run;
	}
	public static ApplicationContext applicationContext;

}
