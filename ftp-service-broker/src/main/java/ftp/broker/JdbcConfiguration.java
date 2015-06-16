package ftp.broker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;


@Configuration
@EnableTransactionManagement
class JdbcConfiguration {

    @Bean
    TransactionTemplate transactionTemplate(PlatformTransactionManager tx) {
        return new TransactionTemplate(tx);
    }

    @Bean
    JdbcOperations jdbcTemplate(DataSource ds) {
        return new JdbcTemplate(ds);
    }

}
