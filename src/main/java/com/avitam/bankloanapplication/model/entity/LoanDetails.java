package com.avitam.bankloanapplication.model.entity;

import com.avitam.bankloanapplication.model.dto.LoanEmiDetailDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document("loanDetails")
public class LoanDetails extends BaseEntity {
    private double instalment;
    private double interestAmount;
    private String loanLimitId;
    private double loanAmount;
    private String loanId;
    private double totalPayable;
    private List<LoanEmiDetailDto> loanDetailsDtoList;
    private String paymentStatus;
    private double emiFromCustomer;
    private double interestRate;
    private LocalDate loanSanctionedDate;
    private double totalInterestAmount;
    private double totalInstalmentAmount;
    private double totalPayableAmount;
}

