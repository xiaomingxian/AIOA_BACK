package com.cfcc.modules.workflow.service;

import com.cfcc.modules.workflow.pojo.TaskWithDepts;

public interface DepartWithTaskService {
    void save(String procInstId,TaskWithDepts taskWithDepts);
}
