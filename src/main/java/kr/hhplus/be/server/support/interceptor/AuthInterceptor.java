package kr.hhplus.be.server.support.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Long userId = extractUserIdFromRequest(request);
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String errorResponse = objectMapper.writeValueAsString(Map.of(
                    "code", "UNAUTHORIZED",
                    "message", "userId가 누락되었거나 인증되지 않았습니다."
            ));
            response.getWriter().write(errorResponse);
        }

        log.info("[AuthInterceptor] userId = {}", userId);
    }

    private Long extractUserIdFromRequest(HttpServletRequest request) throws IOException {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            String userId = request.getParameter("userId");
            return (userId != null) ? Long.valueOf(userId) : null;
        }

        if (request.getContentType() != null && request.getContentType().contains("application/json")) {
            ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) request;

            String body = new String(cachingRequest.getContentAsByteArray(), StandardCharsets.UTF_8);

            JsonNode node = objectMapper.readTree(body);
            JsonNode userIdNode = node.get("userId");

            return (userIdNode != null && userIdNode.isNumber()) ? userIdNode.asLong() : null;
        }

        return null;
    }
}
