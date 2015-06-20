package ftp.service.nodes;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class FtpServerNodeTest {

    @Test
    public void createFtpServerNode() {
        FtpServerNode node = new FtpServerNode(null, 10, "ip");
        Assert.assertEquals(node, new FtpServerNode(10, "ip"));
        assertNotNull(node.getIpAddress());
        assertEquals(node.getIpAddress(), "ip");
        assertEquals(node.getPort(), 10);
    }
}