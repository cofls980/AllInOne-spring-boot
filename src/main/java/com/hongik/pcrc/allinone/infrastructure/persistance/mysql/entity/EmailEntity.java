package com.hongik.pcrc.allinone.infrastructure.persistance.mysql.entity;

import com.hongik.pcrc.allinone.application.domain.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@Table(name = "email")
public class EmailEntity {

    @Id //@GeneratedValue
    private String id;
    @Column(nullable = false)
    private String code;

    public Email toEmail() {
        return Email.builder()
                .id(this.id)
                .code(this.code)
                .build();
    }

    public EmailEntity(Email email) {
        this.id = email.getId();
        this.code = email.getCode();
    }
}
