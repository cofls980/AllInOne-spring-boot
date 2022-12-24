package com.hongik.pcrc.allinone.auth.ui.controller;

import com.hongik.pcrc.allinone.auth.application.service.MyPageOperationUseCase;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.exception.view.ApiResponseView;
import com.hongik.pcrc.allinone.exception.view.SuccessView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "/v2/users")
@Api(tags = {"User API"})
public class MyPageController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MyPageOperationUseCase myPageOperationUseCase;

    public MyPageController(MyPageOperationUseCase myPageOperationUseCase) {
        this.myPageOperationUseCase = myPageOperationUseCase;
    }

    @PutMapping(value = "/profile", produces = "application/json")
    @ApiOperation(value = "프로필 수정")
    public ResponseEntity<ApiResponseView<SuccessView>> updateReview(@RequestParam("profile") MultipartFile profile) throws IOException {

        logger.info("프로필 수정");

        if (ObjectUtils.isEmpty(profile)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        var command = MyPageOperationUseCase.ProfileUpdateCommand.builder().profile(profile).build();

        myPageOperationUseCase.UpdateProfile(command);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }
}
