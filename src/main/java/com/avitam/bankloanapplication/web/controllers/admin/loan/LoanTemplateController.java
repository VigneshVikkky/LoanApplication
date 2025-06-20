package com.avitam.bankloanapplication.web.controllers.admin.loan;

import com.avitam.bankloanapplication.model.dto.LoanDto;
import com.avitam.bankloanapplication.model.dto.LoanTemplateDto;
import com.avitam.bankloanapplication.model.dto.LoanTemplateWsDto;
import com.avitam.bankloanapplication.model.dto.SearchDto;
import com.avitam.bankloanapplication.model.entity.Loan;
import com.avitam.bankloanapplication.model.entity.LoanLimit;
import com.avitam.bankloanapplication.model.entity.LoanTemplate;
import com.avitam.bankloanapplication.repository.CustomerRepository;
import com.avitam.bankloanapplication.repository.LoanLimitRepository;
import com.avitam.bankloanapplication.repository.LoanTemplateRepository;
import com.avitam.bankloanapplication.repository.LoanTypeRepository;
import com.avitam.bankloanapplication.service.LoanTemplateService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/loans/loantemplate")
public class LoanTemplateController extends BaseController {
    private static final String ADMIN_LOAN = "/loans/loantemplate";
    @Autowired
    private LoanTemplateService loanTemplateService;
    @Autowired
    private LoanTemplateRepository loanTemplateRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private LoanLimitRepository loanLimitRepository;
    @Autowired
    private LoanTypeRepository loanTypeRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping
    public LoanTemplateWsDto getAllLoan(@RequestBody LoanTemplateWsDto loanTemplateWsDto) {
        Pageable pageable = getPageable(loanTemplateWsDto.getPage(), loanTemplateWsDto.getSizePerPage(), loanTemplateWsDto.getSortDirection(), loanTemplateWsDto.getSortField());
        LoanTemplateDto loanTemplateDto = CollectionUtils.isNotEmpty(loanTemplateWsDto.getLoanTemplateDtoList()) ? loanTemplateWsDto.getLoanTemplateDtoList().get(0) : new LoanTemplateDto();
        LoanTemplate loanTemplate = modelMapper.map(loanTemplateDto, LoanTemplate.class);
        Page<LoanTemplate> page = isSearchActive(loanTemplate) != null ? loanTemplateRepository.findAll(Example.of(loanTemplate), pageable) : loanTemplateRepository.findAll(pageable);
        Type listType = new TypeToken<List<LoanTemplateDto>>() {
        }.getType();
        loanTemplateWsDto.setLoanTemplateDtoList(modelMapper.map(page.getContent(), listType));
        loanTemplateWsDto.setBaseUrl(ADMIN_LOAN);
        loanTemplateWsDto.setTotalPages(page.getTotalPages());
        loanTemplateWsDto.setTotalRecords(page.getTotalElements());
        return loanTemplateWsDto;

    }

    @GetMapping("/getEligibleLoans")
    public List<LoanTemplateDto> getEligibleLoans(@RequestParam String customerId, @RequestParam String loanTypeId) {
        List<LoanTemplateDto> loanTemplateDtoList = new ArrayList<>();
        LoanLimit loanLimit = loanLimitRepository.findByCustomerId(customerId);
        if (loanLimit != null) {
            List<LoanTemplate> loanTemplateList = loanTemplateRepository.findByLoanTypeAndStatus(loanTypeId, true);
            Double loanLimitAmt = loanLimit.getLoanLimitAmount();
            Type listType = new TypeToken<List<LoanTemplateDto>>() {
            }.getType();
            loanTemplateDtoList.addAll(modelMapper.map(loanTemplateList.stream().filter(loan -> loanLimitAmt >
                    loan.getDesiredLoan()).collect(Collectors.toList()), listType));
        }
        return loanTemplateDtoList;
    }


    @PostMapping("/edit")
    public LoanTemplateWsDto createLoan(@RequestBody LoanTemplateWsDto request) {
        return loanTemplateService.createLoan(request);
    }

    @PostMapping("/delete")
    public LoanTemplateWsDto deleteLoan(@RequestBody LoanTemplateWsDto request) {
        for (LoanTemplateDto loanTemplateDto : request.getLoanTemplateDtoList()) {
            loanTemplateRepository.deleteByRecordId(loanTemplateDto.getRecordId());
        }
        request.setMessage("Data deleted Successfully");
        request.setBaseUrl(ADMIN_LOAN);
        return request;
    }

    @GetMapping("/get")
    public LoanTemplateWsDto getLoanById() {
        LoanTemplateWsDto loanTemplateWsDto = new LoanTemplateWsDto();
        List<LoanTemplate> loans = loanTemplateRepository.findByStatus(true);
        Type listType = new TypeToken<List<LoanTemplateDto>>() {
        }.getType();
        loanTemplateWsDto.setLoanTemplateDtoList(modelMapper.map(loans, listType));
        loanTemplateWsDto.setBaseUrl(ADMIN_LOAN);
        return loanTemplateWsDto;
    }

    @GetMapping("/getByLoanType")
    public LoanTemplateWsDto getByLoanType(@RequestParam String loanType) {
        LoanTemplateWsDto loanTemplateWsDto = new LoanTemplateWsDto();
        List<LoanTemplate> loans = loanTemplateRepository.findByLoanTypeAndStatus(loanType, true);
        Type listType = new TypeToken<List<LoanTemplateDto>>() {
        }.getType();
        loanTemplateWsDto.setLoanTemplateDtoList(modelMapper.map(loans, listType));
        loanTemplateWsDto.setBaseUrl(ADMIN_LOAN);
        return loanTemplateWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new LoanTemplate());
    }
}





