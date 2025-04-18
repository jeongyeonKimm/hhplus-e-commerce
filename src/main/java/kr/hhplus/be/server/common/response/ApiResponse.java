package kr.hhplus.be.server.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "공통 응답 포맷")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ApiResponse<T> {

    private int code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("요청이 정상적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> successWithCreated(T data) {
        return ApiResponse.<T>builder()
                .code(201)
                .message("요청이 정상적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> successWithNoContent() {
        return ApiResponse.<T>builder()
                .code(204)
                .message("요청이 정상적으로 처리되었습니다.")
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .build();
    }

    @Builder
    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
