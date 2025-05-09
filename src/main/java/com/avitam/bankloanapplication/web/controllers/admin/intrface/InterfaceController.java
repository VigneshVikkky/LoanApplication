package com.avitam.bankloanapplication.web.controllers.admin.intrface;

import com.avitam.bankloanapplication.core.service.UserService;
import com.avitam.bankloanapplication.model.dto.CustomerDto;
import com.avitam.bankloanapplication.model.dto.NodeDto;
import com.avitam.bankloanapplication.model.dto.NodeWsDto;
import com.avitam.bankloanapplication.model.dto.SearchDto;
import com.avitam.bankloanapplication.model.entity.Node;
import com.avitam.bankloanapplication.repository.NodeRepository;
import com.avitam.bankloanapplication.service.NodeService;
import com.avitam.bankloanapplication.web.controllers.BaseController;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/admin/interface")
public class InterfaceController extends BaseController {
    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private NodeService nodeService;
    @Autowired
    private UserService userService;
    private static final String ADMIN_INTERFACE="/admin/interface";

    @PostMapping
    @ResponseBody
    public NodeWsDto getAllModels(@RequestBody NodeWsDto nodeWsDto) {
        Pageable pageable = getPageable(nodeWsDto.getPage(), nodeWsDto.getSizePerPage(), nodeWsDto.getSortDirection(), nodeWsDto.getSortField());
        NodeDto nodeDto = CollectionUtils.isNotEmpty(nodeWsDto.getNodeDtos()) ? nodeWsDto.getNodeDtos().get(0) : new NodeDto();
        Node node = modelMapper.map(nodeDto, Node.class);
        Page<Node> page = isSearchActive(node) != null ? nodeRepository.findAll(Example.of(node), pageable) : nodeRepository.findAll(pageable);
        Type listType = new TypeToken<List<NodeDto>>() {}.getType();
        nodeWsDto.setNodeDtos(modelMapper.map(page.getContent(), listType));
        nodeWsDto.setBaseUrl(ADMIN_INTERFACE);
        nodeWsDto.setTotalPages(page.getTotalPages());
        nodeWsDto.setTotalRecords(page.getTotalElements());
        return nodeWsDto;
    }

    @GetMapping("/get")
    @ResponseBody
    public NodeWsDto getActiveNodes() {
        NodeWsDto nodeWsDto = new NodeWsDto();
        nodeWsDto.setBaseUrl(ADMIN_INTERFACE);
        Type listType = new TypeToken<List<NodeDto>>() {}.getType();
        nodeWsDto.setNodeDtos(modelMapper.map(nodeRepository.findByStatusOrderByDisplayPriority(true), listType));
        return nodeWsDto;
    }

    @PostMapping("/getedit")
    @ResponseBody
    public NodeWsDto editInterface(@RequestBody NodeWsDto request) {
        NodeWsDto nodeWsDto = new NodeWsDto();
        nodeWsDto.setBaseUrl(ADMIN_INTERFACE);
        Node node = nodeRepository.findByRecordId(request.getNodeDtos().get(0).getRecordId());
        if (node != null) {
            nodeWsDto.setNodeDtos(List.of(modelMapper.map(node, NodeDto.class)));
        }
        return nodeWsDto;
    }

    @GetMapping("/getMenu")
    @ResponseBody
    public List<NodeDto> getMenu() {
        return userService.isAdminRole() ? nodeService.getAllNodes() : nodeService.getNodesForRoles();
    }
    @PostMapping("/edit")
    @ResponseBody
    public NodeWsDto handleEdit(@RequestBody NodeWsDto request) {
        return nodeService.handleEdit(request);
    }

    @GetMapping("/add")
    @ResponseBody
    public NodeWsDto addInterface() {
        NodeWsDto nodeWsDto = new NodeWsDto();
        Type listType = new TypeToken<List<NodeDto>>() {}.getType();

        nodeWsDto.setNodeDtos(modelMapper.map(nodeRepository.findByStatusOrderByDisplayPriority(true), listType));
        nodeWsDto.setBaseUrl(ADMIN_INTERFACE);
        return nodeWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public NodeWsDto deleteInterface(@RequestBody NodeWsDto nodeWsDto) {
        for (NodeDto nodeDto : nodeWsDto.getNodeDtos()) {
            nodeRepository.deleteByRecordId(nodeDto.getRecordId());
        }
        nodeWsDto.setBaseUrl(ADMIN_INTERFACE);
        nodeWsDto.setMessage("Data deleted successfully!!");
        return nodeWsDto;
    }
    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Node());
    }
}
