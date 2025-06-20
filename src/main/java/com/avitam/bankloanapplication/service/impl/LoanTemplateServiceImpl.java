package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.model.dto.LoanDetailsDto;
import com.avitam.bankloanapplication.model.dto.LoanDetailsWsDto;
import com.avitam.bankloanapplication.model.dto.LoanTemplateDto;
import com.avitam.bankloanapplication.model.dto.LoanTemplateWsDto;
import com.avitam.bankloanapplication.model.dto.LoanWsDto;
import com.avitam.bankloanapplication.model.entity.LoanTemplate;
import com.avitam.bankloanapplication.repository.LoanTemplateRepository;
import com.avitam.bankloanapplication.service.LoanDetailsService;
import com.avitam.bankloanapplication.service.LoanTemplateService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LoanTemplateServiceImpl implements LoanTemplateService {


    public static final String ADMIN_lOAN = "/loans/loantemplate";
    @Autowired
    private LoanTemplateRepository loanTemplateRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private LoanDetailsService loanDetailsService;

    public LoanTemplateWsDto createLoan(LoanTemplateWsDto request) {
        LoanTemplate loanTemplate = null;
        List<LoanTemplateDto> loanDtos = request.getLoanTemplateDtoList();
        List<LoanTemplate> loanTemplateList = new ArrayList<>();
        for (LoanTemplateDto loanDto : loanDtos) {
            if (loanDto.getRecordId() != null) {
                loanTemplate = loanTemplateRepository.findByRecordId(loanDto.getRecordId());
                modelMapper.map(loanDto, loanTemplate);
                loanTemplateRepository.save(loanTemplate);
                request.setMessage("Data updated successfully");
            } else {
                loanTemplate = modelMapper.map(loanDto, LoanTemplate.class);
                loanTemplate.setStatus(true);
                loanTemplate.setCreationTime(new Date());
                modelMapper.map(loanDto, loanTemplate);
                loanTemplateRepository.save(loanTemplate);
            }
            if (request.getRecordId() == null) {
                loanTemplate.setRecordId(String.valueOf(loanTemplate.getId().getTimestamp()));
            }

            loanTemplateRepository.save(loanTemplate);
            loanTemplateList.add(loanTemplate);
            loanDto.setBaseUrl(ADMIN_lOAN);

            request.setMessage("Data added Successfully");
        }
        request.setLoanTemplateDtoList(modelMapper.map(loanTemplateList, List.class));

        return request;
    }
}