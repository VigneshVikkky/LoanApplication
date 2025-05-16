package com.avitam.bankloanapplication.model.dto;

import com.avitam.bankloanapplication.model.entity.LoanDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document("loanDetails")
public class LoanDetailsDto extends CommonDto{

     private double instalment;
     private double interestAmount;
     private double totalPayable;
     private String loanLimitId;
     private double loanAmount;
     private List<LoanDetails> loanDetailsList;
     private String loanId;
     private LocalDate dueDate;
     private String paymentStatus;
     private double emiFromCustomer;
     private double penalty;
     private double interestRate;
     private LocalDate loanSanctionedDate;


    private double totalInterestAmount;
    private double totalInstalmentAmount;
    private double totalPayableAmount;
//    private int monthDuration;
//    private double interestRate;

}
