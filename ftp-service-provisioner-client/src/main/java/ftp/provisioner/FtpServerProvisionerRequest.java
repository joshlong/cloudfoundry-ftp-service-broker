package ftp.provisioner;


/**
 * encodes the request to the FTP provisioner. This will be transformed into JSON
 * and then sent as the payload to the FTP api provisioner
 */
public class FtpServerProvisionerRequest {

    private String user, workspace;

    public FtpServerProvisionerRequest(String user, String workspace) {
        this.user = user;
        this.workspace = workspace;
    }

    public String getUser() {
        return user;
    }

    public String getWorkspace() {
        return workspace;
    }
}
