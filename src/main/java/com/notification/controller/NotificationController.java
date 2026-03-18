package com.notification.controller;

import com.notification.dto.*;
import com.notification.model.enums.NotificationStatus;
import com.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<NotificationResponse>> sendNotification(
            @Valid @RequestBody SendNotificationRequest request) {
        NotificationResponse response = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Notification processed successfully", response));
    }

    @PostMapping("/send/bulk")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> sendBulkNotifications(
            @Valid @RequestBody BulkNotificationRequest request) {
        List<NotificationResponse> responses = notificationService.sendBulkNotifications(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bulk notifications processed", responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotificationById(@PathVariable Long id) {
        NotificationResponse response = notificationService.getNotificationById(id);
        return ResponseEntity.ok(ApiResponse.success("Notification retrieved", response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotificationsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<NotificationResponse> responses = notificationService.getNotificationsByUserId(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success("User notifications retrieved", responses));
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsByUserAndStatus(
            @PathVariable Long userId,
            @PathVariable NotificationStatus status) {
        List<NotificationResponse> responses = notificationService.getNotificationsByUserAndStatus(userId, status);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved by status", responses));
    }
}
