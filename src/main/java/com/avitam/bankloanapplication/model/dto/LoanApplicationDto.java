package com.avitam.bankloanapplication.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoanApplicationDto extends CommonDto{

    private String customerId;
    private String notificationId;
    private String loanId;
    private CustomerDto customerDto;
    private LoanDto loanDto;
    private String loanName;
    private String loanStatus;
    private String images;
}
