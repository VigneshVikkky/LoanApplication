package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.model.dto.LoanEmiDetailDto;
import com.avitam.bankloanapplication.model.dto.PaymentHistoryDto;
import com.avitam.bankloanapplication.model.dto.PaymentHistoryWsDto;
import com.avitam.bankloanapplication.model.entity.Loan;
import com.avitam.bankloanapplication.model.entity.PaymentHistory;
import com.avitam.bankloanapplication.repository.LoanRepository;
import com.avitam.bankloanapplication.repository.PaymentHistoryRepository;
import com.avitam.bankloanapplication.service.PaymentHistoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class PaymentHistoryServiceImpl implements PaymentHistoryService {

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;


    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private LoanRepository loanRepository;


    public static final String ADMIN_PAYMENTHISTORY = "/loans/paymentHistory";

    @Override
    public PaymentHistoryWsDto createPaymentHistory(PaymentHistoryWsDto request) {
        PaymentHistory paymentHistory = new PaymentHistory();
        List<PaymentHistoryDto> paymentHistoryDtoList = request.getPaymentHistoryDtoList();
        List<PaymentHistory> paymentHistoryList = new ArrayList<>();
        for (PaymentHistoryDto paymentHistoryDto : paymentHistoryDtoList) {
            if (paymentHistoryDto.getRecordId() != null) {
                paymentHistory = paymentHistoryRepository.findByRecordId(paymentHistoryDto.getRecordId());
                modelMapper.map(paymentHistoryDto, paymentHistory);
                paymentHistoryRepository.save(paymentHistory);
                request.setMessage("Data updated successfully");
            } else {
                paymentHistory = modelMapper.map(paymentHistoryDto, PaymentHistory.class);
                paymentHistory.setStatus(true);
                paymentHistory.setCreationTime(new Date());
                modelMapper.map(paymentHistoryDto, paymentHistory);
                paymentHistoryRepository.save(paymentHistory);
            }
            if (request.getRecordId() == null) {
                paymentHistory.setRecordId(String.valueOf(paymentHistory.getId().getTimestamp()));
            }
            //getLoanType(loan);
            //getCustomer(loan);
            // checkPaymentHistoryStatus(paymentHistory);
            paymentHistoryRepository.save(paymentHistory);
            paymentHistoryList.add(paymentHistory);
            paymentHistoryDto.setBaseUrl(ADMIN_PAYMENTHISTORY);

            request.setMessage("Data added Successfully");
        }
        request.setPaymentHistoryDtoList(modelMapper.map(paymentHistoryList, List.class));

        return request;
    }


    public void getLoanByLoanId(PaymentHistoryDto paymentHistoryDto, int monthlyIndex) {
        Loan loan = loanRepository.findByRecordId(paymentHistoryDto.getLoanDto().getRecordId());
        for (LoanEmiDetailDto loanEmiDetailDto:loan.getLoanEmiDetailDtoList()){
            int month = Integer.parseInt(loanEmiDetailDto.getRecordId());
            if (monthlyIndex==month){


            }
        }

    }
}
