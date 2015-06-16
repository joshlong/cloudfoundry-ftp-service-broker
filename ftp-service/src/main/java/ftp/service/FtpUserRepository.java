package ftp.service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FtpUserRepository extends JpaRepository<FtpUser, String> {

    Optional<FtpUser> findByUsername(String u);
}
