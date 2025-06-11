package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.model.dto.LoanEmiDetailDto;
import com.avitam.bankloanapplication.model.dto.PaymentDetailsDto;
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
        List<PaymentHistoryDto> paymentHistoryDtos = new ArrayList<>();

        List<PaymentHistory> paymentHistoryList = new ArrayList<>();
        for (PaymentHistoryDto paymentHistoryDto : paymentHistoryDtoList) {
            boolean exists = paymentHistoryRepository.existsByLoan_RecordId(paymentHistoryDto.getLoan().getRecordId());
            //if (paymentHistoryDto.getRecordId() != null) {
                if (exists) {
                //paymentHistory = paymentHistoryRepository.findByRecordId(paymentHistoryDto.getRecordId());
                paymentHistory = paymentHistoryRepository.findByLoan_RecordId(paymentHistoryDto.getLoan().getRecordId());
                modelMapper.map(paymentHistoryDto, paymentHistory);
                getLoanByLoanId(paymentHistoryDto);
                paymentHistoryRepository.save(paymentHistory);
                request.setMessage("Data updated successfully");
            } else {
                paymentHistory = modelMapper.map(paymentHistoryDto, PaymentHistory.class);
                paymentHistory.setStatus(true);
                paymentHistory.setCreationTime(new Date());
                modelMapper.map(paymentHistoryDto, paymentHistory);
                getLoanByLoanId(paymentHistoryDto);
                paymentHistoryRepository.save(paymentHistory);
            }
            if (request.getRecordId() == null) {
                paymentHistory.setRecordId(String.valueOf(paymentHistory.getId().getTimestamp()));
            }

            paymentHistoryDtos.add(paymentHistoryDto);
            modelMapper.map(paymentHistoryDto, paymentHistory);
            paymentHistoryRepository.save(paymentHistory);
            paymentHistoryList.add(paymentHistory);
            paymentHistoryDto.setBaseUrl(ADMIN_PAYMENTHISTORY);

            request.setMessage("Data added Successfully");
        }
        request.setPaymentHistoryDtoList(modelMapper.map(paymentHistoryList, List.class));

        return request;
    }

    public void getLoanByLoanId(PaymentHistoryDto paymentHistoryDto) {

        Loan loan = loanRepository.findByRecordId(paymentHistoryDto.getLoan().getRecordId());
        List<PaymentDetailsDto> paymentDetailsDtoList;
//        if(paymentHistoryDto.getRecordId()== null){
//            paymentDetailsDtoList=new ArrayList<>();
//        }
//        else{
//            PaymentHistory paymentHistory = paymentHistoryRepository.findByRecordId(paymentHistoryDto.getRecordId());
//            paymentDetailsDtoList = paymentHistory.getPaymentDetailsDtoList();
//        }

        if(paymentHistoryRepository.findByLoan_RecordId(paymentHistoryDto.getLoan().getRecordId()) == null){
            paymentDetailsDtoList=new ArrayList<>();
        }
        else{
            PaymentHistory paymentHistory = paymentHistoryRepository.findByLoan_RecordId(paymentHistoryDto.getLoan().getRecordId());
            paymentDetailsDtoList = paymentHistory.getPaymentDetailsDtoList();
        }
        PaymentDetailsDto paymentDetailsDto1 = new PaymentDetailsDto();
        for(PaymentDetailsDto paymentDetailsDto : paymentHistoryDto.getPaymentDetailsDtoList()){
        for (LoanEmiDetailDto loanEmiDetailDto:loan.getLoanEmiDetailDtoList()) {
            int month = Integer.parseInt(loanEmiDetailDto.getRecordId());
            if (paymentDetailsDto.getMonthlyIndex() == month) {
                paymentDetailsDto1.setLoanEmiDetailDto(loanEmiDetailDto);
                paymentDetailsDto1.setPaidStatus(paymentDetailsDto.getPaidStatus());
                paymentDetailsDto1.setMonthlyIndex(paymentDetailsDto.getMonthlyIndex());
                paymentDetailsDto1.setTransactionId(paymentDetailsDto.getTransactionId());
                break;
            }
        }
            paymentDetailsDtoList.add(paymentDetailsDto1);
        }
        paymentHistoryDto.setPaymentDetailsDtoList(paymentDetailsDtoList);

    }


}
