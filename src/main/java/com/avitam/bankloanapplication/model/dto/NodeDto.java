package com.avitam.bankloanapplication.model.dto;

import com.avitam.bankloanapplication.model.entity.Node;
import com.avitam.bankloanapplication.model.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class NodeDto extends CommonDto {

    private String path;
    private Set<Role> roles;
    private Node parentNode;
    private String parentNodeId;
    private List<Node> childNodes;
    private Integer displayPriority;
}
