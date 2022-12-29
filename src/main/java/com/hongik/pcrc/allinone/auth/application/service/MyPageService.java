package com.hongik.pcrc.allinone.auth.application.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthMapperRepository;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.MyPageMapperRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class MyPageService implements MyPageOperationUseCase{
    private final AuthMapperRepository authMapperRepository;
    private final MyPageMapperRepository myPageMapperRepository;
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.bucket.name}")
    private String S3Bucket;

    public MyPageService(AuthMapperRepository authMapperRepository, MyPageMapperRepository myPageMapperRepository, AmazonS3Client amazonS3Client) {
        this.authMapperRepository = authMapperRepository;
        this.myPageMapperRepository = myPageMapperRepository;
        this.amazonS3Client = amazonS3Client;
    }

    @Override
    public void UpdateProfile(ProfileUpdateCommand command) throws IOException {
        String email = getUserEmail();
        String uuid = authMapperRepository.getUUIDByEmail(email);
        MultipartFile profile = command.getProfile();
        String fileName = profile.getOriginalFilename();

        // DB에 저장
        String profile_path = "mypage/" + uuid + "/" + fileName;
        myPageMapperRepository.updateProfile(uuid, profile_path);

        // AWS S3에 저장 + 기존 사진 삭제 후 저장할까?
        ObjectMetadata objectMetadata = new ObjectMetadata();

        objectMetadata.setContentLength(profile.getInputStream().available());
        objectMetadata.setContentType(profile.getContentType());

        amazonS3Client.putObject(S3Bucket, profile_path, profile.getInputStream(), objectMetadata);
    }

    public String getUserEmail() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.equals("anonymousUser"))
            return null;

        UserDetails userDetails = (UserDetails) principal;
        return userDetails.getUsername();
    }
}
