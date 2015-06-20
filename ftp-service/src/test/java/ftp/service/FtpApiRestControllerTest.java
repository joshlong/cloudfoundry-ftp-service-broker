package ftp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ftp.service.nodes.FtpServerNode;
import ftp.service.nodes.FtpServerNodeRepository;
import ftp.service.users.FtpUserRepository;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class FtpApiRestControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FtpServerNodeRepository ftpServerNodeRepository;

    @Autowired
    private FtpUserRepository ftpUserRepository;

    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();

        this.ftpUserRepository.deleteAll();
        this.ftpServerNodeRepository.deleteAll();

        IntStream.range(0, 10).forEach(i -> this.ftpServerNodeRepository.save(
                new FtpServerNode(1000 + i, String.format("%s.0.0.1", i))));
        assertEquals(this.ftpServerNodeRepository.findAll().size(), 10);
    }

    @Test
    public void testCreate() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        FtpApiRestController.FtpProvisionRequest ftpProvisionRequest =
                new FtpApiRestController.FtpProvisionRequest("user", "workspace", "password");

        String requestString = objectMapper.writeValueAsString(ftpProvisionRequest);
        Assert.assertNotNull("the FTP provision request string should not be null", requestString);

        // we should be able to submit a JSON request containing a user, a workspace, and a password to the /ftp/provision API
        this.mvc.perform(post("/ftp/provisions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestString))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", new BaseMatcher<String>() {

                    @Override
                    public void describeTo(Description description) {
                        description.appendText("the location should be an FTP endpoint!");
                    }

                    @Override
                    public boolean matches(Object item) {
                        LogFactory.getLog(getClass()).info("uri: " + item);
                        String v = String.class.cast( item) ;

                        return v.startsWith("ftp://") && v.contains(":") && v.contains("@");
                    }
                }));
    }
}