package com.hongik.pcrc.allinone.auth.application.scheduling;

import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.EmailEntityRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class EmailScheduler {

    private final EmailEntityRepository emailRepository;

    public EmailScheduler(EmailEntityRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60)
    public void scheduleEmailCodeDeleteTask() {

        var emailEntities = emailRepository.findAll();
        for (var email : emailEntities) {
            Duration duration = Duration.between(email.getCreated_date(), LocalDateTime.now());
            if (duration.getSeconds() > 180) {
                emailRepository.delete(email);
            }
        }
        System.out.println("[" + LocalDateTime.now() + "] (Email) Delete Complete");
    }
}
