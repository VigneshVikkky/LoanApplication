package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.model.dto.LoanLimitDto;
import com.avitam.bankloanapplication.model.dto.LoanLimitWsDto;
import com.avitam.bankloanapplication.model.entity.Customer;
import com.avitam.bankloanapplication.model.entity.LoanLimit;
import com.avitam.bankloanapplication.model.entity.LoanType;
import com.avitam.bankloanapplication.repository.CustomerRepository;
import com.avitam.bankloanapplication.repository.LoanLimitRepository;
import com.avitam.bankloanapplication.repository.LoanTypeRepository;
import com.avitam.bankloanapplication.service.LoanLimitService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LoanLimitServiceImpl implements LoanLimitService {
    @Autowired
    private LoanLimitRepository loanLimitRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private LoanTypeRepository loanTypeRepository;
    @Autowired
    private ModelMapper modelMapper;
    private static final String ADMIN_LOANLIMIT = "/loans/loanLimit";
    private double emi;

    public LoanLimitWsDto editLoanLimit(LoanLimitDto loanLimitDto) {
        LoanLimitWsDto request = new LoanLimitWsDto();
        LoanLimit loanLimit = new LoanLimit();
        List<LoanLimit> loanLimits = new ArrayList<>();

        if (loanLimitDto.getRecordId() != null) {
            loanLimit = loanLimitRepository.findByRecordId(loanLimitDto.getRecordId());
            modelMapper.map(loanLimitDto, loanLimit);
            loanLimitRepository.save(loanLimit);
            request.setMessage("Data updated successfully");
        } else {
            Customer customer = customerRepository.findByRecordId(loanLimitDto.getCustomerId());
            Double monthlyIncome = customer.getMonthlyIncome();
            if (loanLimitDto.getCibilScore() >= 750 & loanLimitDto.getCibilScore() <= 900) {
                loanLimitDto.setEmi(monthlyIncome * 50 / 100);

            } else if (loanLimitDto.getCibilScore() >= 700 & loanLimitDto.getCibilScore() <= 749) {
                loanLimitDto.setEmi(monthlyIncome * 40 / 100);

            } else if (loanLimitDto.getCibilScore() >= 650 & loanLimitDto.getCibilScore() <= 699) {
                loanLimitDto.setEmi(monthlyIncome * 30 / 100);

            } else if (loanLimitDto.getCibilScore() >= 600 & loanLimitDto.getCibilScore() <= 649) {
                loanLimitDto.setEmi(monthlyIncome * 20 / 100);

            } else {
                loanLimitDto.setEmi(emi);
            }

            loanLimitDto.setLoanLimitAmount(LoanLimitCalculator(loanLimitDto));
            //loanLimitDto.setLoanTypeList(eligibleLoanTypes(loanLimitDto));

            loanLimit = modelMapper.map(loanLimitDto, LoanLimit.class);
            loanLimit.setCreationTime(new Date());
            loanLimit.setStatus(true);
            loanLimitRepository.save(loanLimit);
        }
        if (request.getRecordId() == null) {
            loanLimit.setRecordId(String.valueOf(loanLimit.getId().getTimestamp()));
        }
        loanLimitRepository.save(loanLimit);
        loanLimits.add(loanLimit);
        request.setBaseUrl(ADMIN_LOANLIMIT);
        request.setMessage("Data added Successfully");
        request.setLoanLimitDtos(modelMapper.map(loanLimits, List.class));
        return request;
    }

    public Double LoanLimitCalculator(LoanLimitDto loanLimitDto) {

        Double emi = loanLimitDto.getEmi();
        Double interestRate = (loanLimitDto.getInterestRate() / 100) / 12;

        Integer tenure = loanLimitDto.getTenure() * 12;

        Double numerator = emi * (Math.pow(1 + interestRate, tenure) - 1);
        Double denominator = interestRate * Math.pow(1 + interestRate, tenure);

        return Math.round((numerator / denominator) * 100.0) / 100.0;
    }

    /*public Map<String, List<Double>> eligibleLoanTypes(LoanLimitDto loanLimitDto) {

        List<LoanType> loanTypeList = loanTypeRepository.findAll();
        Map<String, List<Double>> eliglibleLoanAmount = new HashMap<>();
        //List<Double> loanLimitAmounts = new ArrayList<>();
        for (LoanType loanType : loanTypeList) {
            if (loanLimitDto.getLoanLimitAmount() <= loanType.getMaxLoanLimit()) {
                List<Double> loanLimitAmounts = new ArrayList<>();
                for (double i = loanLimitDto.getLoanLimitAmount(); i >= 50000; i = i - 100000) {
                    loanLimitAmounts.add(i);
                }
                eliglibleLoanAmount.put(loanType.getName(), loanLimitAmounts);
            } else {
                List<Double> loanLimitAmounts = new ArrayList<>();
                for (double i = loanType.getMaxLoanLimit(); i >= 50000; i = i - 100000) {
                    loanLimitAmounts.add(i);
                }
                eliglibleLoanAmount.put(loanType.getName(), loanLimitAmounts);
            }
            //eliglibleLoanAmount.put(loanType.getName(), loanLimitAmounts);
        }

        return eliglibleLoanAmount;
    }*/
}
