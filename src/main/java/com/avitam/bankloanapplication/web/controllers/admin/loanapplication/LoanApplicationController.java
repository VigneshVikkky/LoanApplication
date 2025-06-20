package com.avitam.bankloanapplication.web.controllers.admin.loanapplication;

import com.avitam.bankloanapplication.model.dto.LoanApplicationDto;
import com.avitam.bankloanapplication.model.dto.LoanApplicationWsDto;
import com.avitam.bankloanapplication.model.dto.LoanDetailsDto;
import com.avitam.bankloanapplication.model.dto.LoanDetailsWsDto;
import com.avitam.bankloanapplication.model.dto.LoanDto;
import com.avitam.bankloanapplication.model.dto.LoanTemplateDto;
import com.avitam.bankloanapplication.model.dto.LoanWsDto;
import com.avitam.bankloanapplication.model.dto.SearchDto;
import com.avitam.bankloanapplication.model.entity.Loan;
import com.avitam.bankloanapplication.model.entity.LoanApplication;
import com.avitam.bankloanapplication.model.entity.LoanTemplate;
import com.avitam.bankloanapplication.repository.LoanApplicationRepository;
import com.avitam.bankloanapplication.repository.LoanDetailsRepository;
import com.avitam.bankloanapplication.repository.LoanRepository;
import com.avitam.bankloanapplication.repository.LoanTemplateRepository;
import com.avitam.bankloanapplication.service.LoanApplicationService;
import com.avitam.bankloanapplication.service.LoanDetailsService;
import com.avitam.bankloanapplication.service.LoanService;
import com.avitam.bankloanapplication.service.impl.LoanApplicationServiceImpl;
import com.avitam.bankloanapplication.web.controllers.BaseController;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/loans/loanApplication")
public class LoanApplicationController extends BaseController {

    public static final String ADMIN_LOANAPPLICATION = "/loans/loanApplication";
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private LoanApplicationRepository loanApplicationRepository;
    @Autowired
    private LoanDetailsService loanDetailsService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private LoanApplicationServiceImpl loanApplicationServiceImpl;
    @Autowired
    private LoanDetailsRepository loanDetailsRepository;
    @Autowired
    private LoanService loanService;
    @Autowired
    private LoanTemplateRepository loanTemplateRepository;

    @PostMapping
    @ResponseBody
    public LoanApplicationWsDto getAllLoanApplications(@RequestBody LoanApplicationWsDto loanApplicationWsDto) {
        Pageable pageable = getPageable(loanApplicationWsDto.getPage(), loanApplicationWsDto.getSizePerPage(), loanApplicationWsDto.getSortDirection(), loanApplicationWsDto.getSortField());
        LoanApplicationDto loanApplicationDto = CollectionUtils.isNotEmpty(loanApplicationWsDto.getLoanApplicationDtos()) ? loanApplicationWsDto.getLoanApplicationDtos().get(0) : new LoanApplicationDto();
        LoanApplication loanApplication = modelMapper.map(loanApplicationDto, LoanApplication.class);
        Page<LoanApplication> page = isSearchActive(loanApplication) != null ? loanApplicationRepository.findAll(Example.of(loanApplication), pageable) : loanApplicationRepository.findAll(pageable);
        Type listType = new TypeToken<List<LoanApplicationDto>>() {
        }.getType();
        loanApplicationWsDto.setLoanApplicationDtos(modelMapper.map(page.getContent(), listType));
        loanApplicationWsDto.setBaseUrl(ADMIN_LOANAPPLICATION);
        loanApplicationWsDto.setTotalPages(page.getTotalPages());
        loanApplicationWsDto.setTotalRecords(page.getTotalElements());
        return loanApplicationWsDto;
    }

    @GetMapping("/get")
    @ResponseBody
    public LoanApplicationWsDto getLoanType() {
        LoanApplicationWsDto loanApplicationWsDto = new LoanApplicationWsDto();
        Type listType = new TypeToken<List<LoanApplicationDto>>() {
        }.getType();
        loanApplicationWsDto.setLoanApplicationDtos(modelMapper.map(loanApplicationRepository.findByStatusOrderByIdentifier(true), listType));
        loanApplicationWsDto.setBaseUrl(ADMIN_LOANAPPLICATION);
        return loanApplicationWsDto;
    }

    @PostMapping("/getLoansByStatusAndId")
    @ResponseBody
    public LoanApplicationWsDto getLoansByStatusAndId(@RequestBody LoanApplicationDto loanApplicationDto) {
        LoanApplicationWsDto loanApplicationWsDto = new LoanApplicationWsDto();
        List<LoanApplication> loanApplicationList = loanApplicationRepository.findByCustomerIdAndLoanStatus(loanApplicationDto.getCustomerId(), loanApplicationDto.getLoanStatus());
        Type listType = new TypeToken<List<LoanApplicationDto>>() {
        }.getType();
        List<LoanApplicationDto> loanApplicationDtoList = modelMapper.map(loanApplicationList, listType);
        List<LoanApplicationDto> loanApplicationDtos = new ArrayList<>();
        for (LoanApplicationDto loanApplicationDto1 : loanApplicationDtoList) {
            loanApplicationDto1.setLoanTemplateDto(modelMapper.map(loanTemplateRepository.findByRecordId(loanApplicationDto1.getLoanId()), LoanTemplateDto.class));
            loanApplicationDtos.add(loanApplicationDto1);
        }
        loanApplicationWsDto.setLoanApplicationDtos(loanApplicationDtos);
        loanApplicationWsDto.setBaseUrl(ADMIN_LOANAPPLICATION);
        return loanApplicationWsDto;
    }

    @PostMapping("/getLoansByStatus")
    @ResponseBody
    public LoanApplicationWsDto getLoanStatus(@RequestBody LoanApplicationDto loanApplicationDto) {

        LoanApplicationWsDto loanApplicationWsDto = new LoanApplicationWsDto();
        List<LoanApplication> loanApplicationList = loanApplicationRepository.findByLoanStatus(loanApplicationDto.getLoanStatus());
        Type listType = new TypeToken<List<LoanApplicationDto>>() {
        }.getType();
        List<LoanApplicationDto> loanApplicationDtoList = modelMapper.map(loanApplicationList, listType);
        List<LoanApplicationDto> loanApplicationDtos = new ArrayList<>();
        for (LoanApplicationDto loanApplicationDto1 : loanApplicationDtoList) {
            loanApplicationDto1.setLoanTemplateDto(modelMapper.map(loanTemplateRepository.findByRecordId(loanApplicationDto1.getLoanId()), LoanTemplateDto.class));
            loanApplicationDtos.add(loanApplicationDto1);
        }
        loanApplicationWsDto.setLoanApplicationDtos(loanApplicationDtos);
        loanApplicationWsDto.setBaseUrl(ADMIN_LOANAPPLICATION);
        return loanApplicationWsDto;
    }

        @PostMapping("/updateLoansStatus")
    @ResponseBody
    public LoanApplicationWsDto updateLoansStatus(@RequestBody LoanApplicationDto loanApplicationDto) {
        LoanApplicationWsDto loanApplicationWsDto = new LoanApplicationWsDto();
        LoanApplication loanApplication = loanApplicationRepository.findByRecordId(loanApplicationDto.getRecordId());
        loanApplication.setLoanStatus(loanApplicationDto.getLoanStatus());
        loanApplicationRepository.save(loanApplication);
        if (loanApplication.getLoanStatus().equalsIgnoreCase("Approved")) {
            LoanTemplateDto loanTemplateDto = modelMapper.map(loanTemplateRepository.findByRecordId(loanApplication.getLoanId()), LoanTemplateDto.class);
            loanTemplateDto.setRecordId(null);
            LoanWsDto loanWsDto = new LoanWsDto();
            LoanDto loanDto = modelMapper.map(loanTemplateDto, LoanDto.class);
            loanDto.setRecordId(null);
            loanWsDto.setLoanDtoList(List.of(loanDto));
            LoanWsDto loanWsDto1 = loanService.createLoan(loanWsDto);
            Loan loan = new Loan();
            List<Loan> loanList = modelMapper.map(loanWsDto1.getLoanDtoList(), List.class);
            for(Loan loanDto1 : loanList){
                loan = loanRepository.findByRecordId(loanDto1.getRecordId());
            }
            LoanDetailsWsDto loanDetailsWsDto = new LoanDetailsWsDto();
            LoanDetailsDto loanDetailsDto = new LoanDetailsDto();
            loanDetailsDto.setLoanId(loan.getRecordId());
            loanDetailsWsDto.setLoanDetailsDtos(List.of(loanDetailsDto));
            loanDetailsService.createLoan(loanDetailsWsDto);
        }
        loanApplicationWsDto.setMessage("Loan status updated successfully!!");
        loanApplicationWsDto.setBaseUrl(ADMIN_LOANAPPLICATION);
        return loanApplicationWsDto;
    }



    @PostMapping("/getedit")
    @ResponseBody
    public LoanApplicationWsDto editLoanApplication(@RequestBody LoanApplicationWsDto request) {
        LoanApplicationWsDto loanApplicationWsDto = new LoanApplicationWsDto();
        List<LoanApplication> loanApplications = new ArrayList<>();
        for (LoanApplicationDto loanApplicationDto : request.getLoanApplicationDtos()) {
            loanApplications.add(loanApplicationRepository.findByRecordId(loanApplicationDto.getRecordId()));
        }
        Type listType = new TypeToken<List<LoanApplicationDto>>() {
        }.getType();
        loanApplicationWsDto.setLoanApplicationDtos(modelMapper.map(loanApplications, listType));
        loanApplicationWsDto.setBaseUrl(ADMIN_LOANAPPLICATION);
        return loanApplicationWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public LoanApplicationWsDto handleEdit(@RequestBody LoanApplicationWsDto request) {
        return loanApplicationService.handleEdit(request);
    }

    @GetMapping("/getLoan")
    @ResponseBody
    public LoanWsDto getLoanApplicationResult(@RequestBody LoanApplicationWsDto request) {
        return loanApplicationServiceImpl.getLoanApplicationResult(request);
    }

    @GetMapping("/add")
    @ResponseBody
    public LoanApplicationWsDto addLoanApplication() {
        LoanApplicationWsDto loanApplicationWsDto = new LoanApplicationWsDto();
        List<LoanApplicationDto> loanApplicationDtoList = new ArrayList<>();
        loanApplicationDtoList.add(loanApplicationRepository.findByStatusOrderByIdentifier(true));
        loanApplicationWsDto.setLoanApplicationDtos(loanApplicationDtoList);
        loanApplicationWsDto.setBaseUrl(ADMIN_LOANAPPLICATION);
        return loanApplicationWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public LoanApplicationWsDto deleteLoanApplication(@RequestBody LoanApplicationWsDto loanApplicationWsDto) {

        for (LoanApplicationDto loanApplicationDto1 : loanApplicationWsDto.getLoanApplicationDtos()) {
            loanApplicationRepository.deleteByRecordId(loanApplicationDto1.getRecordId());
        }
        loanApplicationWsDto.setMessage("Data deleted successfully");
        loanApplicationWsDto.setBaseUrl(ADMIN_LOANAPPLICATION);
        return loanApplicationWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new LoanApplication());
    }


//    @GetMapping("/emi-summary/{recordId}")
//    public List<LoanEmiSummaryDto> getEMISummary(@PathVariable String recordId) {
//        return loanDetailsService.getLoanEmiSummary(recordId);
//    }


}
