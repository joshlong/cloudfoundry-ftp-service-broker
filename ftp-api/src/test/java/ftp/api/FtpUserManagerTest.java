package ftp.api;

import org.apache.ftpserver.ftplet.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration
public class FtpUserManagerTest {


    @Test
    public void testGetUserByName() throws Exception {

        FtpUser u = new FtpUser("ws", "u", "pw", true);
        this.ftpUserManager.save(u);
        assertEquals(u.getName(), this.ftpUserManager.getUserByName("u").getName());
    }

    @Test
    public void testGetAllUserNames() throws Exception {

        List<String> names = Arrays.asList("a,b".split(","));
        for (String n : names)
            this.ftpUserManager.save(new FtpUser("ws", n, "pw", true));
        assertEquals(this.ftpUserRepository.findAll().size(),
                this.ftpUserManager.getAllUserNames().length);
    }

    @Test
    public void testDelete() throws Exception {

    }

    @Test
    public void testSave() throws Exception {

    }

    @Test
    public void testDoesExist() throws Exception {

    }

    @Test
    public void testAuthenticate() throws Exception {

    }

    @Test
    public void testGetAdminName() throws Exception {

    }

    @Test
    public void testIsAdmin() throws Exception {

    }

    @Configuration
    @EnableAutoConfiguration
    @Import(FtpUserManagerConfiguration.class)
    public static class ConfConfig {
    }

    @Autowired
    private FtpUserManager ftpUserManager;

    @Autowired
    private FtpUserRepository ftpUserRepository;

    @Test
    public void testCreatingFtpUsers() throws Exception {

        FtpUser ftp = new FtpUser("ws", "user", "pw", true);
        this.ftpUserManager.save(ftp);

        User user = this.ftpUserManager.getUserByName("user");
        assertNotNull(user);
        assertEquals(user.getName(), "user");

        assertEquals(this.ftpUserRepository.count(), 1);
    }


    @Before
    public void setUp() throws Exception {
        this.ftpUserRepository.deleteAll();
        assertEquals(this.ftpUserRepository.count(), 0);
    }

}