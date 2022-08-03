package com.hongik.pcrc.allinone.exception.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseView<T> {

    private final T data;

    public ApiResponseView(T data) {
        this.data = data;
    }
}
