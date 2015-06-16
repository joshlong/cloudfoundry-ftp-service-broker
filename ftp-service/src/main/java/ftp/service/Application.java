package ftp.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@Import(FtpServerConfiguration.class)
public class Application {

    public static void main(String args[]) {
        SpringApplication.run(Application.class, args);
    }
}