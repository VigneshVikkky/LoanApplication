package com.avitam.bankloanapplication.model.entity;

import com.avitam.bankloanapplication.model.dto.CustomerDto;
import com.avitam.bankloanapplication.model.dto.LoanDetailsDto;
import com.avitam.bankloanapplication.model.dto.LoanEmiDetailDto;
import com.avitam.bankloanapplication.model.dto.LoanTypeDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document("loan")
public class Loan extends BaseEntity{

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
   private String customerId;
   private CustomerDto customerDto;
   @JsonFormat(pattern = "dd-MM-yyyy")
   private LocalDate sanctionDate;
   private double totalInterestAmount;
   private double totalInstalmentAmount;
   private double totalPayableAmount;
   private double totalPenalty;
   private String loanStatus;
   private double pendingInstallmentAmount;
   private double foreClosingCharges;
   private boolean foreClosing=false;


}
