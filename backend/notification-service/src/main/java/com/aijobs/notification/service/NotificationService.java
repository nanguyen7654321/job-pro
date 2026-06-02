package com.aijobs.notification.service;

import com.aijobs.notification.dto.EmailNotificationRequest;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
  public String sendEmail(EmailNotificationRequest request) {
    return "queued email to " + request.to();
  }
}

