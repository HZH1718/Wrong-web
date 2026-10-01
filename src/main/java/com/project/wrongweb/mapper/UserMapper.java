package com.project.wrongweb.mapper;

import com.project.wrongweb.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Select;

/**
 * 用户 Mapper。
 */
@Mapper
public interface UserMapper {

    /**
     * 根据账号查询用户。
     */
    @Select("SELECT user_id, account, email, phone, password, birth_date, gender, created_at, updated_at " +
            "FROM sys_user WHERE account = #{account}")
    User selectByAccount(@Param("account") String account);

    /**
     * 根据用户ID查询用户。
     */
    @Select("SELECT user_id, account, email, phone, password, birth_date, gender, created_at, updated_at " +
            "FROM sys_user WHERE user_id = #{userId}")
    User selectByUserId(@Param("userId") Long userId);

    /**
     * 根据邮箱与手机号查询用户，用于忘记密码时的身份校验。
     */
    @Select("SELECT user_id, account, email, phone, password, birth_date, gender, created_at, updated_at " +
            "FROM sys_user WHERE email = #{email} AND phone = #{phone}")
    User selectByEmailAndPhone(@Param("email") String email, @Param("phone") String phone);

    /**
     * 新增用户。
     */
    @Insert("INSERT INTO sys_user(account, email, phone, password, birth_date, gender) " +
            "VALUES(#{account}, #{email}, #{phone}, #{password}, #{birthDate}, #{gender})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(User user);

    /**
     * 修改用户基础信息。
     */
    @Update("UPDATE sys_user SET birth_date = #{birthDate}, gender = #{gender}, updated_at = NOW() WHERE user_id = #{userId}")
    int updateByUserId(User user);

    /**
     * 更新用户密码，仅覆盖 password 字段。
     */
    @Update("UPDATE sys_user SET password = #{password}, updated_at = NOW() WHERE user_id = #{userId}")
    int updatePassword(@Param("userId") Long userId, @Param("password") String password);
}
