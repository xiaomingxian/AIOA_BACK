package com.cfcc.modules.workflow.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AddUsersMsg implements Serializable {

    private String taskId;

    private String executionId;

    private List<String> userIds;



}
