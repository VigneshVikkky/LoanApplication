package com.avitam.bankloanapplication.repository;


import com.avitam.bankloanapplication.model.entity.OTP;
import com.avitam.bankloanapplication.repository.generic.GenericImportRepository;

public interface OtpRepository extends GenericImportRepository<OTP> {
    Object findByStatusOrderByIdentifier(boolean b);

    OTP findByRecordId(String recordId);

    void deleteByRecordId(String recordId);

    OTP findByUserId(String userId);
}
