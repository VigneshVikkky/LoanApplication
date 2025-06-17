package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.model.dto.NotificationDto;
import com.avitam.bankloanapplication.model.dto.NotificationWsDto;
import com.avitam.bankloanapplication.model.entity.Notification;
import com.avitam.bankloanapplication.repository.NotificationRepository;
import com.avitam.bankloanapplication.service.NotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {
    private static final String ADMIN_NOTIFICATION = "/admin/notification";
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private ModelMapper modelMapper;


    public Optional<Notification> findNotificationById(String id) {
        return Optional.ofNullable(notificationRepository.findByRecordId(id));
    }

    public Notification getNotificationById(String id) {
        return findNotificationById(id).get();
    }

    public NotificationWsDto handelEdit(NotificationWsDto request) {
        NotificationWsDto notificationWsDto = new NotificationWsDto();
        Notification notification = new Notification();
        List<NotificationDto> notificationDtos = request.getNotificationDtoList();
        List<Notification> notifications = new ArrayList<>();
        for (NotificationDto notificationDto : notificationDtos) {
            if (notificationDto.getRecordId() != null) {
                notification = notificationRepository.findByRecordId(notificationDto.getRecordId());
                modelMapper.map(notificationDto, notification);
                notificationRepository.save(notification);
                request.setMessage("Data updated successfully");


            } else {
                notification = modelMapper.map(notificationDto, Notification.class);
                notification.setCreationTime(new Date());
                notification.setStatus(true);
                notificationRepository.save(notification);
            }
            if (request.getRecordId() == null) {
                notification.setRecordId(String.valueOf(notification.getId().getTimestamp()));
            }
            notificationRepository.save(notification);
            notifications.add(notification);
            request.setBaseUrl(ADMIN_NOTIFICATION);
            request.setMessage("Data added Successfully");

        }
        request.setNotificationDtoList(modelMapper.map(notifications, List.class));
        return request;
    }


}
