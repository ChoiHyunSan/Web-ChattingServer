package com.ll.webchattingserver.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result<T> {
    private int statusCode;
    private String message;
    private T data;

    public static <T> Result<T> success(final T data) {
        return createResult(200, "Success", data);
    }

    public static <T> Result<T> error(final int statusCode, final String message) {
        return createResult(statusCode, message, null);
    }

    public static <T> Result<T> createResult(final int statusCode, final String message, final T data) {
        return Result.<T>builder()
                .statusCode(statusCode)
                .message(message)
                .data(data)
                .build();
    }
}
