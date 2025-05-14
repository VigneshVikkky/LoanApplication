package com.avitam.bankloanapplication.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document("loanDetails")
public class LoanDetails extends BaseEntity {

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
    //private LocalDate loanSanctionedDate;


//    private double totalInterestAmount;
//    private double totalInstalmentAmount;
//    private double totalPayableAmount;
//    private int monthDuration;
//    private double interestRate;

}
