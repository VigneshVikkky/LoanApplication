package com.avitam.bankloanapplication;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@SpringBootApplication
@EnableMongoRepositories("com.avitam.bankloanapplication.repository")
@ComponentScan(
        {
                "com.avitam.bankloanapplication.tokenGeneration",
                "com.avitam.bankloanapplication.repository",
                "com.avitam.bankloanapplication.service",
                "com.avitam.bankloanapplication.config",
                "com.avitam.bankloanapplication.web.controllers",
                "com.avitam.bankloanapplication.core",
                "com.avitam.bankloanapplication.exception",
                "com.avitam.bankloanapplication.model"
        }
)
public class BankLoanApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankLoanApplication.class, args);
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        var source = new ResourceBundleMessageSource();
        source.setBasename("message");
        source.setDefaultEncoding("UTF-8");
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        return sessionLocaleResolver;
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        mapper.getConfiguration().setSkipNullEnabled(true);
        return mapper;
    }

}
