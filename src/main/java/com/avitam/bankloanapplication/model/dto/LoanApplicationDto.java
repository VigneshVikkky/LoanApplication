package com.avitam.bankloanapplication.model.dto;

import com.avitam.bankloanapplication.model.entity.LoanTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoanApplicationDto extends CommonDto {

    private String customerId;
    private String notificationId;
    private String loanId;
    private CustomerDto customerDto;
    private LoanTemplateDto loanTemplateDto;
    private String loanName;
    private String loanStatus;
    private String images;
    private LocalDate sanctionDate;
    private LoanTypeDto loanTypeDto;

}
