package com.avitam.bankloanapplication.repository;

import com.avitam.bankloanapplication.model.entity.LoanType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanTypeRepository extends MongoRepository<LoanType, String> {
    LoanType findByRecordId(String recordId);

    LoanType findByName(String name);

    void deleteByRecordId(String id);

    List<LoanType> findByStatus(boolean b);
}