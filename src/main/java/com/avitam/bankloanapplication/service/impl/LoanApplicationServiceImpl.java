package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.core.service.CoreService;
import com.avitam.bankloanapplication.exception.InvalidLoanApplicationException;
import com.avitam.bankloanapplication.model.dto.*;
import com.avitam.bankloanapplication.model.entity.*;
import com.avitam.bankloanapplication.repository.*;
import com.avitam.bankloanapplication.service.LoanApplicationService;
import lombok.extern.slf4j.Slf4j;
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

@Service
@Slf4j
public class LoanApplicationServiceImpl implements LoanApplicationService {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CoreService coreService;
    @Autowired
    private LoanApplicationRepository loanApplicationRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private LoanDetailsRepository loanDetailsRepository;
    @Autowired
    private LoanScoreResultRepository loanScoreResultRepository;
    @Autowired
    private LoanLimitRepository loanLimitRepository;
    @Autowired
    private LoanTypeRepository loanTypeRepository;

    public static final String ADMIN_LOANAPPLICATION = "/loans/loanApplication";


    @Override
    public LoanApplicationWsDto handleEdit(LoanApplicationWsDto request) {
        LoanApplication loanApplication = new LoanApplication();
        List<LoanApplicationDto> loanApplicationDtos = request.getLoanApplicationDtos();
        List<LoanApplication> loanApplications = new ArrayList<>();

        for (LoanApplicationDto loanApplicationDto : loanApplicationDtos) {

            if (loanApplicationDto.getRecordId() != null) {
                loanApplication = loanApplicationRepository.findByRecordId(loanApplicationDto.getRecordId());
                getLoanType(loanApplication);
                modelMapper.map(loanApplicationDto, loanApplication);
                loanApplicationRepository.save(loanApplication);
                request.setMessage("Data updated successfully");
            } else {
                loanApplication = modelMapper.map(loanApplicationDto, LoanApplication.class);
                loanApplication.setStatus(true);
                loanApplication.setCreationTime(new Date());
                getLoanType(loanApplication);
                loanApplicationRepository.save(loanApplication);
            }
            if (request.getRecordId() == null) {
                loanApplication.setRecordId(String.valueOf(loanApplication.getId().getTimestamp()));
            }
            //loanApplication.setLoanStatus("Applied");
            loanApplicationRepository.save(loanApplication);
            request.setBaseUrl(ADMIN_LOANAPPLICATION);
            request.setMessage("Data added Successfully");
            loanApplications.add(loanApplication);
        }
        Type listType = new TypeToken<List<LoanApplicationDto>>() {
        }.getType();
        request.setLoanApplicationDtos(modelMapper.map(loanApplications, listType));
        return request;
    }

    public LoanApplication getLoanType(LoanApplication loanApplication){

        Loan loan = loanRepository.findByRecordId(loanApplication.getLoanId());
        LoanType loanType = loanTypeRepository.findByRecordId(loan.getLoanType());
        LoanTypeDto loanTypeDto = modelMapper.map(loanType, LoanTypeDto.class);
        loanApplication.setLoanTypeDto(loanTypeDto);
        return loanApplication;
    }

//    @Override
//    public LoanApplicationDto getEmiStatusTillDate(LoanApplicationDto loanApplicationDto) {
//
//        Loan loan = loanRepository.findByRecordId(loanApplicationDto.getLoanId());
//        LocalDate sanctionDate = loan.getSanctionDate();
//        LocalDate baseDate = sanctionDate.withDayOfMonth(5);
//        LocalDate currentDate = LocalDate.now();
//        currentDate=currentDate.plusMonths(3);
//        int noOfMonths = (int) ChronoUnit.MONTHS.between(baseDate, currentDate);
//
//        /*if (sanctionDate.getDayOfMonth() > 5) {
//            baseDate = baseDate.plusMonths(1);
//        } else {
//            baseDate = baseDate.plusMonths(0);
//        }*/
//        LoanDetails loanDetails = loanDetailsRepository.findByRecordId(loan.getLoanDetailsId());
//
//        for(LoanEmiDetailDto loanEmiDetailDto: loanDetails.getLoanDetailsDtoList()) {
//
//            if(loanEmiDetailDto.getPaymentStatus().equalsIgnoreCase("Unpaid")){
//                LocalDate dueDate = loanEmiDetailDto.getDueDate();
//                if (dueDate == currentDate) {
//                loanEmiDetailDto.setPaymentStatus("Paid");
//                break;
//                } else {
//                int noOfDays = (int) ChronoUnit.DAYS.between(dueDate, currentDate);
//                double totalPayable = loanEmiDetailDto.getTotalPayable();
//                loanEmiDetailDto.setTotalPayable(totalPayable+(loanEmiDetailDto.getTotalPayable() * 0.04 * noOfDays));
//                loanEmiDetailDto.setPenalty(loanEmiDetailDto.getTotalPayable()-totalPayable);
//                loanEmiDetailDto.setPaymentStatus("Paid");
//                break;
//                }
//            }
//        }
//        loanDetails.setLoanDetailsDtoList(loanDetails.getLoanDetailsDtoList());
//        loanDetailsRepository.save(loanDetails);
//        return loanApplicationDto;
//    }


    public LoanWsDto getLoanApplicationResult(LoanApplicationWsDto request) {
        LoanWsDto loanWsDto = new LoanWsDto();
        List<LoanApplicationDto> loanAppDto = request.getLoanApplicationDtos();
        LoanApplication finalizedApplication = new LoanApplication();
        for (LoanApplicationDto applicationDto : loanAppDto) {

            finalizedApplication = finalizeLoanApplication(applicationDto);
        }
        if (finalizedApplication == null) {
            // Handle the case where no application was finalized, e.g., the list was empty
            throw new InvalidLoanApplicationException("No loan application finalized.");
        }

        Loan loan = loanRepository.findByRecordId(finalizedApplication.getLoanId());
        LoanScoreResult loanScoreResult = loanScoreResultRepository.findByRecordId(loan.getLoanScoreResultId());
        List<LoanDto> loanDtoList = new ArrayList<>();
        if (loanScoreResult.getName().equalsIgnoreCase("NOT_RESULTED")) {
            throw new InvalidLoanApplicationException("!");
        } else if (loanScoreResult.getName().equalsIgnoreCase("REJECTED")) {
            LoanDto loanDto = new LoanDto();
            modelMapper.map(loan, loanDto);
            loanDtoList.add(loanDto);
            loanWsDto.setLoanDtoList(loanDtoList);
            return loanWsDto;
        }
        LoanDto loanDto = new LoanDto();
        modelMapper.map(loan, loanDto);
        loanDtoList.add(loanDto);
        loanWsDto.setLoanDtoList(loanDtoList);

        return loanWsDto;
    }

    private LoanApplication finalizeLoanApplication(LoanApplicationDto applicationDto) {

        LoanApplication loanApplication = loanApplicationRepository.findByRecordId(applicationDto.getRecordId());
        if (loanApplication != null) {
            verifyLoan(loanApplication);
        }

        // return loanApplicationList.stream().findAny().orElseThrow((() -> new InvalidLoanApplicationException(".")));
        return loanApplication;

        //Optional<Customer> customerByNationalIdentityNumber = customerRepository.findByNationalIdentityNumber(nationalIdentityNumber);
        /*if (customerByNationalIdentityNumber.isPresent()) {
            LoanApplication loanApplication1 = customerByNationalIdentityNumber.get().getLoanApplications().stream()
                    .findFirst()
                    .get();
            verifyLoan(loanApplication1);
        }

        return customerByNationalIdentityNumber.get().getLoanApplications().stream()
                .filter(loanApplication -> loanApplication.getCustomer() == customerByNationalIdentityNumber.get())
                //.filter(loanApplication -> loanApplication.getLoan().getLoanStatus() == LoanStatus.ACTIVE)
                .findAny()
                .orElseThrow(() -> new InvalidLoanApplicationException("."));*/
    }

    private void verifyLoan(LoanApplication loanApplication) {
        String loanCustomer = loanApplication.getCustomerId();

        Loan loanToUpdate = getNotResultedLoanApplicationOfCustomer(loanApplication);
        if (loanToUpdate == null) return;
        log.info("Getting loan application for result");

        Customer customer = customerRepository.findByRecordId(loanCustomer);

        Integer loanScore = customer.getLoanScore();
        LoanScoreResult loanScoreResult = loanScoreResultRepository.findByName("REJECTED");
        boolean loanScoreForApproval = (loanScore >= loanScoreResult.getLoanScoreLimit());

        if (loanScoreForApproval) {
            LoanScoreResult loanScoreResult2 = loanScoreResultRepository.findByName("APPROVED");
            loanToUpdate.setLoanScoreResultId(loanScoreResult2.getRecordId());
            loanApplication.setLoanId(loanToUpdate.getRecordId());
            loanRepository.save(loanToUpdate);
            loanToUpdate = loanLimitCalculator(loanApplication);

        } else {
            loanToUpdate.setLoanScoreResultId(loanScoreResult.getRecordId());
            loanRepository.save(loanToUpdate);
        }
        loanRepository.save(loanToUpdate);
        log.info("resulted the application");
        //TODO: modify sms
        log.info("Sent sms result");
    }

    private Loan loanLimitCalculator(LoanApplication loanApplication) {

        Loan loan = loanRepository.findByRecordId(loanApplication.getLoanId());
        Loan updatedLoan = modelMapper.map(loan, Loan.class);
        Customer customer = customerRepository.findByRecordId(loanApplication.getCustomerId());

        Integer loanScore = customer.getLoanScore();
        Double income = customer.getMonthlyIncome();
        Integer loanMultiplier = loan.getCreditMultiplier();

        LoanLimit loanLimit1 = loanLimitRepository.findByName("HIGHER");
        LoanLimit loanLimit2 = loanLimitRepository.findByName("SPECIAL");
        boolean loanLimitCheck = (income >= loanLimit1.getLoanLimitAmount());
        LoanScoreResult loanScoreResult = loanScoreResultRepository.findByName("APPROVED");
        boolean loanScoreCheck = (loanScore >= loanScoreResult.getLoanScoreLimit());
        LoanLimit loanLimit3 = loanLimitRepository.findByName("LOWER");

        if (loanScoreCheck) {
            loanLimit2.setLoanLimitAmount(income * loanMultiplier);
            //   loan.setLoanLimit(loanLimit2.getLoanLimitAmount());
        } else if (loanLimitCheck) {
            //   loan.setLoanLimit(loanLimit1.getLoanLimitAmount());
        } else {
            //  loan.setLoanLimit(loanLimit3.getLoanLimitAmount());
        }
        return loan;
    }

    private Loan getNotResultedLoanApplicationOfCustomer(LoanApplication loanApplication) {

            String loanId = loanApplication.getLoanId();
            Loan loan = loanRepository.findByRecordId(loanId);
            LoanScoreResult loanScoreResult = loanScoreResultRepository.findByRecordId(loan.getLoanScoreResultId());
            return loan;

        /*var optionalLoan =
                customer.getLoanApplicationId().stream()
                        .filter(c -> c.getLoan().getLoanScoreResult().equals(LoanScoreResult.NOT_RESULTED))
                        .findFirst();

        return optionalLoan.isPresent() ? optionalLoan.get().getLoan() : null;*/

    }

    public Object getLoanApplicationById(Long id) {

        /*protected Optional<LoanApplication> findLoanApplicationById (Long id){
            return Optional.ofNullable(loanApplicationRepository.findById(id).orElseThrow(() ->
                    new LoanNotFoundException("Related loan with id: " + id + " not found")));
        }

        public LoanApplication getLoanApplicationById (Long loanApplicationId){
            return findLoanApplicationById(loanApplicationId).get();
        }


    /*public void createLoanApplication(LoanApplicationDto request) {

        Customer customer = customerRepository.findByRecordId(request.getCustomerId());
        Loan loan = loanRepository.findByRecordId(request.getLoanId());

        Optional<Customer> customerOptional = Optional.ofNullable(customerRepository.findByRecordId(customer.getRecordId()));
        customerOptional.ifPresent(customer1 -> {
            LoanApplication loanApplication = new LoanApplication();
            loanApplication.setCustomerId(request.getCustomerId());
            loanApplication.setLoanId(request.getLoanId());
            loanApplicationRepository.save(loanApplication);

        });
    }*/

     /*private Loan createLoan() {
        var newLoan = new Loan(LoanType.PERSONAL, 0.0, LoanScoreResult.NOT_RESULTED, LoanStatus.ACTIVE, new Date());
        loanRepository.save(newLoan);
        return newLoan;
    }*/

    /*public void updateByRecordId(String recordId) {
        LoanApplication  loanApplicationOptional=loanApplicationRepository.findByRecordId(recordId);
        if(loanApplicationOptional!=null)
        {
            loanApplicationRepository.save(loanApplicationOptional);
        }
    }*/

        return null;
    }


}