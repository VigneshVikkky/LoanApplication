package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.core.service.ReflectionDataService;
import com.avitam.bankloanapplication.model.dto.NotificationDto;
import com.avitam.bankloanapplication.model.dto.NotificationWsDto;
import com.avitam.bankloanapplication.model.entity.Notification;
import com.avitam.bankloanapplication.repository.NotificationRepository;
import com.avitam.bankloanapplication.service.NotificationService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Service
public class NotificationServiceImpl implements NotificationService {
    private static final String ADMIN_NOTIFICATION = "/admin/notification";
    Logger LOG = LoggerFactory.getLogger(NotificationService.class);

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

    public void sendNotification(String title, String message) {
        try {
            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic OGUwNDJjZmMtZjA1Mi00NTU3LWE4N2MtNTNjY2ZiNDRhMzQ2");
            con.setRequestMethod("POST");
            String strJsonBody = "{\"app_id\": \"2fa2f97f-1c8d-41f1-888c-d468aa9899a3\",\"included_segments\": [\"All\"],\"data\": {\"foo\": \"bar\"},\"contents\": {\"en\": \""
                    + message + "\"}," + "\"headings\": {\"en\": " + "\"" + title + "\"}" + "}";
            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
            con.setFixedLengthStreamingMode(sendBytes.length);
            OutputStream outputStream = con.getOutputStream();
            outputStream.write(sendBytes);
            int httpResponse = con.getResponseCode();
            if (httpResponse >= 200 && httpResponse < 400) {
                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                LOG.debug(scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "");
                scanner.close();
            } else {
                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                LOG.debug(scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "");
                scanner.close();
            }
        } catch (Exception t) {
            LOG.error(t.getMessage(), t);
        }
    }
}
