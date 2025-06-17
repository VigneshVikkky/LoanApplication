package com.avitam.bankloanapplication.service;

import com.avitam.bankloanapplication.model.dto.PaymentHistoryWsDto;

public interface PaymentHistoryService {
    PaymentHistoryWsDto createPaymentHistory(PaymentHistoryWsDto request);

}
