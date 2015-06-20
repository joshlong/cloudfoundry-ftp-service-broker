package ftp.service.provisions;

import ftp.service.nodes.FtpServerNode;
import ftp.service.users.FtpUser;

import java.net.URI;


public class FtpProvision {

    private final FtpUser ftpUser;
    private final FtpServerNode node;

    private URI uri;

    public FtpProvision(FtpUser ftpUser, FtpServerNode node, URI uri) {
        this.ftpUser = ftpUser;
        this.node = node;
        this.uri = uri;
    }

    public FtpUser getFtpUser() {
        return ftpUser;
    }

    public URI getUri() {
        return uri;
    }

    public FtpServerNode getNode() {
        return node;
    }
}