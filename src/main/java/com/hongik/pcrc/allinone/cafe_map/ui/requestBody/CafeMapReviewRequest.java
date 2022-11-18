package com.hongik.pcrc.allinone.cafe_map.ui.requestBody;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CafeMapReviewRequest {

    @NotNull
    private Double star_rating;
    @NotNull
    @NotBlank
    private String content;
//    private MultipartFile photo;

}
