package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.model.dto.LoanApplicationDto;
import com.avitam.bankloanapplication.model.dto.LoanStatusDto;
import com.avitam.bankloanapplication.model.dto.LoanStatusWsDto;
import com.avitam.bankloanapplication.model.entity.LoanStatus;
import com.avitam.bankloanapplication.repository.LoanStatusRepository;
import com.avitam.bankloanapplication.service.LoanStatusService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LoanStatusServiceImpl implements LoanStatusService{
    @Autowired
    private LoanStatusRepository loanStatusRepository;
    @Autowired
    private ModelMapper modelMapper;
    private static final String ADMIN_LOANSTATUS= "/loans/loanStatus";


    public LoanStatusWsDto handleEdit(LoanStatusWsDto request) {
        LoanStatusWsDto loanStatusWsDto = new LoanStatusWsDto();
        LoanStatus loanStatus=new LoanStatus();
        List<LoanStatusDto> loanStatusDtos = request.getLoanStatusDtos();
        List<LoanStatus> loanStatuses = new ArrayList<>();
        for(LoanStatusDto loanStatusDto: loanStatusDtos) {

            if (loanStatusDto.getRecordId() != null) {
                loanStatus = loanStatusRepository.findByRecordId(loanStatusDto.getRecordId());
                modelMapper.map(loanStatusDto, loanStatus);
                loanStatusRepository.save(loanStatus);
                request.setMessage("Data updated successfully");
            } else {
                loanStatus = modelMapper.map(loanStatusDto, LoanStatus.class);
                loanStatus.setStatus(true);
                loanStatus.setCreationTime(new Date());
                loanStatusRepository.save(loanStatus);
            }
            if (request.getRecordId() == null) {
                loanStatus.setRecordId(String.valueOf(loanStatus.getId().getTimestamp()));
            }
            loanStatusRepository.save(loanStatus);
            loanStatuses.add(loanStatus);
            request.setBaseUrl(ADMIN_LOANSTATUS);
            request.setMessage("Data added Successfully");
        }
        Type listType = new TypeToken<List<LoanStatusDto>>() {}.getType();
        request.setLoanStatusDtos(modelMapper.map(loanStatuses,listType));
        return request;
    }
}
