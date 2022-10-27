package com.hongik.pcrc.allinone.cafe_map.ui.controller;

import com.hongik.pcrc.allinone.cafe_map.application.service.CafeMapOperationUseCase;
import com.hongik.pcrc.allinone.cafe_map.application.service.CafeMapReadUseCase;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/v2/cafe-map")
@Api(tags = {"Cafe-Map API"})
public class CafeMapController {

    private final CafeMapReadUseCase cafeMapReadUseCase;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CafeMapController(CafeMapReadUseCase cafeMapReadUseCase) {
        this.cafeMapReadUseCase = cafeMapReadUseCase;
    }

    @GetMapping(value = "/cafe", produces = "application/json")
    @ApiOperation(value = "카페 리스트")
    public List<CafeMapReadUseCase.FindCafeResult> getCafeList() {

        logger.info("카페 리스트");

        var result = cafeMapReadUseCase.getCafeList();

        if (result.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return result;
    }

    @GetMapping(value = "/station", produces = "application/json")
    @ApiOperation(value = "지하철 역 리스트")
    public List<CafeMapReadUseCase.FindStationResult> getStationList() {

        logger.info("지하철 역 리스트");

        var result = cafeMapReadUseCase.getStationList();

        if (result.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return result;
    }
}
// TODO
// 지하철, 카페 데이터 디비에 저장 후 api 생성 (o) -> 일단 서울 지역만