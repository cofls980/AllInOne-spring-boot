package com.hongik.pcrc.allinone.application.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    public static final String eCode = createKey();

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    private MimeMessage createMessage(String email) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, email);
        message.setSubject("All In One 인증 코드");
        message.setText("인증 코드: " + eCode);

        return message;
    }

    public static  String createKey() {
        StringBuilder key = new StringBuilder();
        Random random = new Random();

        for (int i = 0;i < 6;i++) {
            key.append(random.nextInt(10));
        }
        return key.toString();
    }

    public void sendMessage(String email) throws MessagingException {
        MimeMessage message = createMessage(email);
        emailSender.send(message);

    }
}
