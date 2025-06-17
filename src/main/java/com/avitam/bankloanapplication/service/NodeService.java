package com.avitam.bankloanapplication.service;

import com.avitam.bankloanapplication.model.dto.NodeDto;
import com.avitam.bankloanapplication.model.dto.NodeWsDto;
import com.avitam.bankloanapplication.model.entity.Node;

import java.util.List;

public interface NodeService {
    List<NodeDto> getAllNodes();

    List<NodeDto> getNodesForRoles();

    NodeWsDto handleEdit(NodeWsDto request);

    Node findById(String id);

}
