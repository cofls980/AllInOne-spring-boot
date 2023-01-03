package com.hongik.pcrc.allinone.main_page.ui.controller;

import com.hongik.pcrc.allinone.main_page.application.service.MainPageReadUseCase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/v2/main")
@Api(tags = {"MainPage API"})
public class MainPageController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MainPageReadUseCase mainPageReadUseCase;

    public MainPageController(MainPageReadUseCase mainPageReadUseCase) {
        this.mainPageReadUseCase = mainPageReadUseCase;
    }

    @GetMapping(value = "/cafelist", produces = "application/json")
    @ApiOperation(value = "메인페이지 - 인기카페")
    public ResponseEntity<List<MainPageReadUseCase.FindCafeListResult>> mainCafeList() {
        logger.info("메인페이지 - 인기카페");

        var result = mainPageReadUseCase.mainCafeList();

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/postlist", produces = "application/json")
    @ApiOperation(value = "메인페이지 - 인기게시글")
    public ResponseEntity<List<MainPageReadUseCase.FindPostListResult>> mainPostList() {
        logger.info("메인페이지 - 인기게시글");

        var result = mainPageReadUseCase.mainPostList();

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/channellist", produces = "application/json")
    @ApiOperation(value = "메인페이지 - 참가자 많은 채팅방")
    public ResponseEntity<List<MainPageReadUseCase.FindChannelListResult>> mainChannelList() {
        logger.info("메인페이지 - 참가자 많은 채팅방");

        var result = mainPageReadUseCase.mainChannelList();

        return ResponseEntity.ok(result);
    }
}
