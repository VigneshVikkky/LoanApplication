package com.avitam.bankloanapplication.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class LoanEmiSummaryDto {

    private LocalDate dueDate;
    private String paymentStatus;
    private double totalPayable;
}
