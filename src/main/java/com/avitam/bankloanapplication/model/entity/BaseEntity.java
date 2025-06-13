package com.avitam.bankloanapplication.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class BaseEntity {


    private ObjectId id;
    private String name;
    private String creator;
    private Boolean status;

    private String recordId;
    @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
    private Date creationTime;
    @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
    private Date lastModified;
    private String pic;
    private String identifier;
}
