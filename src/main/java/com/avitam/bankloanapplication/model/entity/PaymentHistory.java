package com.avitam.bankloanapplication.model.entity;

import com.avitam.bankloanapplication.model.dto.LoanEmiDetailDto;
import com.avitam.bankloanapplication.model.entity.Loan;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@ToString
@Document("PaymentHistory")
public class PaymentHistory extends BaseEntity{


    private Loan loan;
    private String paidStatus;
    private int monthlyIndex;
    private LoanEmiDetailDto loanEmiDetailDto;

}
