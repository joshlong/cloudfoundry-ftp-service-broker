package ftp.service;

import org.apache.ftpserver.ftplet.UserManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.io.File;

@Configuration
public class FtpUserManagerConfiguration {

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    FtpUserRepository ftpUserRepository(JdbcTemplate jdbcTemplate) {
        FtpUserRepository ftpUserRepository = new FtpUserRepository();
        ftpUserRepository.setJdbcOperations(jdbcTemplate);
        return ftpUserRepository;
    }

    @Bean
    UserManager userManager(FtpUserRepository r, @Value("${ftp.root:${HOME}/Desktop/root}") File root) {
        Assert.isTrue(root.exists() || root.mkdirs());
        return new FtpUserManager(r, root);
    }
}
