package com.hongik.pcrc.allinone.security.handler;

import com.hongik.pcrc.allinone.exception.MessageType;
import org.json.simple.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class WebAccessDeniedHandler extends RuntimeException implements AccessDeniedHandler {
    /*
    * 권한이 없는 경우
    * */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {


        JSONObject json = new JSONObject();
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        json.put("errorType", "FORBIDDEN");
        json.put("errorMessage", MessageType.FORBIDDEN.getMessage());
        response.getWriter().print(json);
    }
}
