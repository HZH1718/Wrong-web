package com.project.wrongweb.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 用户安全返回对象，不包含密码。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    /** 用户ID。 */
    private Long userId;

    /** 账号。 */
    private String account;

    /** 出生日期。 */
    private LocalDate birthDate;

    /** 性别：1-男，2-女。 */
    private Integer gender;

    /** 登录令牌，预留字段，后续接入认证时返回。 */
    private String token;
}
