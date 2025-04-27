package kr.hhplus.be.server.support.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class LogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        final UUID uuid = UUID.randomUUID();
        MDC.put("traceId", uuid.toString());

        try {
            chain.doFilter(cachingRequest, cachingResponse);

            String uri = cachingRequest.getRequestURI();
            String method = cachingRequest.getMethod();
            String requestBody = new String(cachingRequest.getContentAsByteArray(), cachingRequest.getCharacterEncoding());

            log.info("[REQUEST {}] {} {} body = {}", MDC.get("traceId"), method, uri, requestBody);

            int httpStatus = cachingResponse.getStatus();
            String responseBody = new String(cachingResponse.getContentAsByteArray(), cachingResponse.getCharacterEncoding());

            log.info("[RESPONSE {}] {} body = {}", MDC.get("traceId"), httpStatus, responseBody);
        } finally {
            cachingResponse.copyBodyToResponse();
            MDC.clear();
        }
    }
}
