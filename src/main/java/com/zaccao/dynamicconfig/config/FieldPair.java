package com.zaccao.dynamicconfig.config;

import org.springframework.core.env.Environment;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.File;
import java.io.FileDescriptor;
import java.lang.reflect.Field;


public class FieldPair {

    private PropertyPlaceholderHelper propertyPlaceholderHelper=
            new PropertyPlaceholderHelper("${","}",":",true);

    private Object bean;
    private Field field;
    private String value;

    public FieldPair(Object bean, Field field, String value) {
        this.bean = bean;
        this.field = field;
        this.value = value;
    }

    public void resetValue(Environment environment){
        boolean access=field.isAccessible();
        if(!access){
            field.setAccessible(true);
        }
        String resetValue=propertyPlaceholderHelper.replacePlaceholders(value,environment::getProperty);
        try {
            //modify bean's value using reflection
            field.set(bean,resetValue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
