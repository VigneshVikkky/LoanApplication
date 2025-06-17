package com.avitam.bankloanapplication.web.controllers.admin.websiteSetting;

import com.avitam.bankloanapplication.core.service.CoreService;
import com.avitam.bankloanapplication.model.dto.SearchDto;
import com.avitam.bankloanapplication.model.dto.WebsiteSettingDto;
import com.avitam.bankloanapplication.model.dto.WebsiteSettingWsDto;
import com.avitam.bankloanapplication.model.entity.WebsiteSetting;
import com.avitam.bankloanapplication.repository.WebsiteSettingRepository;
import com.avitam.bankloanapplication.service.WebsiteSettingService;
import com.avitam.bankloanapplication.web.controllers.BaseController;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/admin/websitesetting")
public class WebsiteSettingController extends BaseController {

    private static final String ADMIN_WEBSITESETTING = "/admin/websitesetting";
    @Autowired
    private WebsiteSettingRepository websiteSettingRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CoreService coreService;
    @Autowired
    private WebsiteSettingService websiteSettingService;

    @PostMapping
    public WebsiteSettingWsDto getAllSetting(@RequestBody WebsiteSettingWsDto websiteSettingWsDto) {
        Pageable pageable = getPageable(websiteSettingWsDto.getPage(), websiteSettingWsDto.getSizePerPage(), websiteSettingWsDto.getSortDirection(), websiteSettingWsDto.getSortField());
        WebsiteSettingDto websiteSettingDto = CollectionUtils.isNotEmpty(websiteSettingWsDto.getWebSiteSettingDtoList()) ? websiteSettingWsDto.getWebSiteSettingDtoList().get(0) : new WebsiteSettingDto();
        WebsiteSetting websiteSetting = modelMapper.map(websiteSettingDto, WebsiteSetting.class);
        Page<WebsiteSetting> page = isSearchActive(websiteSetting) != null ? websiteSettingRepository.findAll(Example.of(websiteSetting), pageable) : websiteSettingRepository.findAll(pageable);
        Type listType = new org.modelmapper.TypeToken<List<WebsiteSettingDto>>() {
        }.getType();
        websiteSettingWsDto.setWebSiteSettingDtoList(modelMapper.map(page.getContent(), listType));
        websiteSettingWsDto.setBaseUrl(ADMIN_WEBSITESETTING);
        websiteSettingWsDto.setTotalPages(page.getTotalPages());
        websiteSettingWsDto.setTotalRecords(page.getTotalElements());
        return websiteSettingWsDto;


    }

    @GetMapping("/get")
    public WebsiteSettingWsDto getActiveStatus() {
        WebsiteSettingWsDto websiteSettingWsDto = new WebsiteSettingWsDto();
        Type listType = new org.modelmapper.TypeToken<List<WebsiteSettingDto>>() {
        }.getType();
        websiteSettingWsDto.setWebSiteSettingDtoList(modelMapper.map(websiteSettingRepository.findByStatusOrderById(true), listType));
        websiteSettingWsDto.setBaseUrl(ADMIN_WEBSITESETTING);
        return websiteSettingWsDto;


    }

    @PostMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public WebsiteSettingWsDto handleEdit(@ModelAttribute WebsiteSettingWsDto request) {
        return websiteSettingService.handleEdit(request);

    }

    @PostMapping("/delete")
    public WebsiteSettingWsDto deleteKyc(@RequestBody WebsiteSettingWsDto websiteSettingWsDto) {
        for (WebsiteSettingDto websiteSettingDto : websiteSettingWsDto.getWebSiteSettingDtoList()) {
            websiteSettingRepository.deleteByRecordId(websiteSettingDto.getRecordId());
        }
        websiteSettingWsDto.setMessage("Data deleted successfully");
        websiteSettingWsDto.setBaseUrl(ADMIN_WEBSITESETTING);
        return websiteSettingWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new WebsiteSetting());
    }
}
