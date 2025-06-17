package com.avitam.bankloanapplication.repository;

import com.avitam.bankloanapplication.model.entity.LoanLimit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanLimitRepository extends MongoRepository<LoanLimit, String> {

    LoanLimit findByRecordId(String recordId);

    void deleteByRecordId(String id);

    LoanLimit findByName(String higher);

    LoanLimit findByCustomerId(String customerId);
}
