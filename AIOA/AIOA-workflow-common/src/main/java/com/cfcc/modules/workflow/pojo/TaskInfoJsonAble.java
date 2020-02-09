package com.cfcc.modules.workflow.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
public class TaskInfoJsonAble extends TaskCommon implements Serializable {

    private String id;

    private String name;
    private String allNames;

    private String executionId;

    private String processInstanceId;

    private String processDefinitionId;

    private String parentTaskId;

    private String description;

    private String taskDefinitionKey;

    private String owner;

    private String assignee;

    private String delegation;

    private Date createTime;

    private String category;

    private String suspensionState;

    private String tenantId;

    private Date dueDate;


    private String formKey;

    private int priority;


    private Date endTime;

    private String proDefName;


    private Map<String, Object> taskLocalVariables;

    Map<String, Object> processVariables;

    private String agentUserName;

    private String deleteReason;//删除原因 用来区分是已办还是待办


}
