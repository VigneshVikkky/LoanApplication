package com.avitam.bankloanapplication.repository;

import com.avitam.bankloanapplication.model.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    Notification findByRecordId(String recordId);

    void deleteByRecordId(String id);
}
