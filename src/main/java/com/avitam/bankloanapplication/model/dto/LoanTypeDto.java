package com.avitam.bankloanapplication.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class
LoanTypeDto extends CommonDto{
    private String description;
    private String images;
    private Double maxLoanLimit;

}
