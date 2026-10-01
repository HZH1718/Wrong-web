package com.project.wrongweb.dto;

import lombok.Data;

/**
 * 用户登录请求。
 */
@Data
public class UserLoginRequest {

    /** 账号，手机号或邮箱，必填。 */
    private String account;

    /** 密码，必填。 */
    private String password;
}
