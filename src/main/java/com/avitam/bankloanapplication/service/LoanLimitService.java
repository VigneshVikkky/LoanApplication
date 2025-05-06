package com.avitam.bankloanapplication.service;

import com.avitam.bankloanapplication.model.dto.LoanLimitDto;
import com.avitam.bankloanapplication.model.dto.LoanLimitWsDto;

public interface LoanLimitService {

    LoanLimitWsDto editLoanLimit(LoanLimitDto request);
}
