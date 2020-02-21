package com.cfcc.modules.workflow.service;

import com.cfcc.common.api.vo.Result;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.workflow.pojo.Activity;
import com.cfcc.modules.workflow.pojo.JumpMsg;
import com.cfcc.modules.workflow.pojo.OaProcActinst;
import com.cfcc.modules.workflow.pojo.TaskInfoJsonAble;
import com.cfcc.modules.workflow.vo.TaskInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TaskCommonFoldService {
//    根据流程key和任务名称查询-lvjian
    OaProcActinst queryByKeyAndName(OaProcActinst oaProcActinst);
//流程监控一级折叠
    Result monitorFoldUrgency(String urgencyDegree, TaskInfoVO taskInfoVO,boolean isAdmin);

    Result queryTaskToDo(String urgencyDegree, TaskInfoVO taskInfoVO, Integer pageNo, Integer pageSize,Integer jY);
//待办部门类型
    Result deptTask(String urgencyDegree, TaskInfoVO taskInfoVO, String type, Integer pageNo, Integer pageSize ,Integer jY);

    Result deptTaskHaveDone(String urgencyDegree, TaskInfoVO taskInfoVO, String type, Integer pageNo, Integer pageSize ,Integer jY);

    Result queryTaskDone(String urgencyDegree,TaskInfoVO taskInfoVO, Integer pageNo, Integer pageSize ,Integer jY);

    Result queryTaskMonitor(String urgencyDegree,TaskInfoVO taskInfoVO, Integer pageNo, Integer pageSize ,Integer jY,boolean isAdmin);

    Result queryTaskMyAgent(String urgencyDegree,TaskInfoVO taskInfoVO, Integer pageNo, Integer pageSize ,Integer jY);

}
