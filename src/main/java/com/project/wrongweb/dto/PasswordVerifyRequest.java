package com.project.wrongweb.dto;

import lombok.Data;

/**
 * 忘记密码第一步：校验邮箱 + 手机号请求。
 */
@Data
public class PasswordVerifyRequest {

    /** 邮箱，必填。 */
    private String email;

    /** 手机号码，必填。 */
    private String phone;
}
