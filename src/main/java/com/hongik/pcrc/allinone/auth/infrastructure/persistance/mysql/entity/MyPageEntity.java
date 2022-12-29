package com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "user_mypage")
public class MyPageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int mypage_id;
    @Column(columnDefinition = "char(36)")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID user_id;
    @Column(nullable = false)
    private String profile_path;
}
