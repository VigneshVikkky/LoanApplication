package com.avitam.bankloanapplication.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Document("loanLimit")
public class LoanLimit extends BaseEntity{

    private String description;
    private Double loanLimitAmount;
    private Double incomeLimit;
    private String customerId;
    private Double cibilScore;
    private Double emi;
    private Double interestRate;
    private Integer tenure;
   // private Map<String, List<Double>> loanTypeList;

}
