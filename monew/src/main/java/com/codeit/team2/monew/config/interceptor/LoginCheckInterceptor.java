package com.codeit.team2.monew.config.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        String userId = request.getHeader("MoNew-Request-User-ID");

        if (userId == null || userId.isEmpty()) {
            // JSON 형태의 에러 응답 구성, 추후 수정
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("timestamp", Instant.now().toString());
            errorBody.put("code", "Unauthorized");
            errorBody.put("message", "로그인이 필요합니다.");
            errorBody.put("details", new HashMap<>());
            errorBody.put("exceptionType", "LoginRequiredException");
            errorBody.put("status", 401);

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(errorBody);

            response.getWriter().write(json);
            return false;
        }

        return true;
    }
}

