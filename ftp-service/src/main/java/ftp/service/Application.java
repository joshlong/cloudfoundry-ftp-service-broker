package ftp.service;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Collections;


/**
 *
 *  <p>This is the comment for this class</p>
 *
 *  @author Josh Long
 *
 */
@SpringBootApplication
public class Application {

    @Bean
    FileSystemFactory fileSystemFactory() {
        NativeFileSystemFactory fileSystemFactory = new NativeFileSystemFactory();
        fileSystemFactory.setCaseInsensitive(false);
        fileSystemFactory.setCreateHome(true);
        return fileSystemFactory::createFileSystemView;
    }

    @Bean
    Listener nioListener(@Value("${ftp.port}") int port) throws Exception {
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(port);
        return listenerFactory.createListener();
    }

    @Bean
    FtpServer ftpServer(UserManager userManager, Listener nioListener, FileSystemFactory fileSystemFactory) throws FtpException {
        FtpServerFactory ftpServerFactory = new FtpServerFactory();
        ftpServerFactory.setListeners(Collections.singletonMap("default", nioListener));
        ftpServerFactory.setFileSystem(fileSystemFactory);
        ftpServerFactory.setUserManager(userManager);
        return ftpServerFactory.createServer();
    }

    @Bean
    DisposableBean destroysFtpServer(Listener l,
                                     FtpServer ftpServer) {

        return () -> {
            ftpServer.stop();
            if (!l.isStopped()) l.stop();
        };
    }

    @Bean
    InitializingBean startsFtpServer(Listener l,
                                     FtpServer ftpServer) throws Exception {
        return ftpServer::start;
    }

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    public static void main(String args[]) {
        SpringApplication.run(Application.class, args);
    }
}