package com.avitam.bankloanapplication.repository;

import com.avitam.bankloanapplication.model.entity.LoanScoreResult;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LoanScoreResultRepository extends MongoRepository<LoanScoreResult, String> {
    void deleteByRecordId(String id);

    LoanScoreResult findByRecordId(String RecordId);

    List<LoanScoreResult> findByStatusOrderByIdentifier(boolean b);

    List<LoanScoreResult> findByStatus(boolean b);

    LoanScoreResult findByName(String approved);
}
