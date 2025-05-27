package com.avitam.bankloanapplication.web.controllers;

import com.avitam.bankloanapplication.core.service.CoreService;
import com.avitam.bankloanapplication.core.service.UserService;
import com.avitam.bankloanapplication.model.dto.*;
import com.avitam.bankloanapplication.model.entity.Customer;
import com.avitam.bankloanapplication.model.entity.User;
import com.avitam.bankloanapplication.repository.CustomerRepository;
import com.avitam.bankloanapplication.repository.RoleRepository;
import com.avitam.bankloanapplication.repository.UserRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/user")
public class AdminController extends BaseController{

    public static final String ADMIN_USER="/admin/user";
    Logger logger= LoggerFactory.getLogger(AdminController.class);
    @Autowired
    private CoreService coreService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("")
    @ResponseBody
    public CustomerWsDto getAllUsers(@RequestBody CustomerWsDto customerWsDto) {
        Pageable pageable=getPageable(customerWsDto.getPage(),customerWsDto.getSizePerPage(),customerWsDto.getSortDirection(),customerWsDto.getSortField());
        CustomerDto customerDto = CollectionUtils.isNotEmpty(customerWsDto.getCustomerDtoList()) ? customerWsDto.getCustomerDtoList().get(0) : new CustomerDto();
        Customer customer = modelMapper.map(customerDto, Customer.class);
        Page<Customer> page=isSearchActive(customer) !=null ? customerRepository.findAll(Example.of(customer),pageable) : customerRepository.findAll(pageable);
        Type listType = new org.modelmapper.TypeToken<List<CustomerDto>>() {}.getType();
        customerWsDto.setCustomerDtoList(modelMapper.map(page.getContent(), listType));
        customerWsDto.setBaseUrl(ADMIN_USER);
        customerWsDto.setTotalPages(page.getTotalPages());
        customerWsDto.setTotalRecords(page.getTotalElements());
        return customerWsDto;
    }

    @GetMapping("/get")
    @ResponseBody
    public CustomerWsDto getLoanType(@RequestBody CustomerWsDto request) {
        CustomerWsDto customerWsDto = new CustomerWsDto();
        List<Customer> customers = new ArrayList<>();
        for(CustomerDto customerDto: request.getCustomerDtoList()) {
            customers.add(customerRepository.findByRecordId(customerDto.getRecordId()));
        }
        Type listType = new org.modelmapper.TypeToken<List<CustomerDto>>() {}.getType();
        customerWsDto.setCustomerDtoList(modelMapper.map(customers,listType));
        customerWsDto.setBaseUrl(ADMIN_USER);

        return customerWsDto;
    }

    /*@PostMapping("/getedit")
    @ResponseBody
    public CustomerWsDto editLoanApplication(@RequestBody CustomerWsDto request) {
        CustomerWsDto customerWsDto = new CustomerWsDto();
        List<Customer> customers=new ArrayList<>();
        for(CustomerDto customerDto:request.getCustomerDtoList()) {
            customers.add(customerRepository.findByRecordId(customerDto.getRecordId()));
        }
        Type listType = new org.modelmapper.TypeToken<List<CustomerDto>>() {}.getType();
        customerWsDto.setCustomerDtoList(modelMapper.map(customers,listType));
        customerWsDto.setBaseUrl(ADMIN_USER);
        return customerWsDto;
    }*/

    @PostMapping("/getedit")
    @ResponseBody
    public UserWsDto editLoanApplication(@RequestBody UserWsDto request) {
        UserWsDto userWsDto = new UserWsDto();
        List<User> users=new ArrayList<>();
        for(UserDto userDto:request.getUserDtoList()) {
            users.add(userRepository.findByRecordId(userDto.getRecordId()));
        }
        Type listType = new org.modelmapper.TypeToken<List<UserDto>>() {}.getType();
        userWsDto.setUserDtoList(modelMapper.map(users,listType));
        userWsDto.setBaseUrl(ADMIN_USER);
        return userWsDto;
    }



    @PostMapping("/edit")
    @ResponseBody
    public UserDto save(@RequestBody UserDto request) {
        return userService.save(request);
        //return request;
    }

    @PostMapping("/delete")
    @ResponseBody
    public CustomerWsDto deleteUser(@RequestBody CustomerWsDto customerWsDto) {

        for (CustomerDto customerDto : customerWsDto.getCustomerDtoList()) {
            customerRepository.deleteByRecordId(customerDto.getRecordId());
        }
        customerWsDto.setBaseUrl(ADMIN_USER);
        customerWsDto.setMessage("Data Deleted Successfully");
        return customerWsDto;
    }

    @GetMapping("/getAdvanceSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes(){
        return getGroupedParentAndChildAttributes(new User());
    }


}
