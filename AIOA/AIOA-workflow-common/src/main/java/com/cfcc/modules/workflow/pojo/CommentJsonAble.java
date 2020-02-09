package com.cfcc.modules.workflow.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 意见
 */
@Data
public class CommentJsonAble implements Serializable {
    String id;

    String userId;

    Date time;

    String taskId;

    String processInstanceId;

    String type;

    String fullMessage;

}
