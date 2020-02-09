package com.cfcc.modules.workflow.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class JumpVo implements Serializable {

    //String userId;
    String processDefId;
    String taskId;
    String exeId;
    String sourceName;
    String destName;
    Map<String, Object> vars;
}
