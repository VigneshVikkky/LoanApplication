package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.model.dto.CreditDto;
import com.avitam.bankloanapplication.model.dto.CreditWsDto;
import com.avitam.bankloanapplication.model.entity.Credit;
import com.avitam.bankloanapplication.repository.CreditRepository;
import com.avitam.bankloanapplication.service.CreditService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CreditServiceImpl implements CreditService {
    public static final String ADMIN_CREDIT = "/admin/credit";
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CreditRepository creditRepository;

    @Override
    public CreditWsDto handelEdit(CreditWsDto request) {
        Credit credit;
        List<CreditDto> creditDtos = request.getCreditDtoList();
        List<Credit> credits = new ArrayList<>();
        for (CreditDto creditDto : creditDtos) {
            if (creditDto.getRecordId() != null) {
                credit = creditRepository.findByRecordId(creditDto.getRecordId());
                modelMapper.map(creditDto, credit);
                creditRepository.save(credit);
                request.setMessage("Data Updated Successfully");
            } else {
                credit = modelMapper.map(creditDto, Credit.class);
                credit.setCreationTime(new Date());
                credit.setStatus(true);
                creditRepository.save(credit);
                request.setMessage("Data Added Successfully");
            }
            if (request.getRecordId() == null) {
                credit.setRecordId(String.valueOf(credit.getId().getTimestamp()));
            }
            creditRepository.save(credit);
            credits.add(credit);

            request.setBaseUrl(ADMIN_CREDIT);
        }
        request.setCreditDtoList(modelMapper.map(credits, List.class));
        return request;
    }
}
