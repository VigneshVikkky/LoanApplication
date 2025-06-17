package com.avitam.bankloanapplication.repository;

import com.avitam.bankloanapplication.model.entity.KYC;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface KYCRepository extends MongoRepository<KYC, String> {

    List<KYC> findByStatusOrderByIdentifier(boolean b);

    KYC findByRecordId(String recordId);

    void deleteByRecordId(String recordId);
}
