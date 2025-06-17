package com.avitam.bankloanapplication.web.controllers;

import com.avitam.bankloanapplication.core.service.UserService;
import com.avitam.bankloanapplication.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@RestController
public class HomeController {

    Logger LOG = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;
    //    @Autowired
//    private MailService mailService;
    @Autowired
    private Environment env;


    @GetMapping("/home")
    @ResponseBody
    public ModelAndView home(HttpSession session, Model model) {
        // model.addAttribute("nodes", userService.isAdminRole() ? nodeService.getAllNodes() : nodeService.getNodesForRoles());
        String currentUserSession = (String) session.getAttribute("currentUserSession");
        UUID uuid = UUID.randomUUID();
        model.addAttribute("currentUserSession", StringUtils.isNotEmpty(currentUserSession) ? currentUserSession : uuid.toString());
//        List<WebsiteSetting> siteSettings = websiteSettingRepository.findAll();
//        if (CollectionUtils.isNotEmpty(siteSettings)) {
//            model.addAttribute("siteSetting", siteSettings.get(0));
//        }
        return new ModelAndView("home");
    }

}