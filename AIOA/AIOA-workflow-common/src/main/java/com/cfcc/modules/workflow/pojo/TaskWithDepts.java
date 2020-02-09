package com.cfcc.modules.workflow.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 节点是选部门的，存储节点与部门关系
 */

@Data
public class TaskWithDepts implements Serializable {

    private String tskId;
    private String taskDefKey;
    private String mainDept;
    /**
     * key:主办/辅办/传阅
     * val:用户id
     */
    private Map<String, String[]> deptMsg;

}
