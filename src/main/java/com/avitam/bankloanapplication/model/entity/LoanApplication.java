package com.avitam.bankloanapplication.model.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Document("loan_application")
public class LoanApplication extends BaseEntity{

    private String customerId;
    private String notificationId;
    private String loanId;
    private String loanStatus;
    private String images;
    private LocalDate sanctionDate;

}
