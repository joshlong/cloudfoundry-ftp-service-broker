package ftp.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FtpUserRepositoryConfig.class)
public class FtpUserRepositoryTest {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FtpUserRepository ftpUserRepository;

    private FtpUser one, two;

    @Before
    public void before() {

        this.jdbcTemplate.execute("DELETE FROM FTP_USER");

        this.one = this.ftpUserRepository.save(new FtpUser("ws", "one", "pw", true));
        assertNotNull("user should not be null", this.one);

        this.two = this.ftpUserRepository.save(new FtpUser("ws", "two", "pw", true));
        assertNotNull("user should not be null", this.two);
    }

/*
    @Test
    public void testFindByUsername() throws Exception {
        Optional<FtpUser> byUsername = this.ftpUserRepository.findByUsername(this.one.getName());
        assertEquals(this.one.getName(), byUsername.get().getName());

    }*/

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

@Configuration
@SpringBootApplication
@Import(FtpUserManagerConfiguration.class)
class FtpUserRepositoryConfig {

}