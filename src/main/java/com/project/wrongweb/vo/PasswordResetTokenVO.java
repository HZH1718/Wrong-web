package com.project.wrongweb.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邮箱 + 手机号校验通过后返回的一次性凭证。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetTokenVO {

    /** 一次性重置凭证，修改密码接口需要携带。 */
    private String token;

    /** 凭证有效期，单位秒。 */
    private Long expireSeconds;
}
