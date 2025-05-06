package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.model.dto.LoanDetailsDto;
import com.avitam.bankloanapplication.model.dto.LoanDetailsWsDto;
import com.avitam.bankloanapplication.model.entity.LoanDetails;
import com.avitam.bankloanapplication.model.entity.LoanLimit;
import com.avitam.bankloanapplication.repository.LoanDetailsRepository;
import com.avitam.bankloanapplication.repository.LoanLimitRepository;
import com.avitam.bankloanapplication.service.LoanDetailsService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LoanDetailsServiceImpl implements LoanDetailsService {
    @Autowired
    private LoanDetailsRepository loanDetailsRepository;

    @Autowired
    private LoanLimitRepository loanLimitRepository;

    @Autowired
    private ModelMapper modelMapper;

    public static final String ADMIN_LOANDETAILS = "/loans/loanDetails";

    @Override
    public LoanDetailsWsDto createLoan(LoanDetailsWsDto request) {
        LoanDetailsWsDto loanDetailsWsDto = new LoanDetailsWsDto();
        LoanDetails loanDetails = new LoanDetails();
        List<LoanDetailsDto> loanDetailsDtos = request.getLoanDetailsDtos();
        List<LoanDetails> loanDetailsList = new ArrayList<>();
        for (LoanDetailsDto loanDetailsDto : loanDetailsDtos) {
            if (loanDetailsDto.getRecordId() != null) {
                loanDetails = loanDetailsRepository.findByRecordId(loanDetailsDto.getRecordId());
                modelMapper.map(loanDetailsDto, loanDetails);
                loanDetailsRepository.save(loanDetails);
                request.setMessage("Data updated successfully");
            } else {
                loanDetails = modelMapper.map(loanDetailsDto, LoanDetails.class);
                loanDetails.setCreationTime(new Date());
                loanDetails.setStatus(true);
                calculateLoanDetails(loanDetails);
                //loanDetails.setLoanDetailsList(calculateLoanDetails(loanDetails));

                List<LoanDetails> loanDetailsList1 = loanDetails.getLoanDetailsList();
                double totalInterestAmount = 0.0;
                double totalInstalmentAmount = 0.0;
                double totalPayableAmount = 0.0;
                for (LoanDetails loanDetails1 : loanDetailsList1) {

                    totalInterestAmount = totalInterestAmount + loanDetails1.getInterestAmount();
                    loanDetails.setTotalInterestAmount(totalInterestAmount);
                    totalInstalmentAmount = totalInstalmentAmount + loanDetails1.getInstalment();
                    loanDetails.setTotalInstalmentAmount(totalInstalmentAmount);
                    totalPayableAmount = totalPayableAmount + loanDetails1.getTotalPayable();
                    loanDetails.setTotalPayableAmount(totalPayableAmount);
                }

                //totalAmountCalculation(loanDetails);
                loanDetailsRepository.save(loanDetails);
            }
            if (request.getRecordId() == null) {
                loanDetails.setRecordId(String.valueOf(loanDetails.getId().getTimestamp()));
            }
            loanDetailsRepository.save(loanDetails);
            loanDetailsList.add(loanDetails);
            request.setBaseUrl(ADMIN_LOANDETAILS);
            request.setMessage("Data added Successfully");
        }
       // request.setLoanDetailsDtos(modelMapper.map(loanDetails, List.class));
        Type listType = new TypeToken<List<LoanDetailsDto>>() {}.getType();
        List<LoanDetailsDto> dtoList = modelMapper.map(loanDetailsList, listType);
        request.setLoanDetailsDtos(dtoList);

        return request;

    }

    public LoanDetails calculateLoanDetails(LoanDetails loanDetails) {

        List<LoanDetails> loanDetailsList = new ArrayList<>();
        //LoanDetails loanDetails1 = new LoanDetails();
        LoanLimit loanLimit = loanLimitRepository.findByRecordId(loanDetails.getLoanLimitId());

        double totalLoanAmount = loanLimit.getLoanLimitAmount();
        double installment = totalLoanAmount / (loanLimit.getTenure()*12);
        double interestRate = (loanLimit.getInterestRate()/100) / 12;
        double interestAmount;
        double emi;
        int i;

        for (i = 1; i <= loanLimit.getTenure()*12; i++) {

            LoanDetails loanDetails1 = new LoanDetails();
            loanDetails.setLoanAmount(totalLoanAmount);
            interestAmount = totalLoanAmount * interestRate;
            emi = installment + interestAmount;
            totalLoanAmount = totalLoanAmount - installment;
            loanDetails1.setInterestAmount(interestAmount);
           // loanDetails1.setInterestRate(interestRate);
            loanDetails1.setInstalment(installment);
            loanDetails1.setInterestAmount(interestAmount);
            loanDetails1.setTotalPayable(emi);
            loanDetailsList.add(loanDetails1);
        }
        loanDetails.setLoanDetailsList(loanDetailsList);
        return loanDetails;
    }


    public LoanDetails totalAmountCalculation(LoanDetails loanDetails) {

        double totalInterestAmount = 0.0;
        double totalInstalmentAmount = 0.0;
        double totalPayableAmount = 0.0;
        for (LoanDetails loanDetails1 : loanDetails.getLoanDetailsList()) {

            totalInterestAmount = totalInterestAmount + loanDetails1.getInterestAmount();
            loanDetails.setTotalInterestAmount(totalInterestAmount);
            totalInstalmentAmount = totalInstalmentAmount + loanDetails1.getInstalment();
            loanDetails.setTotalInstalmentAmount(totalInstalmentAmount);
            totalPayableAmount = totalPayableAmount + loanDetails1.getTotalPayable();
            loanDetails.setTotalPayableAmount(totalPayableAmount);
        }


        return loanDetails;
    }


}
