package com.avitam.bankloanapplication.web.controllers.admin.notification;


import com.avitam.bankloanapplication.model.dto.CommonDto;
import com.avitam.bankloanapplication.model.dto.NotificationDto;
import com.avitam.bankloanapplication.model.dto.NotificationWsDto;
import com.avitam.bankloanapplication.model.dto.SearchDto;
import com.avitam.bankloanapplication.model.entity.Notification;
import com.avitam.bankloanapplication.repository.NotificationRepository;
import com.avitam.bankloanapplication.service.impl.NotificationServiceImpl;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/admin/notification")
public class NotificationController extends BaseController {
    private static final String ADMIN_NOTIFICATION = "/admin/notification";
    @Autowired
    private NotificationServiceImpl notificationService;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private ModelMapper modelMapper;


    @PostMapping
    public NotificationWsDto getAllNotification(@RequestBody NotificationWsDto notificationwsDto) {
        Pageable pageable = getPageable(notificationwsDto.getPage(), notificationwsDto.getSizePerPage(), notificationwsDto.getSortDirection(), notificationwsDto.getSortField());
        CommonDto notificationDto = CollectionUtils.isNotEmpty(notificationwsDto.getNotificationDtoList()) ? notificationwsDto.getNotificationDtoList().get(0) : new NotificationDto();
        Notification notification = modelMapper.map(notificationwsDto, Notification.class);
        Page<Notification> page = isSearchActive(notification) != null ? notificationRepository.findAll(Example.of(notification), pageable) : notificationRepository.findAll(pageable);
        Type listType = new TypeToken<List<NotificationDto>>() {
        }.getType();
        notificationwsDto.setNotificationDtoList(modelMapper.map(page.getContent(), listType));
        notificationwsDto.setBaseUrl(ADMIN_NOTIFICATION);
        notificationwsDto.setTotalPages(page.getTotalPages());
        notificationwsDto.setTotalRecords(page.getTotalElements());
        return notificationwsDto;

    }

    @GetMapping("/get")
    public NotificationWsDto getNotificationById(@RequestBody NotificationWsDto request) {
        NotificationWsDto notificationWsDto = new NotificationWsDto();
        List<Notification> notifications = new ArrayList<>();
        for (NotificationDto notificationDto : request.getNotificationDtoList()) {
            notifications.add(notificationRepository.findByRecordId(notificationDto.getRecordId()));
        }
        Type listType = new TypeToken<List<NotificationDto>>() {
        }.getType();
        notificationWsDto.setNotificationDtoList(modelMapper.map(notifications, listType));
        notificationWsDto.setBaseUrl(ADMIN_NOTIFICATION);
        return notificationWsDto;
    }

    @PostMapping("/edit")
    public NotificationWsDto handelEdit(@RequestBody NotificationWsDto request) {

        return notificationService.handelEdit(request);
    }

    @PostMapping("/delete")
    public NotificationWsDto delete(@RequestBody NotificationWsDto notificationwsDto) {
        for (NotificationDto notificationDto : notificationwsDto.getNotificationDtoList()) {
            notificationRepository.deleteByRecordId(notificationDto.getRecordId());
        }
        notificationwsDto.setMessage("Data deleted successfully");
        notificationwsDto.setBaseUrl(ADMIN_NOTIFICATION);
        return notificationwsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Notification());
    }
}
