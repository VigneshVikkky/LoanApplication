package com.avitam.bankloanapplication.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.Binary;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class WebsiteSetting extends BaseEntity {
    private Binary logo;
    private Binary favicon;
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
