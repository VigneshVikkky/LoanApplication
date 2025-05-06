package com.avitam.bankloanapplication.web.controllers.admin.loanlimit;

import com.avitam.bankloanapplication.model.dto.*;
import com.avitam.bankloanapplication.model.entity.LoanLimit;
import com.avitam.bankloanapplication.repository.LoanLimitRepository;
import com.avitam.bankloanapplication.service.LoanLimitService;
import com.avitam.bankloanapplication.web.controllers.BaseController;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/loans/loanLimit")
public class LoanLimitController extends BaseController {
   @Autowired
   LoanLimitService loanLimitService;
    @Autowired
    private LoanLimitRepository loanLimitRepository;
    @Autowired
    private ModelMapper modelMapper;

    private static final String ADMIN_LOANLIMIT = "/loans/loanLimit" ;

    @PostMapping
    public LoanLimitWsDto getAllLoanTypes(@RequestBody LoanLimitWsDto loanLimitWsDto){
        Pageable pageable=getPageable(loanLimitWsDto.getPage(),loanLimitWsDto.getSizePerPage(),loanLimitWsDto.getSortDirection(),loanLimitWsDto.getSortField());
        LoanLimitDto loanLimitDto = CollectionUtils.isNotEmpty(loanLimitWsDto.getLoanLimitDtos()) ? loanLimitWsDto.getLoanLimitDtos().get(0) : new LoanLimitDto();
        LoanLimit loanLimit = modelMapper.map(loanLimitWsDto, LoanLimit.class);
        Page<LoanLimit> page=isSearchActive(loanLimit) !=null ? loanLimitRepository.findAll(Example.of(loanLimit),pageable) : loanLimitRepository.findAll(pageable);
        loanLimitWsDto.setLoanLimitDtos(modelMapper.map(page.getContent(), List.class));
        loanLimitWsDto.setBaseUrl(ADMIN_LOANLIMIT);
        loanLimitWsDto.setTotalPages(page.getTotalPages());
        loanLimitWsDto.setTotalRecords(page.getTotalElements());
        return loanLimitWsDto;

    }
    @GetMapping("/get")
    public LoanLimitWsDto getLoanLimit(@RequestBody LoanLimitWsDto request) {
        LoanLimitWsDto loanLimitWsDto = new LoanLimitWsDto();
        List<LoanLimit> loanLimits = new ArrayList<>();
        for(LoanLimitDto loanLimitDto: request.getLoanLimitDtos()) {
            loanLimits.add(loanLimitRepository.findByRecordId(loanLimitDto.getRecordId()));
        }
        loanLimitWsDto.setLoanLimitDtos(modelMapper.map(loanLimits,List.class));
        loanLimitWsDto.setBaseUrl(ADMIN_LOANLIMIT);
        return loanLimitWsDto;
    }

    @PostMapping("/edit")
    public LoanLimitWsDto createLoanLimit(@RequestBody LoanLimitDto request){
        return loanLimitService.editLoanLimit(request);
    }

//    @PostMapping("/getedit")
//    public LoanLimitWsDto getActiveLoanScore(@RequestBody LoanLimitWsDto request){
//        LoanLimitWsDto loanLimitWsDto=new LoanLimitWsDto();
//        List<LoanLimit> loanLimitList=new ArrayList<>();
//        for(LoanLimitDto loanScoreResultDto: request.getLoanLimitDtos()){
//            loanLimitList.add(loanLimitRepository.findByRecordId(loanScoreResultDto.getRecordId()));
//        }
//        loanLimitWsDto.setLoanLimitDtos(modelMapper.map(loanLimitList, List.class));
//        loanLimitWsDto.setBaseUrl(ADMIN_LOANLIMIT);
//        return loanLimitWsDto;
//    }


    @PostMapping("/delete")
    public LoanLimitWsDto delete(@RequestBody LoanLimitWsDto loanLimitWsDto) {
            for(LoanLimitDto loanLimitDto : loanLimitWsDto.getLoanLimitDtos()){
                loanLimitRepository.deleteByRecordId(loanLimitDto.getRecordId());
            }
            loanLimitWsDto.setMessage("Data deleted successfully");
            loanLimitWsDto.setBaseUrl(ADMIN_LOANLIMIT);
            return loanLimitWsDto;
        }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new LoanLimit());
    }

}
