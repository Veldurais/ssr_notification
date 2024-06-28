# Spring Boot SSE System Notifications

This project demonstrates how to implement Server-Sent Events (SSE) in a Spring Boot application and display received events as system notifications using the Web Notifications API.

## Overview

This example project includes a Spring Boot backend that sends real-time notifications to connected clients using SSE. The client-side HTML and JavaScript code listens for these notifications and displays them as system notifications.

## Features

- Real-time server-to-client communication using SSE.
- Display notifications as system notifications on the client.
- Simple and lightweight implementation.

## Prerequisites

- Java 17
- Maven
- A web browser that supports the Web Notifications API

## Getting Started

### 1. Clone the Repository

```sh
git clone [https://github.com/Veldurais/ssr_notification.git]
cd ssr_notification
```

### 2. Build and Run the Spring Boot Application

```sh
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

### 3. Open the Client-Side HTML

Open the `index.html` file in your web browser.

### 4. Trigger a Notification

Use the following `curl` command to trigger a notification from the server:

```sh
curl -X POST "http://localhost:8080/notify?message=Hello+World"
```

## Project Structure

- `src/main/java/com/example/sse/`
  - `NotificationController.java`: Handles SSE connections and sending notifications.
  - `NotificationService.java`: Manages SSE emitters and broadcasting notifications.

- `src/main/resources/static/`
  - `index.html`: The client-side HTML and JavaScript for displaying system notifications.

## Code Explanation

### NotificationController.java

```java
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

    @PostMapping("/notify")
    public String sendNotification(@RequestParam String message) {
        notificationService.sendNotification(message);
        return "Notification sent";
    }
}
```

### NotificationService.java

```java
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {
    private final List<SseEmitter> emitters = new ArrayList<>();

    public SseEmitter addEmitter() {
        SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        return emitter;
    }

    public void sendNotification(String message) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().data(message));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
    }
}
```

### index.html

```html
<!DOCTYPE html>
<html>
<head>
    <title>SSE System Notifications Example</title>
</head>
<body>
    <h1>Server-Sent Events with System Notifications</h1>
    <div id="notifications"></div>

    <script>
        // Check if the browser supports notifications
        if ('Notification' in window) {
            // Request permission to display notifications
            Notification.requestPermission().then(permission => {
                if (permission !== 'granted') {
                    alert('Permission to display notifications was denied');
                }
            });
        } else {
            alert('This browser does not support desktop notifications');
        }

        const eventSource = new EventSource("http://localhost:8080/notifications");

        eventSource.onmessage = function(event) {
            console.log("Received push notification: " + event.data);

            if (Notification.permission === 'granted') {
                new Notification('New Notification', {
                    body: event.data,
                });
            }

            const notificationDiv = document.getElementById("notifications");
            notificationDiv.innerHTML += `<p>${event.data}</p>`;
        };

        eventSource.onerror = function(error) {
            console.error("SSE error: " + error);
        };
    </script>
</body>
</html>
```

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
