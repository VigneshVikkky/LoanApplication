package com.avitam.bankloanapplication.model.entity;

import com.avitam.bankloanapplication.model.dto.PaymentDetailsDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@ToString
@Document("paymentHistory")
public class PaymentHistory extends BaseEntity {

    private Loan loan;
    private List<PaymentDetailsDto> paymentDetailsDtoList;

}
