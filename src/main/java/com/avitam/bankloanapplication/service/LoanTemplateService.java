package com.avitam.bankloanapplication.service;

import com.avitam.bankloanapplication.model.dto.LoanTemplateWsDto;

public interface LoanTemplateService {
    LoanTemplateWsDto createLoan(LoanTemplateWsDto request);
}


