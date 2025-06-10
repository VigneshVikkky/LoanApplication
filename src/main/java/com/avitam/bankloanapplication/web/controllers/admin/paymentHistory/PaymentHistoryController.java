package com.avitam.bankloanapplication.web.controllers.admin.paymentHistory;

import com.avitam.bankloanapplication.model.dto.*;
import com.avitam.bankloanapplication.model.entity.Customer;
import com.avitam.bankloanapplication.model.entity.Loan;
import com.avitam.bankloanapplication.model.entity.LoanLimit;
import com.avitam.bankloanapplication.model.entity.LoanType;
import com.avitam.bankloanapplication.model.entity.PaymentHistory;
import com.avitam.bankloanapplication.repository.CustomerRepository;
import com.avitam.bankloanapplication.repository.LoanLimitRepository;
import com.avitam.bankloanapplication.repository.LoanRepository;
import com.avitam.bankloanapplication.repository.LoanTypeRepository;
import com.avitam.bankloanapplication.repository.PaymentHistoryRepository;
import com.avitam.bankloanapplication.service.LoanService;
import com.avitam.bankloanapplication.service.PaymentHistoryService;
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
@RequestMapping("/loans/paymentHistory")
public class PaymentHistoryController extends BaseController {
    @Autowired
    private PaymentHistoryService paymentHistoryService;
    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private LoanLimitRepository loanLimitRepository;
    @Autowired
    private LoanTypeRepository loanTypeRepository;
    @Autowired
    private CustomerRepository customerRepository;


    private static final String ADMIN_PAYMENTHISTORY = "/loans/paymentHistory";

    @PostMapping
    public PaymentHistoryWsDto getAllPaymentHistory(@RequestBody PaymentHistoryWsDto paymentHistoryWsDto) {
        Pageable pageable = getPageable(paymentHistoryWsDto.getPage(), paymentHistoryWsDto.getSizePerPage(), paymentHistoryWsDto.getSortDirection(), paymentHistoryWsDto.getSortField());
        PaymentHistoryDto paymentHistoryDto = CollectionUtils.isNotEmpty(paymentHistoryWsDto.getPaymentHistoryDtoList()) ? paymentHistoryWsDto.getPaymentHistoryDtoList().get(0) : new PaymentHistoryDto();
        PaymentHistory paymentHistory = modelMapper.map(paymentHistoryDto, PaymentHistory.class);
        Page<PaymentHistory> page = isSearchActive(paymentHistory) != null ? paymentHistoryRepository.findAll(Example.of(paymentHistory), pageable) : paymentHistoryRepository.findAll(pageable);
        Type listType = new TypeToken<List<PaymentHistoryDto>>() {}.getType();
        paymentHistoryWsDto.setPaymentHistoryDtoList(modelMapper.map(page.getContent(), listType));
        paymentHistoryWsDto.setBaseUrl(ADMIN_PAYMENTHISTORY);
        paymentHistoryWsDto.setTotalPages(page.getTotalPages());
        paymentHistoryWsDto.setTotalRecords(page.getTotalElements());
        return paymentHistoryWsDto;
    }

    @PostMapping("/getPaymentHistoryByLoanId")
    public PaymentHistoryDto getPaymentHistoryByLoanId(@RequestBody PaymentHistoryDto paymentHistoryDto){

        PaymentHistory paymentHistory = paymentHistoryRepository.findByLoan_RecordId(paymentHistoryDto.getLoan().getRecordId());
        Type listType = new TypeToken<PaymentHistoryDto>() {}.getType();
        paymentHistoryDto = modelMapper.map(paymentHistory, listType);
        return paymentHistoryDto;
    }


    @PostMapping("/edit")
    public PaymentHistoryWsDto createPaymentHistory(@RequestBody PaymentHistoryWsDto request) {
        return paymentHistoryService.createPaymentHistory(request);

    }

    @PostMapping("/delete")
    public PaymentHistoryWsDto deletePaymentHistory(@RequestBody PaymentHistoryWsDto request) {
        for (PaymentHistoryDto paymentHistoryDto : request.getPaymentHistoryDtoList()) {
            paymentHistoryRepository.deleteByRecordId(paymentHistoryDto.getRecordId());
        }
        request.setMessage("Data deleted Successfully");
        request.setBaseUrl(ADMIN_PAYMENTHISTORY);
        return request;

    }

    @GetMapping("/get")
    public PaymentHistoryWsDto getPaymentHistoryById() {
        PaymentHistoryWsDto paymentHistoryWsDto = new PaymentHistoryWsDto();
        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findByStatus(true);
        Type listType = new TypeToken<List<PaymentHistoryDto>>() {}.getType();
        paymentHistoryWsDto.setPaymentHistoryDtoList(modelMapper.map(paymentHistoryList, listType));
        paymentHistoryWsDto.setBaseUrl(ADMIN_PAYMENTHISTORY);
        return paymentHistoryWsDto;
    }


    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Loan());
    }

//    @PostMapping("/updatePaymentStatus")
//    @ResponseBody
//    public PaymentHistoryDto updatePaymentStatus(@RequestBody LoanDto loanDto) {
//        return loanService.updatePaymentStatus(loanDto);
//    }
//
//    @PostMapping("/getPaymentHistory")
//    @ResponseBody
//    public LoanDto getPaymentHistory(@RequestBody LoanDto loanDto) {
//        return loanDto;
//    }

}

