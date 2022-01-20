package com.zaccao.dynamicconfig;

import com.zaccao.dynamicconfig.config.RefreshScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RefreshScope  //indicate the beans require dynamic refreshing
@RestController
public class ConfigController {

    @Autowired
    Environment environment;

    //link the fields to zookeeper
    @Value("${name}")
    private String name;

    @Value("${job}")
    private String job;

    @Value("${zookeeper}")
    private String command;

    @GetMapping("/env")
    public String env(){
        return environment.getProperty("name")+"\n"+command;
    }
}

