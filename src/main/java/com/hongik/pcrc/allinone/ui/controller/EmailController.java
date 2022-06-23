package com.hongik.pcrc.allinone.ui.controller;

import com.hongik.pcrc.allinone.application.service.EmailService;
import com.hongik.pcrc.allinone.ui.view.ApiResponseView;
import com.hongik.pcrc.allinone.ui.view.Auth.EmailView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.util.Map;

@RestController
@RequestMapping(value = "/v1/auth/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /** 이메일 인증 코드 보내기*/
    @PostMapping("")
    public ResponseEntity<ApiResponseView<EmailView>> emailAuth(@RequestBody Map<String, String> email) throws MessagingException {
        emailService.sendMessage(email.get("email"));

        return ResponseEntity.ok(new ApiResponseView<>(new EmailView("true")));
    }

    /** 이메일 인증 코드 검증*/
    @PostMapping("/verifyCode")
    public ResponseEntity<ApiResponseView<EmailView>> verifyCode(@RequestBody Map<String, String> code) {
        if (EmailService.eCode.equals(code.get("code"))) {
            return ResponseEntity.ok(new ApiResponseView<>(new EmailView("true")));
        }
        return ResponseEntity.ok(new ApiResponseView<>(new EmailView("false")));
    }

}
