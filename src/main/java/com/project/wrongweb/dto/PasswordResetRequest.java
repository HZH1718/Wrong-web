package com.project.wrongweb.dto;

import lombok.Data;

/**
 * 忘记密码第二步：重置密码请求。
 */
@Data
public class PasswordResetRequest {

    /** 邮箱 + 手机号校验通过后返回的一次性凭证，必填。 */
    private String token;

    /** 新密码，必填。 */
    private String newPassword;
}
