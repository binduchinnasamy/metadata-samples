package com.ms.cse.dqprofileapp.cloudfunctions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@Component
@SpringBootApplication(scanBasePackages={"com.ms.cse.dqprofileapp"})
public class CloudFunctionsApp {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(CloudFunctionsApp.class, args);
    }
}
