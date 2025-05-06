package com.avitam.bankloanapplication.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Document("loanType")
public class LoanType extends BaseEntity {

    private String description;
    private String images;
    private Double maxLoanLimit;

}
