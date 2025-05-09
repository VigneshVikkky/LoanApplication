package com.avitam.bankloanapplication.web.controllers.admin.credit;

import com.avitam.bankloanapplication.model.dto.CreditDto;
import com.avitam.bankloanapplication.model.dto.CreditWsDto;
import com.avitam.bankloanapplication.model.dto.LoanDto;
import com.avitam.bankloanapplication.model.dto.SearchDto;
import com.avitam.bankloanapplication.model.entity.Credit;
import com.avitam.bankloanapplication.repository.CreditRepository;
import com.avitam.bankloanapplication.service.CreditService;
import com.avitam.bankloanapplication.web.controllers.BaseController;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/credit")
public class CreditController extends BaseController {
    @Autowired
    private CreditRepository creditRepository;

    @Autowired
   private ModelMapper modelMapper;

    @Autowired
    private CreditService creditService;

    public static final String ADMIN_CREDIT = "/admin/credit";

    @PostMapping
    public CreditWsDto getAllCredit(@RequestBody CreditWsDto creditWsDto){
        Pageable pageable =getPageable(creditWsDto.getPage(),creditWsDto.getSizePerPage(),creditWsDto.getSortDirection(),creditWsDto.getSortField());
        CreditDto creditDto = CollectionUtils.isNotEmpty(creditWsDto.getCreditDtoList()) ? creditWsDto.getCreditDtoList().get(0):new CreditDto();
        Credit credit = modelMapper.map(creditDto,Credit.class);
        Page<Credit> page = isSearchActive(credit) !=null ? creditRepository.findAll(Example.of(credit),pageable) :creditRepository.findAll(pageable);
        Type listType = new TypeToken<List<CreditDto>>() {}.getType();
        creditWsDto.setCreditDtoList(modelMapper.map(page.getContent(),listType));
        creditWsDto.setTotalPages(page.getTotalPages());
        creditWsDto.setTotalRecords(page.getTotalElements());
        return creditWsDto;

    }
    @GetMapping("/get")
    public CreditWsDto getActiveCredit(){
        CreditWsDto creditWsDto = new CreditWsDto();
        creditWsDto.setCreditDtoList(modelMapper.map(creditRepository.findByStatusOrderByIdentifier(true),List.class));
        creditWsDto.setBaseUrl(ADMIN_CREDIT);
        return creditWsDto;

//        CreditWsDto creditWsDto = new CreditWsDto();
//        List<CreditDto> creditDtoList;
//        List<Credit> credits = creditRepository.findByStatusOrderByIdentifier(true);
//        creditDtoList=modelMapper.map(credits,List.class);
//        creditWsDto.setCreditDtoList(creditDtoList);
//        creditWsDto.setBaseUrl(ADMIN_CREDIT);
//        return creditWsDto;
    }

    @PostMapping("/getEdit")
    public CreditWsDto getEdit(@RequestBody CreditWsDto request){
        CreditWsDto creditwsDto = new CreditWsDto();
        creditwsDto.setBaseUrl(ADMIN_CREDIT);
        Credit credit = creditRepository.findByRecordId(request.getRecordId());
        Type listType = new TypeToken<List<CreditDto>>() {}.getType();
        creditwsDto.setCreditDtoList(modelMapper.map(credit,listType));
        return creditwsDto;
    }
    @PostMapping(value = "/edit" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CreditWsDto handelEdit(@ModelAttribute CreditWsDto request){
        return creditService.handelEdit(request);
    }



    @PostMapping("/delete")
    public CreditWsDto deleteCredit(@RequestBody CreditWsDto creditWsDto){
        for(CreditDto creditDto : creditWsDto.getCreditDtoList()){
            creditRepository.deleteByRecordId(creditDto.getRecordId());
        }
        creditWsDto.setBaseUrl(ADMIN_CREDIT);
        creditWsDto.setMessage("Data Deleted Successfully");
        return creditWsDto;

    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Credit());
    }

}
