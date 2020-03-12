package com.cfcc.modules.workflow.pojo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class JumpMsg {

    private String processDefinitionId;
    private String taskId;
    private String executionId;
    private String processInstanceId;
    private String destActDefKey;

    private String destActDefName;
    private String currActDefKey;
    private String deleteReason;

    private Map<String, Object> vars;

    //部门相关数据
    private Boolean isDept=false;

    private TaskWithDepts taskWithDepts;
    //参与人信息
    private List<String> assignee;
    private String table;
    private Integer tableId;
    private Integer functionId;


    //退回原因
    private String backReason;

}
