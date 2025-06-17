package com.avitam.bankloanapplication.web.controllers;

import com.avitam.bankloanapplication.model.dto.SearchDto;
import com.avitam.bankloanapplication.model.entity.BaseEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class BaseController {

    protected <T> T isSearchActive(T type) {
        if (type != null) {
            Field[] fields = type.getClass().getDeclaredFields();
            boolean isSearchActive = isSearchActive(fields, type);
            if (!isSearchActive) {
                Field[] superFields = type.getClass().getSuperclass().getDeclaredFields();
                isSearchActive = isSearchActive(superFields, type);
            }
            return isSearchActive ? type : null;
        }
        return null;
    }

    private boolean isSearchActive(Field[] fields, Object type) {
        AtomicBoolean isSearchActive = new AtomicBoolean(false);
        Arrays.stream(fields).forEach(field -> {
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(type);
            } catch (IllegalAccessException e) {
            }
            if (value != null) {
                isSearchActive.set(true);
            }
        });
        return isSearchActive.get();
    }

    protected Pageable getPageable(int pageNumber, int pageSize, Sort.Direction direction, String... sort) {
        List<Sort.Order> orders = new ArrayList<>();
        Arrays.stream(sort).collect(Collectors.toList()).forEach(field -> {
            Sort.Order order = new Sort.Order(direction, field);
            orders.add(order);
        });
        return sort != null ? PageRequest.of(pageNumber, pageSize, Sort.by(orders)) : PageRequest.of(pageNumber, pageSize);
    }

    protected List<SearchDto> getGroupedParentAndChildAttributes(BaseEntity type) {
        List<SearchDto> searchDtoList = new ArrayList<>();
        String regex = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])";
        Field[] fields = type.getClass().getDeclaredFields();
        Field[] staticFields = type.getClass().getSuperclass().getDeclaredFields();
        Field[] basicStaticFields = type.getClass().getSuperclass().getSuperclass().getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            SearchDto searchDto = new SearchDto();
            String attrName = field.getName();
            String[] labels = attrName.split(regex);
            searchDto.setAttribute(attrName);
            String label = labels.length > 1 ? labels[0] + " " + labels[1] : labels[0];
            searchDto.setLabel(StringUtils.capitalize(label));
            searchDto.setDynamicAttr(true);
            searchDto.setDataType(field.getType().getName());
            searchDtoList.add(searchDto);
        });
        Arrays.stream(staticFields).forEach(field -> {
            SearchDto searchDto = new SearchDto();
            String attrName = field.getName();
            String[] labels = attrName.split(regex);
            searchDto.setAttribute(attrName);
            String label = labels.length > 1 ? labels[0] + " " + labels[1] : labels[0];
            searchDto.setLabel(StringUtils.capitalize(label));
            searchDto.setDynamicAttr(false);
            searchDto.setDataType(field.getType().getName());
            searchDtoList.add(searchDto);
        });
        Arrays.stream(basicStaticFields).forEach(field -> {
            SearchDto searchDto = new SearchDto();
            String attrName = field.getName();
            String[] labels = attrName.split(regex);
            searchDto.setAttribute(attrName);
            String label = labels.length > 1 ? labels[0] + " " + labels[1] : labels[0];
            searchDto.setLabel(StringUtils.capitalize(label));
            searchDto.setDynamicAttr(false);
            searchDto.setDataType(field.getType().getName());
            searchDtoList.add(searchDto);
        });
        return searchDtoList;
    }
}
