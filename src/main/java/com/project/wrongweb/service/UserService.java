package com.project.wrongweb.service;

import com.project.wrongweb.common.ResultVO;
import com.project.wrongweb.dto.PasswordResetRequest;
import com.project.wrongweb.dto.PasswordVerifyRequest;
import com.project.wrongweb.dto.UserLoginRequest;
import com.project.wrongweb.dto.UserRegisterRequest;
import com.project.wrongweb.vo.PasswordResetTokenVO;
import com.project.wrongweb.vo.UserVO;

/**
 * 用户服务。
 */
public interface UserService {

    /**
     * 用户注册。
     *
     * @param request 注册请求
     * @return 注册后的用户信息
     */
    ResultVO<UserVO> register(UserRegisterRequest request);

    /**
     * 用户登录，账号为注册时使用的邮箱。
     *
     * @param request 登录请求
     * @return 登录成功后的用户信息与令牌
     */
    ResultVO<UserVO> login(UserLoginRequest request);

    /**
     * 忘记密码第一步：校验邮箱与手机号是否同时匹配同一条用户记录。
     *
     * @param request 邮箱 + 手机号
     * @return 校验通过时返回一次性重置凭证
     */
    ResultVO<PasswordResetTokenVO> verifyIdentity(PasswordVerifyRequest request);

    /**
     * 忘记密码第二步：凭验证凭证修改密码。
     *
     * @param request 凭证 + 新密码
     * @return 修改结果
     */
    ResultVO<Void> resetPassword(PasswordResetRequest request);
}
