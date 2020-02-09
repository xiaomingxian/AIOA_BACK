package com.cfcc.config;

import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.stereotype.Component;

@Component
public class ActivitiConfig implements ProcessEngineConfigurationConfigurer {



    @Override
    public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
        //关闭异步执行
        processEngineConfiguration.setAsyncExecutorActivate(false);
        processEngineConfiguration.setActivityFontName("宋体");
        processEngineConfiguration.setLabelFontName("宋体");
        processEngineConfiguration.setAnnotationFontName("宋体");
        System.out.println("#############Activiti设置中文#############");

    }
}
