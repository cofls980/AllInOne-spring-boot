package com.hongik.pcrc.allinone.security.ui.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessTokenView {

    private final String accessToken;

    public AccessTokenView(String accessToken) {
        this.accessToken = accessToken;
    }
}
