package com.avitam.bankloanapplication.model.dto;

import com.avitam.bankloanapplication.model.entity.LoanType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class LoanLimitDto extends CommonDto{

    private String description;
    private Double loanLimitAmount;
    private Double incomeLimit;
    private String customerId;
    private Double cibilScore;
    private Double emi;
    private Double interestRate;
    private Integer tenure;
    private Map<String, List<Double>> loanTypeList;

}