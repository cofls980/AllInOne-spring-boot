package com.hongik.pcrc.allinone.chat.ui.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.hongik.pcrc.allinone.chat.application.service.KafkaProducer;
import com.hongik.pcrc.allinone.chat.ui.requestBody.ChatSendRequest;
import com.hongik.pcrc.allinone.exception.view.ApiResponseView;
import com.hongik.pcrc.allinone.exception.view.SuccessView;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;

@RestController
@RequestMapping(value = "/v2/chat")
public class KafkaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final KafkaProducer producer;
    @Value("${cloud.aws.bucket.name}")
    private String S3Bucket;
    @Autowired
    AmazonS3Client amazonS3Client;

    public KafkaController(KafkaProducer producer) {
        this.producer = producer;
    }

    @PostMapping(value = "/{channel_id}", produces = "application/json")
    @ApiOperation(value = "메시지 전송")
    public ResponseEntity<ApiResponseView<SuccessView>> sendMessage(@PathVariable int channel_id,
                                                                    @Valid @RequestBody ChatSendRequest request) {

        logger.info("메시지 전송");

        producer.sendMessage(channel_id, request.getContent());

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @PostMapping(value = "/{channel_id}/image", produces = "application/json")
    @ApiOperation(value = "이미지 업로드")
    public ResponseEntity<ApiResponseView<SuccessView>> uploadImages(@PathVariable int channel_id,
                                                                     @RequestParam("image") MultipartFile image) throws IOException {

        logger.info("이미지 업로드");

        producer.uploadImages(channel_id, image);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    // 나중에 다운로드 기능 넣을 떄 사용
    @GetMapping(value = "/{channel_id}/image", produces = "application/json")
    @ApiOperation(value = "이미지 다운로드")
    public void uploadImage(@PathVariable int channel_id,
                                              @RequestParam(value = "name", required = false) String name,
                                              HttpServletResponse response) throws IOException {
        logger.info("이미지 다운로드");

        name = "chat/" + channel_id + "/" + name;
        //사진 가져오는 부분에 S3 연동
        S3Object object = amazonS3Client.getObject(new GetObjectRequest(S3Bucket, name));
        S3ObjectInputStream objectInputStream = object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        response.setContentType(object.getObjectMetadata().getContentType());
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
    }
}
// TODO
// 1. GET - AWS S3 버킷에서 사진 불러오고 전송 (o)
// 2. POST - 전송 받은 사진을 AWS S3 버킷에 저장 (o)
// 3. 위 과정을 성공하면 kafka를 사용한 데이터 스트림에서도 사진이 전송되는지 테스트 (o)
// 4. react에서 base64 string을 가지고 이미지로 표현할 수 있을지 확인 -> 프론트한테 해보라고 하기
/*
if (contentType.contains("image/jpeg")) {
    originalFileExtension = ".jpg";
} else if (contentType.contains("image/png")) {
    originalFileExtension = ".png";
} else {
    throw new AllInOneException(MessageType.BAD_REQUEST);
}
* */