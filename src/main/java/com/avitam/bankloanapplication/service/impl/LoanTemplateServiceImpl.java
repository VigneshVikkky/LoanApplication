package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.model.dto.LoanTemplateDto;
import com.avitam.bankloanapplication.model.dto.LoanTemplateWsDto;
import com.avitam.bankloanapplication.model.entity.LoanTemplate;
import com.avitam.bankloanapplication.repository.LoanTemplateRepository;
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

    public LoanTemplateWsDto createLoan(LoanTemplateWsDto request) {
        LoanTemplate loan = null;
        List<LoanTemplateDto> loanDtos = request.getLoanDtoList();
        List<LoanTemplate> loans = new ArrayList<>();
        for (LoanTemplateDto loanDto : loanDtos) {
            if (loanDto.getRecordId() != null) {
                loan = loanTemplateRepository.findByRecordId(loanDto.getRecordId());
                modelMapper.map(loanDto, loan);
                loanTemplateRepository.save(loan);
                request.setMessage("Data updated successfully");
            } else {
                loan = modelMapper.map(loanDto, LoanTemplate.class);
                loan.setStatus(true);
                loan.setCreationTime(new Date());
                modelMapper.map(loanDto, loan);
                loanTemplateRepository.save(loan);
            }
            if (request.getRecordId() == null) {
                loan.setRecordId(String.valueOf(loan.getId().getTimestamp()));
            }
            loanTemplateRepository.save(loan);
            loans.add(loan);
            loanDto.setBaseUrl(ADMIN_lOAN);

            request.setMessage("Data added Successfully");
        }
        request.setLoanDtoList(modelMapper.map(loans, List.class));
        return request;
    }
}