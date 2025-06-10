package com.avitam.bankloanapplication.repository;

import com.avitam.bankloanapplication.model.entity.PaymentHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PaymentHistoryRepository  extends MongoRepository<PaymentHistory, String> {
    void deleteByRecordId(String recordId);

    List<PaymentHistory> findByStatus(boolean b);

    PaymentHistory findByRecordId(String recordId);

    PaymentHistory findByLoanId(String recordId);

    PaymentHistory findByLoan_RecordId(String recordId);

    boolean existsByLoan_RecordId(String recordId);
}
