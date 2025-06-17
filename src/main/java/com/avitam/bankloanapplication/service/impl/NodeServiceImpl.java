package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.core.service.CoreService;
import com.avitam.bankloanapplication.model.dto.NodeDto;
import com.avitam.bankloanapplication.model.dto.NodeWsDto;
import com.avitam.bankloanapplication.model.entity.Node;
import com.avitam.bankloanapplication.model.entity.Role;
import com.avitam.bankloanapplication.model.entity.User;
import com.avitam.bankloanapplication.repository.NodeRepository;
import com.avitam.bankloanapplication.repository.UserRepository;
import com.avitam.bankloanapplication.service.NodeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NodeServiceImpl implements NodeService {

    private static final String ADMIN_INTERFACE = "/admin/interface";
    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CoreService coreService;

    @Override
    public List<NodeDto> getAllNodes() {
        List<NodeDto> allNodes = new ArrayList<>();
        List<Node> nodeList = nodeRepository.findByStatusOrderByDisplayPriority(true).stream().filter(node -> node.getParentNode() == null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(nodeList)) {
            for (Node node : nodeList) {
                NodeDto nodeDto = new NodeDto();
                modelMapper.map(node, nodeDto);
                List<Node> childNodes = nodeRepository.findByParentNodeId(node.getRecordId());
                if (CollectionUtils.isNotEmpty(childNodes)) {
                    List<Node> childNodeList = childNodes.stream().filter(childNode -> BooleanUtils.isTrue(childNode.getStatus()))
                            .sorted(Comparator.comparing(nodes -> nodes.getDisplayPriority())).collect(Collectors.toList());
                    Type listType = new TypeToken<List<NodeDto>>() {
                    }.getType();
                    nodeDto.setChildNodes(modelMapper.map(childNodeList, listType));
                }
                allNodes.add(nodeDto);
            }
        }
        return allNodes;
    }

    @Override
    //@Cacheable(cacheNames = "roleBasedNodes")
    public List<NodeDto> getNodesForRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User principalObject = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        User currentUser = userRepository.findByUsername(principalObject.getUsername());
        Set<Role> roles = currentUser.getRoles();
        Set<Node> nodes = new HashSet<>();
        for (Role role : roles) {
            nodes.addAll(role.getPermissions());
        }
        List<NodeDto> allNodes = new ArrayList<>();
        Set<Node> nodeList = nodes.stream().filter(node -> BooleanUtils.isTrue(node.getStatus())).collect(Collectors.toSet());
        List<Node> nodeListArray = new ArrayList<>(nodeList);
        nodeListArray.sort(Comparator.comparing(node -> node.getDisplayPriority(),
                Comparator.nullsLast(Comparator.naturalOrder())));
        Map<String, Set<Node>> parentChildNodes = new HashMap<>();
        for (Node node : nodeListArray) {
            String parent = node.getParentNode().getIdentifier();
            Set<Node> childNodes = new HashSet<>();
            if (parentChildNodes.containsKey(parent)) {
                childNodes.addAll(parentChildNodes.get(parent));
            }
            childNodes.add(node);
            parentChildNodes.put(parent, childNodes);
        }

        for (String key : parentChildNodes.keySet()) {
            NodeDto nodeDto = new NodeDto();
            nodeDto.setIdentifier(key);
            Type listType = new TypeToken<List<NodeDto>>() {
            }.getType();
            nodeDto.setChildNodes(modelMapper.map(parentChildNodes.get(key), listType));
            allNodes.add(nodeDto);
        }
        return allNodes;
    }

    @Override
    public NodeWsDto handleEdit(@RequestBody NodeWsDto request) {
        NodeWsDto nodeWsDto = new NodeWsDto();
        List<Node> nodes = new ArrayList<>();
        for (NodeDto nodeDto : request.getNodeDtos()) {
            Node node = null;
            if (nodeDto.getRecordId() != null) {
                node = nodeRepository.findByRecordId(nodeDto.getRecordId());
                modelMapper.map(nodeDto, node);
                request.setMessage("Data updated successfully");
            } else {
                node = modelMapper.map(nodeDto, Node.class);
                node.setCreationTime(new Date());
                Node parentNode = node;
                if (parentNode != null) {
                    if (parentNode.getRecordId() != null) {
                        node.setParentNode(nodeRepository.findByRecordId(parentNode.getRecordId()));
                    }
                }
                nodeRepository.save(node);
            }
            node.setLastModified(new Date());
            if (nodeDto.getRecordId() == null) {
                node.setRecordId(String.valueOf(node.getId().getTimestamp()));
            }
            nodeRepository.save(node);
            nodes.add(node);
            nodeWsDto.setMessage("Nodes are updated successfully!!");
            nodeWsDto.setBaseUrl(ADMIN_INTERFACE);

        }
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        nodeWsDto.setNodeDtos(modelMapper.map(nodes, listType));
        return nodeWsDto;
    }

    @Override
    public Node findById(String id) {
        return null;
    }

//    @Override
//    public Node findById(String id) {
//        return nodeRepository.findByRecordId(id);
//    }
}
