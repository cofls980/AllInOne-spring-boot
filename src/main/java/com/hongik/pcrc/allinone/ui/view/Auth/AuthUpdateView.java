package com.hongik.pcrc.allinone.ui.view.Auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthUpdateView {

    private final String success;

    public AuthUpdateView(String success) {
        this.success = success;
    }
}
