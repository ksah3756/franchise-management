package com.goorm.api.notification.event;


import com.goorm.core.notification.domain.Notification;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationReadEvent {
    private final Notification notification;
}
