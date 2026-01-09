package com.example.hello_sring_boot.service;

import com.example.hello_sring_boot.entity.ForgotPassword;
import com.example.hello_sring_boot.entity.User;
import com.example.hello_sring_boot.enums.OtpStatus;
import com.example.hello_sring_boot.repository.ForgotPasswordRepository;
import com.example.hello_sring_boot.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ForgotPasswordRepository repository;
    private final UserRepository userRepository;
    private final EmailService emailService;

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
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .build();

        return repository.save(forgotPassword);
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        ForgotPassword otp = createOtp(user.getId());
        String otpText = "Otp của bạn là : " + otp.getOtp();

        jobScheduler.enqueue(() -> emailService.sendMail(
                "\"Techmaster 👻\" <kakitani2000@gmail.com>",
                email,
                "Forgot Password OTP",
                "<b>" + otpText + "</b>"
        ));
    }
}
