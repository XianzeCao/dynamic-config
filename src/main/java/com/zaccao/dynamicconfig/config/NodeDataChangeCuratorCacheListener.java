package com.zaccao.dynamicconfig.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCacheListenerBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;

public class NodeDataChangeCuratorCacheListener implements CuratorCacheListenerBuilder.ChangeListener {

    private Environment environment;
    private ConfigurableApplicationContext applicationContext;

    public NodeDataChangeCuratorCacheListener(Environment environment, ConfigurableApplicationContext applicationContext) {
        this.environment = environment;
        this.applicationContext = applicationContext;
    }

    @Override
    public void event(ChildData oldNode, ChildData node) {
        System.out.println("received change event");
        String resultData = new String(node.getData());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            //json to map
            Map<String, Object> map = objectMapper.readValue(resultData, Map.class);
            //replace old PropertySource
            ConfigurableEnvironment cfe = (ConfigurableEnvironment) this.environment;
            MapPropertySource mapPropertySource = new MapPropertySource("configService", map);
            cfe.getPropertySources().replace("configService", mapPropertySource);
            //publish change event
            applicationContext.publishEvent(new EnvironmentChangeEvent(this));
            System.out.println("data update successful");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
}
