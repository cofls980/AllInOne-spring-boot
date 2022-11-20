package com.hongik.pcrc.allinone.cafe_map.infrastructure.persistance.mysql.entity;

import com.hongik.pcrc.allinone.cafe_map.application.domain.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "cafe_map_category")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int category_id;
    @Column(nullable = false)
    private int cafe_id;
    @Column(nullable = false)
    private int store_num;
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer 경치좋은;
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer 공부맛집;
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer 데이트코스;
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer 드라이브;
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer 디저트맛집;
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer 소개팅;
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer 인스타감성;
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer 조용한;
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer 커피맛집;
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer 큰규모;
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer 테이크아웃;
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer 테마있는;

    public CategoryEntity(Category category) {
        this.category_id = category.getCategory_id();
        this.cafe_id = category.getCafe_id();
        this.store_num = category.getStore_num();
        this.경치좋은 = category.get경치좋은();
        this.공부맛집 = category.get공부맛집();
        this.데이트코스 = category.get데이트코스();
        this.드라이브 = category.get드라이브();
        this.디저트맛집 = category.get디저트맛집();
        this.소개팅 = category.get소개팅();
        this.인스타감성 = category.get인스타감성();
        this.조용한 = category.get조용한();
        this.커피맛집 = category.get커피맛집();
        this.큰규모 = category.get큰규모();
        this.테마있는 = category.get테마있는();
        this.테이크아웃 = category.get테이크아웃();
    }
}
