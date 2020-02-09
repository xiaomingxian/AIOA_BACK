package com.cfcc.modules.workflow.pojo;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

@Data
public class BackRecord {
    private String id;
    private String userName;
    private String userId;
    private String depart;
    private String backLog;

    private String processDefinitionId;
    private String taskDefinitionKey;
    private String processInstanceId;
    private String executionId;
    private String name;
    private String assignee;
    private Date startTime;
    private Date endTime;
    private String log;

    private String backReason;

    public String getUserName() {
        String assignee = this.assignee;
        String userName = "";
        if (StringUtils.isNotBlank(assignee)) {
            userName = assignee.split("\\(")[1].replaceAll("\\)", "");
        }
        return userName;
    }

    public String getLog() {

        String assignee = this.assignee;
        String log = "";
        if (StringUtils.isNotBlank(assignee)) {
            log = assignee.split("\\(")[0];
        }
        return log;

    }

}
