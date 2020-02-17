package com.cfcc.modules.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.constant.workflow.TaskActType;
import com.cfcc.common.exception.AIOAException;
import com.cfcc.common.system.service.CommonDynamicTableService;
import com.cfcc.common.util.workflow.VarsWithBus;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.entity.SysUserAgent;
import com.cfcc.modules.system.mapper.SysUserAgentMapper;
import com.cfcc.modules.system.mapper.SysUserMapper;
import com.cfcc.modules.system.service.ISysUserService;
import com.cfcc.modules.utils.*;
import com.cfcc.modules.workflow.mapper.DepartWithTaskMapper;
import com.cfcc.modules.workflow.mapper.TaskMapper;
import com.cfcc.modules.workflow.mapper.TaskTransferMapper;
import com.cfcc.modules.workflow.pojo.*;
import com.cfcc.modules.workflow.service.*;
import com.cfcc.modules.workflow.vo.TaskInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.TaskServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.form.DefaultTaskFormHandler;
import org.activiti.engine.impl.form.FormPropertyHandler;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.ReadOnlyProcessDefinition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Transactional
@Slf4j
public class TaskCommonServiceImpl implements TaskCommonService {


    @Autowired
    private CommonDynamicTableService dynamicTableService;


    @Autowired
    private SysUserAgentMapper userAgentMapper;


    @Autowired
    private DepartWithTaskMapper departWithTaskMapper;

    @Autowired
    private IoaProcActinstService ioaProcActinstService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskServiceImpl taskServiceImpl;

    @Autowired
    private ReCallTaskService reCallTaskService;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private ProcessManagerService processManagerService;

    @Autowired
    private TaskTransferMapper taskTransferMapper;
    @Autowired
    private OaBusDataPermitService oaBusDataPermitService;

    @Autowired
    private ISysUserService userService;


    @Override
    public void del(String id) {
        runtimeService.deleteProcessInstance(id, "删除流程实例");
    }

    /**
     * 查询所有小于代办期限的数据
     *
     * @return
     */
    @Override
    public List<TaskInfoJsonAble> allUndoltLimitTime() {
        //1 查询所有时间限制之内的数据(流程实例id)[仅participant数据]
        List<TaskInfoJsonAble> list = taskMapper.allUndoltLimitTimeHaveAssignee();

        Iterator<TaskInfoJsonAble> iterator = list.iterator();

        HashMap<String, TaskInfoJsonAble> taskInfoJsonAbleHashMap = new HashMap<>();
        while (iterator.hasNext()) {
            TaskInfoJsonAble next = iterator.next();
            if (next.getAssignee() == null) {
                taskInfoJsonAbleHashMap.put(next.getId(), next);
                iterator.remove();
            }
        }
        //2 查询 candidate 数据
        if (taskInfoJsonAbleHashMap.size() > 0) {
            List<Map<String, Object>> allUndoltLimitTimeNoAssignee = taskMapper.allUndoltLimitTimeNoAssignee(taskInfoJsonAbleHashMap);
            allUndoltLimitTimeNoAssignee.stream().forEach(i -> {
                String taskId = i.get("taskId") + "";
                String userId = i.get("userId") + "";
                TaskInfoJsonAble taskInfoJsonAble = taskInfoJsonAbleHashMap.get(taskId);
                taskInfoJsonAble.setAssignee(userId);
                list.add(taskInfoJsonAble);
            });
        }

        return list;
    }

    /**
     * 根据用户id 查询自己的待办+委托给我的
     */
    @Override
    public Result queryTaskToDo(TaskInfoVO taskInfoVO, Integer pageNo, Integer pageSize) {

        /**
         * 判断是否是查部门的
         */
        if (taskInfoVO.getIsDept() != null && taskInfoVO.getIsDept()) {
            return deptTask(taskInfoVO, taskInfoVO.getDeptType(), pageNo, pageSize);
        }


        // 考虑时间范围(已在sql中实现)
        List<SysUserAgent> userAgents = userAgentMapper.agentIsMe(taskInfoVO.getUserName());
        List<String> ids = new ArrayList<>();
        if (userAgents.size() > 0) {
            //委托给自己的人
            ids = userMapper.queryUserIdsByName(userAgents);
        }
        //自己
        if (null != taskInfoVO.getUserId()) ids.add(taskInfoVO.getUserId());

        Result<IPage> iPageResult = new Result<>();
        IPage iPage = new Page(pageNo, pageSize);


        long count = taskMapper.queryTaskToDoCount(taskInfoVO, ids);
        //1 作为assignee与候选人
        List<TaskInfoJsonAble> taskInfoJsonAbles = taskMapper.queryTaskToDo(taskInfoVO, ids, (pageNo - 1) * pageSize, pageSize);
        String agentUserName = taskInfoVO.getAgentUserName();
        if (StringUtils.isNotBlank(agentUserName)) {
            taskInfoJsonAbles.stream().forEach(i -> {
                i.setAgentUserName(agentUserName);
            });
        }
        //分页参数
        iPageResult.setSuccess(true);
        iPage.setTotal(count);
        iPage.setRecords(taskInfoJsonAbles);
        iPage.setCurrent(pageNo);
        iPage.setSize(pageSize);
        iPageResult.setResult(iPage);
        return iPageResult;
    }

    /**
     * 流程监控数据  所有参与过的数据(直接参与与身为候选人)
     */
    @Override
    public Result queryTaskMonitor(TaskInfoVO taskInfoVO, Integer pageNo, Integer pageSize, boolean isAdmin) {

        //TODO 考虑代理人情况

        //判断是否是部门类型
        Boolean isDept = taskInfoVO.getIsDept();
        if (isDept != null && isDept) {
            //查询部门类型
            return deptTaskMonitor(taskInfoVO, taskInfoVO.getDeptType(), pageNo, pageSize, isAdmin);
        }


        Result<IPage> iPageResult = new Result<>();
        IPage iPage = new Page(pageNo, pageSize);

        int count = taskMapper.monitorCount(taskInfoVO, isAdmin);

        List<TaskInfoJsonAble> list = taskMapper.monitorData(taskInfoVO, (pageNo - 1) * pageSize, pageSize, isAdmin);

        //分页参数
        iPage.setTotal(count);
        iPageResult.setSuccess(true);
        iPage.setRecords(list);
        iPage.setCurrent(pageNo);
        iPage.setSize(pageSize);
        iPageResult.setResult(iPage);
        return iPageResult;
    }

    private Result deptTaskMonitor(TaskInfoVO taskInfoVO, String type, Integer pageNo, Integer pageSize, boolean isAdmin) {

        Result<IPage> iPageResult = new Result<>();
        IPage iPage = new Page(pageNo, pageSize);

        Long count = taskMapper.deptTaskMonitorCount(taskInfoVO, type, isAdmin);
        count = count == null ? 0 : count;

        List<TaskInfoJsonAble> list = taskMapper.deptTaskMonitorQuery(taskInfoVO, type, (pageNo - 1) * pageSize, pageSize, isAdmin);

        //分页参数
        iPage.setTotal(count);
        iPageResult.setSuccess(true);
        iPage.setRecords(list);
        iPage.setCurrent(pageNo);
        iPageResult.setResult(iPage);
        iPage.setSize(pageSize);
        return iPageResult;
    }

    /**
     * 是否是流程的已办用户
     *
     * @param userId
     * @param proInstanId
     * @return
     */
    @Override
    public boolean isLastsender(String userId, String proInstanId, String taskDefKey, String busDataId) {
        //long count = historyService.createHistoricTaskInstanceQuery().processInstanceId(proInstanId)
        //        .processDefinitionKey(taskDefKey).processInstanceBusinessKey(busDataId)
        //        .taskAssignee(userId).finished().count();
        //判断是否是 当前任务的 发出环节
        List<Task> list = taskService.createTaskQuery().processInstanceId(proInstanId).list();

        if (list.size() <= 0) {
            return false;
        }
        List<HistoricTaskInstance> list1 = historyService.createHistoricTaskInstanceQuery().processInstanceId(proInstanId)
                .processDefinitionKey(taskDefKey).processInstanceBusinessKey(busDataId)
                .taskAssignee(userId).finished().list();
        ArrayList<String> hisIds = new ArrayList<>();
        for (HistoricTaskInstance task : list1) {
            String id = task.getId();
            hisIds.add(id);
        }

        //遍历当前待办 存在任何一条 父id是自己的已办 任务id的就认为是 已办
        for (Task task : list) {
            String parentTaskId = task.getParentTaskId();
            if (hisIds.contains(parentTaskId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isTransactors(String userId, String proInstanId, String taskDefKey, String busDataId) {
        long count = taskService.createTaskQuery().processInstanceId(proInstanId)
                .processDefinitionKey(taskDefKey).processInstanceBusinessKey(busDataId)
                .taskCandidateOrAssigned(userId).count();
        return count > 0;
    }

    /**
     * 为任务属性赋值
     *
     * @param maps
     * @param userIds
     * @param identitylinks
     * @param idens
     */
    private void taskPropertiesSet(List<Map<String, Object>> maps, ArrayList<String> userIds,
                                   ArrayList<String> identitylinks, Map<String,
            Map<String, String>> idens, Map<String, Map<String, Object>> deptMsg) {
        //把候选人的用户id加进去
        if (null != identitylinks && identitylinks.size() > 0) {

            Set<String> keySet = idens.keySet();

            Map<String, Map<String, String>> finalIdens = idens;
            keySet.stream().forEach(taskId -> {
                String ids = finalIdens.get(taskId).get("userIds");

                List<String> idss = Arrays.asList(ids.split(","));
                userIds.addAll(idss);
            });

        }


        Map<String, SysUser> users = userMapper.queryUserMsgByIds(userIds);

        //代办信息组装
        //Iterator<Map<String, Object>> iterator = maps.iterator();
        ListIterator<Map<String, Object>> iterator = maps.listIterator();


        while (iterator.hasNext()) {
            Map<String, Object> next = iterator.next();
            String taskId = next.get("taskId") + "";
            String type = next.get("type") + "";
            Date endTime = (Date) next.get("endTime");
            //类型是否是部门办理
            boolean isDept = TaskActType.DEPT.equals(type) && deptMsg != null && deptMsg.size() > 0;

            if (null == next.get("user")) {
                //候选人 任务//移除本组记录 插入所有候选人记录
                if (idens.size() > 0 && idens.get(taskId) != null) {
                    List<String> ids = Arrays.asList(idens.get(taskId).get("userIds").split(","));

                    String taskDefName = next.get("taskDefName") + "";
                    iterator.remove();

                    for (String id : ids) {
                        if ("".equals(id)) continue;
                        SysUser sysUser = users.get(id);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("userName", sysUser.getUsername());
                        String deptName = sysUser.getDeptName();
                        map.put("taskDefName", taskDefName);
                        map.put("endTime", endTime);
                        if (isDept) deptName += "【" + deptMsg.get(sysUser.getId()).get("type") + "】";
                        map.put("deptName", deptName);

                        iterator.add(map);

                    }

                }

            } else {
                TaskTransfer taskTransfer = taskTransferMapper.selectTaskLog(taskId);

                String uId = next.get("user") + "";
                SysUser sysUser = users.get(uId);
                if (sysUser == null) continue;
                String userName = sysUser.getUsername();
                if (taskTransfer != null) {
                    userName = taskTransfer.getTransferLog();
                }
                String deptName = sysUser.getDeptName();
                //部门
                if (isDept) {
                    //判断是否是移交过来的
                    if (deptMsg.get(sysUser.getId()) == null) {
                        //当前任务已经被移交给其他人
                        //查询移交记录----根据taskid去查移交记录表
                        String sourceUserId = taskTransfer.getSourceUserId();
                        if (sourceUserId == null) ;
                        else {
                            sysUser.setUsername(taskTransfer.getTransferLog());
                            deptName += "【" + deptMsg.get(sourceUserId).get("type") + "】";
                        }
                    } else {
                        deptName += "【" + deptMsg.get(sysUser.getId()).get("type") + "】";
                    }
                }
                next.put("userName", userName);
                next.put("deptName", deptName);
            }
        }


    }

    @Override
    public List<Map<String, Object>> workTrack(String proInstId, boolean all) {

        List<Map<String, Object>> res = new ArrayList<>();

        List<HistoricTaskInstance> list = null;
        if (all) {//查询已办和待办
            list = historyService.createHistoricTaskInstanceQuery().processInstanceId(proInstId)
                    .orderByTaskDueDate().asc()
                    .orderByTaskCreateTime().asc()
                    .list();
        } else {//只查待办
            list = historyService.createHistoricTaskInstanceQuery().processInstanceId(proInstId)
                    .orderByTaskDueDate().asc()
                    .orderByTaskCreateTime().asc()
                    .unfinished().list();
        }
        //获取流程定义key
        if (list.size() == 0) return res;
        String processDefinitionId = list.get(0).getProcessDefinitionId();
        String key = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId)
                .singleResult().getKey();
        //去查所有任务的类型
        Map<String, OaProcActinst> oaProcActinstMap = taskMapper.queryTaskDefType(key);
        //记录部门类型的任务id
        List<String> deptTaskIds = new ArrayList<>();
        //候选人的任务id与assignee查询方式不同
        ArrayList<String> identitylinks = new ArrayList<>();
        //查询候选人的用户id 记录到所有用户id集合中
        Map<String, Map<String, String>> idens = new HashMap<>();


        ArrayList<String> userIds = new ArrayList<>();
        for (HistoricTaskInstance h : list) {
            String assignee = h.getAssignee();
            if (assignee != null && assignee.startsWith("撤回:")) continue;

            Map<String, Object> one = new HashMap<>();

            String taskId = h.getId();
            String taskName = h.getName();
            Date endTime = h.getEndTime();
            String taskDefinitionKey = h.getTaskDefinitionKey();

            //查看类型[用户/角色/部门]
            OaProcActinst oaProcActinst = oaProcActinstMap.get(taskDefinitionKey);
            if (oaProcActinst == null) return null;

            String type = oaProcActinstMap.get(taskDefinitionKey).getUserOrRole();
            if (TaskActType.DEPT.equals(type)) {
                deptTaskIds.add(taskId);
            }
            //是assignee还是候选人
            if (assignee == null) {
                //是候选人任务
                identitylinks.add(taskId);
            }
            one.put("taskDefName", taskName);
            one.put("endTime", endTime);
            one.put("user", assignee);
            one.put("dept", "");
            one.put("type", type);
            one.put("taskId", taskId);
            one.put("taskDefinitionKey", taskDefinitionKey);

            userIds.add(assignee);

            if (assignee != null && (assignee.startsWith("撤回:") || assignee.startsWith("回退"))) continue;
            res.add(one);

        }
        if (identitylinks.size() > 0) idens = taskMapper.identitylinksUsers(identitylinks);
        //查询部门的相关(主办/辐办/传阅)
        Map<String, Map<String, Object>> deptMsg = null;
        if (deptTaskIds.size() > 0) {
            deptMsg = taskMapper.taskDeptMsg(deptTaskIds);
        }
        taskPropertiesSet(res, userIds, identitylinks, idens, deptMsg);

        return res;
    }


    @Override
    public Result queryTaskMyAgent(TaskInfoVO taskInfoVO, Integer pageNo, Integer pageSize) {
        //查询出代理人的起止时间
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_name", taskInfoVO.getUserName());
        List<SysUserAgent> sysUserAgents = userAgentMapper.selectByMap(map);
        SysUserAgent agent = sysUserAgents.get(0);
        Date startTimeAgent = agent.getStartTime();
        Date endTimeAgent = agent.getEndTime();
        Date startQueryTime = taskInfoVO.getStartQueryTime();
        Date endQueryTime = taskInfoVO.getEndQueryTime();

        if (startQueryTime != null) {//开始时间[查询时间早于代理开始时间就以代理时间为准]
            startTimeAgent = startQueryTime.getTime() <= startTimeAgent.getTime() ? startTimeAgent : startQueryTime;
        }

        if (endQueryTime != null) {//结束时间[查询时间小于代理结束时间，就以查询时间为准]
            endTimeAgent = endQueryTime.getTime() <= endTimeAgent.getTime() ? endQueryTime : endTimeAgent;
        }
        //查询待办
        taskInfoVO.setStartQueryTime(startTimeAgent);
        taskInfoVO.setEndQueryTime(endTimeAgent);
        taskInfoVO.setAgentUserName(agent.getAgentUserName());
        Result<IPage> result = queryTaskToDo(taskInfoVO, pageNo, pageSize);

        return result;
    }


    /**
     * 待办部门类型
     */
    @Override
    public Result deptTask(TaskInfoVO taskInfoVO, String type, Integer pageNo, Integer pageSize) {
        Result<IPage> iPageResult = new Result<>();
        IPage iPage = new Page(pageNo, pageSize);

        Long count = taskMapper.deptTaskCount(taskInfoVO, type);
        count = count == null ? 0 : count;

        List<TaskInfoJsonAble> list = taskMapper.deptTaskQuery(taskInfoVO, type, (pageNo - 1) * pageSize, pageSize);

        //分页参数
        iPage.setTotal(count);
        iPageResult.setSuccess(true);
        iPage.setRecords(list);
        iPage.setCurrent(pageNo);
        iPageResult.setResult(iPage);
        iPage.setSize(pageSize);
        return iPageResult;
    }


    /**
     * 待办部门类型
     */
    @Override
    public Result deptTaskHaveDone(TaskInfoVO taskInfoVO, String type, Integer pageNo, Integer pageSize) {


        Result<IPage> iPageResult = new Result<>();
        IPage iPage = new Page(pageNo, pageSize);

        Long count = taskMapper.deptTaskHaveDoneCount(taskInfoVO, type);
        count = count == null ? 0 : count;


        List<TaskInfoJsonAble> list = taskMapper.deptTaskHaveDone(taskInfoVO, type, (pageNo - 1) * pageSize, pageSize);

        //分页参数
        iPage.setTotal(count);
        iPageResult.setSuccess(true);
        iPage.setRecords(list);
        iPage.setCurrent(pageNo);
        iPageResult.setResult(iPage);
        iPage.setSize(pageSize);
        return iPageResult;
    }


    /**
     * 根据id查询已办
     *
     * @return
     */
    @Override
    public Result queryTaskDone(TaskInfoVO taskInfoVO, Integer pageNo, Integer pageSize) {

        //如果是部门任务
        if (taskInfoVO.getIsDept() != null && taskInfoVO.getIsDept()) {
            return deptTaskHaveDone(taskInfoVO, taskInfoVO.getDeptType(), pageNo, pageSize);
        }

        List<HisTaskJsonAble> list = taskMapper.queryTaskDone(taskInfoVO, (pageNo - 1) * pageSize, pageSize);
        //数据后续组装*************(不需要已办结的了)
        //ArrayList<String> ids = new ArrayList<>();
        //for (HisTaskJsonAble hisTaskJsonAble : list) {
        //if (hisTaskJsonAble.getIsDone() == null) {
        //    hisTaskJsonAble.setTaskDefinitionKey("end");
        //    hisTaskJsonAble.setTaskDefinitionKeyName("已结束");
        //}
        //ids.add(hisTaskJsonAble.getProcessInstanceId());
        //}

        Long count = taskMapper.queryTaskDoneCount(taskInfoVO);
        //分页
        Result<IPage> iPageResult = new Result<>();
        IPage iPage = new Page(pageNo, pageSize);
        //分页参数
        iPageResult.setSuccess(true);
        iPage.setTotal(count);
        iPage.setRecords(list);
        iPage.setSize(pageSize);
        iPage.setCurrent(pageNo);
        iPageResult.setResult(iPage);
        return iPageResult;
    }


    /**
     * 入口只有下一任务
     */
    @Override
    public String doTasksMore(List<TaskInfoVO> taskInfoVOs) {
        //将数据变成一条
        TaskInfoVO taskInfoVOSource = taskInfoVOs.get(0);
        TaskInfoVO taskInfoVO = new TaskInfoVO();
        BeanUtils.copyProperties(taskInfoVOSource, taskInfoVO);
        String taskId = taskInfoVO.getTaskId();
        //判断有没有开启流程
        Map<String, Object> map = taskInfoVO.getBusData();
        //获取任务描述
        String busMsg = getTaskDescript(taskInfoVO);

        Task task = null;
        if (taskId == null) {
            task = haveNoStartProc(taskId, map, taskInfoVO);
            taskId = task.getId();
            addTaskDescript(task.getProcessInstanceId(), busMsg);
        } else {
            task = taskService.createTaskQuery().taskId(taskId).singleResult();

        }

        String assignee = task.getAssignee();
        if (assignee == null) {//如果是候选人-先签收
            taskService.claim(taskId, taskInfoVO.getUserId());
        }

        //将所有的参数拼接在一起
        HashMap<String, Object> vars = new HashMap<>();
        taskInfoVOs.stream().forEach(t -> {
            vars.putAll(t.getVars());
        });
        taskInfoVO.setVars(vars);

        //boolean qinaFa = isQinaFa(task);


        //判断下一环节是否需要记录用户与使用记录的用户
        //recordKeyAndUse(taskInfoVO, taskId, task);
        //1 流程已办
        taskService.complete(taskId, taskInfoVO.getVars());
        // 记录接下来的待办的上一环节 用来撤回
        List<Task> list = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        if (list.size() > 0) {
            for (Task task1 : list) {
                task1.setParentTaskId(taskId);
                taskService.saveTask(task1);
            }
        }

        //补充任务描述
        addTaskDescript(task.getProcessInstanceId(), busMsg);


        ////2 查询下n任务节点信息
        //if (qinaFa) {
        //    return nextActMore(taskInfoVOs, task) + "  ";
        //} else {
        //    return nextActMore(taskInfoVOs, task);
        //}
        //2 查询下一任务节点信息
        return nextActMore(taskInfoVOs, task);
    }

    /**
     * 任务移交
     */
    @Override
    public Result queryTaskShift(TaskInfoVO taskInfoVO, Integer pageNo, Integer pageSize) {
        //TODO
        String shiftUsers = taskInfoVO.getShiftUsers();
        List<String> ids = new ArrayList<>();

        if (shiftUsers != null && shiftUsers.length() > 0) {
            if (shiftUsers.contains(",")) ids = Arrays.asList(shiftUsers.split(","));
            if (!shiftUsers.contains(",")) ids.add(shiftUsers);
        }
        long count = taskMapper.queryTaskShiftCount(taskInfoVO, ids);
        List<TaskInfoJsonAble> list = taskMapper.queryTaskShift(taskInfoVO, ids, (pageNo - 1) * pageSize, pageSize);

        Result<IPage> iPageResult = new Result<>();
        IPage iPage = new Page(pageNo, pageSize);
        //分页参数
        iPageResult.setSuccess(true);
        iPage.setTotal(count);
        iPage.setRecords(list);
        iPage.setSize(pageSize);
        iPage.setCurrent(pageNo);
        iPageResult.setResult(iPage);
        return iPageResult;
    }

    @Override
    public void updateTitle(Map<String, Object> query) {
        String i_id = query.get("i_id") == null ? "" : query.get("i_id").toString();
        String table = query.get("table") == null ? "" : query.get("table").toString();
        String title = query.get("s_title") == null ? "" : query.get("s_title").toString();

        HashMap<String, Object> queryWrapper = new HashMap<>();
        queryWrapper.put("table", table);
        queryWrapper.put("i_id", i_id);

        //IPage<Map<String, Object>> mapIPage = dynamicTableService.queryByEquals(1, 1, queryWrapper);
        //List<Map<String, Object>> records = mapIPage.getRecords();
        //Map<String, Object> dataInTable = new HashMap<>();
        //if (records.size() > 0) {
        //    dataInTable = records.get(0);
        //}


        List<String> busMsgs = taskMapper.queryTitleMsg(i_id, table);
        if (busMsgs.size() == 0) return;
        String busMsg = busMsgs.get(0);
        if (busMsg == null) {
            log.error("-----------------table:" + table + ",id:" + i_id + ",对应的流程变量不存在");
            return;
        }

        String[] msgs = busMsg.split(",");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < msgs.length; i++) {
            String msg = msgs[i];
            if (i == 3) {
                if (query.get("s_title") != null) {
                    msg = "[s_title:" + title + "]";
                }
            }
            if (i == 0) {
                if (query.get("i_urgency") != null) {
                    msg = query.get("i_urgency").toString();
                }
            }
            if (i == 2) {
                if (query.get("s_file_num") != null) {
                    msg = "{file_num:" + (query.get("s_file_num").toString()) + "}";
                }
            }
            if (i == 12) {
                if (query.get("i_is_important") != null) {
                    msg = "^important:" + (query.get("i_is_important").toString()) + "^";
                }
            }
            if (i == msgs.length - 1) {
                sb.append(msg);
            } else {
                sb.append(msg + ",");
            }

        }

        taskMapper.updateHisMsg(i_id, table, sb.toString());
        List<String> inTask = taskMapper.queryTitleMsgInTask(i_id, table);
        if (inTask.size() == 0) return;
        taskMapper.updateInTaskMsg(i_id, table, sb.toString());
    }

    @Override
    public ActivityImpl currentAct(String taskId, String processDefinitionId, String procDefkey) {


        //String processDefinitionId = null;
        HistoricTaskInstance task = null;
        if (taskId == null) {
            ProcessDefinition processDefinition = null;
            if (processDefinitionId == null) {
                processDefinition = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionKey(procDefkey)
                        .latestVersion().singleResult();
            } else {
                processDefinition = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(processDefinitionId)
                        //.processDefinitionKey(procDefkey)
                        .latestVersion().singleResult();
            }
            if (processDefinition == null) throw new AIOAException("未找到对应的流程信息");

            processDefinitionId = processDefinition.getId();
        } else {
            task = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            processDefinitionId = task.getProcessDefinitionId();
            if (processDefinitionId==null) {
                //可能是追加的 没有在历史表里产生数据
                Task task1 = taskService.createTaskQuery().taskId(taskId).singleResult();
                if (task1==null)throw new AIOAException("追加用户信息不完整");
                processDefinitionId=task1.getProcessDefinitionId();
            }
        }

        ProcessDefinitionEntity proc = (ProcessDefinitionEntity) repositoryService
                .getProcessDefinition(processDefinitionId);
        //所有节点
        List<ActivityImpl> activities = proc.getActivities();
        List<ActivityImpl> actsAll = new ArrayList<>();
        processManagerService.getAllAct(activities, actsAll);
        proc.getActivities();
        //下一任务节点
        List<Activity> nexts = new ArrayList<Activity>();
        //当前节点
        ActivityImpl currentAct = null;
        String key = proc.getKey();
        ArrayList<String> taskIds = new ArrayList<>();


        if (task == null) {
            currentAct = activities.get(0);
        }
        Integer count = 0;
        for (ActivityImpl activity : actsAll) {
            String id = activity.getId();
            taskIds.add(id);

            //直接获取第二个节点
            if (null != task && task.getTaskDefinitionKey().equals(id)) {
                currentAct = activity;
            }
        }


        return currentAct;
    }

    @Override
    public void addUsers(AddUsersMsg addUsersMsg) {
        //先判断 当前节点是否是可追加性质的节点
        Task task = taskService.createTaskQuery().taskId(addUsersMsg.getTaskId()).singleResult();
        ActivityImpl activity = currentAct(task.getId(), null, null);
        ActivityBehavior activityBehavior = activity.getActivityBehavior();

        //只有多实例


        AddUserCmd addUserCmd = null;

        String executionId = task.getExecutionId();
        addUsersMsg.setExecutionId(executionId);


    }

    @Override
    public List<BackRecord> backRecord(String procInstId, String table) {


        return taskMapper.backRecord(procInstId, table);
    }

    @Override
    public void back(JumpMsg jumpMsg, HttpServletRequest request) {
        //所回退的节点
        Task back = this.jump(jumpMsg, request);
        if (back == null) throw new AIOAException("所回退的环节不存在");
        LoginInfo loginInfo = userService.getLoginInfo(request);

        //存储意见
        String table = jumpMsg.getTable() + "_opinion";
        HashMap<String, Object> map = new HashMap<>();
        map.put("table", table);
        map.put("i_busdata_id", jumpMsg.getTableId());

        map.put("s_taskdef_key", jumpMsg.getCurrActDefKey());

        map.put("s_name", loginInfo.getUsername());
        SysDepart depart = loginInfo.getDepart();
        if (depart != null) {
            map.put("s_dept_id", loginInfo.getDepart().getId());
            map.put("s_dept_name", loginInfo.getDepart().getDepartName());
        }


        map.put("i_user_id", loginInfo.getId());
        map.put("d_sign", new Date());
        map.put("s_opinion", jumpMsg.getBackReason());
        map.put("s_opinion_type", -1);
        map.put("s_task_id", back.getId());

        dynamicTableService.insertData(map);

    }

    @Override
    public void finsh(String taskId) {
        //查询当前任务是否是多实例的一员
        List<Task> list = taskService.createTaskQuery().taskId(taskId).list();
        if (list.size() <= 0) throw new AIOAException("当前任务不属于您的办理环节");
        taskService.complete(taskId);
    }

    @Override
    public List<TaskInfoJsonAble> undo(String processInstanceId, LoginInfo loginInfo) {

        ArrayList<TaskInfoJsonAble> taskInfoJsonAbles = new ArrayList<>();
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).taskCandidateOrAssigned(loginInfo.getId()).list();

        list.stream().forEach(task -> {

            TaskInfoJsonAble taskInfoJsonAble = new TaskInfoJsonAble();
            BeanUtils.copyProperties(task, taskInfoJsonAble);
            taskInfoJsonAbles.add(taskInfoJsonAble);

        });

        return taskInfoJsonAbles;
    }

    @Override
    public List<TaskInfoJsonAble> sendByMe(String taskId) {
        ArrayList<TaskInfoJsonAble> sendByMes = new ArrayList<>();


        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(historicTaskInstance
                        .getProcessDefinitionId())
                .singleResult();
        if (historicTaskInstance == null) throw new AIOAException("taskId:" + taskId + " 对应的任务不存在");
        String processInstanceId = historicTaskInstance.getProcessInstanceId();
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
        ArrayList<String> taskDefs = new ArrayList<>();
        //记录选择的用户
        HashMap<String, TaskInfoJsonAble> usersHaveChoice = new HashMap<>();
        list.stream().forEach(task -> {
            String parentTaskId = task.getParentTaskId();
            String taskDefinitionKey = task.getTaskDefinitionKey();
            String assignee = task.getAssignee();
            //记录已经选择过的用户
            if (taskId.equals(parentTaskId) && taskDefs.contains(taskDefinitionKey)) {
                TaskInfoJsonAble taskInfoJsonAble = usersHaveChoice.get(taskDefinitionKey);
                taskInfoJsonAble.getUsersHaveChoice().add(assignee);
            }
            //是自己发出且 已经记录过节点
            if (taskId.equals(parentTaskId) && !taskDefs.contains(taskDefinitionKey)) {
                taskDefs.add(taskDefinitionKey);
                TaskInfoJsonAble hisTaskJsonAble = new TaskInfoJsonAble();
                BeanUtils.copyProperties(task, hisTaskJsonAble);
                hisTaskJsonAble.setBusMsg(task.getDescription());
                hisTaskJsonAble.setProDefName(processDefinition.getKey());
                sendByMes.add(hisTaskJsonAble);
                hisTaskJsonAble.getUsersHaveChoice().add(assignee);
                usersHaveChoice.put(taskDefinitionKey, hisTaskJsonAble);
            }


        });

        return sendByMes;
    }

    @Override
    public List<Map<String, String>> userHaveChoice(ArrayList<String> taskIds) {

        return taskMapper.userHaveChoice(taskIds);
    }

    @Override
    public void updateHisAct(Task tasks) {
        taskMapper.updateHisAct(tasks);
    }

    @Override
    public int haveMainDept(String taskIdRecord) {
        return taskMapper.haveMainDept(taskIdRecord);
    }

    @Override
    public List<String> deptUsers(String taskIdRecord) {
        return taskMapper.deptUsers(taskIdRecord);
    }


    private String nextActMore(List<TaskInfoVO> taskInfoVOs, Task task) {
        String processInstanceId = task.getProcessInstanceId();
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        StringBuffer nextTaskMsg = new StringBuffer();
        if (list.size() == 0) {
            //任务已经完成
            nextTaskMsg.append("end").append("-'").append("已结束");
        } else {

            ArrayList<String> next = new ArrayList<>();

            for (TaskInfoVO taskInfoVO : taskInfoVOs) {
                Map<String, Object> vars = taskInfoVO.getVars();
                Collection<Object> values = vars.values();
                boolean flag = false;
                for (Object value : values) {
                    if (value instanceof String && ((String) value).startsWith("no-")) {
                        flag = true;
                        break;
                    }
                }
                if (flag) continue;


                //记录下n个环节
                for (Task t : list) {
                    String id = t.getId();
                    String taskDefinitionKey = t.getTaskDefinitionKey();
                    String name = t.getName();
                    if (!next.contains(taskDefinitionKey + "-" + name)) {
                        next.add(taskDefinitionKey + "-" + name);
                    }

                    Boolean isDept = taskInfoVO.getIsDept();
                    //部门信息存储
                    if (null != isDept && isDept) {
                        //存储部门相关信息
                        TaskWithDepts taskWithDepts = taskInfoVO.getTaskWithDepts();
                        //变量与任务的对应关系----如何区分主办/辐办/传阅
                        taskWithDepts.setTaskDefKey(taskDefinitionKey);
                        //存储下个节点的任务id---主办/辐办/传阅等类型都存储起来(会有重复数据)[数量=用户量*种类]
                        taskWithDepts.setTskId(id);
                        //请求数据库 2-3 次
                        departWithTaskMapper.save(processInstanceId, taskWithDepts);
                    }
                }


            }

            String s = next.get(0);
            //TODO 暂时这么做,后续修改
            nextTaskMsg.append(s);
        }
        return nextTaskMsg.toString();
    }

    /**
     * 办理任务
     */
    @Override
    public String doTask(TaskInfoVO taskInfoVO) {
        String taskId = taskInfoVO.getTaskId();
        //判断有没有开启流程
        Map<String, Object> map = taskInfoVO.getBusData();
        String busMsg = getTaskDescript(taskInfoVO);

        Task task = null;
        if (taskId == null) {
            task = haveNoStartProc(taskId, map, taskInfoVO);
            taskId = task.getId();
            addTaskDescript(task.getProcessInstanceId(), busMsg);
        } else {
            task = taskService.createTaskQuery().taskId(taskId).singleResult();

        }


        //判断节点类型：抢签或者普通
        String assignee = task.getAssignee();
        if (assignee == null) {//如果是候选人-先签收
            taskService.claim(taskId, taskInfoVO.getUserId());
        }
        boolean qinaFa = isQinaFa(task);


        //判断下一环节是否需要记录用户与使用记录的用户
        recordKeyAndUse(taskInfoVO, taskId, task);
        //保存任务时仅开启流程
        Object justStart = map.get("justStart");
        if (justStart == null) {

            //1 流程已办
            taskService.complete(taskId, taskInfoVO.getVars());
            //查询待办任务 为 parent 设置id 为上一环节的taskId 用来撤回
            String processInstanceId = task.getProcessInstanceId();
            List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            if (list.size() > 0) {
                for (Task task1 : list) {
                    task1.setParentTaskId(taskId);
                    taskService.saveTask(task1);
                }
            }

        }

        //补充任务描述
        addTaskDescript(task.getProcessInstanceId(), busMsg);

        //2 查询下一任务节点信息
        if (qinaFa) {
            return nextAct(taskInfoVO, task) + "  ";
        } else {
            return nextAct(taskInfoVO, task);
        }

    }

    private String getTaskDescript(TaskInfoVO taskInfoVO) {
        String busMsg = null;//业务描述信息
        Map<String, Object> vars = taskInfoVO.getVars();
        if (vars != null) {
            busMsg = vars.get("busMsg") == null ? null : vars.get("busMsg").toString();
        }
        return busMsg;
    }

    private void addTaskDescript(String procInstId, String busMsg) {

        List<Task> list = taskService.createTaskQuery().processInstanceId(procInstId).list();

        list.stream().forEach(task -> {
            task.setDescription(busMsg);
            taskService.saveTask(task);
        });

    }

    /**
     * 是否是签发人
     *
     * @param task
     * @return
     */
    private boolean isQinaFa(Task task) {
        String processDefinitionId = task.getProcessDefinitionId();
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().
                processDefinitionId(processDefinitionId).latestVersion().list();
        ProcessDefinition processDefinition = null;
        if (list.size() > 0) {
            processDefinition = list.get(0);
        }
        if (processDefinition == null) return false;
        LambdaQueryWrapper<OaProcActinst> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OaProcActinst::getProcDefKey, processDefinition.getKey())
                .eq(OaProcActinst::getActId, task.getTaskDefinitionKey())
                .eq(OaProcActinst::getActName, task.getName());
        List<OaProcActinst> list1 = ioaProcActinstService.list(wrapper);
        if (list1.size() > 0) {
            Boolean signer = list1.get(0).getSigner();
            signer = signer == null ? false : signer;
            return signer;
        } else {
            return false;
        }
    }

    /**
     * 办完现在的查询之后的
     */
    private String nextAct(TaskInfoVO taskInfoVO, Task task) {
        String processInstanceId = task.getProcessInstanceId();
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();

        StringBuffer nextTaskMsg = new StringBuffer();
        if (list.size() == 0) {
            //任务已经完成
            nextTaskMsg.append("end").append("-'").append("已结束");
        } else {
            //
            String taskKey = list.get(0).getTaskDefinitionKey();
            String name = list.get(0).getName();
            nextTaskMsg.append(taskKey).append("-'").append(name);

            //部门信息存储
            Boolean isDept = taskInfoVO.getIsDept();
            if (null != isDept && isDept) {
                //存储部门相关信息
                TaskWithDepts taskWithDepts = taskInfoVO.getTaskWithDepts();
                //变量与任务的对应关系----如何区分主办/辐办/传阅
                for (Task t : list) {
                    String id = t.getId();
                    String taskDefinitionKey = t.getTaskDefinitionKey();
                    taskWithDepts.setTaskDefKey(taskDefinitionKey);
                    //存储下个节点的任务id---主办/辐办/传阅等类型都存储起来(会有重复数据)[数量=用户量*种类]
                    taskWithDepts.setTskId(id);
                    //请求数据库 2-3 次
                    departWithTaskMapper.save(processInstanceId, taskWithDepts);
                }
            }
        }
        return nextTaskMsg.toString();
    }

    private Task haveNoStartProc(String taskId, Map<String, Object> map, TaskInfoVO taskInfoVO) {
        Task task = null;
        if (taskId == null) {
            String key = (String) map.get("s_cur_proc_name");
            String bussinessKey = map.get("i_id") + "";
            String userId = map.get("s_create_by") + "";
            boolean qiangQian = false;
            if (userId.contains(",")) {
                userId = userId.substring(0, (userId.length() - 1));
                qiangQian = true;
                map.put("s_create_by", "");
            }
            String busMsg = VarsWithBus.getBusMsg(map);

            HashMap<String, Object> vars = new HashMap<>();
            firstActEl(key, vars);
            boolean normal = (boolean) vars.entrySet().iterator().next().getValue();
            String draft = vars.entrySet().iterator().next().getKey();
            //true表示普通 ,false表示抢签
            if (qiangQian && normal) {
                //传过来的是抢签 但是只允许普通 提示
                throw new AIOAException("该流程第一环节不支持抢签,请检查流程设计或业务配置");
            }
            vars.put(draft, userId);


            //流程相关展示存储
            vars.put("busMsg", busMsg);
            //开启
            ProcessInstance processInstance = processManagerService.start(key, bussinessKey, vars);
            //先开启(正常情况下一条数据)
            List<Task> list = taskService.createTaskQuery()
                    .processDefinitionKey(key)
                    .processInstanceBusinessKey(bussinessKey)
                    .processInstanceId(processInstance.getId())
                    .taskCandidateOrAssigned(taskInfoVO.getUserId())
                    .list();
            if (list.size() == 0) throw new AIOAException("未找到您的待办任务，请刷新页面再次尝试");

            task = list.get(0);


        }
        return task;
    }

    private void firstActEl(String key, HashMap<String, Object> vars) {

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(key).latestVersion().singleResult();

        if (processDefinition == null) {
            throw new AIOAException("该类业务无对应流程,请检查配置");
        }
        //查询第一个节点的信息
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService
                .getProcessDefinition(processDefinition.getId());

        //获取输出
        List<ActivityImpl> activities = processDefinitionEntity.getActivities();
        //只允许有assignee或候选人表达式 其他为非法(自定义)
        ActivityImpl activity = activities.get(0);
        ActivityBehavior activityBehavior = activity.getActivityBehavior();
        if (!(activityBehavior instanceof UserTaskActivityBehavior)) return;//提示报错 第一环节只允许普通类型节点
        TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activityBehavior).getTaskDefinition();
        Expression assigneeExpression = taskDefinition.getAssigneeExpression();
        Set<Expression> candidateUserIdExpressions = taskDefinition.getCandidateUserIdExpressions();
        //Set<Expression> candidateGroupIdExpressions = taskDefinition.getCandidateGroupIdExpressions();
        if (assigneeExpression == null && candidateUserIdExpressions.size() == 0) {//不能都没设置
            throw new AIOAException("签收人与候选人必须设置一个,请检查流程图是设计");
        }
        if (assigneeExpression != null && candidateUserIdExpressions.size() > 0) {//不能都设置
            throw new AIOAException("签收人与候选人只能设置一个,请检查流程图是设计");

        }
        if (assigneeExpression != null) vars.put(ElParse.parseNormal(assigneeExpression.getExpressionText()), true);
        if (candidateUserIdExpressions.size() > 0) {
            Iterator<Expression> iterator = candidateUserIdExpressions.iterator();
            Expression next = iterator.next();
            String expressionText = next.getExpressionText();
            vars.put(ElParse.parseNormal(expressionText), false);
        }
    }

    private void recordKeyAndUse(TaskInfoVO taskInfoVO, String taskId, Task task) {
        //获取下n个环节(满足哪个条件：判断传过来的参数是否需携带了变量)
        Map<String, Object> vars = taskInfoVO.getVars();

        String processDefinitionId = task.getProcessDefinitionId();

        List<Activity> nextActs = searchNextActs(taskId, processDefinitionId, null);

        //找出选择的下一个环节
        Activity nextAct = null;
        for (Activity activity : nextActs) {
            String id = activity.getId();
            String taskDefKey = taskInfoVO.getTaskDefKey();
            if (id.equals(taskDefKey)) {
                nextAct = activity;
            }
        }
        if (nextAct == null) return;


        String processInstanceId = task.getProcessInstanceId();
        String userId = taskInfoVO.getUserId();

        String taskDefKeyNext = nextAct.getId();

        OaProcActinst oaProcActinst = taskMapper.isRecordCurrentUser(taskDefKeyNext, processDefinitionId);

        String assignee = nextAct.getAssignee();
        if (assignee == null) return;


        boolean multi = nextAct.isMulti();
        if (multi) return;
        //下一环节是否记录用户
        Boolean recordCurrentuser = oaProcActinst.getRecordCurrentuser();
        if (recordCurrentuser != null && recordCurrentuser) {
            String recordKey = oaProcActinst.getRecordKey();
            if (StringUtils.isNotBlank(recordKey)) {
                String[] keys = recordKey.contains(",") ? recordKey.split(",") : recordKey.split("");
                if (keys.length > 0) {
                    //记录是选择的人
                    Object o = vars.get(assignee);
                    if (o != null) {
                        //查询哪些有只值哪些没值
                        //先删除
                        taskMapper.deleteOld(processInstanceId, Arrays.asList(keys));
                        taskMapper.recordUser(processInstanceId, Arrays.asList(keys), userId);
                    }
                }
            }
        }

        //下一环节是否使用已经记录的用户
        Boolean userRecordVal = oaProcActinst.getUserRecordVal();
        if (userRecordVal != null && userRecordVal) {
            //使用记录的用户
            if (vars.get(assignee) == null) {
                String valByEl = taskMapper.getValByEl(processDefinitionId, taskDefKeyNext + "-" + assignee);
                if (StringUtils.isNotBlank(valByEl)) vars.put(assignee, valByEl);
            }
        }
    }

    /**
     * 人工终止流程
     */
    @Override
    public void endProcess(TaskInfoVO taskInfoVO) {
        Authentication.setAuthenticatedUserId(taskInfoVO.getUserId());
        runtimeService.deleteProcessInstance(taskInfoVO.getProcessId(), "人工终止");
    }

    @Override
    public Result commentQuery(String processInstanceId, Integer pageNo, Integer pageSize) {


        Result<IPage> iPageResult = new Result<>();
        IPage iPage = new Page(pageNo, pageSize);

        List<CommentJsonAble> res = new ArrayList<>();

        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricTaskInstanceEndTime().desc();

        List<HistoricTaskInstance> list = query.listPage(pageNo - 1, pageSize);

        long count = query.count();

        for (HistoricTaskInstance historicTaskInstance : list) {
            //历史任务id
            String id = historicTaskInstance.getId();
            // 3. 获取办理人--办理结果-同意与否--以历史id作为name去流程变量中查询
            List<Comment> taskComments = taskService.getTaskComments(id);
            for (Comment comment : taskComments) {
                CommentJsonAble commentJsonAble = new CommentJsonAble();
                BeanUtils.copyProperties(comment, commentJsonAble);
                res.add(commentJsonAble);
            }
        }

        //分页参数
        iPageResult.setSuccess(true);
        iPage.setRecords(res);
        iPage.setTotal(count);
        iPage.setSize(pageSize);
        iPage.setCurrent(pageNo);
        iPageResult.setResult(iPage);

        return iPageResult;
    }

    @Override
    public boolean exeHaveDone(String processId) {

        Integer count = taskMapper.exeHaveDone(processId);
        if (count == 0) {
            //已经结束
            return true;
        }
        return false;
    }


    @Override
    public Map<String, Object> getActForm(String getProcessDefinitionId, String taskdefid) {
        Map<String, Object> map = new HashMap<>();
        ProcessDefinitionEntity proc = (ProcessDefinitionEntity) repositoryService
                .getProcessDefinition(getProcessDefinitionId);
        TaskDefinition td = proc.getTaskDefinitions().get(taskdefid);

        String assignee = td.getAssigneeExpression().getExpressionText();
        map.put("assignee", assignee);//办理人

        DefaultTaskFormHandler handler = (DefaultTaskFormHandler) td.getTaskFormHandler();

        List<FormPropertyHandler> fphs = handler.getFormPropertyHandlers();


        if (fphs != null && !fphs.isEmpty()) {
            for (FormPropertyHandler hdler : fphs) {
                try {
                    String value = hdler.getDefaultExpression().getExpressionText();
                    String key = hdler.getId();
                    map.put(key, value);
                    //log.info(hdler.getId(),  hdler.getDefaultExpression().getExpressionText());
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("error:id=【" + hdler.getId() + "】,exp=【" + hdler.getDefaultExpression() + "】", e);
                }
            }
        }

        return map;

    }

    /**
     * 节点跳转
     */
    @Override
    public Task jump(JumpMsg jumpMsg, HttpServletRequest request) {

        String sourceTaskId = jumpMsg.getTaskId();


        CommandExecutor commandExecutor = taskServiceImpl.getCommandExecutor();
        ReadOnlyProcessDefinition processDefinition = (ReadOnlyProcessDefinition) repositoryService
                .getProcessDefinition(jumpMsg.getProcessDefinitionId());

        String currentName = null;//回退的环节
        Task task = null;
        List<Task> list1 = taskService.createTaskQuery().taskId(jumpMsg.getTaskId()).list();
        if (list1.size() > 0) {
            task = list1.get(0);
            currentName = list1.get(0).getName();

        } else {
            HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
                    .taskId(jumpMsg.getTaskId()).singleResult();
            if (null == historicTaskInstance) throw new AIOAException("当前页面已过期,请重新进入页面进行操作");
            currentName = historicTaskInstance.getName();
            String processInstanceId = historicTaskInstance.getProcessInstanceId();
            List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            task = list.get(0);
            jumpMsg.setExecutionId(task.getExecutionId());
            jumpMsg.setTaskId(task.getId());
            jumpMsg.setExecutionId(task.getExecutionId());
            jumpMsg.setCurrActDefKey(task.getTaskDefinitionKey());
        }

        String deleteReason = jumpMsg.getDeleteReason();


        if (deleteReason.contains("back")) {
            LoginInfo loginInfo = userService.getLoginInfo(request);
            //记录办理信息
            String name = task.getName();
            if (name.equals(currentName)) {
                task.setAssignee("回退:" + currentName + "->"
                        + jumpMsg.getDestActDefName() + "(" + loginInfo.getUsername() + ")");
            } else {
                task.setAssignee("回退:[从 " + currentName + " 回退]" + name + "->"
                        + jumpMsg.getDestActDefName() + "(" + loginInfo.getUsername() + ")");
            }
            task.setParentTaskId(null);
            taskService.saveTask(task);
        }
        //目标节点
        ActivityImpl dest = (ActivityImpl) processDefinition.findActivity(jumpMsg.getDestActDefKey());
        //当前节点
        ActivityImpl curr = (ActivityImpl) processDefinition.findActivity(jumpMsg.getCurrActDefKey());


        commandExecutor.execute(new JumpTaskCmd(jumpMsg.getTaskId(), jumpMsg.getExecutionId(),
                jumpMsg.getProcessInstanceId(), dest, jumpMsg.getVars(), curr, jumpMsg.getDeleteReason()));

        //查到所有 产生的任务实例
        List<Task> newTasks = taskService.createTaskQuery().processInstanceId(jumpMsg.getProcessInstanceId()).list();
        String type = deleteReason.contains("back") ? "_back" : "_jump";
        if (newTasks.size() > 0) {
            for (Task newTask : newTasks) {
                newTask.setParentTaskId(sourceTaskId + type);
                taskService.saveTask(newTask);
            }
        }


        String table = jumpMsg.getTable() + "_permit";
        Integer functionId = jumpMsg.getFunctionId();
        Integer tableId = jumpMsg.getTableId();
        //4 存储用户信息到 业务数据权限表 - 构造用户信息
        oaBusDataPermitService.save(table, jumpMsg.getAssignee(), functionId, tableId);


        //查看接下来的流程---并将信息记录在部门表里
        Boolean isDept = jumpMsg.getIsDept();
        if (isDept != null && isDept) {
            String processInstanceId = jumpMsg.getProcessInstanceId();
            List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            //存储部门相关信息
            TaskWithDepts taskWithDepts = jumpMsg.getTaskWithDepts();
            //变量与任务的对应关系----如何区分主办/辐办/传阅
            for (Task t : list) {
                String id = t.getId();
                String taskDefinitionKey = t.getTaskDefinitionKey();
                taskWithDepts.setTskId(id);
                taskWithDepts.setTaskDefKey(taskDefinitionKey);
                //存储下个节点的任务id---主办/辐办/传阅等类型都存储起来(会有重复数据)[数量=用户量*种类]
                //请求数据库 2-3 次
                departWithTaskMapper.save(processInstanceId, taskWithDepts);
            }
        }

        String processInstanceId = jumpMsg.getProcessInstanceId();
        taskMapper.deleteHiParent(processInstanceId, sourceTaskId);
        taskMapper.deleteRuParent(processInstanceId, sourceTaskId);
        //将父节点是它的任务的节点置空
        //update   `act_hi_taskinst` set PARENT_TASK_ID_=null   where PARENT_TASK_ID_='10027' PROC_INST_ID_=2560
        //  update  act_ru_task set PARENT_TASK_ID_=null   where PARENT_TASK_ID_='10027' and PROC_INST_ID_=10022
        historyService.deleteHistoricTaskInstance(sourceTaskId);

        return task;

    }


    /**
     * @param proDefKey    流程定义key
     * @param userId       办理人id
     * @param bussinessKey 业务id
     * @return
     */
    @Override
    public String queryTaskDefId(String proDefKey, String userId, String bussinessKey) {

        return taskMapper.queryTaskDefId(proDefKey, userId, bussinessKey);
    }

    @Override
    public void taskShift(String taskId, String sourceUserId, String sourceUserName, String destUserId) {

        //查询被移交的用户名
        String destUserName = userMapper.getUserByUserId(destUserId).getUsername();

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task.getAssignee() == null) {
            //先签收
            taskService.claim(taskId, destUserId);
            //taskService.saveTask(task);//TODO 更新时间
        } else {
            taskService.setAssignee(taskId, destUserId);
        }


        TaskTransfer taskTransfer = taskTransferMapper.selectTaskLog(taskId);
        if (taskTransfer != null) {
            //更新
            String transferLog = taskTransfer.getTransferLog();
            transferLog += "->" + destUserName;
            taskTransfer.setTransferLog(transferLog);
            taskTransferMapper.update(taskTransfer);
        } else {
            //新增---记录最原始的移交人id(如果是部门类型-可用来查部门类型)
            TaskTransfer newT = new TaskTransfer();
            newT.setProcInstId(task.getProcessInstanceId());
            newT.setTaskId(taskId);
            newT.setTransferLog(sourceUserName + "->" + destUserName);
            newT.setSourceUserId(sourceUserId);
            taskTransferMapper.insert(newT);
        }

    }


    /**
     * 直接获取结束点的坐标
     *
     * @return
     */
    @Override
    public List<Map> getEndLocation(String processDefinitionId) {

        ProcessDefinitionEntity proc = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);

        ActivityImpl activityEnd = null;
        //所有节点信息
        List<ActivityImpl> acts = proc.getActivities();
        for (ActivityImpl activity : acts) {
            //最后一个
            if (((String) activity.getProperty("type")).equals("endEvent")) {
                activityEnd = activity;
            }
        }
        List<Map> maps = new ArrayList<>();
        Map xy = ProcssUtil.getXY(activityEnd);
        maps.add(xy);

        return maps;
    }


    /**
     * 获取运行中的节点坐标
     *
     * @return
     */
    @Override
    public List<Map> getLocationRun(TaskInfoVO taskInfoVO) {
        Set<ActivityImpl> activitis = getActiviti(taskInfoVO);

        List<Map> maps = new ArrayList<>();
        activitis.stream().forEach(activity -> {
            Map xy = ProcssUtil.getXY(activity);
            maps.add(xy);
        });

        return maps;
    }


    @Override
    public List<Activity> searchNextActs(String taskId, String processDefinitionId, String proDefKey) {


        HistoricTaskInstance task = null;
        if (taskId == null) {
            ProcessDefinition processDefinition = null;
            if (processDefinitionId == null) {
                processDefinition = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionKey(proDefKey)
                        .latestVersion().singleResult();
            } else {
                processDefinition = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(processDefinitionId)
                        .latestVersion().singleResult();
            }
            if (processDefinition == null) throw new AIOAException("未找到对应的流程信息");

            processDefinitionId = processDefinition.getId();
        } else {
            task = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            processDefinitionId = task.getProcessDefinitionId();
            if (processDefinitionId==null) {
                //可能是追加的 没有在历史表里产生数据
                Task task1 = taskService.createTaskQuery().taskId(taskId).singleResult();
                if (task1==null)throw new AIOAException("追加用户信息不完整");
                processDefinitionId=task1.getProcessDefinitionId();
            }
        }

        ProcessDefinitionEntity proc = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
        //所有节点
        List<ActivityImpl> activities = proc.getActivities();
        List<ActivityImpl> actsAll = new ArrayList<>();
        processManagerService.getAllAct(activities, actsAll);
        proc.getActivities();
        //下一任务节点
        List<Activity> nexts = new ArrayList<Activity>();
        //当前节点
        ActivityImpl currentAct = null;
        String key = proc.getKey();
        ArrayList<String> taskIds = new ArrayList<>();


        if (task == null) {
            currentAct = activities.get(0);
        }
        Integer count = 0;
        for (ActivityImpl activity : actsAll) {
            String id = activity.getId();
            taskIds.add(id);

            //直接获取第二个节点
            if (null != task && task.getTaskDefinitionKey().equals(id)) {
                currentAct = activity;
            }
            if ("endEvent".equalsIgnoreCase(activity.getProperty("type").toString())) continue;//结束
            if ("startevent".equalsIgnoreCase(activity.getProperty("type").toString())) continue;//开始
            if ((activity.getProperty("type").toString()).endsWith("Gateway")) continue;//网关
            count++;
        }

        //查询业务
        Map<String, Map<String, Object>> acts = ioaProcActinstService.queryActs(key, taskIds);
        if (count != acts.size()) return null;//环节配置信息不完善请检查

        //查询下几个节点
        TaskUtil.searchNextActsInfo(nexts, currentAct, acts, -1);

        return nexts;
    }

    @Override
    public void batchEnd(String[] ids, SysUser user) {

        Arrays.stream(ids).forEach(id -> {
            Authentication.setAuthenticatedUserId(user.getId());
            runtimeService.deleteProcessInstance(id, "人工终止(批量)");
        });
    }


    @Override
    public Result batchChuanYue(List<Map<String, Object>> data, SysUser user) {


        Map<String, Object> userMsg = new HashMap<>();
        userMsg.put("s_name", user.getUsername());
        userMsg.put("d_sign", new Date());
        userMsg.put("s_opinion", "已阅");


        for (Map<String, Object> datum : data) {
            datum.putAll(userMsg);
        }

        dynamicTableService.insertDataBatch(data);

        //校验是否可批阅
        //查询当前用户的所有传阅--判断是否是包含关系
        List<String> taskIds = departWithTaskMapper.selectTaskIdsByUserId(user.getId());

        ArrayList<String> ids = new ArrayList<>();
        for (Map<String, Object> datum : data) {
            String id = datum.get("id") + "";
            ids.add(id);
        }

        if (!taskIds.containsAll(ids)) {
            //不可传阅--存在不是传阅类型的数据
            return Result.error("批量批阅失败:存在不是传阅类型的数据");
        }
        for (String id : ids) {
            List<Activity> nextAs = searchNextActs(id, null, null);
            Activity end = null;
            for (Activity activity : nextAs) {
                if ("endevent".equalsIgnoreCase(activity.getType())) {
                    end = activity;
                }
            }
            if (end == null) return Result.error("不存才结束节点，请检查环节配置是否正确");
            Map vars = end.getConditionContext();
            taskService.complete(id, vars);
        }
        return Result.ok("批量批阅成功");
    }


    @Override
    public Result departFinish(String taskId, SysUser user) {

        Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        Map<String, Object> nextActNeed = new HashMap<>();

        //当前节点的下几个节点
        List<Activity> nextActs = searchNextActs(taskId, null, null);
        Activity endAct = null;
        for (Activity activity : nextActs) {
            if ("endevent".equalsIgnoreCase(activity.getType())) {
                endAct = activity;
            }
        }
        if (endAct == null) return Result.error("该部门环节没有结束节点，请检查流程图设计");
        String assignee = getSubNextAct(currentTask);
        if (assignee == null) return Result.error("下一节点没有设置办理人，请检查流程图设计");
        //去查询记录的用户
        String val = taskMapper.getValByEl(currentTask.getProcessInstanceId(), assignee);
        if (val == null) return Result.error("未为下一环节记录好用户，请检查环节配置");
        nextActNeed.put(assignee.split("-")[1], val);


        //读取到完成条件
        Map vars = endAct.getConditionContext();

        //判断是否是主办
        boolean isZhuBan = taskMapper.isZhuBane(taskId, user.getId());

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String executionId = task.getExecutionId();
        List<Task> list = new ArrayList<>();

        if (!isZhuBan) {
            //向上一级结束自己部门的
            list = taskService.createTaskQuery().executionId(executionId).list();
            //将这些都完成
            for (int i = 0; i < list.size(); i++) {
                Task t = list.get(i);

                if (i == list.size() - 1) {
                    //检查该任务是否是该excutionId下的唯一一个任务
                    List<Task> tasks = new ArrayList<>();
                    queryAllTaskInSubAct(executionId, tasks);
                    if (tasks.size() == 1 && tasks.get(0) != null && t.getId().equals(tasks.get(0).getId())) {
                        vars.putAll(nextActNeed);
                    }
                }
                taskService.complete(t.getId(), vars);
            }
        } else {
            queryAllTaskInSubAct(executionId, list);

            for (int i = 0; i < list.size(); i++) {
                Task t = list.get(i);

                if (i == list.size() - 1) {
                    vars.putAll(nextActNeed);
                }
                taskService.complete(t.getId(), vars);
            }

        }

        return Result.ok("部门完成成功");
    }


    //查询所有位于子流程中的任务
    private void queryAllTaskInSubAct(String executionId, List<Task> list) {
        //子流程得向上2级
        //(22511)
        Execution execution = runtimeService.createExecutionQuery().executionId(executionId).singleResult();
        //(22510)
        Execution execution1 = runtimeService.createExecutionQuery().executionId(execution.getParentId()).list().get(0);

        Execution top = runtimeService.createExecutionQuery().executionId(execution1.getParentId()).list().get(0);

        //一级
        List<Execution> executions1 = runtimeService.createExecutionQuery().parentId(top.getId()).list();

        List<String> executionIds = new ArrayList<>();

        for (Execution e : executions1) {
            List<Execution> executions2 = runtimeService.createExecutionQuery().parentId(e.getId()).list();
            executions2.stream().forEach(ee -> {
                executionIds.add(ee.getId());
            });
        }
        for (String eId : executionIds) {

            List<Task> taskList = taskService.createTaskQuery().executionId(eId).list();

            list.addAll(taskList);

        }
    }

    /**
     * 获取子流程的下一环节(规定只能写环节)
     *
     * @param currentTask
     * @return
     */
    private String getSubNextAct(Task currentTask) {
        //父节点的下个节点
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.
                getProcessDefinition(currentTask.getProcessDefinitionId());
        //获取输出
        ActivityImpl activity = processDefinitionEntity.findActivity(currentTask.getTaskDefinitionKey());
        ActivityImpl parent = (ActivityImpl) activity.getParent();
        //规定只能有一条出去的线
        ActivityImpl destination = (ActivityImpl) parent.getOutgoingTransitions().get(0).getDestination();
        UserTaskActivityBehavior activityBehavior = (UserTaskActivityBehavior) destination.getActivityBehavior();
        TaskDefinition taskDefinition = activityBehavior.getTaskDefinition();
        String expressionText = taskDefinition.getAssigneeExpression().getExpressionText();
        if (StringUtils.isBlank(expressionText)) return null;
        return taskDefinition.getKey() + "-" + ElParse.parseNormal(expressionText);
    }


    @Override
    public List<Map<String, String>> queryAllHaveDone(String assignee, String procInstId) {

        return taskMapper.queryAllHaveDone(assignee, procInstId);
    }


    /**
     * 判断是否可撤回
     * 可撤回就撤回
     * 这里的taskId是最新的taskId
     */
    @Override
    public Result reCallAble(String taskId, SysUser currentUser) {


        //判断任务是否已办 - 查
        List<HistoricTaskInstance> list1 = historyService.createHistoricTaskInstanceQuery().taskId(taskId).list();
        if (list1.size() <= 0) return Result.error("无可撤回环节，请检查taskId:" + taskId);
        HistoricTaskInstance historicTaskInstance = list1.get(0);
        String processInstanceId = historicTaskInstance.getProcessInstanceId();


        //父节点的下个节点
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.
                getProcessDefinition(historicTaskInstance.getProcessDefinitionId());
        //获取输出
        ActivityImpl activity = processDefinitionEntity.findActivity(historicTaskInstance.getTaskDefinitionKey());
        if (activity.getParent() instanceof ActivityImpl) {
            ActivityImpl parent = (ActivityImpl) activity.getParent();
            String type = parent.getProperties().get("type").toString();
            if ("subProcess".equalsIgnoreCase(type)) throw new AIOAException("当前环节位于子流程内,不允许撤回,可沟通管理员进行重置");
        }

        ////查询运行中的任务中-该节点的代办(当前环节)
        boolean haveDone = false;//任何一个已办
        //boolean allDone = true;//全部已办
        boolean back = false;//回退的
        boolean jump = false;//跳转的

        Set<TaskInfoVO> taskDefs = new HashSet<>();//去重
        Set<TaskInfoVO> taskDefsBack = new HashSet<>();//回退的数据 去重
        Set<TaskInfoVO> taskDefsJump = new HashSet<>();//回退的数据 去重
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
        for (HistoricTaskInstance taskInstance : list) {
            String parentTaskId = taskInstance.getParentTaskId();
            String deleteReason = taskInstance.getDeleteReason();
            String taskDefinitionKey = taskInstance.getTaskDefinitionKey();
            String name = taskInstance.getName();
            String id = taskInstance.getId();
            TaskInfoVO taskInfoVO = new TaskInfoVO();
            taskInfoVO.setTaskId(id);
            taskInfoVO.setTaskDefKey(taskDefinitionKey);
            taskInfoVO.setTaskName(name);


            if (taskId.equals(parentTaskId) && deleteReason != null) {//表示发出的任务已办理
                if (!haveDone) haveDone = true;
            }
            if (taskId.equals(parentTaskId) && deleteReason == null) {
                //未办理
                taskDefs.add(taskInfoVO);
            }
            if ((taskId + "_back").equals(parentTaskId) && deleteReason == null) {
                //回退的数据
                back = true;
                taskDefsBack.add(taskInfoVO);
            }
            if ((taskId + "_jump").equals(parentTaskId) && deleteReason == null) {
                //回退的数据
                jump = true;
                taskDefsJump.add(taskInfoVO);
            }

        }

        Set<TaskInfoVO> taskInfoVOS = null;
        if (haveDone && !back && !jump) throw new AIOAException("发出的任务已经办理，不可撤回");//没有跳转没有回退 只有已办
        if (!haveDone && !back && !jump) {
            if (taskDefs.size() == 0) throw new AIOAException("没有您发出的任务,不可撤回");
            taskInfoVOS = taskDefs;
            //TODO 将任务不显示
        }
        if (back) {
            if (taskDefsBack.size() == 0) throw new AIOAException("没有您发出的[回退]任务,不可撤回");
            taskInfoVOS = taskDefsBack;
        }
        if (jump) {
            if (taskDefsJump.size() == 0) throw new AIOAException("没有您发出的[重置]任务,不可撤回");
            taskInfoVOS = taskDefsJump;
        }


        try {


            TaskInfoVO next = taskInfoVOS.iterator().next();
            reCallTaskService.backProcess(next.getTaskId(), historicTaskInstance.getTaskDefinitionKey(),
                    null, currentUser, "撤回", historicTaskInstance.getName());

            historyService.deleteHistoricTaskInstance(taskId);


            //删除被撤回用户的权限信息

            //需要返回节点的信息 用来刷新页面
            List<Task> list2 = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            Result<Object> result = Result.ok("撤回成功");
            if (list2.size() > 0) {
                Task task = list2.get(0);
                TaskInfoJsonAble taskInfoJsonAble = new TaskInfoJsonAble();
                taskInfoJsonAble.setId(task.getId());
                taskInfoJsonAble.setProcessInstanceId(task.getProcessInstanceId());
                taskInfoJsonAble.setTaskDefinitionKey(task.getTaskDefinitionKey());
                taskInfoJsonAble.setName(task.getName());
                result.setResult(taskInfoJsonAble);
            }

            return result;
        } catch (Exception e) {
            log.error("撤回步骤出错:" + e.toString());
            return Result.error("撤回步骤出错");
        }

    }

    /**
     * 定位任务中的节点
     *
     * @param
     * @return
     */
    public Set<ActivityImpl> getActiviti(TaskInfoVO taskInfoVO) {

        //获取该实例所有运行中的任务
        List<Task> list = taskService.createTaskQuery().processInstanceId(taskInfoVO.getProcessId()).list();


        //流程实例Id
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(taskInfoVO.getProcessId())
                .singleResult();

        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());
        //    获取输出
        Set<ActivityImpl> activities = new HashSet<>();
        list.stream().forEach(task -> {

            ActivityImpl activity = processDefinitionEntity.findActivity(task.getTaskDefinitionKey());
            activities.add(activity);
        });
        return activities;
    }


}
