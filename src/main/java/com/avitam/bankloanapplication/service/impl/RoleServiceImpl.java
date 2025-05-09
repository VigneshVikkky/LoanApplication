package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.model.dto.LoanApplicationDto;
import com.avitam.bankloanapplication.model.dto.RoleDto;
import com.avitam.bankloanapplication.model.dto.RoleWsDto;
import com.avitam.bankloanapplication.model.entity.Role;
import com.avitam.bankloanapplication.repository.RoleRepository;
import com.avitam.bankloanapplication.service.RoleService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.avitam.bankloanapplication.web.controllers.admin.role.RoleController.ADMIN_ROLE;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ModelMapper modelMapper;
    public RoleWsDto createRole(RoleWsDto request) {
        Role role = new Role();
        List<RoleDto> roleDtos = request.getRoleDtoList();
        List<Role> roles = new ArrayList<>();
        for(RoleDto roleDto :roleDtos) {
            if (roleDto.getRecordId() != null) {
                role = roleRepository.findByRecordId(roleDto.getRecordId());
                modelMapper.map(roleDto, role);
                roleRepository.save(role);
                request.setMessage("Data updated successfully");
            } else {
                role = modelMapper.map(roleDto, Role.class);
                role.setCreationTime(new Date());
                role.setStatus(true);
                roleRepository.save(role);
            }
            if (request.getRecordId() == null) {
                role.setRecordId(String.valueOf(role.getId().getTimestamp()));
            }
            roleRepository.save(role);
            roles.add(role);
            request.setBaseUrl(ADMIN_ROLE);
            request.setMessage("Data added Successfully");

        }
        Type listType = new TypeToken<List<RoleDto>>() {}.getType();
        request.setRoleDtoList(modelMapper.map(roles, listType));
        return request;
        }

    }



