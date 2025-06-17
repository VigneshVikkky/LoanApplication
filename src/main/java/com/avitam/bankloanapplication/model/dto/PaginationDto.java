package com.avitam.bankloanapplication.model.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PaginationDto {

    private int page;
    private int sizePerPage = 50;
    private Sort.Direction sortDirection = Sort.Direction.DESC;
    private String sortField = "identifier";
    private int totalPages;
    private long totalRecords;
}
