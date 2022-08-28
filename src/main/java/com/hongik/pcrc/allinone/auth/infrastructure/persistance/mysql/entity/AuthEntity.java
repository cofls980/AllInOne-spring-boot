package com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.entity;

import com.hongik.pcrc.allinone.auth.application.domain.Auth;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "users")
public class AuthEntity {

    @Id //@GeneratedValue
    @Column(nullable = false, length = 50)
    private String id;
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
                .name(this.name)
                .birth(this.birth)
                .gender(this.gender)
                .phoneNumber(this.phone_number)
                .build();
    }

    public AuthEntity(Auth auth) {
        this.id = auth.getId();
        this.password = auth.getPassword();
        this.name = auth.getName();
        this.birth = auth.getBirth();
        this.gender = auth.getGender();
        this.phone_number = auth.getPhoneNumber();
        this.refresh_token = auth.getRefreshToken();
    }
}
