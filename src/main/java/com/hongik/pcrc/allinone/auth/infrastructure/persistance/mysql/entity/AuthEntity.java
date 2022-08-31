package com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.entity;

import com.hongik.pcrc.allinone.auth.application.domain.Auth;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@ToString
//@Entity
@NoArgsConstructor
@Table(name = "users_test")
@Entity(name = "users_test")
public class AuthEntity {

    @Id //@GeneratedValue
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "char(36)")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;
    @Column(nullable = false)//, length = 320
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
