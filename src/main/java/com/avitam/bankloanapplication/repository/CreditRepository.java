package com.avitam.bankloanapplication.repository;

import com.avitam.bankloanapplication.model.entity.Credit;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CreditRepository extends MongoRepository<Credit, String> {

    Credit findByRecordId(String recordId);

    void deleteByRecordId(String recordId);

    List<Credit> findByStatusOrderByIdentifier(boolean b);
}
