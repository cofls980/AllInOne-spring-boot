package com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.entity;

import com.hongik.pcrc.allinone.cafe_map.application.domain.CafeReview;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private String photo;
    @Column(nullable = false)
    private int like_number;
    @Column(nullable = false)
    private String category_1;
    @Column(nullable = false)
    private String category_2;
    @Column(nullable = false)
    private String category_3;

    public CafeReviewEntity(CafeReview cafeReview) {
        this.review_id = cafeReview.getReview_id();
        this.cafe_id = cafeReview.getCafe_id();
        this.user_id = cafeReview.getUser_id();
        this.review_date = cafeReview.getReview_date();
        this.star_rating = cafeReview.getStar_rating();
        this.content = cafeReview.getContent();
        this.photo = cafeReview.getPhoto();
        this.like_number = cafeReview.getLike_number();
        this.category_1 = cafeReview.getCategory_1();
        this.category_2 = cafeReview.getCategory_2();
        this.category_3 = cafeReview.getCategory_3();
    }
}
