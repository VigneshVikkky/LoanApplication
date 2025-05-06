package com.avitam.bankloanapplication.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class LoanLimit extends BaseEntity{

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
