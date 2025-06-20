package com.avitam.bankloanapplication.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class NotificationDto extends CommonDto {
    private String message;
    private String title;
}
