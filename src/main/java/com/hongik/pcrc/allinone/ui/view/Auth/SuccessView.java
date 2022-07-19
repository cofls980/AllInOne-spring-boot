package com.hongik.pcrc.allinone.ui.view.Auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessView {

    private final String success;


    public SuccessView(String success) {
        this.success = success;
    }
}
