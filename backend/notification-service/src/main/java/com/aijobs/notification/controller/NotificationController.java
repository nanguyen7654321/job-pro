package com.aijobs.notification.controller;

import com.aijobs.common.dto.ApiResponse;
import com.aijobs.notification.dto.EmailNotificationRequest;
import com.aijobs.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @PostMapping("/email")
  public ApiResponse<String> sendEmail(@Valid @RequestBody EmailNotificationRequest request) {
    return ApiResponse.ok(notificationService.sendEmail(request));
  }
}

