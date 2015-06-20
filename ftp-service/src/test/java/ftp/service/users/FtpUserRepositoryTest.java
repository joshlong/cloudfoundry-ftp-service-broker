package ftp.service.users;

import ftp.service.Application;
import ftp.service.nodes.FtpServerNode;
import ftp.service.nodes.FtpServerNodeRepository;
import ftp.service.users.FtpUser;
import ftp.service.users.FtpUserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class FtpUserRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FtpUserRepository ftpUserRepository;

    private FtpUser one, two;
    private FtpServerNode ftpServerNode;

    @Autowired
    private FtpServerNodeRepository ftpServerNodeRepository;

    @Before
    public void before() {

        this.jdbcTemplate.execute("DELETE FROM FTP_USER");
        this.ftpServerNode = this.ftpServerNodeRepository.save(new FtpServerNode(8999, "127.0.0.1"));

        assertNotNull(this.ftpServerNode);


        this.one = this.ftpUserRepository.save(
            new FtpUser("ws", "one", "pw", true, 0, true, this.ftpServerNode));
        assertNotNull("user should not be null", this.one);

        this.two = this.ftpUserRepository.save(new FtpUser("ws", "two", "pw", true, 0, true, this.ftpServerNode));
        assertNotNull("user should not be null", this.two);
    }

    @Test
    public void findById() throws Exception {
        FtpUser twoMatch = this.ftpUserRepository.findOne(this.two.getName());
        assertEquals(this.two, twoMatch);

        FtpUser oneMatch = this.ftpUserRepository.findOne(this.one.getName());
        assertEquals(this.one, oneMatch);
    }

    @Test
    public void findAll() throws Exception {
        assertEquals(2, this.ftpUserRepository.findAll().size());
    }

    @Test
    public void delete() throws Exception {
        this.ftpUserRepository.delete(this.two);
        assertEquals(this.ftpUserRepository.findAll().size(), 1);
    }

    @Test
    public void update() {
        FtpUser updatedTwo = new FtpUser(
                this.two.getWorkspace(),
                this.two.getId(),
                this.two.getPassword(),
                this.two.isAdmin(),
                this.two.getMaxIdleTime(),
                false,
                this.ftpServerNode,
                true // is this an existing entity that needs to be updated?
        );

        assertEquals(this.ftpUserRepository.findOne(this.two.getName()).isEnabled(), true);

        this.ftpUserRepository.save(updatedTwo);
        assertEquals(this.ftpUserRepository.findOne(this.two.getName()).isEnabled(), false);
    }

    @Test
    public void findByUsername() {
        assertNotNull(this.ftpUserRepository.findByUsername(this.two.getName())
                .get());
    }

}
