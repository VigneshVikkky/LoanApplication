package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.model.dto.LoanApplicationDto;
import com.avitam.bankloanapplication.model.dto.LoanScoreResultDto;
import com.avitam.bankloanapplication.model.dto.LoanScoreResultWsDto;
import com.avitam.bankloanapplication.model.entity.LoanScoreResult;
import com.avitam.bankloanapplication.repository.LoanScoreResultRepository;
import com.avitam.bankloanapplication.service.LoanScoreResultService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class LoanScoreResultServiceImpl implements LoanScoreResultService {
    private static final String ADMIN_LOANSCORE = "/loans/loanScoreResult";
    @Autowired
    private LoanScoreResultRepository loanScoreResultRepository;

    @Autowired
    private ModelMapper modelMapper;

    public LoanScoreResultWsDto createLoanScore(LoanScoreResultWsDto request) {
        LoanScoreResult loanScoreResult = null;
        List<LoanScoreResultDto> loanScoreDtos=request.getLoanScoreDtos();
        List<LoanScoreResult> loanScoreResults=new ArrayList<>();
        for(LoanScoreResultDto loanScoreDto:loanScoreDtos){
        if(loanScoreDto.getRecordId()!=null){
            loanScoreResult= loanScoreResultRepository.findByRecordId(loanScoreDto.getRecordId());
            modelMapper.map(loanScoreDto, loanScoreResult);
            loanScoreResult.setLastModified(new Date());
            loanScoreResultRepository.save(loanScoreResult);
            request.setMessage("Data updated successfully");
        }
        else {
            loanScoreResult = modelMapper.map(loanScoreDto, LoanScoreResult.class);
            loanScoreResult.setCreationTime(new Date());
            loanScoreResult.setStatus(true);
            loanScoreResultRepository.save(loanScoreResult);
        }
        if(request.getRecordId()==null){
            loanScoreResult.setRecordId(String.valueOf(loanScoreResult.getId().getTimestamp()));
        }
        loanScoreResultRepository.save(loanScoreResult);
        loanScoreResults.add(loanScoreResult);
        request.setBaseUrl(ADMIN_LOANSCORE);
            request.setMessage("Data added Successfully");
        }
        Type listType = new TypeToken<List<LoanScoreResultDto>>() {}.getType();
        request.setLoanScoreDtos(modelMapper.map(loanScoreResults,listType));
        return request;

    }
}
