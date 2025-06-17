package com.avitam.bankloanapplication.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class PaymentDetailsDto extends CommonDto {

    private LoanDto loanDto;
    private String paidStatus;
    private int monthlyIndex;
    private LoanEmiDetailDto loanEmiDetailDto;
    private String transactionId;
}
