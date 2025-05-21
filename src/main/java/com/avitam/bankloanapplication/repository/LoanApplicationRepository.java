package com.avitam.bankloanapplication.repository;

import com.avitam.bankloanapplication.model.dto.LoanApplicationDto;
import com.avitam.bankloanapplication.model.entity.Loan;
import com.avitam.bankloanapplication.model.entity.LoanApplication;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LoanApplicationRepository extends MongoRepository<LoanApplication,String> {
    LoanApplication findByRecordId(String recordId);
    void deleteByRecordId(String recordId);
    LoanApplicationDto findByStatusOrderByIdentifier(boolean b);
    List<LoanApplication> findByCustomerIdAndLoanStatus(String customerId,String loanStatus);
    List<LoanApplication> findByLoanStatus(String loanStatus);

    Loan findByLoanId(String loanId);
}
