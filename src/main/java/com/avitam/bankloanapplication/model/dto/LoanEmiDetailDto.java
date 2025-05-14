package com.avitam.bankloanapplication.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document("loanEmiDetail")
public class LoanEmiDetailDto {
    private String month;
    private double instalment;
    private double interestAmount;
    private double totalPayable;
    private String status;
}
