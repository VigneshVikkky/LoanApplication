package com.avitam.bankloanapplication.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@ToString
public class LoanDto extends CommonDto {
    private String loanType;
    private LoanTypeDto loanTypeDto;
    private BigDecimal loanEmi;
    private BigDecimal interestRate;
    private Integer tenure;
    private Double desiredLoan;
    private String images;
    private String loanName;
    private String loanScoreResultId;
    private Integer creditMultiplier;
    private String customerId;
    private CustomerDto customerDto;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate sanctionDate;
    private String loanDetailsId;
    private List<LoanEmiDetailDto> loanEmiDetailDtoList;
    private double totalInterestAmount;
    private double totalInstalmentAmount;
    private double totalPayableAmount;
    private double totalPenalty;
    private String loanStatus;
    private double pendingInstallmentAmount;
    //private double  totalDesiredLoan;
    private double foreClosingCharges;
    private boolean foreClosing = false;
}
