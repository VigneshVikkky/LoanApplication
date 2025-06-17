package com.avitam.bankloanapplication.service.impl;

import com.avitam.bankloanapplication.core.service.CoreService;
import com.avitam.bankloanapplication.model.dto.CustomerDto;
import com.avitam.bankloanapplication.model.dto.CustomerWsDto;
import com.avitam.bankloanapplication.model.entity.Customer;
import com.avitam.bankloanapplication.repository.CustomerRepository;
import com.avitam.bankloanapplication.service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    public static final String ADMIN_CUSTOMER = "/admin/customer";
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CoreService coreService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public Customer findByRecordId(String recordId) {
        return customerRepository.findByRecordId(recordId);
    }

    public void deleteByRecordId(String recordId) {
        customerRepository.deleteByRecordId(recordId);
    }

    public CustomerWsDto handleEdit(CustomerDto customerDto) {
        CustomerWsDto customerWsDto = new CustomerWsDto();
        Customer customer = new Customer();
        List<Customer> customerList = new ArrayList<>();
        if (customerDto.getRecordId() != null) {
            customer = customerRepository.findByRecordId(customerDto.getRecordId());
            modelMapper.map(customerDto, customer);
            customerRepository.save(customer);
            customerWsDto.setMessage("Data updated successfully");
        } else {
            customer = modelMapper.map(customerDto, Customer.class);
            customer.setPassword(bCryptPasswordEncoder.encode(customer.getPassword()));
            customer.setCreationTime(new Date());
            customer.setStatus(true);
            customerRepository.save(customer);
        }
        if (customerDto.getRecordId() == null) {
            customer.setRecordId(String.valueOf(customer.getId().getTimestamp()));
        }
        customerRepository.save(customer);
        customerList.add(customer);
        customerDto.setBaseUrl(ADMIN_CUSTOMER);
        customerDto.setMessage("Data added Successfully");
        customerWsDto.setCustomerDtoList(modelMapper.map(customerList, List.class));
        return customerWsDto;
    }

    public void updateByRecordId(String recordId) {

        Customer customerOptional = customerRepository.findByRecordId(recordId);
        if (customerOptional != null) {
            customerRepository.save(customerOptional);
        }

    }

    @Override
    public Object getCustomerByNationalIdentityNumber(Object o) {
        return null;
    }

    @Override
    public Object getAllCustomers() {
        return null;
    }

}
