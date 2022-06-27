package com.hongik.pcrc.allinone.application.scheduling;

import com.hongik.pcrc.allinone.infrastructure.persistance.mysql.repository.EmailEntityRepository;
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

    @Scheduled(fixedDelay = 1000 * 60 * 1)
    public void scheduleEmailCodeDeleteTask() {
        System.out.println("Delete Complete");

        var emailEntities = emailRepository.findAll();
        for (var email : emailEntities) {
            Duration duration = Duration.between(email.getCreatedDate(), LocalDateTime.now());
            if (duration.getSeconds() > 180) {
                emailRepository.delete(email);
            }
        }
    }
}
