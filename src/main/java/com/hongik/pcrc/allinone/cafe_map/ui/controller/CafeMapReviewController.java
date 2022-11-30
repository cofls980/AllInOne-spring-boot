package com.hongik.pcrc.allinone.cafe_map.ui.controller;

import com.hongik.pcrc.allinone.cafe_map.application.service.AboutCategory;
import com.hongik.pcrc.allinone.cafe_map.application.service.CafeMapReviewOperationUseCase;
import com.hongik.pcrc.allinone.cafe_map.application.service.CafeMapReviewReadUseCase;
import com.hongik.pcrc.allinone.cafe_map.ui.requestBody.CafeMapReviewRequest;
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

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/v2/cafe-map")
@Api(tags = {"Cafe-Map Review API"})
public class CafeMapReviewController {

    private final CafeMapReviewOperationUseCase cafeMapReviewOperationUseCase;
    private final CafeMapReviewReadUseCase cafeMapReviewReadUseCase;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CafeMapReviewController(CafeMapReviewOperationUseCase cafeMapReviewOperationUseCase,
                                   CafeMapReviewReadUseCase cafeMapReviewReadUseCase) {
        this.cafeMapReviewOperationUseCase = cafeMapReviewOperationUseCase;
        this.cafeMapReviewReadUseCase = cafeMapReviewReadUseCase;
    }

    @PostMapping(value = "/{cafe_id}/evaluate", produces = "application/json")
    @ApiOperation(value = "카페 리뷰 작성")
    public ResponseEntity<ApiResponseView<SuccessView>> cafeEvaluate(@Valid @RequestBody CafeMapReviewRequest request, @PathVariable int cafe_id) {

        logger.info("카페 리뷰 작성");

        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        if (AboutCategory.isNotInCategories(request.getCategory_1())
        || AboutCategory.isNotInCategories(request.getCategory_2())
        || AboutCategory.isNotInCategories(request.getCategory_3())) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        var command = CafeMapReviewOperationUseCase.CafeMapReviewCreatedCommand.builder()
                .cafe_id(cafe_id)
                .star_rating(request.getStar_rating())
                .content(request.getContent())
                .category_1(request.getCategory_1())
                .category_2(request.getCategory_2())
                .category_3(request.getCategory_3())
                .build();

        cafeMapReviewOperationUseCase.createReview(command);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @GetMapping(value = "/{cafe_id}/evaluate", produces = "application/json")
    @ApiOperation(value = "카페 상세 페이지")
    public ResponseEntity<CafeMapReviewReadUseCase.FindCafeInfoWithReviewResult> cafeEvaluatedList(@PathVariable int cafe_id) {

        logger.info("카페 상세 페이지");

        var result = cafeMapReviewReadUseCase.getCafeInfoWithReview(cafe_id);

        if (result == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/{cafe_id}/evaluate/{eval_id}", produces = "application/json")
    @ApiOperation(value = "카페 리뷰 업데이트")
    public ResponseEntity<ApiResponseView<SuccessView>> updateReview(@PathVariable int cafe_id, @PathVariable int eval_id,
                                                                     @Valid @RequestBody CafeMapReviewRequest request) {

        logger.info("카페 리뷰 업데이트");

        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        if (AboutCategory.isNotInCategories(request.getCategory_1())
                || AboutCategory.isNotInCategories(request.getCategory_2())
                || AboutCategory.isNotInCategories(request.getCategory_3())) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        var command = CafeMapReviewOperationUseCase.CafeMapReviewUpdatedCommand.builder()
                .review_id(eval_id)
                .cafe_id(cafe_id)
                .star_rating(request.getStar_rating())
                .content(request.getContent())
                .category_1(request.getCategory_1())
                .category_2(request.getCategory_2())
                .category_3(request.getCategory_3())
                .build();

        cafeMapReviewOperationUseCase.updateReview(command);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @DeleteMapping(value = "/{cafe_id}/evaluate/{eval_id}", produces = "application/json")
    @ApiOperation(value = "카페 리뷰 삭제")
    public ResponseEntity<ApiResponseView<SuccessView>> deleteReview(@PathVariable int cafe_id, @PathVariable int eval_id) {

        logger.info("카페 리뷰 삭제");

        var command = CafeMapReviewOperationUseCase.CafeMapReviewDeletedCommand.builder()
                .review_id(eval_id)
                .cafe_id(cafe_id)
                .build();

        cafeMapReviewOperationUseCase.deleteReview(command);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

//    @GetMapping(value = "/dummy", produces = "application/json")
//    @ApiOperation(value = "카테고리 더미 데이터")
//    public void dummy() {
//        cafeMapReviewReadUseCase.dummy();
//    }
}