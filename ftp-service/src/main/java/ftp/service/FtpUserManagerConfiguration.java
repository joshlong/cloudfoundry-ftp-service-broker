package ftp.service;

import org.apache.ftpserver.ftplet.UserManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.util.Assert;

import java.io.File;

@EntityScan
@EnableJpaRepositories
@Configuration
public class FtpUserManagerConfiguration {

    @Bean
    UserManager userManager(FtpUserRepository r, @Value("${ftp.root:${HOME}/Desktop/root}") File root) {
        Assert.isTrue( root.exists() || root.mkdirs());
        return new FtpUserManager(r, root);
    }
}
