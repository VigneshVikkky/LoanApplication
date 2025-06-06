package com.avitam.bankloanapplication.service;

import com.avitam.bankloanapplication.model.dto.LoanDto;
import com.avitam.bankloanapplication.model.dto.LoanEmiSummaryDto;
import com.avitam.bankloanapplication.model.dto.LoanWsDto;

import java.util.List;

public interface LoanService {

    LoanWsDto createLoan(LoanWsDto request);


    LoanDto getEmiStatusTillDate(LoanDto loanDto);

    LoanDto updatePaymentStatus(LoanDto loanDto);


    LoanWsDto getTotalDesiredLoanByCustomerRecordId(String customerRecordId);
    }


