package com.avitam.bankloanapplication.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor

public class LoanEmiDetailDto extends CommonDto {
    private String month;
    private double instalment;
    private double interestAmount;
    private double totalPayable;
    private double penalty;
    private String paymentStatus;
    private LocalDate dueDate;
    private LocalDate loanPaidDate;


}
