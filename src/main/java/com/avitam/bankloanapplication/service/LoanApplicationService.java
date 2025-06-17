package com.avitam.bankloanapplication.service;

import com.avitam.bankloanapplication.model.dto.LoanApplicationWsDto;

public interface LoanApplicationService {

    LoanApplicationWsDto handleEdit(LoanApplicationWsDto request);

    // LoanApplicationDto getEmiStatusTillDate(LoanApplicationDto loanApplicationDto);
}
