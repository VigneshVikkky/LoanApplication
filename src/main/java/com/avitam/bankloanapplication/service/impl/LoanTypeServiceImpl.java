package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.model.dto.LoanTypeDto;
import com.avitam.bankloanapplication.model.dto.LoanTypeWsDto;
import com.avitam.bankloanapplication.model.entity.LoanType;
import com.avitam.bankloanapplication.repository.LoanTypeRepository;
import com.avitam.bankloanapplication.service.LoanTypeService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LoanTypeServiceImpl implements LoanTypeService {

    private static final String ADMIN_LOANTYPE = "/loans/loanType";
    @Autowired
    private LoanTypeRepository loanTypeRepository;
    @Autowired
    private ModelMapper modelMapper;


    public LoanTypeWsDto handleEdit(LoanTypeWsDto request) {
        LoanTypeWsDto loanTypeWsDto = new LoanTypeWsDto();
        LoanType loanType = new LoanType();
        List<LoanTypeDto> loanTypeDtoList = request.getLoanTypeDtoList();
        List<LoanType> loanTypes = new ArrayList<>();
        for (LoanTypeDto loan : loanTypeDtoList) {

            if (loan.getRecordId() != null) {
                loanType = loanTypeRepository.findByRecordId(loan.getRecordId());
                modelMapper.map(loan, loanType);
                loanTypeRepository.save(loanType);
                request.setMessage("Data updated successfully");
            } else {
                loanType = modelMapper.map(loan, LoanType.class);
                loanType.setStatus(true);
                loanType.setCreationTime(new Date());
                loanTypeRepository.save(loanType);
            }
            if (request.getRecordId() == null) {
                loanType.setRecordId(String.valueOf(loanType.getId().getTimestamp()));
            }
            loanTypeRepository.save(loanType);
            loanTypes.add(loanType);
            request.setBaseUrl(ADMIN_LOANTYPE);
            request.setMessage("Data added Successfully");
        }
        Type listType = new TypeToken<List<LoanTypeDto>>() {
        }.getType();
        request.setLoanTypeDtoList(modelMapper.map(loanTypes, listType));
        return request;
    }
}
