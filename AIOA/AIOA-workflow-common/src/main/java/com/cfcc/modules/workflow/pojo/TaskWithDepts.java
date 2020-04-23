package com.cfcc.modules.workflow.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 节点是选部门的，存储节点与部门关系
 */

@Data
public class TaskWithDepts implements Serializable {

    private String tskId;
    private String taskDefKey;
    private String mainDept;
    private String fuDept;
    private String cyDept;
    private String taskIds;
    /**
     * key:主办/辅办/传阅
     * val:用户id
     */
    private Map<String, List<String>> deptMsg;
    private Map<String, String> userDeptMap;

}
