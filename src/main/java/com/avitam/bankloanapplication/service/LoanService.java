package com.avitam.bankloanapplication.service;

import com.avitam.bankloanapplication.model.dto.LoanEmiSummaryDto;
import com.avitam.bankloanapplication.model.dto.LoanWsDto;

import java.util.List;

public interface LoanService {

    LoanWsDto createLoan(LoanWsDto request);



}
