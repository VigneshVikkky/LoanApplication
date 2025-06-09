package com.avitam.bankloanapplication.model.dto;

import com.avitam.bankloanapplication.model.entity.Loan;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class PaymentHistoryDto extends CommonDto{


    private LoanDto loanDto;
    private String paidStatus;
    private int monthlyIndex;
    private LoanEmiDetailDto loanEmiDetailDto;


}
