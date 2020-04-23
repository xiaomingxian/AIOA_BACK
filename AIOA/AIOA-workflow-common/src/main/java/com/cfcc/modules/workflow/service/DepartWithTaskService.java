package com.cfcc.modules.workflow.service;

import com.cfcc.modules.workflow.pojo.TaskWithDepts;

import java.util.Map;

public interface DepartWithTaskService {
    void save(String procInstId,TaskWithDepts taskWithDepts);

    Map<String,Integer> deptDone();
}
