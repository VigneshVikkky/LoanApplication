package com.avitam.bankloanapplication.service;

import com.avitam.bankloanapplication.model.dto.LoanDetailsDto;
import com.avitam.bankloanapplication.model.dto.LoanDetailsWsDto;
import com.avitam.bankloanapplication.model.dto.LoanEmiSummaryDto;
import com.avitam.bankloanapplication.model.dto.LoanWsDto;
import com.avitam.bankloanapplication.model.entity.LoanDetails;

import java.util.List;

public interface LoanDetailsService {

    LoanDetailsWsDto createLoan(LoanDetailsWsDto request);

    List<LoanEmiSummaryDto> getLoanEmiSummary(String recordId);

    LoanDetailsWsDto getUptoDateEmiDetails();

    LoanDetailsDto getEmiStatusTillDate(LoanDetailsDto loanDetailsDto);
}
