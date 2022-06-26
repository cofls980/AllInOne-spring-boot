package com.hongik.pcrc.allinone.ui.requestBody;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EmailVerifyRequest {

    //Email Info
    private String id;
    private String code;
}
