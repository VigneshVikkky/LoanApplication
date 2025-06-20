package com.avitam.bankloanapplication.web.controllers.admin.loanType;

import com.avitam.bankloanapplication.model.dto.LoanDto;
import com.avitam.bankloanapplication.model.dto.LoanTypeDto;
import com.avitam.bankloanapplication.model.dto.LoanTypeWsDto;
import com.avitam.bankloanapplication.model.dto.SearchDto;
import com.avitam.bankloanapplication.model.entity.Loan;
import com.avitam.bankloanapplication.model.entity.LoanLimit;
import com.avitam.bankloanapplication.model.entity.LoanTemplate;
import com.avitam.bankloanapplication.model.entity.LoanType;
import com.avitam.bankloanapplication.repository.LoanLimitRepository;
import com.avitam.bankloanapplication.repository.LoanRepository;
import com.avitam.bankloanapplication.repository.LoanTemplateRepository;
import com.avitam.bankloanapplication.repository.LoanTypeRepository;
import com.avitam.bankloanapplication.service.LoanTypeService;
import com.avitam.bankloanapplication.web.controllers.BaseController;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/loans/loanType")
public class LoanTypeController extends BaseController {

    private static final String ADMIN_LOANTYPE = "/loans/loanType";
    @Autowired
    private LoanTypeRepository loanTypeRepository;
    @Autowired
    private LoanTypeService loanTypeService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private LoanTemplateRepository loanTemplateRepository;
    @Autowired
    private LoanLimitRepository loanLimitRepository;

    @PostMapping
    public LoanTypeWsDto getAllLoanTypes(@RequestBody LoanTypeWsDto loanTypeWsDto) {
        Pageable pageable = getPageable(loanTypeWsDto.getPage(), loanTypeWsDto.getSizePerPage(), loanTypeWsDto.getSortDirection(), loanTypeWsDto.getSortField());
        LoanTypeDto loanTypeDto = CollectionUtils.isNotEmpty(loanTypeWsDto.getLoanTypeDtoList()) ? loanTypeWsDto.getLoanTypeDtoList().get(0) : new LoanTypeDto();
        LoanType loanType = modelMapper.map(loanTypeWsDto, LoanType.class);
        Page<LoanType> page = isSearchActive(loanType) != null ? loanTypeRepository.findAll(Example.of(loanType), pageable) : loanTypeRepository.findAll(pageable);
        Type listType = new TypeToken<List<LoanTypeDto>>() {
        }.getType();
        loanTypeWsDto.setLoanTypeDtoList(modelMapper.map(page.getContent(), listType));
        loanTypeWsDto.setBaseUrl(ADMIN_LOANTYPE);
        loanTypeWsDto.setTotalPages(page.getTotalPages());
        loanTypeWsDto.setTotalRecords(page.getTotalElements());
        return loanTypeWsDto;

    }

    @PostMapping("/getEligibleLoanTypes")
    public List<LoanTypeDto> getEligibleLoans(@RequestBody LoanDto loanDto) {
        List<LoanTypeDto> loanTypeDtoList = new ArrayList<>();
        LoanLimit loanLimit = loanLimitRepository.findByCustomerId(loanDto.getCustomerId());
        if (loanLimit != null) {
            List<LoanType> loanTypeList = loanTypeRepository.findAll();
            for (LoanType loanType : loanTypeList) {
                List<LoanTemplate> loanTemplateList = loanTemplateRepository.findByLoanTypeAndStatus(loanType.getRecordId(), true);
                Double loanLimitAmt = loanLimit.getLoanLimitAmount();
                if (loanTemplateList.stream().anyMatch(loanTemplate -> loanLimitAmt > loanTemplate.getDesiredLoan())) {
                    loanTypeDtoList.add(modelMapper.map(loanType, LoanTypeDto.class));
                }
            }
        }
        return loanTypeDtoList;
    }

    @GetMapping("/get")
    public LoanTypeWsDto getLoanType() {
        LoanTypeWsDto loanTypewsDto = new LoanTypeWsDto();
        List<LoanType> loanTypes = loanTypeRepository.findByStatus(true);
        Type listType = new TypeToken<List<LoanTypeDto>>() {
        }.getType();
        loanTypewsDto.setLoanTypeDtoList(modelMapper.map(loanTypes, listType));
        loanTypewsDto.setBaseUrl(ADMIN_LOANTYPE);
        return loanTypewsDto;
    }


    @PostMapping("/edit")
    public LoanTypeWsDto handleEdit(@RequestBody LoanTypeWsDto request) {

        return loanTypeService.handleEdit(request);
    }

    @PostMapping("/delete")
    public LoanTypeWsDto delete(@RequestBody LoanTypeWsDto loanTypeWsDto) {
        for (LoanTypeDto loanTypeDto : loanTypeWsDto.getLoanTypeDtoList()) {
            loanTypeRepository.deleteByRecordId(loanTypeDto.getRecordId());
        }
        loanTypeWsDto.setMessage("Data deleted successfully");
        loanTypeWsDto.setBaseUrl(ADMIN_LOANTYPE);
        return loanTypeWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new LoanType());
    }
}