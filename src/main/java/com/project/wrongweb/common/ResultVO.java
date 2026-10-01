package com.project.wrongweb.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一接口返回结构。
 *
 * @param <T> data 字段类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultVO<T> {

    /** 业务状态码，200 表示成功，其他值表示失败。 */
    private Integer code;

    /** 提示信息。 */
    private String message;

    /** 业务数据。 */
    private T data;

    /** 服务端返回时间，毫秒时间戳。 */
    private Long timestamp;

    public static <T> ResultVO<T> success(T data) {
        return ResultVO.<T>builder()
                .code(200)
                .message("success")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> ResultVO<T> success() {
        return success(null);
    }

    public static <T> ResultVO<T> fail(Integer code, String message) {
        return ResultVO.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
