package com.avitam.bankloanapplication.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoanWsDto extends CommonWsDto {

    private List<LoanDto> loanDtoList;

    private double totalDesiredLoan;
}
