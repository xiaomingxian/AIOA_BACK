package com.cfcc.modules.workflow.service;

import com.cfcc.modules.system.entity.SysUser;

import java.util.Map;

public interface ReCallTaskService {

    void backProcess(String taskId, String activityId,Map<String, Object> variables,
                     SysUser currentUser,String type,String desActName) throws Exception;
}
