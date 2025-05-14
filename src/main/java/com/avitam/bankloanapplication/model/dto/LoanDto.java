package com.avitam.bankloanapplication.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;


@Setter
@Getter
@NoArgsConstructor
@ToString
public class LoanDto extends CommonDto{
    private String loanType;
    private BigDecimal loanEmi;
    private BigDecimal interestRate;
    private Integer tenure;
    private Double desiredLoan;
    private String images;
    private String loanName;
    private String loanScoreResultId;
    private Integer creditMultiplier;
    private String customerId;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate sanctionDate;
}
