package com.avitam.bankloanapplication.model.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Document("loan")
public class Loan extends BaseEntity{

   private String loanType;
   private BigDecimal loanEmi;
   private BigDecimal interestRate;
   private Integer tenure;
   private Double desiredLoan;
   private String images;
   private String loanName;
   private String loanScoreResultId;
   private Integer creditMultiplier;
}
