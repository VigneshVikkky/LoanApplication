package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.model.dto.LoanDetailsDto;
import com.avitam.bankloanapplication.model.dto.LoanDetailsWsDto;
import com.avitam.bankloanapplication.model.entity.Loan;
import com.avitam.bankloanapplication.model.entity.LoanDetails;
import com.avitam.bankloanapplication.model.entity.LoanLimit;
import com.avitam.bankloanapplication.repository.LoanDetailsRepository;
import com.avitam.bankloanapplication.repository.LoanLimitRepository;
import com.avitam.bankloanapplication.repository.LoanRepository;
import com.avitam.bankloanapplication.service.LoanDetailsService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
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
    private LoanRepository loanRepository;

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

                List<LoanDetails> loanDetailsList1 = loanDetails.getLoanDetailsList();
                double totalInterestAmount = 0.0;
                double totalInstalmentAmount = 0.0;
                double totalPayableAmount = 0.0;
                for (LoanDetails loanDetails1 : loanDetailsList1) {
                    totalInterestAmount += loanDetails1.getInterestAmount();
                    totalInstalmentAmount += loanDetails1.getInstalment();
                    totalPayableAmount += loanDetails1.getTotalPayable();
                }

                loanDetails.setTotalInterestAmount(roundToTwoDecimal(totalInterestAmount));
                loanDetails.setTotalInstalmentAmount(roundToTwoDecimal(totalInstalmentAmount));
                loanDetails.setTotalPayableAmount(roundToTwoDecimal(totalPayableAmount));

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

        Type listType = new TypeToken<List<LoanDetailsDto>>() {}.getType();
        List<LoanDetailsDto> dtoList = modelMapper.map(loanDetailsList, listType);
        request.setLoanDetailsDtos(dtoList);

        return request;
    }

    public LoanDetails calculateLoanDetails(LoanDetails loanDetails) {

        List<LoanDetails> loanDetailsList = new ArrayList<>();
        //LoanLimit loanLimit = loanLimitRepository.findByRecordId(loanDetails.getLoanLimitId());

        Loan loan = loanRepository.findByRecordId(loanDetails.getLoanId());

        double totalLoanAmount = loan.getDesiredLoan();
        double installment = totalLoanAmount / (loan.getTenure());
        double interestRate = (loan.getInterestRate());
        double interestAmount;
        double emi;

        for (int i = 1; i <= loan.getTenure(); i++) {
            LoanDetails loanDetails1 = new LoanDetails();
            loanDetails.setLoanAmount(totalLoanAmount);
            interestAmount = totalLoanAmount * interestRate/100;
            emi = installment + interestAmount;
            totalLoanAmount = totalLoanAmount - installment;

            loanDetails1.setInterestAmount(roundToTwoDecimal(interestAmount));
            loanDetails1.setInstalment(roundToTwoDecimal(installment));
            loanDetails1.setTotalPayable(roundToTwoDecimal(emi));

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
            totalInterestAmount += loanDetails1.getInterestAmount();
            totalInstalmentAmount += loanDetails1.getInstalment();
            totalPayableAmount += loanDetails1.getTotalPayable();
        }

        loanDetails.setTotalInterestAmount(roundToTwoDecimal(totalInterestAmount));
        loanDetails.setTotalInstalmentAmount(roundToTwoDecimal(totalInstalmentAmount));
        loanDetails.setTotalPayableAmount(roundToTwoDecimal(totalPayableAmount));

        return loanDetails;
    }

    private double roundToTwoDecimal(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
