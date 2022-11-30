package com.hongik.pcrc.allinone.cafe_map.application.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public void createReview(CafeMapReviewCreatedCommand command) throws IOException {

        // cafe_id가 있는지 확인
        if (!cafeMapMapperRepository.isExistedCafe(command.getCafe_id())) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        // 이메일을 통해 아이디 얻어오기
        String email = getUserEmail();
        String user_id = authMapperRepository.getUUIDByEmail(email);

        // 이미 리뷰를 작성했는지 확인
        if (cafeReviewMapperRepository.isExistedReview(command.getCafe_id(), user_id)) {
            throw new AllInOneException(MessageType.CONFLICT);
        }

        // 사진이 있는지 없는지 확인
        MultipartFile[] photos = command.getPhotos();
        String directoryName = null;
        if (photos != null) {
            for (MultipartFile m : photos) {
                String fileName = m.getOriginalFilename();
                if (directoryName == null) {
                    directoryName = "";
                }
                directoryName += (fileName + "/");
            }
        }
        // 있으면 디비에 저장하고 S3에 저장
        // 디비에 저장
        cafeReviewMapperRepository.createReview(new CafeReviewEntity(CafeReview.builder()
                        .cafe_id(command.getCafe_id())
                        .user_id(user_id)
                        .review_date(LocalDateTime.now())
                        .star_rating(command.getStar_rating())
                        .content(command.getContent())
                        .category_1(command.getCategory_1())
                        .category_2(command.getCategory_2())
                        .category_3(command.getCategory_3())
                        .photo(directoryName)
                        .build()));

        // category 테이블 변화
        String[] categories = {command.getCategory_1(), command.getCategory_2(), command.getCategory_3()};

        cafeMapMapperRepository.changeCategoryNum(AboutCategory.makeIncreasedValueMap(categories, command.getCafe_id()));
        if (directoryName != null) {
            uploadInS3(photos, command.getCafe_id(), user_id);
        }

    }

    private void uploadInS3(MultipartFile[] photos, int cafe_id, String user_id) throws IOException {
        //AWS S3에 저장
        for (MultipartFile m : photos) {
            String fileName = m.getOriginalFilename();
            ObjectMetadata objectMetadata = new ObjectMetadata();

            objectMetadata.setContentLength(m.getInputStream().available());
            objectMetadata.setContentType(m.getContentType());

            amazonS3Client.putObject(S3Bucket, "cafe-map/" + cafe_id + "/" + user_id + "/" + fileName, m.getInputStream(), objectMetadata);
        }
    }

    @Override
    public FindCafeInfoWithReviewResult getCafeInfoWithReview(int cafe_id) throws IOException {
        // cafe_id가 있는지 확인
        if (!cafeMapMapperRepository.isExistedCafe(cafe_id)) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        List<HashMap<String, Object>> list = cafeReviewMapperRepository.getACafeInfo(cafe_id);
        list.sort((o1, o2) -> {
            LocalDateTime age1 = (LocalDateTime) o1.get("review_date");
            LocalDateTime age2 = (LocalDateTime) o2.get("review_date");
            return age2.compareTo(age1);
        });

        List<FindCafeMapReviewListResult> result = new ArrayList<>();
        List<String> bytes = new ArrayList<>();
        double total_rating = 0.0;

        for (HashMap<String, Object> h : list) {
            String[] photo = h.get("photo").toString().split("/");
            for (String p : photo) {
                String file = "cafe-map/" + cafe_id + "/" + h.get("user_id").toString() + "/" + p;
                S3Object object = amazonS3Client.getObject(new GetObjectRequest(S3Bucket, file));

                S3ObjectInputStream objectInputStream = object.getObjectContent();
                bytes.add(Base64.getEncoder().encodeToString(IOUtils.toByteArray(objectInputStream)));
            }
            total_rating += Double.parseDouble(h.get("star_rating").toString());
            String user_name = authMapperRepository.getUserNameByUUID(h.get("user_id").toString());
            result.add(FindCafeMapReviewListResult.findByCafeReview(h, user_name));
        }
        total_rating /= list.size();
        HashMap<String, Object> info = list.get(0);
        if (!info.get("floor_info").toString().isEmpty()) {
            String floor_info = "";
            int floor = Integer.parseInt(info.get("floor_info").toString());
            if (floor < 0) {
                floor = -floor;
                floor_info += "지하 ";
            }
            floor_info += (floor + "층");
            info.put("floor_info", floor_info);
        }

        return FindCafeInfoWithReviewResult.findByCafeReview(info, bytes, total_rating, AboutCategory.getTop3(info), result);
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

        // 카테고리를 제외한 나머지 업데이트
        cafeReviewMapperRepository.updateReview(command.getReview_id(), command.getStar_rating(), command.getContent(),
                command.getCategory_1(), command.getCategory_2(), command.getCategory_3());

        // 선택한 3가지 알아내서 그에 해당하는 값들 -1씩 하기
        HashMap<String, Object> selected = cafeReviewMapperRepository.getSelectedCategories(command.getReview_id());
        cafeMapMapperRepository.changeCategoryNum(AboutCategory.makeDecreasedValueMap(selected, command.getCafe_id()));
        // cafeMapMapperRepository.decreaseCategoryNum()
        // category 테이블 변화
        String[] categories = {command.getCategory_1(), command.getCategory_2(), command.getCategory_3()};

        cafeMapMapperRepository.changeCategoryNum(AboutCategory.makeIncreasedValueMap(categories, command.getCafe_id()));
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
        // 선택한 3가지 알아내서 그에 해당하는 값들 -1씩 하기?

        cafeReviewMapperRepository.deleteReview(command.getReview_id());
    }

    @Override
    public void dummy() {

        //20092
        // 카페 개수만큼 실행
        for (int i = 1;i <= 20092;i++) {
            HashMap<String, Integer> map = new HashMap<>();
            map.put("cafe_id", i);
            for (String t : AboutCategory.getType()) {
                //램덤 숫자
                int random = (int) (Math.random() * 100);
                map.put(t, random);
            }
            cafeMapMapperRepository.changeCategoryNum(map);
        }
    }

    public String getUserEmail() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.equals("anonymousUser"))
            return null;

        UserDetails userDetails = (UserDetails) principal;
        return userDetails.getUsername();
    }
}
/*
* {
  "star_rating": 1,
  "content": "nop",
  "category_1": "디저트맛집",
  "category_2": "조용한",
  "category_3": "공부맛집"
}
* */