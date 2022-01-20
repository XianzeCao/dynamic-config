package com.zaccao.dynamicconfig.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Map;


public class ZookeeperPropertySourceLocator implements PropertySourceLocator{
    private final CuratorFramework curatorFramework;
    private final String DATA_NODE="/data";

    public ZookeeperPropertySourceLocator() {
        curatorFramework= CuratorFrameworkFactory.builder()
                .connectString("192.168.221.128:2181")
                .sessionTimeoutMs(20000)
                .connectionTimeoutMs(20000)
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .namespace("config")
                .build();
        curatorFramework.start();
    }

    @Override
    public PropertySource<?> locate(Environment environment, ConfigurableApplicationContext applicationContext) {
        //load config from zk into a propertySource
        System.out.println("loading external config");
        CompositePropertySource composite=new CompositePropertySource("configService");
        try {
            Map<String,Object> dataMap=getRemoteEnvironment();
            MapPropertySource mapPropertySource=new MapPropertySource("configService",dataMap);
            composite.addPropertySource(mapPropertySource);
            //add event listener listening for modification
            addListener(environment,applicationContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return composite;
    }

    private void addListener(Environment environment, ConfigurableApplicationContext applicationContext){
        NodeDataChangeCuratorCacheListener ndc=new NodeDataChangeCuratorCacheListener(environment,applicationContext);
        CuratorCache curatorCache=CuratorCache.build(curatorFramework,DATA_NODE,CuratorCache.Options.SINGLE_NODE_CACHE);
        CuratorCacheListener listener=CuratorCacheListener
                .builder()
                .forChanges(ndc).build();
        curatorCache.listenable().addListener(listener);
        curatorCache.start();
    }

    private Map<String,Object> getRemoteEnvironment() throws Exception {
        String data=new String (curatorFramework.getData().forPath(DATA_NODE));

        ObjectMapper objectMapper=new ObjectMapper();
        Map<String,Object> map=objectMapper.readValue(data,Map.class);
        return map;
    }
}
