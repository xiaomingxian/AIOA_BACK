package com.cfcc.modules.workflow.service;

import com.cfcc.common.api.vo.Result;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.workflow.pojo.*;
import com.cfcc.modules.workflow.vo.TaskInfoVO;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TaskCommonService {

    void del(String id);

    List<TaskInfoJsonAble> allUndoltLimitTime();

    Result queryTaskToDo(TaskInfoVO taskInfoVO, Integer pageNo, Integer pageSize);

    Result queryTaskDone(TaskInfoVO taskInfoVO, Integer pageNo, Integer pageSize);

    List<Map> getLocationRun(TaskInfoVO taskInfoVO);

    List<Map> getEndLocation(String processDefinitionId);

    String doTask(TaskInfoVO taskInfoVO);

    void endProcess(TaskInfoVO taskInfoVO);

    Result commentQuery(String processInstanceId, Integer pageNo, Integer pageSize);

    boolean exeHaveDone(String processId);


    Map<String, Object> getActForm(String getProcessDefinitionId, String taskdefid);

    Task jump(JumpMsg jumpMsg, HttpServletRequest request);

    String queryTaskDefId(String proDefKey, String userId, String bussinessKey);

    void taskShift(String taskId, String sourceUserId, String sourceUsername, String destUserId);

    List<Activity> searchNextActs(String taskId, String proDefKey,String procDefkey);

    void batchEnd(String[] ids, SysUser user);

    Result reCallAble(String taskId, SysUser currentUser);

    List<Map<String, String>> queryAllHaveDone(String assignee, String procInstId);

    Result queryTaskMonitor(TaskInfoVO taskInfoVO, Integer pageNo, Integer pageSize,boolean isAdmin);

    boolean isLastsender(String userId, String proInstanId, String taskDefKey, String busDataId);

    boolean isTransactors(String userId, String proInstanId, String taskDefKey, String busDataId);


    List<Map<String, Object>> workTrack(String proInstId, boolean all);

    Result queryTaskMyAgent(TaskInfoVO taskInfoVO, Integer pageNo, Integer pageSize);

    Result deptTask(TaskInfoVO taskInfoVO, String type, Integer pageNo, Integer pageSize);

    Result deptTaskHaveDone(TaskInfoVO taskInfoVO, String type, Integer pageNo, Integer pageSize);

    Result batchChuanYue(List<Map<String, Object>> data, SysUser user);

    Result departFinish(String taskId, String processInstanceId,SysUser user);

    String doTasksMore(List<TaskInfoVO> taskInfoVOs);

    Result queryTaskShift(TaskInfoVO taskInfoVO, Integer pageNo, Integer pageSize);

    void updateTitle(Map<String, Object> query);

    ActivityImpl currentAct(String taskId, String processDefinitionId,String procDefkey);

    void addUsers(AddUsersMsg addUsersMsg);

    List<BackRecord> backRecord(String procInstId, String table);

    void back(JumpMsg jumpMsg, HttpServletRequest request);

    void finsh(String taskId);

    List<TaskInfoJsonAble> undo(String processInstanceId, LoginInfo loginInfo);

    List<TaskInfoJsonAble> sendByMe(String taskId);

    List<Map<String, String>> userHaveChoice(ArrayList<String> taskIds);

    void updateHisAct(Task list);

    int haveMainDept(String taskIdRecord,String procInstId);

    List<String> deptUsers(String taskIdRecord,String procInstId);

    void updateHisActDept(Task task, String randomParent);

    void updateRuActDept(Task task, String randomParent);

    String taskStatus(String taskid);
}
