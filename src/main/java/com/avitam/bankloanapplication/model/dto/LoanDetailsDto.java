package com.avitam.bankloanapplication.model.dto;

import com.avitam.bankloanapplication.model.entity.LoanDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document("loanDetails")
public class LoanDetailsDto extends CommonDto{

   // private double interestRate;
    private double instalment;
    private double interestAmount;
    private double totalPayable;
    private String loanLimitId;
   // private int monthDuration;
    private double loanAmount;
    private List<LoanDetails> loanDetailsList;
    private double totalInterestAmount;
    private double totalInstalmentAmount;
    private double totalPayableAmount;
}
