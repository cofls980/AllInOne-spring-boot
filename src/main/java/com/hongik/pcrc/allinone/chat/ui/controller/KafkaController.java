package com.hongik.pcrc.allinone.chat.ui.controller;

import com.hongik.pcrc.allinone.chat.application.service.KafkaProducer;
import com.hongik.pcrc.allinone.chat.ui.requestBody.ChatSendRequest;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.exception.view.ApiResponseView;
import com.hongik.pcrc.allinone.exception.view.SuccessView;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping(value = "/v2/chat")
public class KafkaController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final KafkaProducer producer;

    public KafkaController(KafkaProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/{channel_id}")
    @ApiOperation(value = "메시지 전송")
    public ResponseEntity<ApiResponseView<SuccessView>> sendMessage(@PathVariable int channel_id,
                                                                    @Valid @RequestBody ChatSendRequest request) {

        logger.info("메시지 전송");

        producer.sendMessage(channel_id, request.getContent());

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @GetMapping("/{channel_id}/image")
    @ApiOperation(value = "이미지 업로드")
    public ResponseEntity<Resource> uploadImage(@PathVariable int channel_id,
                                                @RequestParam(value = "name", required = false) String name) { // 파일 보냄
        logger.info("이미지 업로드");

        //사진 가져오는 부분에 S3 연동
        String imageRoot = "C:/Users/Owner/Desktop/" + name;
        System.out.println("[경로] : " + imageRoot);

        Resource resource = new FileSystemResource(imageRoot);

        if (!resource.exists()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        //사진 보냄
        HttpHeaders header = new HttpHeaders();
        Path filePath = null;
        try {
            filePath = Paths.get(imageRoot);
            System.out.println(filePath);
            header.add("Content-type", Files.probeContentType(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
    }

    @PostMapping("/{channel_id}/image")
    @ApiOperation(value = "이미지 업로드")
    public String uploadImages(@PathVariable int channel_id,
                                       @RequestParam("files")MultipartFile[] files) { // 파일 받음
        logger.info("이미지 업로드");

        //이미지 저장소에 저장
        String absolutePath = new File("").getAbsolutePath() + "\\";
        String path = "images/profile";

        Arrays.stream(files).forEach(f -> { // 이미지 읽어서 원하는 경로에 transfer
            //이미지 저장소에 저장
            String imagePath = null;
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            if (f.isEmpty()) {
                throw new AllInOneException(MessageType.NOT_FOUND);
            }
            String contentType = f.getContentType();
            String originalFileExtension;
            if (ObjectUtils.isEmpty(contentType)) {
                throw new AllInOneException(MessageType.BAD_REQUEST);
            }
            if (contentType.contains("image/jpeg")) {
                originalFileExtension = ".jpg";
            } else if (contentType.contains("image/png")) {
                originalFileExtension = ".png";
            } else {
                throw new AllInOneException(MessageType.BAD_REQUEST);
            }
            imagePath = path + "/" + f.getOriginalFilename() + originalFileExtension;
            file = new File(absolutePath + imagePath);
            try {
                f.transferTo(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return null;
    }

}
