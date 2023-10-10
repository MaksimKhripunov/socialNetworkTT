package ru.khripunov.socialnetworktt;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication(/*exclude = {DataSourceAutoConfiguration.class}*/)
public class SocialNetworkTtApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialNetworkTtApplication.class, args);
    }





}
