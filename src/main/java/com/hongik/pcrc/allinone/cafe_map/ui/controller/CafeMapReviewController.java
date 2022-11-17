package com.hongik.pcrc.allinone.cafe_map.ui.controller;

import com.hongik.pcrc.allinone.cafe_map.application.service.CafeMapReviewOperationUseCase;
import com.hongik.pcrc.allinone.cafe_map.application.service.CafeMapReviewReadUseCase;
import com.hongik.pcrc.allinone.cafe_map.ui.requestBody.CafeMapReviewCreateRequest;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.exception.view.ApiResponseView;
import com.hongik.pcrc.allinone.exception.view.SuccessView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

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
    public ResponseEntity<ApiResponseView<SuccessView>> cafeEvaluate(@Valid @RequestBody CafeMapReviewCreateRequest request, @PathVariable int cafe_id) {

        logger.info("카페 리뷰 작성");

        var command = CafeMapReviewOperationUseCase.CafeMapReviewCreatedCommand.builder()
                .cafe_id(cafe_id)
                .star_rating(request.getStar_rating())
                .content(request.getContent())
                .build();

        cafeMapReviewOperationUseCase.createReview(command);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @GetMapping(value = "/{cafe_id}/evaluate", produces = "application/json")
    @ApiOperation(value = "카페 리뷰 리스트")
    public List<CafeMapReviewReadUseCase.FindCafeMapReviewListResult> cafeEvaluatedList(@PathVariable int cafe_id) {

        logger.info("카페 리뷰 리스트");

        var result = cafeMapReviewReadUseCase.getCafeMapReviewList(cafe_id);

        if (result.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return result;
    }
}

//TODO
//리뷰 작성 (o)
//리뷰 목록 (o)
//리뷰 업데이트
//리뷰 삭제