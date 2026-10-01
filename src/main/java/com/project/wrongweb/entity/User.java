package com.project.wrongweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体，对应表 sys_user。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /** 用户ID，也是数据隔离字段 userId。 */
    private Long userId;

    /** 登录账号，注册时存入邮箱。 */
    private String account;

    /** 邮箱，注册必填，全局唯一。 */
    private String email;

    /** 手机号码，注册必填，11 位。 */
    private String phone;

    /** 登录密码，接口返回时不会序列化。 */
    @JsonIgnore
    private String password;

    /** 出生日期。 */
    private LocalDate birthDate;

    /** 性别：1-男，2-女。 */
    private Integer gender;

    /** 创建时间。 */
    private LocalDateTime createdAt;

    /** 更新时间。 */
    private LocalDateTime updatedAt;
}
