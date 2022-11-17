package com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.entity;

import com.hongik.pcrc.allinone.cafe_map.application.domain.CafeReview;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "cafe_map_review")
public class CafeReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int review_id;
    @Column(nullable = false)
    private int cafe_id;
    @Column(nullable = false, columnDefinition = "char(36)")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private String user_id;
    @Column(nullable = false)
    private LocalDateTime review_date;
    @Column(nullable = false)
    private Double star_rating;
    @Column(nullable = false)
    private String content;
    private String photo; // 다음에
    @Column(nullable = false) // 0으로 초기화 필요 -> 다음에
    private int like_number;

    public CafeReviewEntity(CafeReview cafeReview) {
        this.review_id = cafeReview.getReview_id();
        this.cafe_id = cafeReview.getCafe_id();
        this.user_id = cafeReview.getUser_id();
        this.review_date = cafeReview.getReview_date();
        this.star_rating = cafeReview.getStar_rating();
        this.content = cafeReview.getContent();
        this.photo = cafeReview.getPhoto();
        this.like_number = cafeReview.getLike_number();
    }
}
