package com.example.hello_sring_boot.service;

import com.example.hello_sring_boot.entity.ForgotPassword;
import com.example.hello_sring_boot.entity.User;
import com.example.hello_sring_boot.enums.OtpStatus;
import com.example.hello_sring_boot.repository.ForgotPasswordRepository;
import com.example.hello_sring_boot.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ForgotPasswordRepository repository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

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
}
