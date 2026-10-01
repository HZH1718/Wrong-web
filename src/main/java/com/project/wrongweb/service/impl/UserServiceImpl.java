package com.project.wrongweb.service.impl;

import com.project.wrongweb.common.ResultVO;
import com.project.wrongweb.dto.PasswordResetRequest;
import com.project.wrongweb.dto.PasswordVerifyRequest;
import com.project.wrongweb.dto.UserLoginRequest;
import com.project.wrongweb.dto.UserRegisterRequest;
import com.project.wrongweb.entity.User;
import com.project.wrongweb.mapper.UserMapper;
import com.project.wrongweb.service.UserService;
import com.project.wrongweb.vo.PasswordResetTokenVO;
import com.project.wrongweb.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户服务实现。
 *
 * <p>负责用户注册、登录、忘记密码的邮箱 + 手机号校验与密码重置。</p>
 *
 * <p>密码使用 BCrypt 存储；历史账号仍保留的 SHA-256 摘要在登录成功后会自动升级为 BCrypt。</p>
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    /** 忘记密码凭证有效期，单位毫秒。 */
    private static final long RESET_TOKEN_TTL_MILLIS = 10 * 60 * 1000L;

    private final UserMapper userMapper;

    /** BCrypt 加密器，仅使用 spring-security-crypto，不引入完整 Spring Security。 */
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /** 邮箱 + 手机号校验通过后签发的一次性凭证，服务重启后失效。 */
    private final Map<String, ResetToken> resetTokens = new ConcurrentHashMap<>();

    /**
     * 注册新用户。
     *
     * <p>邮箱与手机号均为必填项；邮箱同时作为登录账号 account 存储，邮箱全局唯一。</p>
     */
    @Override
    public ResultVO<UserVO> register(UserRegisterRequest request) {
        // 1. 基础参数校验，避免后续访问空对象。
        if (request == null) {
            return ResultVO.fail(400, "注册参数不能为空");
        }

        if (!StringUtils.hasText(request.getEmail())) {
            return ResultVO.fail(400, "邮箱不能为空");
        }

        String email = request.getEmail().trim();
        if (!isEmail(email)) {
            return ResultVO.fail(400, "邮箱格式不正确");
        }

        if (!StringUtils.hasText(request.getPhone())) {
            return ResultVO.fail(400, "手机号不能为空");
        }

        String phone = request.getPhone().trim();
        if (!isPhone(phone)) {
            return ResultVO.fail(400, "手机号格式不正确，必须为 11 位手机号");
        }

        if (!StringUtils.hasText(request.getPassword())) {
            return ResultVO.fail(400, "密码不能为空");
        }

        if (request.getPassword().length() < 6) {
            return ResultVO.fail(400, "密码长度不能少于6位");
        }

        if (request.getBirthDate() == null) {
            return ResultVO.fail(400, "出生日期不能为空");
        }

        if (request.getGender() == null || (request.getGender() != 1 && request.getGender() != 2)) {
            return ResultVO.fail(400, "性别必须为1或2");
        }

        // 2. 邮箱即登录账号，检查是否已被注册。
        User existingUser = userMapper.selectByAccount(email);
        if (existingUser != null) {
            return ResultVO.fail(409, "该邮箱已注册");
        }

        User user = User.builder()
                .account(email)
                .email(email)
                .phone(phone)
                .password(passwordEncoder.encode(request.getPassword()))
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .build();

        // 3. 保存用户，成功后 MyBatis 会把自增 userId 回填到 user 对象。
        int rows = userMapper.insert(user);
        if (rows != 1) {
            return ResultVO.fail(500, "用户注册失败");
        }

        return ResultVO.success(toUserVO(user));
    }

    /**
     * 用户登录，账号为注册时使用的邮箱。
     */
    @Override
    public ResultVO<UserVO> login(UserLoginRequest request) {
        if (request == null) {
            return ResultVO.fail(400, "登录参数不能为空");
        }

        if (!StringUtils.hasText(request.getAccount()) || !StringUtils.hasText(request.getPassword())) {
            return ResultVO.fail(400, "邮箱和密码不能为空");
        }

        String account = request.getAccount().trim();
        User user = userMapper.selectByAccount(account);
        if (user == null) {
            return ResultVO.fail(400, "邮箱或密码错误");
        }

        if (!matchesPassword(user, request.getPassword())) {
            return ResultVO.fail(400, "邮箱或密码错误");
        }

        // 历史 SHA-256 密码在本次登录校验通过后就地升级为 BCrypt。
        if (!isBcryptHash(user.getPassword())) {
            userMapper.updatePassword(user.getUserId(), passwordEncoder.encode(request.getPassword()));
        }

        UserVO userVO = toUserVO(user);
        // 当前尚未接入正式认证体系，登录令牌先用 UUID 占位。
        userVO.setToken(UUID.randomUUID().toString());

        return ResultVO.success(userVO);
    }

    /**
     * 忘记密码第一步：校验邮箱与手机号是否同时匹配同一条用户记录。
     */
    @Override
    public ResultVO<PasswordResetTokenVO> verifyIdentity(PasswordVerifyRequest request) {
        if (request == null) {
            return ResultVO.fail(400, "验证参数不能为空");
        }

        if (!StringUtils.hasText(request.getEmail()) || !StringUtils.hasText(request.getPhone())) {
            return ResultVO.fail(400, "邮箱和手机号不能为空");
        }

        String email = request.getEmail().trim();
        String phone = request.getPhone().trim();

        if (!isEmail(email) || !isPhone(phone)) {
            return ResultVO.fail(400, "邮箱或手机号格式不正确");
        }

        // 清理过期凭证，避免内存中堆积无效 token。
        purgeExpiredTokens();

        User user = userMapper.selectByEmailAndPhone(email, phone);
        if (user == null) {
            return ResultVO.fail(400, "账号不存在或信息不符");
        }

        String token = UUID.randomUUID().toString();
        resetTokens.put(token, new ResetToken(user.getUserId(), System.currentTimeMillis() + RESET_TOKEN_TTL_MILLIS));

        return ResultVO.success(PasswordResetTokenVO.builder()
                .token(token)
                .expireSeconds(RESET_TOKEN_TTL_MILLIS / 1000)
                .build());
    }

    /**
     * 忘记密码第二步：凭验证凭证修改密码，凭证一次性使用且带有效期。
     */
    @Override
    public ResultVO<Void> resetPassword(PasswordResetRequest request) {
        if (request == null || !StringUtils.hasText(request.getToken())) {
            return ResultVO.fail(400, "验证凭证不能为空，请先验证邮箱和手机号");
        }

        if (!StringUtils.hasText(request.getNewPassword())) {
            return ResultVO.fail(400, "新密码不能为空");
        }

        if (request.getNewPassword().length() < 6) {
            return ResultVO.fail(400, "密码长度不能少于6位");
        }

        purgeExpiredTokens();

        String token = request.getToken().trim();
        ResetToken resetToken = resetTokens.get(token);
        if (resetToken == null) {
            return ResultVO.fail(400, "验证已过期或无效，请重新验证邮箱和手机号");
        }

        int rows = userMapper.updatePassword(resetToken.userId(), passwordEncoder.encode(request.getNewPassword()));
        if (rows != 1) {
            return ResultVO.fail(500, "密码修改失败");
        }

        // 凭证一次性使用，修改成功后立即作废。
        resetTokens.remove(token);

        return ResultVO.success();
    }

    /**
     * 将用户实体转换为不会暴露密码的返回对象。
     */
    private UserVO toUserVO(User user) {
        return UserVO.builder()
                .userId(user.getUserId())
                .account(user.getAccount())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .build();
    }

    /**
     * 校验明文密码是否与库中摘要一致，兼容 BCrypt 与历史 SHA-256 摘要。
     */
    private boolean matchesPassword(User user, String rawPassword) {
        String stored = user.getPassword();
        if (!StringUtils.hasText(stored)) {
            return false;
        }

        if (isBcryptHash(stored)) {
            return passwordEncoder.matches(rawPassword, stored);
        }

        return legacyHash(user.getAccount(), rawPassword).equals(stored);
    }

    /**
     * 判断摘要是否为 BCrypt 格式。
     */
    private boolean isBcryptHash(String password) {
        return password != null && password.startsWith("$2");
    }

    /**
     * 清理已过期的重置凭证。
     */
    private void purgeExpiredTokens() {
        long now = System.currentTimeMillis();
        resetTokens.entrySet().removeIf(entry -> entry.getValue().expireAt() < now);
    }

    /**
     * 判断是否为常见中国大陆手机号。
     */
    private boolean isPhone(String phone) {
        return phone != null && phone.matches("^1[3-9]\\d{9}$");
    }

    /**
     * 判断是否为常见邮箱格式。
     */
    private boolean isEmail(String email) {
        return email != null && email.matches("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * 历史密码摘要算法：SHA-256 + 账号盐。
     *
     * <p>仅用于兼容改造前注册的老账号，新密码一律使用 BCrypt。</p>
     */
    private String legacyHash(String account, String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest((account + rawPassword).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm is not available", exception);
        }
    }

    /**
     * 一次性重置凭证，记录归属用户与过期时间。
     */
    private record ResetToken(Long userId, long expireAt) {
    }
}
