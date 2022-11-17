package com.hongik.pcrc.allinone.cafe_map.application.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.repository.AuthMapperRepository;
import com.hongik.pcrc.allinone.cafe_map.application.domain.CafeReview;
import com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.entity.CafeReviewEntity;
import com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.repository.CafeMapMapperRepository;
import com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.repository.CafeReviewMapperRepository;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class CafeMapReviewService implements CafeMapReviewOperationUseCase{

    private final CafeReviewMapperRepository cafeReviewMapperRepository;
    private final AuthMapperRepository authMapperRepository;
    private final CafeMapMapperRepository cafeMapMapperRepository;
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.bucket.name}")
    private String S3Bucket;

    public CafeMapReviewService(CafeReviewMapperRepository cafeReviewMapperRepository,
                                AuthMapperRepository authMapperRepository,
                                CafeMapMapperRepository cafeMapMapperRepository, AmazonS3Client amazonS3Client) {
        this.cafeReviewMapperRepository = cafeReviewMapperRepository;
        this.authMapperRepository = authMapperRepository;
        this.cafeMapMapperRepository = cafeMapMapperRepository;
        this.amazonS3Client = amazonS3Client;
    }

    @Override
    public void createReview(CafeMapReviewCreatedCommand command) {

        // cafe_id가 있는지 확인
        if (!cafeMapMapperRepository.isExistedCafe(command.getCafe_id())) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        // 이메일을 통해 아이디 얻어오기
        String email = getUserEmail();
        String user_id = authMapperRepository.getUUIDByEmail(email);

//        // 사진 있는지 확인
//        MultipartFile photo = command.getPhoto();
//        String directoryName = null;
//        if (!photo.isEmpty()) {
//            directoryName = "cafe-map/" + command.getCafe_id() + "/" + user_id;//파일 이름 빼도 되지 않을까
//        }

        // 디비에 저장
        System.out.println("date: " + LocalDateTime.now());
        cafeReviewMapperRepository.createReview(new CafeReviewEntity(CafeReview.builder()
                        .cafe_id(command.getCafe_id())
                        .user_id(user_id)
                        .review_date(LocalDateTime.now())
                        .star_rating(command.getStar_rating())
                        .content(command.getContent())
                        //.photo(directoryName)
                        .build()));
         //AWS S3에 저장
//        if (directoryName != null) {
//            String fileName = command.getPhoto().getOriginalFilename();
//            ObjectMetadata objectMetadata = new ObjectMetadata();
//
//            objectMetadata.setContentLength(photo.getInputStream().available());
//            objectMetadata.setContentType(photo.getContentType());
//
//            amazonS3Client.putObject(S3Bucket, directoryName + "/" + fileName, photo.getInputStream(), objectMetadata);
//        }
    }

    public String getUserEmail() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.equals("anonymousUser"))
            return null;

        UserDetails userDetails = (UserDetails) principal;
        return userDetails.getUsername();
    }
}
