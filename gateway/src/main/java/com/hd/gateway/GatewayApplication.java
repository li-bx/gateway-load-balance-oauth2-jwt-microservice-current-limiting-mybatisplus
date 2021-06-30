package com.hd.gateway;

//import com.netflix.loadbalancer.IRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @Author: liwei
 */
@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		//System.setProperty("spring.cloud.gateway.httpclient.response-timeout", "10s");
		ConfigurableApplicationContext run = SpringApplication.run(GatewayApplication.class, args);
		applicationContext = run;
	}
	public static ApplicationContext applicationContext;
	//TODO: 通过网关访问swagger时，关闭权限检查，开发阶段使用
	public  static  boolean checkPermission=true;
}