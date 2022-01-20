package com.zaccao.dynamicconfig.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ZookeeperApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    
    private final List<PropertySourceLocator> propertySourceLocators;
    
    public ZookeeperApplicationContextInitializer() {
        ClassLoader classLoader= ClassUtils.getDefaultClassLoader();
        //load all SPI for PropertySourceLocator
        //ZookeeperPropertySourceLocator
        propertySourceLocators=new ArrayList<>(SpringFactoriesLoader
                .loadFactories(PropertySourceLocator.class,classLoader));
        System.out.println("====");
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        ConfigurableEnvironment environment=applicationContext.getEnvironment();
        MutablePropertySources mutablePropertySources=environment.getPropertySources();
        for(PropertySourceLocator locator:this.propertySourceLocators){
           Collection<PropertySource<?>> sources=locator.locateCollection(environment,applicationContext);
           if(sources==null||sources.size()==0){
               continue;
           }
           for (PropertySource<?> p:sources){
               mutablePropertySources.addLast(p); //add to Environment
           }
        }
    }
}
