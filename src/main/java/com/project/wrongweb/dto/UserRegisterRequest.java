package com.project.wrongweb.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 用户注册请求。
 */
@Data
public class UserRegisterRequest {

    /** 邮箱，注册必填，同时作为登录账号 account 存储。 */
    private String email;

    /** 手机号码，注册必填，11 位。 */
    private String phone;

    /** 密码，必填。 */
    private String password;

    /** 出生日期，必填。 */
    private LocalDate birthDate;

    /** 性别：1-男，2-女，必填。 */
    private Integer gender;
}
