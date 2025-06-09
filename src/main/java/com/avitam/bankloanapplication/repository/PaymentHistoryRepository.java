package com.avitam.bankloanapplication.repository;

import com.avitam.bankloanapplication.model.entity.PaymentHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PaymentHistoryRepository  extends MongoRepository<PaymentHistory, String> {
    void deleteByRecordId(String recordId);

    List<PaymentHistory> findByStatus(boolean b);

    PaymentHistory findByRecordId(String recordId);
}
