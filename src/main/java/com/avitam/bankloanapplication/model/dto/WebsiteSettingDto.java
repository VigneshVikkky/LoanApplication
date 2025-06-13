package com.avitam.bankloanapplication.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class WebsiteSettingDto extends CommonDto{
    private MultipartFile logo;
    private MultipartFile favicon;
    private String sportsApiUrl;
    private String sportsApiKey;
    private String mailId;
    private String mailPassword;
    private String smtpHost;
    private String smtpPort;
    private String smsKey;
    private String smsProvider;
    private String smsSenderId;
    private String smsTemplateId;
    private String otpExpiryTime;
    private String paymentKey;
    private String paymentProvider;
}
