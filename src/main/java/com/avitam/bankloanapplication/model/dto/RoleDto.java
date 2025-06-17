package com.avitam.bankloanapplication.model.dto;

import com.avitam.bankloanapplication.model.entity.Node;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class RoleDto extends CommonDto {

    public String authority;
    private Set<Node> permissions;

}
