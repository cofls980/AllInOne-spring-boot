package com.hongik.pcrc.allinone.cafe_map.application.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public interface CafeMapReadUseCase {

    List<HashMap<String, Object>> getCafeList();
    List<HashMap<String, Object>> getStationList();

    @Getter
    @ToString
    @Builder
    class FindCafeResult {
    }

    @Getter
    @ToString
    @Builder
    class FindStationResult {
    }
}
