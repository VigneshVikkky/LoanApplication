package com.avitam.bankloanapplication.web.controllers.admin.customer;

import com.avitam.bankloanapplication.core.service.CoreService;
import com.avitam.bankloanapplication.model.dto.CustomerDto;
import com.avitam.bankloanapplication.model.dto.CustomerWsDto;
import com.avitam.bankloanapplication.model.dto.SearchDto;
import com.avitam.bankloanapplication.model.entity.Customer;
import com.avitam.bankloanapplication.repository.CustomerRepository;
import com.avitam.bankloanapplication.service.impl.CustomerServiceImpl;
import com.avitam.bankloanapplication.web.controllers.BaseController;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.List;

@Controller
@RequestMapping("/admin/customer")
public class CustomerController extends BaseController {
    public static final String ADMIN_CUSTOMER = "/admin/customer";
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CoreService coreService;
    @Autowired
    private CustomerServiceImpl customerService;

    @PostMapping
    public CustomerWsDto getAllCustomers(@RequestBody CustomerWsDto customerWsDto) {

        Pageable pageable = getPageable(customerWsDto.getPage(), customerWsDto.getSizePerPage(), customerWsDto.getSortDirection(), customerWsDto.getSortField());
        CustomerDto customerDto = CollectionUtils.isNotEmpty(customerWsDto.getCustomerDtoList()) ? customerWsDto.getCustomerDtoList().get(0) : new CustomerDto();
        Customer customer = modelMapper.map(customerDto, Customer.class);
        Page<Customer> page = isSearchActive(customer) == null ? customerRepository.findAll(Example.of(customer), pageable) : customerRepository.findAll(pageable);
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        customerWsDto.setCustomerDtoList(modelMapper.map(page.getContent(), listType));
        customerWsDto.setTotalPages(page.getTotalPages());
        customerWsDto.setTotalRecords(page.getTotalElements());
        customerWsDto.setBaseUrl(ADMIN_CUSTOMER);
        return customerWsDto;
    }

    @GetMapping("/get")
    public CustomerWsDto getActiveCustomerList() {
        CustomerWsDto customerWsDto = new CustomerWsDto();
        List<Customer> customers = customerRepository.findByStatus(true);
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        customerWsDto.setCustomerDtoList(modelMapper.map(customers, listType));
        customerWsDto.setBaseUrl(ADMIN_CUSTOMER);
        return customerWsDto;
    }
   /* @GetMapping("/getByRecordId")
    @ResponseBody
    public CustomerWsDto getByRecordId(@RequestParam String recordId) {
        CustomerWsDto customerWsDto = new CustomerWsDto();
        List<Customer> customers = customerRepository.findByRecordId(recordId,true);
        Type listType = new TypeToken<List<CustomerDto>>() {}.getType();
        customerWsDto.setCustomerDtoList(modelMapper.map(customers, listType));
        customerWsDto.setBaseUrl(ADMIN_CUSTOMER);
        return customerWsDto;
    }*/


    @PostMapping("/getByRecordId")
    public CustomerDto getByRecordId(@RequestBody CustomerDto customerDto) {
        Customer customer = customerRepository.findByRecordId(customerDto.getRecordId());
        Type listType = new TypeToken<CustomerDto>() {
        }.getType();
        customerDto = modelMapper.map(customer, listType);
        return customerDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public CustomerWsDto handleEdit(@RequestBody CustomerDto request) {
        return customerService.handleEdit(request);
    }

    @PostMapping("/delete")
    @ResponseBody
    public CustomerWsDto deleteCustomer(@RequestBody CustomerWsDto customerWsDto) {
        for (CustomerDto customerDto : customerWsDto.getCustomerDtoList()) {
            customerRepository.deleteByRecordId(customerDto.getRecordId());
        }
        customerWsDto.setMessage("Data deleted successfully");
        customerWsDto.setBaseUrl(ADMIN_CUSTOMER);
        return customerWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Customer());
    }
}
