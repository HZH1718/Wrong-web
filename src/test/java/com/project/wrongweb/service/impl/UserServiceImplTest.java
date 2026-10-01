package com.project.wrongweb.service.impl;

import com.project.wrongweb.common.ResultVO;
import com.project.wrongweb.dto.PasswordResetRequest;
import com.project.wrongweb.dto.PasswordVerifyRequest;
import com.project.wrongweb.dto.UserLoginRequest;
import com.project.wrongweb.dto.UserRegisterRequest;
import com.project.wrongweb.entity.User;
import com.project.wrongweb.mapper.UserMapper;
import com.project.wrongweb.vo.PasswordResetTokenVO;
import com.project.wrongweb.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    private static final String EMAIL = "user@example.com";
    private static final String PHONE = "13800138000";

    @Mock
    private UserMapper userMapper;

    private UserServiceImpl userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userMapper);
    }

    @Test
    void registerShouldSaveUserAndReturnUserId() {
        UserRegisterRequest request = registerRequest();

        when(userMapper.selectByAccount(EMAIL)).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(10L);
            return 1;
        });

        ResultVO<UserVO> result = userService.register(request);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(10L, result.getData().getUserId());
        assertEquals(EMAIL, result.getData().getAccount());
        assertNull(result.getData().getToken());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(captor.capture());
        User saved = captor.getValue();
        assertEquals(EMAIL, saved.getAccount());
        assertEquals(EMAIL, saved.getEmail());
        assertEquals(PHONE, saved.getPhone());
        assertTrue(saved.getPassword().startsWith("$2"), "密码应以 BCrypt 摘要入库");
    }

    @Test
    void registerShouldRejectInvalidEmail() {
        UserRegisterRequest request = registerRequest();
        request.setEmail("not-an-email");

        ResultVO<UserVO> result = userService.register(request);

        assertEquals(400, result.getCode());
    }

    @Test
    void registerShouldRejectInvalidPhone() {
        UserRegisterRequest request = registerRequest();
        request.setPhone("12345");

        ResultVO<UserVO> result = userService.register(request);

        assertEquals(400, result.getCode());
    }

    @Test
    void registerShouldRejectMissingPhone() {
        UserRegisterRequest request = registerRequest();
        request.setPhone("  ");

        ResultVO<UserVO> result = userService.register(request);

        assertEquals(400, result.getCode());
    }

    @Test
    void registerShouldRejectDuplicateEmail() {
        UserRegisterRequest request = registerRequest();

        when(userMapper.selectByAccount(EMAIL)).thenReturn(new User());

        ResultVO<UserVO> result = userService.register(request);

        assertEquals(409, result.getCode());
    }

    @Test
    void loginShouldReturnTokenWhenPasswordMatches() {
        User user = existingUser(passwordEncoder.encode("123456"));

        when(userMapper.selectByAccount(EMAIL)).thenReturn(user);

        ResultVO<UserVO> result = userService.login(loginRequest("123456"));

        assertEquals(200, result.getCode());
        assertNotNull(result.getData().getToken());
    }

    @Test
    void loginShouldUpgradeLegacySha256Password() {
        User user = existingUser(legacyHash(EMAIL, "123456"));

        when(userMapper.selectByAccount(EMAIL)).thenReturn(user);

        ResultVO<UserVO> result = userService.login(loginRequest("123456"));

        assertEquals(200, result.getCode());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(userMapper).updatePassword(eq(1L), captor.capture());
        assertTrue(captor.getValue().startsWith("$2"), "老密码登录后应升级为 BCrypt");
    }

    @Test
    void loginShouldRejectWrongPassword() {
        User user = existingUser(passwordEncoder.encode("123456"));

        when(userMapper.selectByAccount(anyString())).thenReturn(user);

        ResultVO<UserVO> result = userService.login(loginRequest("wrong-password"));

        assertEquals(400, result.getCode());
    }

    @Test
    void verifyIdentityShouldReturnTokenWhenEmailAndPhoneMatch() {
        when(userMapper.selectByEmailAndPhone(EMAIL, PHONE)).thenReturn(existingUser("irrelevant"));

        ResultVO<PasswordResetTokenVO> result = userService.verifyIdentity(verifyRequest());

        assertEquals(200, result.getCode());
        assertNotNull(result.getData().getToken());
        assertEquals(600L, result.getData().getExpireSeconds());
    }

    @Test
    void verifyIdentityShouldRejectWhenNoRecordMatched() {
        when(userMapper.selectByEmailAndPhone(EMAIL, PHONE)).thenReturn(null);

        ResultVO<PasswordResetTokenVO> result = userService.verifyIdentity(verifyRequest());

        assertEquals(400, result.getCode());
        assertEquals("账号不存在或信息不符", result.getMessage());
    }

    @Test
    void resetPasswordShouldUpdatePasswordWhenTokenValid() {
        User user = existingUser("irrelevant");
        when(userMapper.selectByEmailAndPhone(EMAIL, PHONE)).thenReturn(user);
        when(userMapper.updatePassword(eq(1L), anyString())).thenReturn(1);

        String token = userService.verifyIdentity(verifyRequest()).getData().getToken();

        PasswordResetRequest resetRequest = new PasswordResetRequest();
        resetRequest.setToken(token);
        resetRequest.setNewPassword("new-password");

        ResultVO<Void> result = userService.resetPassword(resetRequest);

        assertEquals(200, result.getCode());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(userMapper).updatePassword(eq(1L), captor.capture());
        assertTrue(passwordEncoder.matches("new-password", captor.getValue()));
    }

    @Test
    void resetPasswordShouldRejectUnknownToken() {
        PasswordResetRequest resetRequest = new PasswordResetRequest();
        resetRequest.setToken("not-a-real-token");
        resetRequest.setNewPassword("new-password");

        ResultVO<Void> result = userService.resetPassword(resetRequest);

        assertEquals(400, result.getCode());
    }

    private UserRegisterRequest registerRequest() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail(EMAIL);
        request.setPhone(PHONE);
        request.setPassword("123456");
        request.setBirthDate(LocalDate.of(2000, 1, 1));
        request.setGender(1);
        return request;
    }

    private UserLoginRequest loginRequest(String password) {
        UserLoginRequest request = new UserLoginRequest();
        request.setAccount(EMAIL);
        request.setPassword(password);
        return request;
    }

    private PasswordVerifyRequest verifyRequest() {
        PasswordVerifyRequest request = new PasswordVerifyRequest();
        request.setEmail(EMAIL);
        request.setPhone(PHONE);
        return request;
    }

    private User existingUser(String password) {
        return User.builder()
                .userId(1L)
                .account(EMAIL)
                .email(EMAIL)
                .phone(PHONE)
                .password(password)
                .birthDate(LocalDate.of(2000, 1, 1))
                .gender(1)
                .build();
    }

    private String legacyHash(String account, String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest((account + rawPassword).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
