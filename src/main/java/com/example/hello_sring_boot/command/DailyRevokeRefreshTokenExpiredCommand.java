package com.example.hello_sring_boot.command;

import com.example.hello_sring_boot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyRevokeRefreshTokenExpiredCommand {
    private final UserService userService;

    @Recurring(id = "revoke-refresh-token-daily", cron = "55 10 * * *")
    @Job(name = "Revoke refresh token daily")
    public void revokeRefreshToken() {
        log.error("Starting revoke refresh token...");
        try {
            Integer rows = userService.revokeExpiredRefreshToken();
            log.error("Revoke refresh token has been successfully executed {} rows", rows);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
