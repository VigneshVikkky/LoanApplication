package com.avitam.bankloanapplication.repository;

import com.avitam.bankloanapplication.model.entity.LoanTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanTemplateRepository extends MongoRepository<LoanTemplate, String> {
    LoanTemplate findByRecordId(String recordId);

    void deleteByRecordId(String recordId);

    LoanTemplate findByLoanType(String loanType);

    List<LoanTemplate> findByStatus(boolean status);

    List<LoanTemplate> findByLoanTypeAndStatus(String loanType, boolean status);
}
