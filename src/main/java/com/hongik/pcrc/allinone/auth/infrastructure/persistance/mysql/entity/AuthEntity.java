package com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.entity;

import com.hongik.pcrc.allinone.auth.application.domain.Auth;
import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.entity.BoardEntity;
import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.entity.LikesEntity;
import com.hongik.pcrc.allinone.board.infrastructure.persistance.mysql.entity.ViewsEntity;
import com.hongik.pcrc.allinone.comments.infrastructure.persistance.mysql.entity.CommentsEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "users")
@Entity(name = "users")
public class AuthEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "char(36)")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;
    @Column(nullable = false, length = 320)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String birth;
    @Column(nullable = false)
    private String gender;
    @Column(nullable = false)
    private String phone_number;
    @Column
    private String refresh_token;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user_id",
            cascade = CascadeType.REMOVE)
    private List<LikesEntity> likes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user_id",
            cascade = CascadeType.REMOVE)
    private List<ViewsEntity> views;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user_id",
            cascade = CascadeType.REMOVE)
    private List<BoardEntity> boardEntities;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user_id",
            cascade = CascadeType.REMOVE)
    private List<CommentsEntity> commentsEntities;

    public Auth toAuth() {
        return Auth.builder()
                .id(this.id)
                .email(this.email)
                .name(this.name)
                .birth(this.birth)
                .gender(this.gender)
                .phone_number(this.phone_number)
                .build();
    }

    public AuthEntity(Auth auth) {
        this.id = auth.getId();
        this.email = auth.getEmail();
        this.password = auth.getPassword();
        this.name = auth.getName();
        this.birth = auth.getBirth();
        this.gender = auth.getGender();
        this.phone_number = auth.getPhone_number();
        this.refresh_token = auth.getRefresh_token();
    }
}
