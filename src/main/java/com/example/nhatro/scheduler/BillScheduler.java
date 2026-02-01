package com.example.nhatro.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.nhatro.service.BillService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class BillScheduler {

    private final BillService billService;

    /**
     * Tự động tạo hóa đơn vào ngày 25 hàng tháng lúc 00:00
     * Cron expression: giây phút giờ ngày tháng thứ
     * 0 0 0 25 * ? = 00:00:00 ngày 25 hàng tháng
     */
    @Scheduled(cron = "0 0 0 25 * ?")
    public void generateMonthlyBills() {
        log.info("Scheduler triggered: Generating monthly bills on 25th of the month");
        try {
            billService.generateMonthlyBills();
            log.info("Monthly bills generated successfully");
        } catch (Exception e) {
            log.error("Error generating monthly bills: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Kiểm tra và cập nhật trạng thái hóa đơn quá hạn mỗi ngày lúc 01:00
     * Cron expression: 0 0 1 * * ? = 01:00:00 mỗi ngày
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void checkOverdueBills() {
        log.info("Scheduler triggered: Checking overdue bills");
        try {
            billService.updateOverdueBills();
            log.info("Overdue bills updated successfully");
        } catch (Exception e) {
            log.error("Error updating overdue bills: {}", e.getMessage(), e);
        }
    }
}
