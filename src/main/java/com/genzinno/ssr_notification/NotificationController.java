package com.genzinno.ssr_notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notifications")
    public SseEmitter getNotifications() {
        return notificationService.addEmitter();
    }

    @GetMapping("/notify")
    public String sendNotification(@RequestParam String message) {
        notificationService.sendNotification(message);
        return "Notification sent";
    }
}
