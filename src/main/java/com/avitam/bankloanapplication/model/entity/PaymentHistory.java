package com.avitam.bankloanapplication.model.entity;

import com.avitam.bankloanapplication.model.dto.LoanEmiDetailDto;
import com.avitam.bankloanapplication.model.dto.PaymentDetailsDto;
import com.avitam.bankloanapplication.model.dto.PaymentHistoryDto;
import com.avitam.bankloanapplication.model.entity.Loan;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Setter
@Getter
@ToString
@Document("paymentHistory")
public class PaymentHistory extends BaseEntity{

    private Loan loan;
    private List<PaymentDetailsDto> paymentDetailsDtoList;

}
