package com.avitam.bankloanapplication.web.controllers.admin.kyc;

import com.avitam.bankloanapplication.model.dto.*;

import com.avitam.bankloanapplication.model.entity.KYC;
import com.avitam.bankloanapplication.repository.KYCRepository;
import com.avitam.bankloanapplication.service.KycService;
import com.avitam.bankloanapplication.web.controllers.BaseController;
import org.apache.commons.collections4.CollectionUtils;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


import java.lang.reflect.Type;
import java.util.List;


@RestController
@RequestMapping("/admin/Kyc")
public class KYCController extends BaseController {
    @Autowired
    private KYCRepository kycRepository;

    @Autowired
    private KycService kycService;

    @Autowired
    private ModelMapper modelMapper;

    private static final String ADMIN_KYC="/admin/Kyc";

    @PostMapping
    public KYCWsDto getAllKYC(@RequestBody KYCWsDto kycWsDto){
        Pageable pageable = getPageable(kycWsDto.getPage(),kycWsDto.getSizePerPage(),kycWsDto.getSortDirection(),kycWsDto.getSortField());
        KYCDto kycDto = CollectionUtils.isNotEmpty(kycWsDto.getKycDtoList()) ? kycWsDto.getKycDtoList().get(0): new KYCDto();
        KYC kyc = modelMapper.map(kycDto, KYC.class);
        Page<KYC> page= isSearchActive(kyc)!= null ? kycRepository.findAll(Example.of(kyc),pageable) : kycRepository.findAll(pageable);
        Type listType = new TypeToken<List<KYCDto>>() {}.getType();
        kycWsDto.setKycDtoList(modelMapper.map(page.getContent(), listType));
        kycWsDto.setBaseUrl(ADMIN_KYC);
        kycWsDto.setTotalPages(page.getTotalPages());
        kycWsDto.setTotalRecords(page.getTotalElements());
        return kycWsDto;
    }
    @GetMapping("/get")
    public KYCWsDto getActiveKYC(){
        KYCWsDto kycWsDto = new KYCWsDto();
        Type listType = new TypeToken<List<KYCDto>>() {}.getType();
        kycWsDto.setKycDtoList(modelMapper.map(kycRepository.findByStatusOrderByIdentifier(true),listType));
        kycWsDto.setBaseUrl(ADMIN_KYC);
        return kycWsDto;

//        KYCWsDto kycWsDto = new KYCWsDto();
//        List<KYCDto> kycDtos;
//        List<KYC> kycs = kycRepository.findByStatusOrderByIdentifier(true);
//        kycDtos=modelMapper.map(kycs,List.class);
//        kycWsDto.setKycDtoList(kycDtos);
//        kycWsDto.setBaseUrl(ADMIN_KYC);
//        return kycWsDto;
    }
    @PostMapping(value = "/edit" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public KYCWsDto handelEdit(@ModelAttribute KYCWsDto request){
        return kycService.handelEdit(request);
    }

    @PostMapping("getedit")
    public KYCWsDto edit(@RequestBody KYCWsDto request){
        KYCWsDto kycWsDto = new KYCWsDto();
        kycWsDto.setBaseUrl(ADMIN_KYC);
        KYC kyc = kycRepository.findByRecordId(request.getRecordId());
        Type listType = new TypeToken<List<KYCDto>>() {}.getType();
        kycWsDto.setKycDtoList(modelMapper.map(kyc,listType));
        return kycWsDto;
    }

    @PostMapping("/add")
    public KYCWsDto addKyc(){
        KYCWsDto kycWsDto = new KYCWsDto();
        Type listType = new TypeToken<List<KYCDto>>() {}.getType();
        kycWsDto.setKycDtoList(modelMapper.map(kycRepository.findByStatusOrderByIdentifier(true),listType));
        kycWsDto.setBaseUrl(ADMIN_KYC);
        return kycWsDto;
    }

    @PostMapping("/delete")
    public KYCWsDto deleteKyc(@RequestBody KYCWsDto kycWsDto){
        for (KYCDto kycDto : kycWsDto.getKycDtoList()) {
            kycRepository.deleteByRecordId(kycDto.getRecordId());
        }
        kycWsDto.setMessage("Data deleted successfully");
        kycWsDto.setBaseUrl(ADMIN_KYC);
        return kycWsDto;
    }
    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new KYC());
    }

}
