package ftp.provisioner;

import ftp.service.provisions.FtpProvision;
import ftp.service.provisions.FtpProvisionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class FtpProvisionServiceTest {

    @Autowired
    private FtpProvisionService ftpProvisionService;

    @Test
    public void testCreateFtpUser() throws Exception {
        FtpProvision ftpProvision = this.ftpProvisionService.provisionFtpAccount("ws", "george", "pw");
        assertNotNull(ftpProvision);
    }
}
