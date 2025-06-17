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
public class LoanScoreResultWsDto extends CommonWsDto {

    private List<LoanScoreResultDto> loanScoreDtos;
}
