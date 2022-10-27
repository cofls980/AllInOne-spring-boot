package com.hongik.pcrc.allinone.chat.application.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.util.IOUtils;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthEntityRepository;
import com.hongik.pcrc.allinone.chat.application.domain.KafkaConstants;
import com.hongik.pcrc.allinone.chat.application.domain.KafkaMessage;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.entity.ChatEntity;
import com.hongik.pcrc.allinone.chat.infrastructure.persistance.mysql.repository.ChatMapperRepository;
import com.hongik.pcrc.allinone.chat.application.domain.Chat;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, KafkaMessage> kafkaTemplate;
    private final AuthEntityRepository authEntityRepository;
    private final ChatMapperRepository chatMapperRepository;
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.bucket.name}")
    private String S3Bucket;

    public KafkaProducer(KafkaTemplate<String, KafkaMessage> kafkaTemplate,
                         AuthEntityRepository authEntityRepository, ChatMapperRepository chatMapperRepository,
                         AmazonS3Client amazonS3Client) {
        this.kafkaTemplate = kafkaTemplate;
        this.authEntityRepository = authEntityRepository;
        this.chatMapperRepository = chatMapperRepository;
        this.amazonS3Client = amazonS3Client;
    }

    public void sendMessage(int channel_id, String content) {

        if (!chatMapperRepository.isExistedChannel(channel_id)) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        String email = getUserEmail();
        String name = authEntityRepository.findByEmailResultName(email);
        String type = "text";

        var chat = Chat.builder()
                .channel_id(channel_id)
                .user_email(email)
                .user_name(name)
                .type(type)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();
        kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC, new KafkaMessage(chat));
        chatMapperRepository.createRecord(new ChatEntity(chat));
        System.out.println("Produce message: " +  content);
    }

    public void uploadImages(int channel_id, MultipartFile image) throws IOException {

        if (!chatMapperRepository.isExistedChannel(channel_id)) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        String email = getUserEmail();
        String name = authEntityRepository.findByEmailResultName(email);
        String fileName = image.getOriginalFilename();
        String bytes = Base64.getEncoder().encodeToString(IOUtils.toByteArray(image.getInputStream()));

        // kafka push
        kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC, new KafkaMessage(KafkaOperationUseCase.KafkaImageCommand.builder()
                .channel_id(channel_id)
                .user_email(email)
                .user_name(name)
                .fileName(fileName.substring(0, fileName.lastIndexOf(".")))
                .type(image.getContentType())
                .content(bytes)
                .timestamp(LocalDateTime.now())
                .build()));

        // DB에 저장
        String imageName = "chat/" + channel_id + "/" + fileName;
        chatMapperRepository.createRecord(new ChatEntity(Chat.builder()
                .channel_id(channel_id)
                .user_email(email)
                .user_name(name)
                .content(imageName)
                .type(image.getContentType())
                .timestamp(LocalDateTime.now())
                .build()));

        // AWS S3에 저장
        ObjectMetadata objectMetadata = new ObjectMetadata();

        objectMetadata.setContentLength(image.getInputStream().available());
        objectMetadata.setContentType(image.getContentType());

        amazonS3Client.putObject(S3Bucket, imageName, image.getInputStream(), objectMetadata);
    }

    public String getUserEmail() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.equals("anonymousUser"))
            return null;

        UserDetails userDetails = (UserDetails) principal;
        return userDetails.getUsername();
    }
}