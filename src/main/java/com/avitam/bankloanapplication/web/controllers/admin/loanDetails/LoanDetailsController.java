package com.avitam.bankloanapplication.web.controllers.admin.loanDetails;

import com.avitam.bankloanapplication.model.dto.LoanApplicationDto;
import com.avitam.bankloanapplication.model.dto.LoanDetailsDto;
import com.avitam.bankloanapplication.model.dto.LoanDetailsWsDto;
import com.avitam.bankloanapplication.model.dto.SearchDto;
import com.avitam.bankloanapplication.model.entity.Loan;
import com.avitam.bankloanapplication.model.entity.LoanDetails;
import com.avitam.bankloanapplication.repository.LoanDetailsRepository;
import com.avitam.bankloanapplication.service.LoanDetailsService;
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
import java.util.List;

@RestController
@RequestMapping("/loans/loanDetails")
public class LoanDetailsController extends BaseController {
    @Autowired
    private LoanDetailsService loanDetailsService;
    @Autowired
    private LoanDetailsRepository loanDetailsRepository;
    @Autowired
    private ModelMapper modelMapper;

    private static final String ADMIN_LOANDETAILS = "/loans/loanDetails";

    @PostMapping
    public LoanDetailsWsDto getAllLoan(@RequestBody LoanDetailsWsDto loanDetailsWsDto) {
        Pageable pageable = getPageable(loanDetailsWsDto.getPage(), loanDetailsWsDto.getSizePerPage(), loanDetailsWsDto.getSortDirection(), loanDetailsWsDto.getSortField());
        LoanDetailsDto loanDetailsDto = CollectionUtils.isNotEmpty(loanDetailsWsDto.getLoanDetailsDtos()) ? loanDetailsWsDto.getLoanDetailsDtos().get(0) : new LoanDetailsDto();
        LoanDetails loanDetails = modelMapper.map(loanDetailsDto, LoanDetails.class);
        Page<LoanDetails> page = isSearchActive(loanDetails) != null ? loanDetailsRepository.findAll(Example.of(loanDetails), pageable) : loanDetailsRepository.findAll(pageable);
        Type listType = new TypeToken<List<LoanDetailsDto>>() {}.getType();
        loanDetailsWsDto.setLoanDetailsDtos(modelMapper.map(page.getContent(), listType));
        loanDetailsWsDto.setBaseUrl(ADMIN_LOANDETAILS);
        loanDetailsWsDto.setTotalPages(page.getTotalPages());
        loanDetailsWsDto.setTotalRecords(page.getTotalElements());
        return loanDetailsWsDto;
    }

    @PostMapping("/edit")
    public LoanDetailsWsDto createLoan(@RequestBody LoanDetailsWsDto request) {
        return loanDetailsService.createLoan(request);

    }

    @PostMapping("/delete")
    public LoanDetailsWsDto deleteLoan(@RequestBody LoanDetailsWsDto request) {
        for (LoanDetailsDto loanDto : request.getLoanDetailsDtos()) {
            loanDetailsRepository.deleteByRecordId(loanDto.getRecordId());
        }
        request.setMessage("Data deleted Successfully");
        request.setBaseUrl(ADMIN_LOANDETAILS);
        return request;

    }

    @GetMapping("/get")
    public LoanDetailsWsDto getLoanDetailsById() {
        LoanDetailsWsDto loanDetailsWsDto = new LoanDetailsWsDto();
        List<LoanDetails> loans = loanDetailsRepository.findByStatus(true);
        Type listType = new TypeToken<List<LoanDetailsDto>>() {}.getType();
        loanDetailsWsDto.setLoanDetailsDtos(modelMapper.map(loans, listType));
        loanDetailsWsDto.setBaseUrl(ADMIN_LOANDETAILS);
        return loanDetailsWsDto;
    }

   /* @GetMapping("/getByLoanType")
    public LoanWsDto getByLoanType(@RequestParam String loanType) {
        LoanWsDto loanWsDto = new LoanWsDto();
        List<Loan> loans = loanRepository.findByLoanTypeAndStatus(loanType, true);
        loanWsDto.setLoanDtoList(modelMapper.map(loans, List.class));
        loanWsDto.setBaseUrl(ADMIN_LOAN);
        return loanWsDto;
    }*/

    @PostMapping("/getByLoanRecordId")
    public LoanDetailsWsDto getByRecordId(@RequestBody LoanDetailsDto loanDetailsDto) {
        LoanDetailsWsDto loanDetailsWsDto = new LoanDetailsWsDto();
        List<LoanDetails> loanDetails = loanDetailsRepository.findByRecordIdAndStatus(loanDetailsDto.getRecordId(),true);
        Type listType = new TypeToken<List<LoanDetailsDto>>() {}.getType();
        loanDetailsWsDto.setLoanDetailsDtos(modelMapper.map(loanDetails,listType));
        loanDetailsWsDto.setBaseUrl(ADMIN_LOANDETAILS);
        return loanDetailsWsDto;
    }

    @PostMapping("/getByLoanId")
    public LoanDetailsDto getByLoanId(@RequestBody LoanDetailsDto loanDetailsDto) {
       // LoanDetailsDto loanDetailsDto = new LoanDetailsDto();
       LoanDetails loanDetails = loanDetailsRepository.findByLoanId(loanDetailsDto.getLoanId());
        Type listType = new TypeToken<LoanDetailsDto>() {}.getType();
        loanDetailsDto = modelMapper.map(loanDetails,listType);
        loanDetailsDto.setBaseUrl(ADMIN_LOANDETAILS);
        return loanDetailsDto;
    }

//    //TODO: need enum-string fix in LoanService
//    @PatchMapping("/update/{loanId}")
//    public ResponseEntity<Integer> updateLoanPartially(@PathVariable Long loanId, @RequestBody Map<Object, Object> objectMap) {
//        loanService.updateLoanPartially(loanId, objectMap);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Loan());
    }

//    @PostMapping("/getUptoDateEmi")
//    @ResponseBody
//    public LoanDetailsWsDto getUptoDateEmi(){
//      return  loanDetailsService.getUptoDateEmiDetails();
//
//    }

//    @PostMapping("/getEmiStatusTillDate")
//    @ResponseBody
//    public LoanDetailsDto getEmiStatusTillDate(@RequestBody LoanDetailsDto loanDetailsDto) {
//
//        return loanDetailsService.getEmiStatusTillDate(loanDetailsDto);
//
//    }

}
