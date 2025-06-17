package com.avitam.bankloanapplication.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document("loanScore")
public class LoanScoreResult extends BaseEntity {
    private String description;
    private Integer loanScoreLimit;

}
