package com.cfcc.modules.workflow.pojo;

import lombok.Data;

@Data
public class TaskTransfer {

    private String id;
    private String procInstId;
    private String taskId;
    private String transferLog;
    private String sourceUserId;//最原始任务办理人

}
