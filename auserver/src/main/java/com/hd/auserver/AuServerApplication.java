package com.hd.auserver;

import com.hd.auserver.utils.SpringUtil;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages={"com.hd.auserver"})
@Slf4j
public class AuServerApplication {

    public  static ApplicationContext applicationContext;
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(AuServerApplication.class, args);
        applicationContext = run;
        SpringUtil.setApplicationContext(run);
    }

}