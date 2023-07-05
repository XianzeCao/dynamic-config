package com.zaccao.dynamicconfig.config;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;


public class LocalCustomPropertySourceLocator implements PropertySourceLocator{
    private final Properties properties=new Properties();
    private String propertiesFile="custom.properties";

    @Override
    public PropertySource<?> locate(Environment environment, ConfigurableApplicationContext applicationContext) {
        Resource resource=new ClassPathResource(propertiesFile);
        return loadProperties(resource); //给到ApplicationContextInitializer加载
    }

    private PropertySource<?> loadProperties(Resource resource){
        if(!resource.exists()){
            throw new RuntimeException("file not exist");
        }
        try {
            //custom.properties
            properties.load(resource.getInputStream());
            return new PropertiesPropertySource(resource.getFilename(),properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
