package ftp.service.nodes;

import ftp.service.Application;
import ftp.service.nodes.FtpServerNode;
import ftp.service.nodes.FtpServerNodeRepository;
import ftp.service.users.FtpUserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.InetAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class FtpServerNodeRepositoryTest {

    @Autowired
    private FtpServerNodeRepository ftpServerNodeRepository;
    @Autowired
    private FtpUserRepository ftpUserRepository;

    private FtpServerNode ftpServerNode;

    private int port = 2022;

    private String hostName;

    @Before
    public void createDummyData() throws Exception {
        this.hostName = InetAddress.getLocalHost().getHostName();
        this.ftpServerNode = this.ftpServerNodeRepository.save(new FtpServerNode(this.port, this.hostName));
        assertNotNull("the ftpServerNode should not be null", this.ftpServerNode);
    }

    @After
    public void after() {
        this.ftpUserRepository.deleteAll();
        this.ftpServerNodeRepository.deleteAll();
    }

    @Test
    public void read() throws Exception {
        FtpServerNode match = this.ftpServerNodeRepository.findOne(this.ftpServerNode.getId());
        assertEquals(match.getId(), this.ftpServerNode.getId());
    }

    @Test
    public void update() {
        FtpServerNode update = new FtpServerNode(this.ftpServerNode.getId(), this.ftpServerNode.getPort(), "1.2.3.4");
        this.ftpServerNodeRepository.save(update);
        assertEquals(this.ftpServerNodeRepository.findOne(this.ftpServerNode.getId()).getIpAddress(), "1.2.3.4");
    }


}
