package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.model.dto.*;
import com.avitam.bankloanapplication.model.entity.Customer;
import com.avitam.bankloanapplication.model.entity.LoanType;
import com.avitam.bankloanapplication.repository.*;

import com.avitam.bankloanapplication.model.entity.Loan;
import com.avitam.bankloanapplication.service.LoanService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {


    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanDetailsRepository loanDetailsRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private LoanTypeRepository loanTypeRepository;

    @Autowired
    private LoanScoreResultRepository loanScoreResultRepository;

    public static final String ADMIN_lOAN = "/loans/loan";

    public LoanWsDto createLoan(LoanWsDto request) {
        Loan loan = new Loan();
        List<LoanDto> loanDtos = request.getLoanDtoList();
        List<Loan> loans = new ArrayList<>();
        for (LoanDto loanDto : loanDtos) {
            if (loanDto.getRecordId() != null) {
                loan = loanRepository.findByRecordId(loanDto.getRecordId());
                modelMapper.map(loanDto, loan);
                loanRepository.save(loan);
                request.setMessage("Data updated successfully");
            } else {
                loan = modelMapper.map(loanDto, Loan.class);
                loan.setStatus(true);
                loan.setCreationTime(new Date());
                modelMapper.map(loanDto, loan);
                loanRepository.save(loan);
            }
            if (request.getRecordId() == null) {
                loan.setRecordId(String.valueOf(loan.getId().getTimestamp()));
            }
            //getLoanType(loan);
            //getCustomer(loan);
            checkLoanStatus(loan);
            loanRepository.save(loan);
            loans.add(loan);
            loanDto.setBaseUrl(ADMIN_lOAN);

            request.setMessage("Data added Successfully");
        }
        request.setLoanDtoList(modelMapper.map(loans, List.class));

        return request;

    }


    @Override
    public LoanDto getEmiStatusTillDate(LoanDto loanDto) {
        Loan loan = loanRepository.findByRecordId(loanDto.getRecordId());
        LocalDate sanctionDate = loan.getSanctionDate();
        LocalDate currentDate = LocalDate.now();
        //LocalDate baseDate = currentDate.withDayOfMonth(5);
       LocalDate baseDate = sanctionDate.withDayOfMonth(5);
       currentDate = currentDate.plusMonths(2);
        //int noOfMonths = (int) ChronoUnit.MONTHS.between(baseDate, currentDate);
        currentDate = currentDate.plusDays(15);
        //int noOfMonths = (int) ChronoUnit.MONTHS.between(sanctionDate, currentDate);

        int noOfMonths = 0;
        for(LoanEmiDetailDto loanEmiDetailDto : loan.getLoanEmiDetailDtoList()){
            if(loanEmiDetailDto.getPaymentStatus().equalsIgnoreCase("Paid")){
                noOfMonths++;
            }
        }

        if (sanctionDate.getDayOfMonth() > 5) {
            baseDate = baseDate.plusMonths(noOfMonths+1);
        } else {
            baseDate = baseDate.plusMonths(0);
        }

        double totalInterestAmount=0.0;
        double totalInstalmentAmount=0.0;
        double totalPayableAmount=0.0;
        double totalPenalty=0.0;
        int loopCount=0;
        for (LoanEmiDetailDto loanEmiDetailDto : loan.getLoanEmiDetailDtoList()) {
            if (loanEmiDetailDto.getPaymentStatus().equalsIgnoreCase("Unpaid")) {

                loanEmiDetailDto.setDueDate(baseDate);

                double instalment = loanEmiDetailDto.getInstalment();
                double interest = loanEmiDetailDto.getInterestAmount();
                double baseAmount = instalment + interest;

                double penalty = 0.0;

                if (currentDate.isAfter(baseDate)) {
                    int daysLate = (int) ChronoUnit.DAYS.between(baseDate, currentDate);
                    penalty = roundToTwoDecimal(baseAmount * 0.05 * daysLate);
                }

                double totalPayable = roundToTwoDecimal(baseAmount + penalty);
                loanEmiDetailDto.setPenalty(penalty);
                loanEmiDetailDto.setTotalPayable(totalPayable);
                loopCount++;
                break;
            }
            loopCount++;
        }

            for(int i=0; i<loopCount-1;i++){
                LoanEmiDetailDto loanEmiDetailDto = loan.getLoanEmiDetailDtoList().get(i);
                totalPayableAmount=totalPayableAmount+loanEmiDetailDto.getTotalPayable();
                loan.setTotalPayableAmount(totalPayableAmount);
                totalInterestAmount=totalInterestAmount+loanEmiDetailDto.getInterestAmount();
                loan.setTotalInterestAmount(totalInterestAmount);
                totalInstalmentAmount=totalInstalmentAmount+loanEmiDetailDto.getInstalment();
                loan.setTotalInstalmentAmount(totalInstalmentAmount);
                totalPenalty=totalPenalty+loanEmiDetailDto.getPenalty();
                loan.setTotalPenalty(totalPenalty);
            }

        loan.setLoanEmiDetailDtoList(loan.getLoanEmiDetailDtoList());

//        for(LoanEmiDetailDto loanEmiDetailDto: loan.getLoanEmiDetailDtoList()) {
//            if(loanEmiDetailDto.getPaymentStatus().equalsIgnoreCase("Paid")) {
//                totalPayableAmount=totalPayableAmount+loanEmiDetailDto.getTotalPayable();
//                loan.setTotalPayableAmount(totalPayableAmount);
//                totalInterestAmount=totalInterestAmount+loanEmiDetailDto.getInterestAmount();
//                loan.setTotalInterestAmount(totalInterestAmount);
//                totalInstalmentAmount=totalInstalmentAmount+loanEmiDetailDto.getInstalment();
//                loan.setTotalInstalmentAmount(totalInstalmentAmount);
//                totalPenalty=totalPenalty+loanEmiDetailDto.getPenalty();
//                loan.setTotalPenalty(totalPenalty);
//            }
//        }
        loan.setPendingInstallmentAmount(loan.getDesiredLoan() - loan.getTotalInstalmentAmount());
        loanRepository.save(loan);
        modelMapper.map(loan, loanDto);
        return loanDto;
    }

    public LoanDto updatePaymentStatus(LoanDto loanDto) {

        Loan loan = loanRepository.findByRecordId(loanDto.getRecordId());

            for(LoanEmiDetailDto loanEmiDetail: loan.getLoanEmiDetailDtoList()) {
                LoanEmiDetailDto loanEmiDetailDto1 = loanDto.getLoanEmiDetailDtoList().get(0);
                if (loanEmiDetail.getPaymentStatus().equalsIgnoreCase("Unpaid")) {
                    if (loanEmiDetailDto1.getRecordId().equalsIgnoreCase(loanEmiDetail.getRecordId())) {
                        loanEmiDetail.setPaymentStatus(loanEmiDetailDto1.getPaymentStatus());
                        loanEmiDetail.setRecordId(loanEmiDetailDto1.getRecordId());
                        break;
                    }
                }
            }
            modelMapper.map(loan, loanDto);
            loanRepository.save(loan);

       return loanDto;
    }

//        for(int i=0; i<loan.getTenure(); i++) {
//            LoanEmiDetailDto loanEmiDetailDto = loan.getLoanEmiDetailDtoList().get(i);
//            for (int j = 0; j < loanDto.getLoanEmiDetailDtoList().size(); j++) {
//                LoanEmiDetailDto loanEmiDetailDto1 = loanDto.getLoanEmiDetailDtoList().get(j);
//                if(loanEmiDetailDto.getRecordId().equalsIgnoreCase(loanEmiDetailDto1.getRecordId())){
//                    loanEmiDetailDto.setPaymentStatus(loanEmiDetailDto1.getPaymentStatus());
//                }
//            }
//        }
//        for(LoanEmiDetailDto loanEmiDetailDto1:loanDto.getLoanEmiDetailDtoList()) {
//            for(LoanEmiDetailDto loanEmiDetailDto: loan.getLoanEmiDetailDtoList()) {
//                if (loanEmiDetailDto.getPaymentStatus().equalsIgnoreCase("Unpaid")) {
//                    if (loanEmiDetailDto1.getRecordId().equalsIgnoreCase(loanEmiDetailDto.getRecordId())) {
//                        loanEmiDetailDto.setPaymentStatus(loanEmiDetailDto1.getPaymentStatus());
//                        loanEmiDetailDto.setRecordId(loanEmiDetailDto1.getRecordId());
//                        loanEmiDetailDto1.setTotalPayable(loanEmiDetailDto.getTotalPayable());
//                        loanEmiDetailDto1.setInstalment(loanEmiDetailDto.getInstalment());
//                        loanEmiDetailDto1.setInterestAmount(loanEmiDetailDto.getInterestAmount());
//                        loanEmiDetailDto1.setPenalty(loanEmiDetailDto.getPenalty());
//                        loanEmiDetailDto1.setDueDate(loanEmiDetailDto.getDueDate());
//                        break;
//                    }
//                }
//            }
//        }

    //        for (int i = 0; i < loan.getTenure(); i++) {
//            LoanEmiDetailDto loanEmiDetailDto = loanDto.getLoanEmiDetailDtoList().get(0);
//            int j = Integer.parseInt(loanEmiDetailDto.getRecordId());
//            if (i == j) {
//                LoanEmiDetailDto loanEmiDetailDto1 = loan.getLoanEmiDetailDtoList().get(i);
//                loanEmiDetailDto1.setPaymentStatus(loanEmiDetailDto.getPaymentStatus());
//                loanEmiDetailDto1.setRecordId(loanEmiDetailDto.getRecordId());
//                break;
//            }
//            //loan.setLoanEmiDetailDtoList(loanEmiDetailDto1);
//        }

    //                        loanEmiDetailDto1.setTotalPayable(loanEmiDetail.getTotalPayable());
//                        loanEmiDetailDto1.setInstalment(loanEmiDetail.getInstalment());
//                        loanEmiDetailDto1.setInterestAmount(loanEmiDetail.getInterestAmount());
//                        loanEmiDetailDto1.setPenalty(loanEmiDetail.getPenalty());
//                        loanEmiDetailDto1.setDueDate(loanEmiDetail.getDueDate());

    public void checkLoanStatus(Loan loan){

        int paidCount=0;
        for(LoanEmiDetailDto loanEmiDetailDto : loan.getLoanEmiDetailDtoList()){
            if(loanEmiDetailDto.getPaymentStatus().equalsIgnoreCase("Paid")){
                paidCount++;
            }
        }
        if(loan.getTenure()==paidCount){
            loan.setLoanStatus("Completed");
        }
        else{
            loan.setLoanStatus("Active");
        }
    }

    public void getLoanType(Loan loan){

        LoanType loanType = loanTypeRepository.findByRecordId(loan.getLoanTypeDto().getRecordId());
        //loan.setLoanType(loanType.getRecordId());
        LoanTypeDto loanTypeDto = modelMapper.map(loanType, LoanTypeDto.class);
        loan.setLoanTypeDto(loanTypeDto);

    }

    public void getCustomer(Loan loan){

        Customer customer = customerRepository.findByRecordId(loan.getCustomerDto().getRecordId());
        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        loan.setCustomerDto(customerDto);
    }

    private double roundToTwoDecimal(double value) {
        return Math.round(value * 100.0) / 100.0;
    }


    @Override
    public LoanWsDto getTotalDesiredLoanByCustomerRecordId(LoanWsDto request) {
        LoanWsDto loanWsDto = new LoanWsDto();
        List<LoanDto> loanDtos = new ArrayList<>();
        for(LoanDto loanDto : request.getLoanDtoList()) {
            List<Loan> loans = loanRepository.findByCustomerIdAndLoanStatus(loanDto.getCustomerId(),"Active");
            double total = 0;
            for (Loan loan : loans) {
                total += loan.getDesiredLoan();
                    loanDto.setDesiredLoan(total);
            }
            loanDtos.add(loanDto);
        }
        loanWsDto.setLoanDtoList(loanDtos);
        return loanWsDto;
    }



//    //TODO: known issue:     "message": "Can not set java.util.Date field com.gulbalasalamov.bankloanapplication.model.entity.Loan.loanDate to java.lang.String",
//    public void updateLoanPartially(Long loanId, Map<Object, Object> objectMap) {
//        var loanById = findLoanById(loanId);
//        loanById.ifPresent(loan -> objectMap.forEach((key, value) -> {
//
//            Field field = ReflectionUtils.findField(Loan.class, (String) key);
//            field.setAccessible(true);
//            ReflectionUtils.setField(field, loan, value);
//            loanRepository.save(loan);
//        }));
//    }

//    public void updateLoan(Long loanId, Loan loan) {
//        var loanById = findLoanById(loanId);
//        loanById.ifPresent(updatedLoan -> {
//                    updatedLoan.setId(loan.getId());
//                    updatedLoan.setLoanType(loan.getLoanType());
//                    updatedLoan.setLoanLimit(loan.getLoanLimit());
//                    updatedLoan.setLoanDate(loan.getLoanDate());
//                    updatedLoan.setLoanStatus(loan.getLoanStatus());
//                    updatedLoan.setLoanScoreResult(loan.getLoanScoreResult());
//                    updatedLoan.setLoanApplication(loan.getLoanApplication());
//                    loanRepository.save(updatedLoan);
//                }
//        );
}




//        for (LoanEmiDetailDto loanEmiDetailDto : loan.getLoanEmiDetailDtoList()) {
//            if (loanEmiDetailDto.getPaymentStatus().equalsIgnoreCase("Unpaid")) {
//                if (currentDate.isAfter(baseDate)) {
//                    int noOfDays = (int) ChronoUnit.DAYS.between(baseDate, currentDate);
//                    double totalPayable = loanEmiDetailDto.getTotalPayable();
//                   // loanEmiDetailDto.setPenalty(0);
//                    //loanEmiDetailDto.setTotalPayable(loanEmiDetailDto.getInstalment()+loanEmiDetailDto.getInterestAmount());
//                    loanEmiDetailDto.setTotalPayable(roundToTwoDecimal(totalPayable + (loanEmiDetailDto.getTotalPayable() * 0.04 * noOfDays)));
//                    loanEmiDetailDto.setPenalty(roundToTwoDecimal(loanEmiDetailDto.getTotalPayable() - totalPayable));
//                    loanEmiDetailDto.setDueDate(baseDate);
//                    loan.setTotalPayableAmount(loanEmiDetailDto.getTotalPayable()+loan.getTotalPayableAmount());
//                    loan.setTotalInterestAmount(loanEmiDetailDto.getInterestAmount()+loan.getTotalInterestAmount());
//                    loan.setTotalInstalmentAmount(loanEmiDetailDto.getInstalment()+loan.getTotalInstalmentAmount());
//                    loan.setTotalPenalty(loanEmiDetailDto.getPenalty()+loan.getTotalPenalty());
//                    loan.setPendingInstallmentAmount(loan.getDesiredLoan()-loan.getTotalInstalmentAmount());
//
//                    break;
//                }
//                loan.setTotalPayableAmount(loanEmiDetailDto.getTotalPayable()+loan.getTotalPayableAmount());
//                loan.setTotalInterestAmount(loanEmiDetailDto.getInterestAmount()+loan.getTotalInterestAmount());
//                loan.setTotalInstalmentAmount(loanEmiDetailDto.getInstalment()+loan.getTotalInstalmentAmount());
//                loan.setPendingInstallmentAmount(loan.getDesiredLoan()-loan.getTotalInstalmentAmount());
//                loanEmiDetailDto.setDueDate(baseDate);
//                break;
//            }
//        }




