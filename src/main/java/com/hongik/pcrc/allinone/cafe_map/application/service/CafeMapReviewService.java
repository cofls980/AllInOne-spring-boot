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

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CafeMapReviewService implements CafeMapReviewOperationUseCase, CafeMapReviewReadUseCase {

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

        // 디비에 저장
        cafeReviewMapperRepository.createReview(new CafeReviewEntity(CafeReview.builder()
                        .cafe_id(command.getCafe_id())
                        .user_id(user_id)
                        .review_date(LocalDateTime.now())
                        .star_rating(command.getStar_rating())
                        .content(command.getContent())
                        //.photo(directoryName)
                        .build()));

        String[] categories = {command.getCategory_1(), command.getCategory_2(), command.getCategory_3()};

        cafeMapMapperRepository.increaseCategoryNum(AboutCategory.makeIncreasedValueMap(categories, command.getCafe_id()));
//        // 사진 있는지 확인 후 디비에 저장
//        MultipartFile photo = command.getPhoto();
//        String directoryName = null;
//        if (!photo.isEmpty()) {
//            directoryName = "cafe-map/" + command.getCafe_id() + "/" + user_id;//파일 이름 빼도 되지 않을까
//        }
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

    @Override
    public List<FindCafeMapReviewListResult> getCafeMapReviewList(int cafe_id) {
        // cafe_id가 있는지 확인
        if (!cafeMapMapperRepository.isExistedCafe(cafe_id)) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        // 최근 순으로 정렬
        List<HashMap<String, Object>> getList = cafeReviewMapperRepository.cafeMapReviewList(cafe_id);
        getList.sort((o1, o2) -> {
            LocalDateTime age1 = (LocalDateTime) o1.get("review_date");
            LocalDateTime age2 = (LocalDateTime) o2.get("review_date");
            return age2.compareTo(age1);
        });

        List<FindCafeMapReviewListResult> result = new ArrayList<>();

        for (HashMap<String, Object> h : getList) {
            String user_name = authMapperRepository.getUserNameByUUID(h.get("user_id").toString());
            result.add(FindCafeMapReviewListResult.findByCafeReview(h, user_name));
        }

        return result;
    }

    @Override
    public void updateReview(CafeMapReviewUpdatedCommand command) {
        // cafe_id가 있는지 확인
        if (!cafeMapMapperRepository.isExistedCafe(command.getCafe_id())) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        // 이메일을 통해 아이디 얻어오기
        String email = getUserEmail();
        String user_id = authMapperRepository.getUUIDByEmail(email);

        // 리뷰 작성자가 맞는지 확인
        if (!cafeReviewMapperRepository.isCorrectReview(command.getReview_id(), command.getCafe_id(), user_id)) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        cafeReviewMapperRepository.updateReview(command.getReview_id(), command.getStar_rating(), command.getContent());
    }

    @Override
    public void deleteReview(CafeMapReviewDeletedCommand command) {
        // cafe_id가 있는지 확인
        if (!cafeMapMapperRepository.isExistedCafe(command.getCafe_id())) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        // 이메일을 통해 아이디 얻어오기
        String email = getUserEmail();
        String user_id = authMapperRepository.getUUIDByEmail(email);

        // 리뷰 작성자가 맞는지 확인
        if (!cafeReviewMapperRepository.isCorrectReview(command.getReview_id(), command.getCafe_id(), user_id)) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        cafeReviewMapperRepository.deleteReview(command.getReview_id());
    }

    public String getUserEmail() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.equals("anonymousUser"))
            return null;

        UserDetails userDetails = (UserDetails) principal;
        return userDetails.getUsername();
    }
}
