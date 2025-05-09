package com.avitam.bankloanapplication.web.controllers.admin.loanscoreresult;

import com.avitam.bankloanapplication.model.dto.LoanLimitDto;
import com.avitam.bankloanapplication.model.dto.LoanScoreResultDto;
import com.avitam.bankloanapplication.model.dto.LoanScoreResultWsDto;
import com.avitam.bankloanapplication.model.dto.SearchDto;
import com.avitam.bankloanapplication.model.entity.LoanScoreResult;
import com.avitam.bankloanapplication.repository.LoanScoreResultRepository;
import com.avitam.bankloanapplication.service.LoanScoreResultService;
import com.avitam.bankloanapplication.web.controllers.BaseController;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.collections4.CollectionUtils;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/loans/loanScoreResult")
public class LoanScoreResultController extends BaseController {
    @Autowired
    private LoanScoreResultService loanScoreResultService;
    @Autowired
    private LoanScoreResultRepository loanScoreRepository;
    @Autowired
    private ModelMapper modelMapper;

    private static final String ADMIN_LOANSCORE = "/loans/loanScore" ;

    @PostMapping
    @ResponseBody
    public LoanScoreResultWsDto getAllLoanScore(@RequestBody LoanScoreResultWsDto loanScoreWsDto){
        Pageable pageable=getPageable(loanScoreWsDto.getPage(),loanScoreWsDto.getSizePerPage(),loanScoreWsDto.getSortDirection(),loanScoreWsDto.getSortField());
        LoanScoreResultDto loanScoreResultDto=CollectionUtils.isNotEmpty(loanScoreWsDto.getLoanScoreDtos()) ? loanScoreWsDto.getLoanScoreDtos().get(0) : new LoanScoreResultDto();
        LoanScoreResult loanScoreResult = modelMapper.map(loanScoreWsDto, LoanScoreResult.class);
        Page<LoanScoreResult> page=isSearchActive(loanScoreResult) !=null ? loanScoreRepository.findAll(Example.of(loanScoreResult),pageable) : loanScoreRepository.findAll(pageable);
        Type listType = new TypeToken<List<LoanScoreResultDto>>() {}.getType();
        loanScoreWsDto.setLoanScoreDtos(modelMapper.map(page.getContent(), listType));
        loanScoreWsDto.setBaseUrl(ADMIN_LOANSCORE);
        loanScoreWsDto.setTotalPages(page.getTotalPages());
        loanScoreWsDto.setTotalRecords(page.getTotalElements());
        return loanScoreWsDto;

    }
    @GetMapping("/get")
    @ResponseBody
    public LoanScoreResultWsDto getActiveLoanScoreResult() {
        LoanScoreResultWsDto loanScoreResultWsDto = new LoanScoreResultWsDto();
        loanScoreResultWsDto.setBaseUrl(ADMIN_LOANSCORE);
        Type listType = new TypeToken<List<LoanScoreResultDto>>() {}.getType();
        loanScoreResultWsDto.setLoanScoreDtos(modelMapper.map(loanScoreRepository.findByStatus(true), listType));
        return loanScoreResultWsDto;
    }
    @PostMapping("/edit")
    @ResponseBody
    public LoanScoreResultWsDto createLoanScore(@RequestBody LoanScoreResultWsDto request){
        return loanScoreResultService.createLoanScore(request);
    }

    @PostMapping("/delete")
    @ResponseBody
    public LoanScoreResultWsDto deleteLoanScore(@RequestBody LoanScoreResultWsDto loanScoreWsDto){
            for(LoanScoreResultDto loanScoreDto : loanScoreWsDto.getLoanScoreDtos()){
            loanScoreRepository.deleteByRecordId(loanScoreDto.getRecordId());
            }
        loanScoreWsDto.setMessage("Data deleted successfully");
        loanScoreWsDto.setBaseUrl(ADMIN_LOANSCORE);
        return loanScoreWsDto;
    }

    @PostMapping("/getedit")
    @ResponseBody
    public LoanScoreResultWsDto getActiveLoanScore(@RequestBody LoanScoreResultWsDto request){
        LoanScoreResultWsDto loanScoreResultWsDto=new LoanScoreResultWsDto();
        List<LoanScoreResult> loanScoreList=new ArrayList<>();
        for(LoanScoreResultDto loanScoreResultDto: request.getLoanScoreDtos()){
           loanScoreList.add(loanScoreRepository.findByRecordId(loanScoreResultDto.getRecordId()));
        }
        Type listType = new TypeToken<List<LoanScoreResultDto>>() {}.getType();
        loanScoreResultWsDto.setLoanScoreDtos(modelMapper.map(loanScoreList, listType));
        loanScoreResultWsDto.setBaseUrl(ADMIN_LOANSCORE);
        return loanScoreResultWsDto;
    }
    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new LoanScoreResult());
    }
}
