package com.cfcc.modules.utils;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class WorkFlowUtil implements ApplicationContextAware {


    protected static ApplicationContext applicationContext;
    protected static RuntimeService runtimeService;
    protected static RepositoryService repositoryService;
    protected static TaskService taskService;
    protected static HistoryService historyService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        WorkFlowUtil.applicationContext = applicationContext;
        WorkFlowUtil.runtimeService = applicationContext.getBean(RuntimeService.class);
        WorkFlowUtil.repositoryService = applicationContext.getBean(RepositoryService.class);
        WorkFlowUtil.taskService = applicationContext.getBean(TaskService.class);
        WorkFlowUtil.historyService = applicationContext.getBean(HistoryService.class);
    }


}
