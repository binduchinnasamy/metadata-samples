package com.ms.cse.dqprofileapp.cloudfunctions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.stereotype.Component;

@Component
@SpringBootApplication(scanBasePackages={"com.ms.cse.dqprofileapp"})
public class CloudFunctionsApp {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(CloudFunctionsApp.class, args);
    }
}
