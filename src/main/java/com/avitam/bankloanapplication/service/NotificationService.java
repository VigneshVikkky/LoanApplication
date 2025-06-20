package com.avitam.bankloanapplication.service;

import com.avitam.bankloanapplication.model.dto.NotificationWsDto;
import com.avitam.bankloanapplication.model.entity.Notification;

import java.util.Optional;

public interface NotificationService {

    Optional<Notification> findNotificationById(String id);

    NotificationWsDto handelEdit(NotificationWsDto request);
    void sendNotification(String title, String message);
}
