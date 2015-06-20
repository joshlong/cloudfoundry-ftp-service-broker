package ftp.service;

import ftp.service.provisions.FtpProvision;
import ftp.service.provisions.FtpProvisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * This API is what we ultimately expose to the outside world via Eureka.
 * <p>
 * we'll also expose health endpoints for the adjacent FTP service (running in
 * the same process but on a different port, naturally).
 */
@RestController
public class FtpApiRestController {

    // TODO: can we create users?
    // TODO: can we provision FTP nodes
    // TODO: can we assign users to those nodes?
    ///  - /nodes
    ///  - /nodes/{id}
    ///  - /users
    ///  - /users/{id}
    ///  - /provisions
    private final FtpProvisionService ftpProvisionService;


    // TODO: can we get capacity for a given instance?

    ///  - /provisions/{id}


    // the provisions API will map a user to a node if ones available?

    public static class FtpProvisionRequest {

        public String user, workspace, password;

        FtpProvisionRequest() {
        }

        public FtpProvisionRequest(String user, String workspace, String password) {
            this.user = user;
            this.workspace = workspace;
            this.password = password;
        }

        public String getUser() {
            return user;
        }

        public String getWorkspace() {
            return workspace;
        }

        public String getPassword() {
            return password;
        }

    }

    @RequestMapping(value = "/ftp/provisions", method = RequestMethod.POST)
    ResponseEntity<?> create(@RequestBody FtpProvisionRequest request)
            throws Exception {

        FtpProvision provision = this.ftpProvisionService.provisionFtpAccount(
                request.getWorkspace(),
                request.getUser(),
                request.getPassword());
        
        return ResponseEntity.created(provision.getUri()).build();
    }

    @Autowired
    public FtpApiRestController(FtpProvisionService ftpProvisionService) {
        this.ftpProvisionService = ftpProvisionService;
    }
}
