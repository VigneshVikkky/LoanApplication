package com.avitam.bankloanapplication.service;

import com.avitam.bankloanapplication.model.dto.LoanDto;
import com.avitam.bankloanapplication.model.dto.LoanWsDto;

public interface LoanService {

    LoanWsDto createLoan(LoanWsDto request);


    LoanDto getEmiStatusTillDate(LoanDto loanDto);

    LoanDto updatePaymentStatus(LoanDto loanDto);


    LoanWsDto getTotalDesiredLoanByCustomerRecordId(LoanWsDto request);
}


