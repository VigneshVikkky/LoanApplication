package com.avitam.bankloanapplication.web.controllers.admin.loanstatus;

import com.avitam.bankloanapplication.model.dto.*;
import com.avitam.bankloanapplication.model.entity.LoanStatus;
import com.avitam.bankloanapplication.model.entity.LoanType;
import com.avitam.bankloanapplication.repository.LoanStatusRepository;
import com.avitam.bankloanapplication.service.LoanStatusService;
import com.avitam.bankloanapplication.web.controllers.BaseController;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/loans/loanStatus")
public class LoanStatusController extends BaseController {
    @Autowired
    private LoanStatusService loanStatusService;
    @Autowired
    private LoanStatusRepository loanStatusRepository;
    @Autowired
    private ModelMapper modelMapper;
    private static final String ADMIN_LOANSTATUS= "/loans/loanStatus";

    @PostMapping
    public LoanStatusWsDto getAllLoanStatus(@RequestBody LoanStatusWsDto loanStatusWsDto){
        Pageable pageable=getPageable(loanStatusWsDto.getPage(),loanStatusWsDto.getSizePerPage(),loanStatusWsDto.getSortDirection(),loanStatusWsDto.getSortField());
        LoanStatusDto loanTypeDto = CollectionUtils.isNotEmpty(loanStatusWsDto.getLoanStatusDtos()) ? loanStatusWsDto.getLoanStatusDtos().get(0) : new LoanStatusDto();
        LoanStatus loanStatus = modelMapper.map(loanStatusWsDto, LoanStatus.class);
        Page<LoanStatus> page=isSearchActive(loanStatus) !=null ? loanStatusRepository.findAll(Example.of(loanStatus),pageable) : loanStatusRepository.findAll(pageable);
        Type listType = new TypeToken<List<LoanStatusDto>>() {}.getType();
        loanStatusWsDto.setLoanStatusDtos(modelMapper.map(page.getContent(), listType));
        loanStatusWsDto.setBaseUrl(ADMIN_LOANSTATUS);
        loanStatusWsDto.setTotalPages(page.getTotalPages());
        loanStatusWsDto.setTotalRecords(page.getTotalElements());
        return loanStatusWsDto;

    }
    @GetMapping("/get")
    public LoanStatusWsDto getLoanStatus() {
        LoanStatusWsDto loanStatusWsDto = new LoanStatusWsDto();
        List<LoanStatus> loanStatuses =  loanStatusRepository.findByStatus(true) ;
        Type listType = new TypeToken<List<LoanStatusDto>>() {}.getType();
        loanStatusWsDto.setLoanStatusDtos(modelMapper.map(loanStatuses,listType));
        loanStatusWsDto.setBaseUrl(ADMIN_LOANSTATUS);
        return loanStatusWsDto;
    }

    @PostMapping("/edit")
    public LoanStatusWsDto handleEdit(@RequestBody LoanStatusWsDto request) {

        return loanStatusService.handleEdit(request);
    }

    @PostMapping("/delete")
    public LoanStatusWsDto delete(@RequestBody LoanStatusWsDto loanStatusWsDto) {
            for(LoanStatusDto loanStatusDto: loanStatusWsDto.getLoanStatusDtos()){
                loanStatusRepository.deleteByRecordId(loanStatusDto.getRecordId());
            }
            loanStatusWsDto.setMessage("Data deleted successfully");
            loanStatusWsDto.setBaseUrl(ADMIN_LOANSTATUS);
            return loanStatusWsDto;
        }
    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new LoanStatus());
    }

}
