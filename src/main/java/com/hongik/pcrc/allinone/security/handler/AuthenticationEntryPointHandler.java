package com.hongik.pcrc.allinone.security.handler;

import com.hongik.pcrc.allinone.exception.MessageType;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {
    /*
    * 스프링 시큐리티 컨텍스트 내에 존재하는 인증절차 중 인증과정이 실패하거나 인증헤더(Authorization)를 보내지 않게 되는 경우 401에러 값을 던지는데 이를 처리해주는 인터페이스이다.
    * 401에러가 발생하는 경우 commerce() 메서드가 실행된다.
    * */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        String exception = (String)request.getAttribute("exception");

        //System.out.println("exception:" + exception);

        //토큰이 없는 경우 예외처리
        if (exception == null) {
            setResponse(response, "UNAUTHORIZED", MessageType.UNAUTHORIZED);
            return;
        }

        //토큰이 만료된 경우 예외처리
        if (exception.equals("ExpiredJwtException")) {
            setResponse(response, "ExpiredJwtException", MessageType.ExpiredJwtException);
            return;
        }

        if (exception.equals("MalformedJwtException")) {
            setResponse(response, "MalformedJwtException", MessageType.MalformedJwtException);
            return;
        }

        // 이후 모든 exception은 illegal로 돌릴지./....
        if (exception.equals("IllegalArgumentException") || exception.equals("UnsupportedJwtException")) {
            setResponse(response, "IllegalArgumentJwtException", MessageType.MalformedJwtException);
        }

    }

    private void setResponse(HttpServletResponse response, String type, MessageType messageType)  throws IOException {

        JSONObject json = new JSONObject();
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        json.put("errorType", type);
        json.put("errorMessage", messageType.getMessage());
        response.getWriter().print(json);
    }
}
