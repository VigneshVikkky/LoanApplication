package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.core.service.CoreService;
import com.avitam.bankloanapplication.model.dto.KYCDto;
import com.avitam.bankloanapplication.model.dto.LoanApplicationDto;
import com.avitam.bankloanapplication.model.dto.WebsiteSettingDto;
import com.avitam.bankloanapplication.model.dto.WebsiteSettingWsDto;
import com.avitam.bankloanapplication.model.entity.KYC;
import com.avitam.bankloanapplication.model.entity.WebsiteSetting;
import com.avitam.bankloanapplication.repository.WebsiteSettingRepository;
import com.avitam.bankloanapplication.service.WebsiteSettingService;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.Binary;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class WebSiteSettingServiceImpl implements WebsiteSettingService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CoreService coreService;
    @Autowired
    private WebsiteSettingRepository websiteSettingRepository;

    private static final String ADMIN_WEBSITESETTING = "/admin/websitesetting";

    @Override
    public WebsiteSettingWsDto handleEdit(WebsiteSettingWsDto request) {
        WebsiteSetting websiteSetting = new WebsiteSetting();
        List<WebsiteSettingDto> websiteSettingDtos = request.getWebSiteSettingDtoList();
        List<WebsiteSetting> websiteSettings = new ArrayList<>();
        for (WebsiteSettingDto websiteSettingDto : websiteSettingDtos) {
            if (websiteSettingDto.getRecordId() != null) {
                websiteSetting = websiteSettingRepository.findByRecordId(websiteSettingDto.getRecordId());
                modelMapper.map(websiteSettingDto, websiteSetting);
                websiteSettingRepository.save(websiteSetting);
                request.setMessage("Data Updated successfully");
            } else {
                websiteSetting = modelMapper.map(websiteSettingDto, WebsiteSetting.class);
                websiteSetting.setCreationTime(new Date());
                websiteSetting.setStatus(true);
                if (websiteSettingDto.getLogo() != null && websiteSettingDto.getFavicon()!= null) {
                    try {
                        websiteSetting.setLogo(new Binary(websiteSettingDto.getLogo().getBytes()));
                        websiteSetting.setFavicon(new Binary(websiteSettingDto.getFavicon().getBytes()));

                    } catch (IOException e) {
                        e.printStackTrace();
                        request.setMessage("Error processing image file");
                        return request;
                    }
                }
                websiteSettingRepository.save(websiteSetting);
            }
            if (request.getRecordId() == null) {
                websiteSetting.setRecordId(String.valueOf(websiteSetting.getId().getTimestamp()));
            }
            websiteSettingRepository.save(websiteSetting);
            websiteSettings.add(websiteSetting);
            request.setBaseUrl(ADMIN_WEBSITESETTING);
            request.setMessage("Data Added Successfully");
        }
        Type listType = new TypeToken<List<WebsiteSettingDto>>() {}.getType();
        request.setWebSiteSettingDtoList(modelMapper.map(websiteSettings, listType));
        return request;
    }


}
