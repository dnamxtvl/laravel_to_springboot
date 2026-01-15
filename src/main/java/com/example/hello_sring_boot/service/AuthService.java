package com.example.hello_sring_boot.service;

import com.example.hello_sring_boot.dto.authenication.RefreshTokenDTO;
import com.example.hello_sring_boot.dto.response.LoginResponse;
import com.example.hello_sring_boot.entity.ForgotPassword;
import com.example.hello_sring_boot.entity.User;
import com.example.hello_sring_boot.entity.UserRefreshToken;
import com.example.hello_sring_boot.enums.OtpStatus;
import com.example.hello_sring_boot.repository.ForgotPasswordRepository;
import com.example.hello_sring_boot.repository.UserRefreshTokenRepository;
import com.example.hello_sring_boot.repository.UserRepository;
import com.example.hello_sring_boot.security.JwtProperties;
import com.example.hello_sring_boot.security.JwtTokenManager;
import com.example.hello_sring_boot.utils.Helper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ForgotPasswordRepository repository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenManager jwtTokenManager;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final Helper helper;
    private final JwtProperties jwtProperties;

    @Autowired
    private JobScheduler jobScheduler;

    @Transactional
    public ForgotPassword createOtp(String userId) {
        repository.invalidateOldOtps(userId, OtpStatus.NEW, OtpStatus.EXPIRED);
        String otp = String.format("%06d", new Random().nextInt(1000000));

        ForgotPassword forgotPassword = ForgotPassword.builder()
                .userId(userId)
                .otp(otp)
                .status(OtpStatus.NEW)
                .expiredAt(LocalDateTime.now().plusHours(2))
                .build();

        return repository.save(forgotPassword);
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        ForgotPassword otp = createOtp(user.getId());
        String otpText = "Link reset mật khẩu của bạn của bạn là : https://yrk-fe.jp/reset-password?token=" + otp.getId();

        jobScheduler.enqueue(() -> emailService.sendMail(
                "\"Techmaster 👻\" <kakitani2000@gmail.com>",
                email,
                "Forgot Password Link",
                "<b>" + otpText + "</b>"
        ));
    }

    @Transactional
    public void changePassword(String token, String password) throws BadRequestException {
        ForgotPassword forgotPassword = repository.findById(token)
                .orElseThrow(() -> new EntityNotFoundException("OTP not found with token: " + token));

        if (LocalDateTime.now().isBefore(forgotPassword.getExpiredAt())) {
            throw new BadRequestException("forgot-password.token-expired");
        }

        forgotPassword.setStatus(OtpStatus.EXPIRED);
        repository.save(forgotPassword);

        User user = userRepository.findById(forgotPassword.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + forgotPassword.getUserId()));

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Transactional
    public LoginResponse refreshToken(String token, String userId) {
        final User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("user.not_found"));
        LoginResponse loginResponse = jwtTokenManager.generateToken(user);

        userRefreshTokenRepository.deleteByToken(token);
        UserRefreshToken userRefreshToken = UserRefreshToken.builder().userId(user.getId())
                .token(helper.encryptThisString(loginResponse.getRefreshToken()))
                .expiredAt(LocalDateTime.now().plusMinutes(jwtProperties.getExpirationMinuteRefresh()))
                .build();

        userRefreshTokenRepository.save(userRefreshToken);

        return loginResponse;
    }

    public Optional<RefreshTokenDTO> findByToken(String token) {
        Optional<UserRefreshToken> refreshToken = userRefreshTokenRepository.findByToken(token);
        return refreshToken.map(userRefreshToken -> RefreshTokenDTO.builder()
                .token(userRefreshToken.getToken())
                .userId(userRefreshToken.getUserId())
                .expiredAt(userRefreshToken.getExpiredAt())
                .build());
    }
}
