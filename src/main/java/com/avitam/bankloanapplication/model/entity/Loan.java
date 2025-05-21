package com.avitam.bankloanapplication.model.entity;

import com.avitam.bankloanapplication.model.dto.LoanEmiDetailDto;
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
   private BigDecimal loanEmi;
   private double interestRate;
   private Integer tenure;
   private List<LoanEmiDetailDto> loanEmiDetailList;
   private String loanDetailsId;
   private Double desiredLoan;
   private String images;
   private String loanName;
   private String loanScoreResultId;
   private Integer creditMultiplier;
   private String customerId;
   @JsonFormat(pattern = "dd-MM-yyyy")
   private LocalDate sanctionDate;

}
