package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.model.dto.LoanDetailsDto;
import com.avitam.bankloanapplication.model.dto.LoanDetailsWsDto;
import com.avitam.bankloanapplication.model.dto.LoanEmiDetailDto;
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
import java.util.Date;
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
                totalAmountCalculation(loanDetails);
                loanDetailsRepository.save(loanDetails);
                modelMapper.map(loanDetailsDto, loanDetails);

                request.setMessage("Data updated successfully");
            } else {
                loanDetails = modelMapper.map(loanDetailsDto, LoanDetails.class);
                loanDetails.setCreationTime(new Date());
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
            Loan loan = loanRepository.findByRecordId(loanDetails.getLoanId());
            loan.setLoanEmiDetailDtoList(loanDetails.getLoanDetailsDtoList());
            loanRepository.save(loan);
            loanDetailsList.add(loanDetails);
        }

        Type listType = new TypeToken<List<LoanDetailsDto>>() {
        }.getType();
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
        LocalDate sanctionDate = loan.getSanctionDate();

        double installment = loan.getDesiredLoan() / loan.getTenure();
        double interestRate = loan.getInterestRate();
        double interestAmount;
        double emi;


        List<LoanEmiDetailDto> loanDetailsDtoList = new ArrayList<>();
        List<LocalDate> duedatesList = new ArrayList<>();

        LocalDate baseDate = sanctionDate.withDayOfMonth(5);
        LocalDate currentDate = LocalDate.now();
        //currentDate=currentDate.plusMonths(2);
        int noOfMonths = (int) ChronoUnit.MONTHS.between(baseDate, currentDate);

        if (sanctionDate.getDayOfMonth() > 5) {
            baseDate = baseDate.plusMonths(1);
        } else {
            baseDate = baseDate.plusMonths(0);
        }


        for (int i = 0; i < loan.getTenure(); i++) {
            LoanEmiDetailDto detail = new LoanEmiDetailDto();

            interestAmount = totalLoanAmount * interestRate / 100;
            emi = installment + interestAmount;
            totalLoanAmount = totalLoanAmount - installment;
            LocalDate dueDate = baseDate.plusMonths(i);

            // detail.setLoanId(loan.getRecordId());
            //detail.setLoanAmount(roundToTwoDecimal(totalLoanAmount));
            detail.setInstalment(roundToTwoDecimal(installment));
            detail.setInterestAmount(roundToTwoDecimal(interestAmount));
            detail.setTotalPayable(roundToTwoDecimal(emi));
            detail.setPenalty(0);
            // detail.setInterestRate(interestRate);
            detail.setPaymentStatus("Unpaid");
            // detail.setLoanSanctionedDate(loan.getSanctionDate());
            detail.setDueDate(dueDate);

            loanDetailsDtoList.add(detail);
        }

        loanDetails.setLoanDetailsDtoList(loanDetailsDtoList);
        return loanDetails;
    }

    public LoanDetails totalAmountCalculation(LoanDetails loanDetails) {
        double totalInterestAmount = 0.0;
        double totalInstalmentAmount = 0.0;
        double totalPayableAmount = 0.0;

        for (LoanEmiDetailDto loanDetailDto : loanDetails.getLoanDetailsDtoList()) {
            totalInterestAmount += loanDetailDto.getInterestAmount();
            totalInstalmentAmount += loanDetailDto.getInstalment();
            totalPayableAmount += loanDetailDto.getTotalPayable();
        }

        loanDetails.setTotalInterestAmount(roundToTwoDecimal(totalInterestAmount));
        loanDetails.setTotalInstalmentAmount(roundToTwoDecimal(totalInstalmentAmount));
        loanDetails.setTotalPayableAmount(roundToTwoDecimal(totalPayableAmount));

        return loanDetails;
    }

    private double roundToTwoDecimal(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

//    @Override
//    public List<LoanEmiSummaryDto> getLoanEmiSummary(String loanDetailsRecordId) {
//        LoanDetails loanDetails = loanDetailsRepository.findByRecordId(loanDetailsRecordId);
//
//        if (loanDetails == null || loanDetails.getLoanEmiDetailList() == null) {
//            return new ArrayList<>();
//        }
//
//        return loanDetails.getLoanEmiDetailList().stream().map(detail -> {
//            LoanEmiSummaryDto dto = new LoanEmiSummaryDto();
//            dto.setTotalPayable(detail.getTotalPayable());
//            //dto.setDueDate(detail.getDueDate());
//            dto.setPaymentStatus(detail.getPaymentStatus());
//            return dto;
//        }).collect(Collectors.toList());
//    }

//    @Override
//    public LoanDetailsWsDto getUptoDateEmiDetails() {
//        return null;
//    }

//    @Override
//    public LoanDetailsDto getEmiStatusTillDate(LoanDetailsDto loanDetailsDto) {
//
//        double loanAmount = 0.0;
//        double installment = 0.0;
//        double interestRate = 0.0;
//        double interestAmount;
//        double emi;
//        LocalDate sanctionDate ;
//        Loan loan = loanRepository.findByRecordId(loanDetailsDto.getLoanId());
//        LoanDetails loanDetails=new LoanDetails();
//        List<LoanEmiDetail> loanEmiDetailList= new ArrayList<LoanEmiDetail>();
//
//        if (loanDetailsDto.getRecordId() == null) {
//
//            loanAmount = loan.getDesiredLoan();
//            installment = loan.getDesiredLoan() / loan.getTenure();
//            interestRate = loan.getInterestRate();
//            sanctionDate = loan.getSanctionDate();
//        } else {
//            loanDetails = loanDetailsRepository.findByRecordId(loanDetailsDto.getRecordId());
//            loanAmount = loanDetails.getLoanAmount();
//            installment = loanDetails.getInstalment();
//            interestRate = loanDetails.getInterestRate();
//            sanctionDate = loanDetails.getLoanSanctionedDate();
//            loanEmiDetailList = loanDetails.getLoanEmiDetailList();
//        }
//        LocalDate baseDate = sanctionDate.withDayOfMonth(5);
//        LocalDate currentDate = LocalDate.now();
//        currentDate=currentDate.plusMonths(2);
//        int noOfMonths = (int) ChronoUnit.MONTHS.between(baseDate, currentDate);
//
//        if (sanctionDate.getDayOfMonth() > 5) {
//            baseDate = baseDate.plusMonths(1);
//        } else {
//            baseDate = baseDate.plusMonths(0);
//        }
//
//        for (int i =0; i < loan.getTenure(); i++) {
//        LoanEmiDetail detail = new LoanEmiDetail();
//        interestAmount = loanAmount * interestRate / 100;
//        emi = installment + interestAmount;
//        loanAmount = loanAmount - installment;
//        LocalDate dueDate = baseDate.plusMonths(noOfMonths-1);
//
//       // detail.setLoanId(loanDetailsDto.getLoanId());
//      //  detail.setLoanAmount(roundToTwoDecimal(loanAmount));
//        detail.setInstalment(roundToTwoDecimal(installment));
//        detail.setInterestRate(interestRate);
//        detail.setInterestAmount(roundToTwoDecimal(interestAmount));
//        detail.setTotalPayable(roundToTwoDecimal(emi));
//      //  detail.setDueDate(dueDate);
//
//        if (dueDate == currentDate) {
//            detail.setTotalPayable(roundToTwoDecimal(emi));
//            detail.setPenalty(0);
//            //detail.setPaymentStatus("Paid");
//        } else {
//            int noOfDays = (int) ChronoUnit.DAYS.between(dueDate, currentDate);
//            detail.setTotalPayable(roundToTwoDecimal(emi) + (roundToTwoDecimal(emi) * 0.04 * noOfDays));
//            detail.setPenalty(roundToTwoDecimal(emi) * 0.04 * noOfDays);
//            //detail.setPaymentStatus("Paid");
//        }
//
//        loanEmiDetailList.add(detail);
//        loanDetails.setLoanAmount(loanAmount);
//        loanDetails.setInstalment(detail.getInstalment());
//        loanDetails.setInterestAmount(detail.getInterestAmount());
//        loanDetails.setInterestRate(detail.getInterestRate());
//        loanDetails.setTotalPayable(detail.getTotalPayable());
//        loanDetails.setLoanSanctionedDate(loan.getSanctionDate());
//        loanDetails.setDueDate(dueDate);
//        }
//        loanDetails.setLoanEmiDetailList(loanEmiDetailList);
//        loanDetails.setLoanId(loanDetailsDto.getLoanId());
//
//        totalAmountCalculation(loanDetails);
//        //Type listType = new TypeToken<LoanDetails>() {}.getType();
//        // LoanDetails loanDetails=modelMapper.map(detail, LoanDetails.class);
//        loanDetailsRepository.save(loanDetails);
//        if (loanDetailsDto.getRecordId() == null) {
//            loanDetails.setRecordId(String.valueOf(loanDetails.getId().getTimestamp()));
//        }
//        loanDetailsRepository.save(loanDetails);
//        modelMapper.map(loanDetails, loanDetailsDto);
//        return loanDetailsDto;
//    }


    //        loanDetails.setTotalPayable(roundToTwoDecimal(emi));
//        loanDetails.setInterestAmount(roundToTwoDecimal(interestAmount));
//        loanDetails.setInstalment(roundToTwoDecimal(installment));
//        loanDetails.setLoanAmount(roundToTwoDecimal(totalLoanAmount));

}