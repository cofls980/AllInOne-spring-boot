package com.hongik.pcrc.allinone.cafe_map.application.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public interface CafeMapOperationUseCase {
    void createScrap(CafeMapScrapCreatedCommand command);
    void deleteScrap(CafeMapScrapDeletedCommand command);

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class CafeMapScrapCreatedCommand {
        private final int cafe_id;
    }

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Getter
    @ToString
    class CafeMapScrapDeletedCommand {
        private final int cafe_id;
        private final int scrap_id;
    }
}
