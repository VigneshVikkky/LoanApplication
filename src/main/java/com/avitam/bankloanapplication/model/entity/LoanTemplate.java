package com.avitam.bankloanapplication.model.entity;

import com.avitam.bankloanapplication.model.dto.LoanEmiDetailDto;
import com.avitam.bankloanapplication.model.dto.LoanTypeDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document("LoanTemplate")
public class LoanTemplate extends BaseEntity {
    private String loanType;
    private LoanTypeDto loanTypeDto;
    private BigDecimal loanEmi;
    private double interestRate;
    private Integer tenure;
    private List<LoanEmiDetailDto> loanEmiDetailDtoList;
    private String loanDetailsId;
    private Double desiredLoan;
    private String images;
    private String loanName;
    private String loanScoreResultId;
    private Integer creditMultiplier;
    private double totalInterestAmount;
    private double totalInstalmentAmount;
    private double totalPayableAmount;
    private double totalPenalty;
}
