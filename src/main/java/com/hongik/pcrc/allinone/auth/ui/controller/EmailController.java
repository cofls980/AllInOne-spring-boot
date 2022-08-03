package com.hongik.pcrc.allinone.auth.ui.controller;

//import com.hongik.pcrc.allinone.application.service.EmailRedisService;
import com.hongik.pcrc.allinone.auth.application.service.EmailService;
import com.hongik.pcrc.allinone.auth.ui.requestBody.EmailVerifyRequest;
import com.hongik.pcrc.allinone.auth.ui.view.ApiResponseView;
import com.hongik.pcrc.allinone.auth.ui.view.Auth.SuccessView;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.util.Map;

@RestController
@RequestMapping(value = "/v1/email")
public class EmailController {

    private final EmailService emailService;
    //private final EmailRedisService emailRedisService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /** 이메일 인증 코드 보내기*/
    @PostMapping("")
    public ResponseEntity<ApiResponseView<SuccessView>> emailAuth(@RequestBody Map<String, String> email) throws MessagingException {
        if (ObjectUtils.isEmpty(email)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }
        emailService.sendMessage(email.get("id"));

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    /** 이메일 인증 코드 검증*/
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponseView<SuccessView>> verifyCode(@RequestBody EmailVerifyRequest request) {
        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }
        String result = emailService.verifyCode(request.getId(), request.getCode());
        if (result.equals("false")) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView(result)));
    }

    @PostMapping("/pwd")
    public ResponseEntity<ApiResponseView<SuccessView>> emailPasswordAuth(@RequestBody Map<String, String> email) throws MessagingException {
        if (ObjectUtils.isEmpty(email)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }
        var result = emailService.sendMessageExist(email.get("id"));
        if (result.equals("not_found")) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView(result)));
    }
}