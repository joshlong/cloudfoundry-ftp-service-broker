package ftp.service.users;

import ftp.service.Application;
import ftp.service.nodes.FtpServerNode;
import ftp.service.nodes.FtpServerNodeRepository;
import ftp.service.users.FtpUser;
import ftp.service.users.FtpUserManager;
import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class FtpUserManagerTest {

    @Autowired
    private FtpUserManager ftpUserManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FtpServerNodeRepository ftpServerNodeRepository;

    private FtpServerNode ftpServerNode;

    @Before
    public void before() throws Exception {

        jdbcTemplate.execute("delete from FTP_USER");

        this.ftpServerNode = this.ftpServerNodeRepository.save(new FtpServerNode(8999, "127.0.0.1"));
        assertNotNull(this.ftpServerNode);

        Arrays.asList("jlong,dsyer,mstine".split(","))
                .forEach(u -> {
                    try {
                        this.ftpUserManager.save(new FtpUser("cloud", u, "password", true, 0, true,
                                this.ftpServerNode));
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });
    }

    @Test
    public void testGetHomeDirectory() throws Exception {
        File jlong = this.ftpUserManager.getHomeDirectoryFor("jlong");
        assertNotNull(jlong);
        LogFactory.getLog(getClass()).info("home directory for " + jlong.getAbsolutePath());
    }


    @Test
    public void testGetUserByName() throws Exception {
        assertNotNull(this.ftpUserManager.getUserByName("jlong"));
    }

    @Test
    public void testGetAllUserNames() throws Exception {
        assertEquals(Arrays.asList(this.ftpUserManager.getAllUserNames()).size(), 3);
    }

    @Test
    public void testDelete() throws Exception {
        ftpUserManager.delete("jlong");
        assertEquals(ftpUserManager.getAllUserNames().length, 2);
    }

    @Test
    public void testSave() throws Exception {
        ftpUserManager.save(new FtpUser("ws", "usr", "pw", false, 0, true, this.ftpServerNode));
        assertNotNull(this.ftpUserManager.getUserByName("usr"));
    }

    @Test
    public void testDoesExist() throws Exception {
        assertTrue(this.ftpUserManager.doesExist("jlong"));
    }

    @Test(expected = AuthenticationFailedException.class)
    public void testAuthenticate() throws Exception {
        User dsyerUser = this.ftpUserManager.authenticate(new UsernamePasswordAuthentication("dsyer", "password"));
        assertEquals(dsyerUser, this.ftpUserManager.getUserByName("dsyer"));

        this.ftpUserManager.authenticate(new UsernamePasswordAuthentication("jlong", "pw"));
    }

    @Test
    public void testGetAdminName() throws Exception {
        assertEquals(this.ftpUserManager.getAdminName(), "admin");
    }

    @Test
    public void testIsAdmin() throws Exception {
        ftpUserManager.save(new FtpUser("ws", "adminUser", "pw", true, 0, true, this.ftpServerNode));
        assertTrue(this.ftpUserManager.isAdmin("adminUser"));

        ftpUserManager.save(new FtpUser("ws", "regUser", "pw", false, 0, true, this.ftpServerNode));
        assertFalse(this.ftpUserManager.isAdmin("regUser"));
    }
}


