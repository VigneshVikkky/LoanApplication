package com.avitam.bankloanapplication.service;

import com.avitam.bankloanapplication.model.dto.LoanDetailsWsDto;

public interface LoanDetailsService {

    LoanDetailsWsDto createLoanDetails(LoanDetailsWsDto request);

    LoanDetailsWsDto createLoanTemplate(LoanDetailsWsDto request);

    // List<LoanEmiSummaryDto> getLoanEmiSummary(String recordId);

    //LoanDetailsWsDto getUptoDateEmiDetails();

    // LoanDetailsDto getEmiStatusTillDate(LoanDetailsDto loanDetailsDto);
}
