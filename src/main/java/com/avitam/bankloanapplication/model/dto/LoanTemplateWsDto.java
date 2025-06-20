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
public class LoanTemplateWsDto extends CommonWsDto {
    private List<LoanTemplateDto> loanTemplateDtoList;
}
