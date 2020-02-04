package fi.vm.yti.messaging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "fi.vm.yti.*")
@EnableJpaRepositories("fi.vm.yti.*")
@EnableTransactionManagement
@EntityScan("fi.vm.yti.messaging.entity")
public class MessagingApplication {

    public static void main(final String[] args) {
        SpringApplication.run(MessagingApplication.class, args);
    }
}