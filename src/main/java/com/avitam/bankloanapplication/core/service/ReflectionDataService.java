package com.avitam.bankloanapplication.core.service;

import org.modelmapper.ModelMapper;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ReflectionDataService {

    Logger LOG = LoggerFactory.getLogger(ReflectionDataService.class);

    @Autowired
    private ModelMapper modelMapper;

    /**
     * returns all class names from the given package
     *
     * @param packageName
     * @return
     */
    public static Set<String> findAllClassesUsingReflectionsLibrary(String packageName) {
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
        return reflections.getSubTypesOf(Object.class)
                .stream()
                .map(Class::getSimpleName) // Map to simple names
                .collect(Collectors.toSet());
    }

    /**
     * returns all public method names in the given class
     *
     * @param className
     * @return
     */
    public Set<String> getMethodNamesForClass(String className) {
        Set<String> methodNames = new HashSet<>();

        try {
            // Load the class by its name
            Class<?> clazz = Class.forName("avitam.fantasy11.qa.pages.abstractpages." + className);

            // Get all declared methods of the class
            Method[] methods = clazz.getDeclaredMethods();

            // Iterate through the methods and add their names to the set
            for (Method method : methods) {
                if (Modifier.isPublic(method.getModifiers())) {
                    methodNames.add(method.getName());
                }
            }
        } catch (ClassNotFoundException e) {
            // Handle the exception if the class is not found
            LOG.error(e.getMessage());
        }

        return methodNames;
    }

    /**
     * returns all enums named Field in the given class
     *
     * @param className
     * @return
     */
    public Set<String> getEnumNamesForClass(String className) {
        Set<String> enumNames = new HashSet<>();

        try {
            // Load the class by its name
            Class<?> enumClass = Class.forName("avitam.fantasy11.qa.pages.abstractpages." + className + "$Field");

            if (enumClass.isEnum()) {
                for (Object enumConstant : enumClass.getEnumConstants()) {
                    String enumName = ((Enum<?>) enumConstant).name();
                    enumNames.add(enumName);
                }
            }
        } catch (ClassNotFoundException e) {
            // Handle the exception if the class is not found
            LOG.error(e.getMessage());
        }

        return enumNames;
    }
}

