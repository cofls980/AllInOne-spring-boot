package com.hongik.pcrc.allinone.auth.infrastructure.persistance.mysql.entity;

import com.hongik.pcrc.allinone.auth.application.domain.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

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

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdDate;

    public Email toEmail() {
        return Email.builder()
                .id(this.id)
                .code(this.code)
                .createdDate(this.createdDate)
                .build();
    }

    public EmailEntity(Email email) {
        this.id = email.getId();
        this.code = email.getCode();
        this.createdDate = email.getCreatedDate();
    }
}
