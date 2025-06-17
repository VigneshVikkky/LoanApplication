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
public class KYCDto extends CommonDto {
    private String customerId;

    private String panNumber;

    private String aadharNumber;

    private MultipartFile image;
}
