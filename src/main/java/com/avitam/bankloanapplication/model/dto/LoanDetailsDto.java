package com.avitam.bankloanapplication.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LoanDetailsDto extends CommonDto {

    private double instalment;
    private double interestAmount;
    private double totalPayable;
    private String loanLimitId;
    private double loanAmount;
    private List<LoanEmiDetailDto> loanDetailsDtoList;
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
