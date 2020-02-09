package com.cfcc.modules.workflow.pojo;

import lombok.Data;
import org.activiti.engine.impl.persistence.entity.HistoricVariableInstanceEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class HisTaskJsonAble extends TaskCommon implements Serializable {


    private String bussinessKey;

    private String hiTaskId;
    private String processDefinitionId;
    private String taskDefinitionKey;
    private String processInstanceId;
    private String executionId;
    private String name;
    private String allNames;
    private String parentTaskId;
    private String description;
    private String owner;
    private String assignee;
    private Date startTime;
    private Date claimTime;
    private Date endTime;
    private String duration;
    private String delReason;
    private int priority;
    private Date dueDate;
    private String formKey;
    private String category;
    private String tenantId = "";
    private String taskDefinitionKeyName;
    private String proDefName;
    //流程是否结束
    private String isDone;


}
