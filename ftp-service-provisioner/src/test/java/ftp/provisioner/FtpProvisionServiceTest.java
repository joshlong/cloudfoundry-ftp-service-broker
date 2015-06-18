package ftp.provisioner;

import ftp.service.FtpUserManager;
import ftp.service.FtpUserManagerConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FtpProvisionServiceConfig.class)
public class FtpProvisionServiceTest {

    @Autowired
    private FtpProvisionService ftpProvisionService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws Exception {
        jdbcTemplate.execute("delete from ftp_user");
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testCreateFtpUser() throws Exception {

        FtpProvision ftpProvision = this.ftpProvisionService.provisionFtpAccount("ws", "george", "pw");
        assertNotNull(ftpProvision);

    }

}

@Configuration
@SpringBootApplication
@EnableTransactionManagement
@Import(FtpUserManagerConfiguration.class)
class FtpProvisionServiceConfig {

    @Bean
    FtpProvisionService ftpProvisionService(JdbcTemplate jdbcTemplate,
                                            FtpUserManager ftpUserManager,
                                            @Value("${ftp.max-per-node:10}") int maxPerNode) {
        return new FtpProvisionService(jdbcTemplate, ftpUserManager, maxPerNode);
    }
}
