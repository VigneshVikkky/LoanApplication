package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.model.dto.LoanDetailsDto;
import com.avitam.bankloanapplication.model.dto.LoanDetailsWsDto;
import com.avitam.bankloanapplication.model.dto.LoanEmiSummaryDto;
import com.avitam.bankloanapplication.model.entity.Loan;
import com.avitam.bankloanapplication.model.entity.LoanDetails;
import com.avitam.bankloanapplication.repository.LoanDetailsRepository;
import com.avitam.bankloanapplication.repository.LoanRepository;
import com.avitam.bankloanapplication.service.LoanDetailsService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDate;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanDetailsServiceImpl implements LoanDetailsService {

    @Autowired
    private LoanDetailsRepository loanDetailsRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ModelMapper modelMapper;

    public static final String ADMIN_LOANDETAILS = "/loans/loanDetails";

    @Override
    public LoanDetailsWsDto createLoan(LoanDetailsWsDto request) {
        List<LoanDetailsDto> loanDetailsDtos = request.getLoanDetailsDtos();
        List<LoanDetails> loanDetailsList = new ArrayList<>();
        for (LoanDetailsDto loanDetailsDto : loanDetailsDtos) {
            LoanDetails loanDetails;
            if (loanDetailsDto.getRecordId() != null) {
                loanDetails = loanDetailsRepository.findByRecordId(loanDetailsDto.getRecordId());
                calculateLoanDetails(loanDetails);
                modelMapper.map(loanDetailsDto, loanDetails);
                loanDetailsRepository.save(loanDetails);
                request.setMessage("Data updated successfully");
            } else {
                loanDetails = modelMapper.map(loanDetailsDto, LoanDetails.class);
                loanDetails.setCreationTime(new java.util.Date());
                loanDetails.setStatus(true);
                calculateLoanDetails(loanDetails);
                totalAmountCalculation(loanDetails);
                loanDetailsRepository.save(loanDetails);
                request.setMessage("Data added successfully");
            }

            if (request.getRecordId() == null) {
                loanDetails.setRecordId(String.valueOf(loanDetails.getId().getTimestamp()));
            }
            loanDetailsRepository.save(loanDetails);
            loanDetailsList.add(loanDetails);
        }

        Type listType = new TypeToken<List<LoanDetailsDto>>() {}.getType();
        List<LoanDetailsDto> dtoList = modelMapper.map(loanDetailsList, listType);
        request.setLoanDetailsDtos(dtoList);
        // List<LoanDetailsDto> dtoList = modelMapper.map(loanDetailsList, new org.modelmapper.TypeToken<List<LoanDetailsDto>>() {}.getType());
        //request.setLoanDetailsDtos(dtoList);
        request.setBaseUrl(ADMIN_LOANDETAILS);

        return request;
    }

    public LoanDetails calculateLoanDetails(LoanDetails loanDetails) {
        Loan loan = loanRepository.findByRecordId(loanDetails.getLoanId());

        double totalLoanAmount = 0.0;
        totalLoanAmount = loan.getDesiredLoan();

        double installment = loan.getDesiredLoan() / loan.getTenure();
        double interestRate = loan.getInterestRate();
        double interestAmount;
        double emi;


        List<LoanDetails> loanDetailsList = new ArrayList<>();
        List<LocalDate> duedatesList = new ArrayList<>();

        for (int i = 0; i < loan.getTenure(); i++) {
            LoanDetails detail = new LoanDetails();

            interestAmount = totalLoanAmount * interestRate / 100;
            emi = installment + interestAmount;
            totalLoanAmount = totalLoanAmount - installment;

            detail.setLoanId(loan.getRecordId());
            detail.setLoanAmount(roundToTwoDecimal(totalLoanAmount));
            detail.setInstalment(roundToTwoDecimal(installment));
            detail.setInterestAmount(roundToTwoDecimal(interestAmount));
            detail.setTotalPayable(roundToTwoDecimal(emi));
            detail.setInterestRate(interestRate);
            detail.setLoanSanctionedDate(loan.getSanctionDate());
            //detail.setDueDate(dueDate);

            loanDetailsList.add(detail);
        }

        loanDetails.setLoanDetailsList(loanDetailsList);
        return loanDetails;
    }


    @Override
    public List<LoanEmiSummaryDto> getLoanEmiSummary(String loanDetailsRecordId) {
        LoanDetails loanDetails = loanDetailsRepository.findByRecordId(loanDetailsRecordId);

        if (loanDetails == null || loanDetails.getLoanDetailsList() == null) {
            return new ArrayList<>();
        }

        return loanDetails.getLoanDetailsList().stream().map(detail -> {
            LoanEmiSummaryDto dto = new LoanEmiSummaryDto();
            dto.setTotalPayable(detail.getTotalPayable());
            //dto.setDueDate(detail.getDueDate());
            dto.setPaymentStatus(detail.getPaymentStatus());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public LoanDetailsWsDto getUptoDateEmiDetails() {
        return null;
    }

    @Override
    public LoanDetailsDto getEmiStatusTillDate(LoanDetailsDto loanDetailsDto) {

        double loanAmount = 0.0;
        double installment = 0.0;
        double interestRate = 0.0;
        double interestAmount;
        double emi;
        LocalDate sanctionDate = null;
        Loan loan = null;
        //LoanDetails loanDetails = null;

       // if (loanDetailsDto.getRecordId() == null) {
            loan = loanRepository.findByRecordId(loanDetailsDto.getLoanId());
            loanAmount = loan.getDesiredLoan();
            installment = loan.getDesiredLoan() / loan.getTenure();
            interestRate = loan.getInterestRate();
            sanctionDate = loan.getSanctionDate();
//            loanDetails = modelMapper.map(loanDetailsDto, LoanDetails.class);
//            calculateLoanDetails(loanDetails);
//        } else {
//            loanDetails = loanDetailsRepository.findByLoanId(loanDetailsDto.getLoanId());
//            loanAmount = loanDetails.getLoanAmount();
//            installment = loanDetails.getInstalment();
//            interestRate = loanDetails.getInterestRate();
//            sanctionDate = loanDetails.getLoanSanctionedDate();
//        }
        LocalDate baseDate = sanctionDate.withDayOfMonth(5);
        LocalDate currentDate = LocalDate.now();
        int noOfMonths = (int) ChronoUnit.MONTHS.between(baseDate, currentDate);

        if (sanctionDate.getDayOfMonth() > 5) {
            baseDate = baseDate.plusMonths(1);
        } else {
            baseDate = baseDate.plusMonths(0);
        }

        List<LoanDetails> loanDetailsList = new ArrayList<>();
        LoanDetails detail = new LoanDetails();

        for (int i = noOfMonths-1; i < noOfMonths; i++) {
            interestAmount = loanAmount * interestRate / 100;
            emi = installment + interestAmount;
            loanAmount = loanAmount - installment;
            LocalDate dueDate = baseDate.plusMonths(noOfMonths-1);

            detail.setLoanId(loan.getRecordId());
            detail.setLoanAmount(roundToTwoDecimal(loanAmount));
            detail.setInstalment(roundToTwoDecimal(installment));
            detail.setInterestAmount(roundToTwoDecimal(interestAmount));
            detail.setTotalPayable(roundToTwoDecimal(emi));
            detail.setDueDate(dueDate);

            if (baseDate == currentDate) {
                detail.setTotalPayable(roundToTwoDecimal(emi));
                detail.setPaymentStatus("Paid");
            } else {
                int noOfDays = (int) ChronoUnit.DAYS.between(dueDate, currentDate);
                detail.setTotalPayable(roundToTwoDecimal(emi) + (roundToTwoDecimal(emi) * 0.04 * noOfDays));
                detail.setPenalty(roundToTwoDecimal(emi) * 0.04 * noOfDays);
                detail.setPaymentStatus("Paid");
            }

            loanDetailsList.add(detail);
        }
        detail.setLoanDetailsList(loanDetailsList);

        //Type listType = new TypeToken<LoanDetails>() {}.getType();
        //loanDetails=modelMapper.map(loanDetailsDto, listType);
        totalAmountCalculation(detail);
        loanDetailsRepository.save(detail);
        if (loanDetailsDto.getRecordId() == null) {
            detail.setRecordId(String.valueOf(detail.getId().getTimestamp()));
        }
        loanDetailsRepository.save(detail);
        modelMapper.map(detail, loanDetailsDto);
        return loanDetailsDto;
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

    //        loanDetails.setTotalPayable(roundToTwoDecimal(emi));
//        loanDetails.setInterestAmount(roundToTwoDecimal(interestAmount));
//        loanDetails.setInstalment(roundToTwoDecimal(installment));
//        loanDetails.setLoanAmount(roundToTwoDecimal(totalLoanAmount));

}
