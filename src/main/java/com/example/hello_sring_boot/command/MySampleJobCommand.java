package com.example.hello_sring_boot.command;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

@Slf4j
@Component
public class MySampleJobCommand {
    @Recurring(id = "daily-cleanup-job", cron = "0 0 * * *") // Chạy vào 00:00 mỗi ngày
    @Job(name = "Cleanup expired data", retries = 3)
    public void cleanupData() {
        log.error("Bắt đầu dọn dẹp dữ liệu lúc: {}", LocalDateTime.now());

        log.error("Dọn dẹp hoàn tất.");
    }

    // Ví dụ job chạy mỗi 1 phút
    @Recurring(id = "sync-data-job", cron = "*/1 * * * *")
    @Job(name = "Sync data from external API")
    public void syncData() {
        log.error("Đang đồng bộ dữ liệu...");
        log.error("Đang đồng bộ dữ liệu done...");
    }
}
