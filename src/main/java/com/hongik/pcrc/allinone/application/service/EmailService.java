package com.hongik.pcrc.allinone.application.service;

import com.hongik.pcrc.allinone.application.domain.Email;
import com.hongik.pcrc.allinone.infrastructure.persistance.mysql.entity.EmailEntity;
import com.hongik.pcrc.allinone.infrastructure.persistance.mysql.repository.EmailEntityRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private final EmailEntityRepository emailRepository;

    public EmailService(JavaMailSender emailSender, EmailEntityRepository emailRepository) {
        this.emailSender = emailSender;
        this.emailRepository = emailRepository;
    }

    /**인증 코드 생성*/
    public String createEmailCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0;i < 6;i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**메일로 보낼 메시지*/
    private MimeMessage createMessage(String email) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, email);
        message.setSubject("All In One 인증 코드");

        return message;
    }
    /**메일 보내기*/
    public void sendMessage(String email) throws MessagingException {
        String code = createEmailCode();
        var result = Email.builder()
                .id(email)
                .code(code)
                .createdDate(LocalDateTime.now())
                .build();
        var emailEntity = new EmailEntity(result);
        emailRepository.save(emailEntity);

        MimeMessage message = createMessage(email);
        message.setText("인증 코드: " + code);

        emailSender.send(message);
    }

    public String verifyCode(String email, String inputCode) {
        var emailEntity = emailRepository.findById(email);

        if (emailEntity.isPresent()) {
            if (emailEntity.get().getCode().equals(inputCode)) {
                Duration duration = Duration.between(emailEntity.get().getCreatedDate(), LocalDateTime.now());
                if (duration.getSeconds() <= 180)
                    return "true";
            }
        }
        return "false";
    }
}
