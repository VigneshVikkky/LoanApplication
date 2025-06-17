package com.avitam.bankloanapplication.repository;

import com.avitam.bankloanapplication.model.entity.LoanDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LoanDetailsRepository extends MongoRepository<LoanDetails, String> {


    void deleteByRecordId(String recordId);

    List<LoanDetails> findByStatus(boolean b);

    List<LoanDetails> findByRecordIdAndStatus(String recordId, boolean b);

    LoanDetails findByRecordId(String recordId);

    LoanDetails findByLoanId(String loanId);
}
