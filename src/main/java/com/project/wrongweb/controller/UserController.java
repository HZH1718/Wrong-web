package com.project.wrongweb.controller;

import com.project.wrongweb.common.ResultVO;
import com.project.wrongweb.dto.PasswordResetRequest;
import com.project.wrongweb.dto.PasswordVerifyRequest;
import com.project.wrongweb.dto.UserLoginRequest;
import com.project.wrongweb.dto.UserRegisterRequest;
import com.project.wrongweb.service.UserService;
import com.project.wrongweb.vo.PasswordResetTokenVO;
import com.project.wrongweb.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口。
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册。
     *
     * <p>请求方式：POST</p>
     * <p>接口地址：/api/user/register</p>
     *
     * <p>请求体字段：</p>
     * <ul>
     *   <li>email：邮箱，String，必填，需符合邮箱格式且未被注册，同时作为登录账号存储</li>
     *   <li>phone：手机号码，String，必填，需为 11 位手机号</li>
     *   <li>password：密码，String，必填，不少于 6 位，入库前使用 BCrypt 加密</li>
     *   <li>birthDate：出生日期，格式 yyyy-MM-dd，String，必填</li>
     *   <li>gender：性别，Integer，必填，1-男，2-女</li>
     * </ul>
     *
     * <p>返回 JSON 结构：</p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "userId": 1,
     *     "account": "user@example.com",
     *     "birthDate": "2000-01-01",
     *     "gender": 1,
     *     "token": null
     *   },
     *   "timestamp": 1735689600000
     * }
     * </pre>
     *
     * <p>返回字段含义：</p>
     * <ul>
     *   <li>code：状态码，200 表示成功；400 表示参数或格式错误；409 表示邮箱已注册</li>
     *   <li>message：提示信息</li>
     *   <li>data.userId：用户ID</li>
     *   <li>data.account：登录账号，等于注册邮箱</li>
     *   <li>data.birthDate：出生日期</li>
     *   <li>data.gender：性别，1-男，2-女</li>
     *   <li>data.token：登录令牌，注册接口不返回</li>
     *   <li>timestamp：服务端毫秒时间戳</li>
     * </ul>
     *
     * @param request 注册请求体
     * @return 注册后的用户信息
     */
    @PostMapping("/register")
    public ResultVO<UserVO> register(@RequestBody UserRegisterRequest request) {
        return userService.register(request);
    }

    /**
     * 用户登录。
     *
     * <p>请求方式：POST</p>
     * <p>接口地址：/api/user/login</p>
     *
     * <p>请求体字段：</p>
     * <ul>
     *   <li>account：登录账号，String，必填，传入注册时使用的邮箱</li>
     *   <li>password：密码，String，必填</li>
     * </ul>
     *
     * <p>返回 JSON 结构：</p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "userId": 1,
     *     "account": "user@example.com",
     *     "birthDate": "2000-01-01",
     *     "gender": 1,
     *     "token": "8b1a5f7c-..."
     *   },
     *   "timestamp": 1735689600000
     * }
     * </pre>
     *
     * <p>返回字段含义：</p>
     * <ul>
     *   <li>code：状态码，200 表示成功；400 表示邮箱或密码错误</li>
     *   <li>message：提示信息</li>
     *   <li>data.userId：用户ID</li>
     *   <li>data.account：登录账号，即注册邮箱</li>
     *   <li>data.birthDate：出生日期</li>
     *   <li>data.gender：性别，1-男，2-女</li>
     *   <li>data.token：登录令牌</li>
     *   <li>timestamp：服务端毫秒时间戳</li>
     * </ul>
     *
     * @param request 登录请求体
     * @return 登录成功后的用户信息
     */
    @PostMapping("/login")
    public ResultVO<UserVO> login(@RequestBody UserLoginRequest request) {
        return userService.login(request);
    }

    /**
     * 忘记密码第一步：验证邮箱 + 手机号。
     *
     * <p>请求方式：POST</p>
     * <p>接口地址：/api/user/verify-identity</p>
     *
     * <p>请求体字段：</p>
     * <ul>
     *   <li>email：邮箱，String，必填</li>
     *   <li>phone：手机号码，String，必填</li>
     * </ul>
     *
     * <p>返回 JSON 结构：</p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "token": "0c2f1e6a-...",
     *     "expireSeconds": 600
     *   },
     *   "timestamp": 1735689600000
     * }
     * </pre>
     *
     * <p>返回字段含义：</p>
     * <ul>
     *   <li>code：状态码，200 表示验证通过；400 表示参数错误或「账号不存在或信息不符」</li>
     *   <li>message：提示信息，验证失败时为「账号不存在或信息不符」</li>
     *   <li>data.token：一次性重置凭证，修改密码接口需要携带</li>
     *   <li>data.expireSeconds：凭证有效期，单位秒</li>
     *   <li>timestamp：服务端毫秒时间戳</li>
     * </ul>
     *
     * @param request 邮箱 + 手机号请求体
     * @return 校验通过时返回一次性重置凭证
     */
    @PostMapping("/verify-identity")
    public ResultVO<PasswordResetTokenVO> verifyIdentity(@RequestBody PasswordVerifyRequest request) {
        return userService.verifyIdentity(request);
    }

    /**
     * 忘记密码第二步：修改密码。
     *
     * <p>请求方式：POST</p>
     * <p>接口地址：/api/user/reset-password</p>
     *
     * <p>请求体字段：</p>
     * <ul>
     *   <li>token：邮箱 + 手机号验证通过后返回的一次性凭证，String，必填</li>
     *   <li>newPassword：新密码，String，必填，不少于 6 位，入库前使用 BCrypt 加密</li>
     * </ul>
     *
     * <p>返回 JSON 结构：</p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": null,
     *   "timestamp": 1735689600000
     * }
     * </pre>
     *
     * <p>返回字段含义：</p>
     * <ul>
     *   <li>code：状态码，200 表示修改成功；400 表示凭证无效/过期或新密码不合法</li>
     *   <li>message：提示信息</li>
     *   <li>data：修改结果，固定为 null</li>
     *   <li>timestamp：服务端毫秒时间戳</li>
     * </ul>
     *
     * @param request 凭证 + 新密码请求体
     * @return 修改结果
     */
    @PostMapping("/reset-password")
    public ResultVO<Void> resetPassword(@RequestBody PasswordResetRequest request) {
        return userService.resetPassword(request);
    }
}
