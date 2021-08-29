package com.acech.demo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 通用响应类
 *
 * @author wahcen@163.com
 * @date 2021/8/28 20:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class R<T> {
    public static final String DEFAULT_CODE = "200";
    public static final String DEFAULT_AUTH_CODE = "403";
    public static final String DEFAULT_FAIL_CODE = "500";
    public static final String DEFAULT_MESSAGE = "";
    public static final Void DEFAULT_DATA = null;

    /**
     * 响应状态码
     */
    private String code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    public static <T> R<T> ok(String code, String message, T data) {
        return new R<>(code, message, data);
    }

    public static <T> R<T> ok(String message, T data) {
        return new R<>(DEFAULT_CODE, message, data);
    }

    public static R<Void> ok(String message) {
        return new R<>(DEFAULT_CODE, message, DEFAULT_DATA);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(DEFAULT_CODE, DEFAULT_MESSAGE, data);
    }

    public static R<Void> ok() {
        return new R<>(DEFAULT_CODE, DEFAULT_MESSAGE, DEFAULT_DATA);
    }

    public static <T> R<T> fail(String code, String message, T data) {
        return new R<>(code, message, data);
    }

    public static <T> R<T> fail(String message, T data) {
        return fail(DEFAULT_FAIL_CODE, message, data);
    }

    public static R<Void> fail(String message) {
        return fail(DEFAULT_FAIL_CODE, message, DEFAULT_DATA);
    }

    public static <T> R<T> fail(T data) {
        return fail(DEFAULT_FAIL_CODE, DEFAULT_MESSAGE, data);
    }

    public static R<Void> fail() {
        return fail(DEFAULT_FAIL_CODE, DEFAULT_MESSAGE, DEFAULT_DATA);
    }

    public static <T> R<T> noAuth(String code, String message, T data) {
        return new R<>(code, message, data);
    }

    public static <T> R<T> noAuth(String message, T data) {
        return noAuth(DEFAULT_AUTH_CODE, message, data);
    }

    public static R<Void> noAuth(String message) {
        return noAuth(DEFAULT_AUTH_CODE, message, DEFAULT_DATA);
    }

    public static <T> R<T> noAuth(T data) {
        return noAuth(DEFAULT_AUTH_CODE, DEFAULT_MESSAGE, data);
    }

    public static R<Void> noAuth() {
        return noAuth(DEFAULT_AUTH_CODE, DEFAULT_MESSAGE, DEFAULT_DATA);
    }
}
