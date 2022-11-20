package com.hongik.pcrc.allinone.cafe_map.ui.controller;

import com.hongik.pcrc.allinone.cafe_map.application.service.AboutCategory;
import com.hongik.pcrc.allinone.cafe_map.application.service.CafeMapReadUseCase;
import com.hongik.pcrc.allinone.cafe_map.application.service.CafeSearchEnum;
import com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.repository.CafeMapMapperRepository;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(value = "/v2/cafe-map")
@Api(tags = {"Cafe-Map API"})
public class CafeMapController {

    private CafeMapMapperRepository cafeMapMapperRepository;
    private final CafeMapReadUseCase cafeMapReadUseCase;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CafeMapController(CafeMapReadUseCase cafeMapReadUseCase) {
        this.cafeMapReadUseCase = cafeMapReadUseCase;
    }

    /*
    * /v2/cafe-map/search?cafe={}&region={}&category={}
    * - only cafe done
    * */
    @GetMapping(value = "/search", produces = "application/json")
    @ApiOperation(value = "카페, 지역, 카테고리를 통한 검색")
    public ResponseEntity<List<CafeMapReadUseCase.FindCafeSearchResult>> searchCafe(@RequestParam(value = "cafe", required = false) String cafe,
                                                                                    @RequestParam(value = "province", required = false) String province,
                                                                                    @RequestParam(value = "city", required = false) String city,
                                                                                    @RequestParam(value = "category", required = false) String category,
                                                                                    HttpServletResponse response) {

        CafeSearchEnum searchEnum;

        if ((cafe == null || cafe.isEmpty()) && (province == null || province.isEmpty()) && (city == null || city.isEmpty()) && (category == null || category.isEmpty())) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        } else if ((province == null || province.isEmpty()) && !(city == null || city.isEmpty())) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        } else if (!(province == null || province.isEmpty()) && (city == null || city.isEmpty())) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        } else if (!(category == null || category.isEmpty()) && AboutCategory.isNotInCategories(category)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        } else if (!(cafe == null || cafe.isEmpty()) && (province == null || province.isEmpty()) && (category == null || category.isEmpty())) {
            logger.info("카페 이름으로만 검색");
            searchEnum = CafeSearchEnum.CAFE;
        } else if ((cafe == null || cafe.isEmpty()) && !(province == null || province.isEmpty()) && (category == null || category.isEmpty())) {
            logger.info("지역으로만 검색");
            searchEnum = CafeSearchEnum.REGION;
        } else if ((cafe == null || cafe.isEmpty()) && (province == null || province.isEmpty()) && !(category == null || category.isEmpty())) {
            logger.info("카테고리로만 검색");
            searchEnum = CafeSearchEnum.CATEGORY;
        } else if (!(cafe == null || cafe.isEmpty()) && !(province == null || province.isEmpty()) && (category == null || category.isEmpty())) {
            logger.info("카페 이름과 지역으로만 검색");
            searchEnum = CafeSearchEnum.CAFEANDREGION;
        } else if (!(cafe == null || cafe.isEmpty()) && (province == null || province.isEmpty()) && !(category == null || category.isEmpty())) {
            logger.info("카페 이름과 카테고리로만 검색");
            searchEnum = CafeSearchEnum.CAFEANDCATEGORY;
        } else if ((cafe == null || cafe.isEmpty()) && !(province == null || province.isEmpty()) && !(category == null || category.isEmpty())) {
            logger.info("지역과 카테고리로만 검색");
            searchEnum = CafeSearchEnum.REGIONANDCATEGORY;
        } else {
            logger.info("카페 이름, 지역, 카테고리 모두로 검색");
            searchEnum = CafeSearchEnum.ALL;
        }

        var result = cafeMapReadUseCase.searchCafe(searchEnum, cafe, province, city, category);

        if (result == null || result.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        response.setHeader("Count-Cafe", String.valueOf(result.size()));

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/region", produces = "application/json")
    @ApiOperation(value = "지역 정보")
    public ResponseEntity<List<CafeMapReadUseCase.FindRegionList>> getRegionInfo() {

        logger.info("지역 정보");

        var result = cafeMapReadUseCase.getRegionInfo();

        if (result == null || result.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/category", produces = "application/json")
    @ApiOperation(value = "카테고리 정보")
    public ResponseEntity<List<CafeMapReadUseCase.FindCategoryList>> getCategoryInfo() {

        logger.info("카테고리 정보");

        var result = cafeMapReadUseCase.getCategoryInfo();

        if (result == null || result.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(result);
    }
}

// TODO
// 지하철, 카페 데이터 디비에 저장 후 api 생성 (o) -> 일단 서울 지역만
// rds 용량이 괜찮은가
// map 구성을 어떻게 할까