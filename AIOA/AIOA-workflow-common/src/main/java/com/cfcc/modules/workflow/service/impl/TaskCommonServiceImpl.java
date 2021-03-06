package com.cfcc.modules.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.constant.workflow.TaskActType;
import com.cfcc.common.exception.AIOAException;
import com.cfcc.common.system.service.CommonDynamicTableService;
import com.cfcc.common.util.FileUtils;
import com.cfcc.common.util.workflow.VarsWithBus;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.entity.SysUserAgent;
import com.cfcc.modules.system.mapper.CommonDynamicTableMapper;
import com.cfcc.modules.system.mapper.SysUserAgentMapper;
import com.cfcc.modules.system.mapper.SysUserMapper;
import com.cfcc.modules.system.service.ISysUserService;
import com.cfcc.modules.utils.*;
import com.cfcc.modules.workflow.mapper.DepartWithTaskMapper;
import com.cfcc.modules.workflow.mapper.OaProcActinstMapper;
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
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
@Transactional
@Slf4j
public class TaskCommonServiceImpl implements TaskCommonService {

    @Autowired
    private OaProcActinstMapper oaProcActinstMapper;

    @Autowired
    private CommonDynamicTableService dynamicTableService;


    @Autowired
    private CommonDynamicTableMapper dynamicTableMapper;


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
    @Autowired
    private ActPicService actPicService;

    @Value("${jeecg.path.upload}")
    private String savePath;


    private static final ThreadLocal<String> haveEndProcess = new ThreadLocal<String>();


    @Override
    public void del(String processInstanceId) {
        //删除表中的数据
//        if (StringUtils.isNotBlank(processInstanceId)) {
//        }
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
        if (list.size() > 0) {
            boolean delFlag = false;
            for (HistoricTaskInstance historicTaskInstance : list) {
                String deleteReason = historicTaskInstance.getDeleteReason();
                if (deleteReason == null) {
                    delFlag = true;
                    break;
                }
            }
            if (delFlag) {
                runtimeService.deleteProcessInstance(processInstanceId, "删除流程实例");
            }
            historyService.deleteHistoricProcessInstance(processInstanceId);
            //删除移交/部门等记录
            taskMapper.deleteByprocessInstanceId(processInstanceId);

        }
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
                        if (StringUtils.isBlank(id)) continue;
                        SysUser sysUser = users.get(id);
                        if (sysUser == null) continue;

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("userName", sysUser.getUsername());
                        String deptName = sysUser.getDeptName();
                        map.put("taskDefName", taskDefName);
                        map.put("endTime", endTime);

                        if (deptMsg != null) {
                            Map<String, Object> dep = deptMsg.get(sysUser.getId());

                            if (isDept && dep != null) {
                                if (StringUtils.isNotBlank((String) deptMsg.get(sysUser.getId()).get("type"))) {

                                    deptName += "【" + deptMsg.get(sysUser.getId()).get("type") + "】";
                                }
                            }
                        }

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
                        String sourceUserId = null;
                        if (taskTransfer != null) {
                            taskTransfer.getSourceUserId();
                        }
                        if (sourceUserId == null) ;
                        else {
                            sysUser.setUsername(taskTransfer.getTransferLog());
                            if (StringUtils.isNotBlank((String) deptMsg.get(sourceUserId).get("type"))) {
                                deptName += "【" + deptMsg.get(sourceUserId).get("type") + "】";
                            }
                        }
                    } else {
                        if (StringUtils.isNotBlank((String) deptMsg.get(sysUser.getId()).get("type"))) {
                            deptName += "【" + deptMsg.get(sysUser.getId()).get("type") + "】";
                        }
                    }
                }
                next.put("userName", userName);
                deptName = deptName == null ? "暂无部门" : deptName;
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
        map.put("status", 1);
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
            if (task == null) throw new AIOAException("未找到您要办理的任务,请刷新所进入的页面重试)");
        }


        taskInfoVOs.get(0).setProcessId(task.getProcessInstanceId());
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

        boolean qinaFa = isQinaFa(task);


        //判断下一环节是否需要记录用户与使用记录的用户
        recordKeyAndUse(taskInfoVO, taskId, task);
        //1 流程已办
        Object justStart = map.get("justStart");
        if (justStart == null) {
            taskService.complete(taskId, taskInfoVO.getVars());
            String processInstanceId = task.getProcessInstanceId();
            taskMapper.setParentId(processInstanceId, taskId);
        }

        //补充任务描述
        addTaskDescript(task.getProcessInstanceId(), busMsg);


        //2 查询下n任务节点信息
        if (qinaFa) {
            return nextActMore(taskInfoVOs, task) + "  ";
        } else {
            return nextActMore(taskInfoVOs, task);
        }
        //2 查询下一任务节点信息
        //return nextActMore(taskInfoVOs, task);
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
            if (processDefinitionId == null) {
                //可能是追加的 没有在历史表里产生数据
                Task task1 = taskService.createTaskQuery().taskId(taskId).singleResult();
                if (task1 == null) throw new AIOAException("追加用户信息不完整");
                processDefinitionId = task1.getProcessDefinitionId();
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
            currentAct = getFirstAct(activities);
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
        if (StringUtils.isBlank(table)) {
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(procInstId).list();
            for (HistoricTaskInstance historicTaskInstance : list) {
                if (StringUtils.isNotBlank(historicTaskInstance.getDescription())) {
                    if (historicTaskInstance.getDescription() != null) {
                        TaskCommon taskCommon = new TaskCommon();
                        taskCommon.setBusMsg(historicTaskInstance.getDescription());
                        table = taskCommon.getTable();
                        if (StringUtils.isBlank(table)) continue;
                        table = table + "_opinion";
                    }

                    break;
                }

            }
        }
        if (StringUtils.isBlank(table)) throw new AIOAException("未找到业务信息");

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
                .processDefinitionId(historicTaskInstance.getProcessDefinitionId())
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
            String deleteReason = task.getDeleteReason();//过滤掉已经办理的任务
            if (taskId.equals(parentTaskId) && deleteReason == null && !taskDefs.contains(taskDefinitionKey)) {
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
    public int haveMainDept(String taskIdRecord, String procInstId) {
        return taskMapper.haveMainDept(taskIdRecord, procInstId);
    }

    @Override
    public List<String> deptUsers(String taskIdRecord, String procInstId) {
        return taskMapper.deptUsers(taskIdRecord, procInstId);
    }

    @Override
    public void updateHisActDept(Task task, String randomParent) {
        taskMapper.updateHisActDept(task, randomParent);
    }

    @Override
    public void updateRuActDept(Task task, String randomParent) {
        taskMapper.updateRuActDept(task, randomParent);
    }

    @Override
    public String taskStatus(String taskid) {
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskid).singleResult();
        if (historicTaskInstance == null) return "del";
        if (StringUtils.isNotBlank(historicTaskInstance.getDeleteReason())) return "done";
        return "todo";
    }

    @Override
    public boolean someActFore(JumpMsg jumpMsg) {
        //TODO 流程中已经走过这个环节 排版
        String juTiAct = jumpMsg.getJuTiAct();//排版
        String destActDefName = jumpMsg.getDestActDefName();
        String processDefinitionId = jumpMsg.getProcessDefinitionId();

        ProcessDefinitionEntity proc = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
        //顺序排列
        List<ActivityImpl> activities = proc.getActivities();
        boolean flag = false;
        List<String> foreTasks = new ArrayList<>();
        //先找到判读那节点 说明目标节点在判断节点之后
        boolean haveJuTi = false;
        for (ActivityImpl activity : activities) {
            String name = (String) activity.getProperty("name");
            if (null!=name&&name.equals(juTiAct)) {
                haveJuTi = true;
                break;
            }
        }
        if (!haveJuTi) return false;
        for (ActivityImpl activity : activities) {
            String name = (String) activity.getProperty("name");
            foreTasks.add(name);
            if (
                    (!foreTasks.contains(juTiAct) && foreTasks.contains(destActDefName)) ||
                            (foreTasks.contains(juTiAct) && foreTasks.contains(destActDefName))
            ) {
                flag = true;
                break;
            }
            if (foreTasks.contains(juTiAct) && !foreTasks.contains(destActDefName)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    @Override
    public void afterDoTask(String table, String id, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> data = dynamicTableMapper.queryBusDataById(table, id);
        if (data == null) return;
        String procInstId = (String) data.get("s_varchar10");
        if (StringUtils.isBlank(procInstId)) return;
        //去代办里查
        long count = taskService.createTaskQuery().processInstanceId(procInstId).count();
        String end = haveEndProcess.get();//当前线程存储是否强制办结标志
        if (count <= 0|| ( "endProcess".equals(end))) {
            Calendar now = Calendar.getInstance();
            int year = now.get(Calendar.YEAR);
            int mounth = now.get(Calendar.MONTH) + 1;
            int day = now.get(Calendar.DAY_OF_MONTH);
            HashMap<String, Object> upData = new HashMap<>();
            upData.put("table", table);
            upData.put("i_id", id);
            //按年月日划分
            upData.put("s_varchar9", year + "-" + mounth + "-" + day);
            upData.put("s_cur_task_name", "end-已结束");
            dynamicTableMapper.updateData(upData);

            String rootPath = savePath + "/activiti/" + year + "/" + mounth + "/" + day + "/";
            String pic = rootPath + procInstId + ".png";
            String workTrace = rootPath + procInstId + "_trace";
            String backData = rootPath + procInstId + "_back";
            try {
                File file = new File(rootPath);
                if (!file.exists()) {
                    file.mkdirs();// 创建文件根目录
                }
                //1 生成图片
                actPicService.savePic(procInstId, response, pic);
                //2 办理信息
                List<Map<String, Object>> all = workTrack(procInstId, true);
                FileUtils.writeJSONOneLine(workTrace, all);
                //3 撤回/退回记录
                List<BackRecord> backRecords = backRecord(procInstId, table + "_opinion");
                FileUtils.writeJSONOneLine(backData, backRecords);
                //4 删除流程相关信息
//                taskMapper.deleteByprocessInstanceId(procInstId);
                del(procInstId);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("-->" + e.getMessage());
            }

        }

    }

    @Override
    public void updateTaskStatus(String taskId, String desc) {
        taskMapper.updateTaskStatus(taskId, desc);
    }


    private String nextActMore(List<TaskInfoVO> taskInfoVOs, Task task) {
        String processInstanceId = task.getProcessInstanceId();
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        StringBuffer nextTaskMsg = new StringBuffer();
        if (list.size() == 0) {
            //任务已经完成
            nextTaskMsg.append("end").append("-'").append("已结束");
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            Map<String, Object> busData = taskInfoVOs.get(0).getBusData();
            String table = busData.get("table") == null ? null : busData.get("table").toString();
            String id = busData.get("i_id") == null ? null : busData.get("i_id").toString();
            if (StringUtils.isNotBlank(table) && StringUtils.isNotBlank(id)) {
                afterDoTask(table, id, request, response);
            }
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
//            addTaskDescript(task.getProcessInstanceId(), busMsg);
        } else {
            task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) throw new AIOAException("未找到您要办理的任务,请刷新所进入的页面重试)");
        }

        //回显流程实例id进业务表
        taskInfoVO.setProcessId(task.getProcessInstanceId());

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
            long l4 = System.currentTimeMillis();
            taskService.complete(taskId, taskInfoVO.getVars());
            //查询待办任务 为 parent 设置id 为上一环节的taskId 用来撤回
            String processInstanceId = task.getProcessInstanceId();
            long l51 = System.currentTimeMillis();

            taskMapper.setParentId(processInstanceId, taskId);

            System.out.println("========================>>complate时间：：:" + (l51 - l4));


        }


        //更新父节点与更新描述可以同时进行


        addTaskDescript(task.getProcessInstanceId(), busMsg);

        //2 查询下一任务节点信息

        if (qinaFa) {
            String nextAct = nextAct(taskInfoVO, task) + "  ";


            return nextAct;
        } else {
            String nextAct = nextAct(taskInfoVO, task);

            return nextAct;
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


        taskMapper.updateTaskDescript(procInstId, busMsg);
//        List<Task> list = taskService.createTaskQuery().processInstanceId(procInstId).list();
//
//        list.stream().forEach(task -> {
//            task.setDescription(busMsg);
//            taskService.saveTask(task);
//        });

    }

    /**
     * 是否是签发人
     *
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
            Boolean signer = list1.get(0).isSigner();
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
            //清空此流程的部门记录信息
//            taskMapper.deleteDeptByProcessInstceId(processInstanceId);
            //办结后写信息
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            Map<String, Object> busData = taskInfoVO.getBusData();
            String table = busData.get("table") == null ? null : busData.get("table").toString();
            String id = busData.get("i_id") == null ? null : busData.get("i_id").toString();
            if (StringUtils.isNotBlank(table) && StringUtils.isNotBlank(id)) {
                afterDoTask(table, id, request, response);
            }

        } else {
            //
            String taskKey = list.get(0).getTaskDefinitionKey();
            String name = list.get(0).getName();
            nextTaskMsg.append(taskKey).append("-'").append(name);

            //部门信息存储
            Boolean isDept = taskInfoVO.getIsDept();
            TaskWithDepts taskWithDepts = taskInfoVO.getTaskWithDepts();
            boolean readDept = true;
            if (taskWithDepts == null) readDept = false;
            if (taskWithDepts != null && taskWithDepts.getDeptMsg() == null) readDept = false;

            if (null != isDept && isDept && readDept) {
                //存储部门相关信息

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
            String[] split = null;
            if (userId.contains(",")) {
                split = userId.split(",");
                userId = userId.substring(0, (userId.length() - 1));
                if (split.length > 1) {
                    qiangQian = true;
                    map.put("s_create_by", "");
                }
                if (split.length == 1) map.put("s_create_by", userId);
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

            //抢签-多用户
            if (qiangQian) {
                vars.put(draft, split);
            } else {//单用户(不区分抢签)
                vars.put(draft, userId);
            }


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
        ActivityImpl activity = getFirstAct(activities);


        ActivityBehavior activityBehavior = activity.getActivityBehavior();
        if (!(activityBehavior instanceof UserTaskActivityBehavior)) return;//提示报错 第一环节只允许普通类型节点
        TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activityBehavior).getTaskDefinition();
        Expression assigneeExpression = taskDefinition.getAssigneeExpression();
        Set<Expression> candidateUserIdExpressions = taskDefinition.getCandidateUserIdExpressions();
        //Set<Expression> candidateGroupIdExpressions = taskDefinition.getCandidateGroupIdExpressions();
        if (assigneeExpression == null && candidateUserIdExpressions.size() == 0) {//不能都没设置
            throw new AIOAException("签收人与候选人必须设置一个,请检查流程图设计");
        }
        if (assigneeExpression != null && candidateUserIdExpressions.size() > 0) {//不能都设置
            throw new AIOAException("签收人与候选人只能设置一个,请检查流程图设计");

        }
        if (assigneeExpression != null) vars.put(ElParse.parseNormal(assigneeExpression.getExpressionText()), true);
        if (candidateUserIdExpressions.size() > 0) {
            Iterator<Expression> iterator = candidateUserIdExpressions.iterator();
            Expression next = iterator.next();
            String expressionText = next.getExpressionText();
            vars.put(ElParse.parseNormal(expressionText), false);
        }
    }

    private ActivityImpl getFirstAct(List<ActivityImpl> activities) {
        ActivityImpl activityFirst = null;
        String taskDefKey = null;
        for (ActivityImpl activity : activities) {
            String type = (String) activity.getProperty("type");
            if ("startevent".equalsIgnoreCase(type)) {
                List<PvmTransition> outgoingTransitions = activity.getOutgoingTransitions();
                if (outgoingTransitions.size() < 0) throw new AIOAException("开始环节后没有连线,请参照手册修改流程图");
                PvmActivity destination = outgoingTransitions.get(0).getDestination();
                if (destination == null) throw new AIOAException("开始环节后没有连接环节,请参照手册修改流程图");
                ActivityBehavior activityBehavior = ((ActivityImpl) destination).getActivityBehavior();
                if (activityBehavior instanceof UserTaskActivityBehavior) {
                    taskDefKey = destination.getId();
                    break;
                } else {
                    throw new AIOAException("第一环节类型有误,请参照手册修改流程图");
                }

            }
        }
        for (ActivityImpl activity : activities) {
            String id = activity.getId();
            if (id.equals(taskDefKey)) {
                activityFirst = activity;
                break;
            }
        }
        return activityFirst;

    }

    private void recordKeyAndUse(TaskInfoVO taskInfoVO, String taskId, Task task) {
        //获取下n个环节(满足哪个条件：判断传过来的参数是否需携带了变量)
        Map<String, Object> vars = taskInfoVO.getVars();

        String processDefinitionId = task.getProcessDefinitionId();
        //下几个环节中找到所选择的下一环节
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
        if (nextAct == null && nextActs.size() == 1) {
            nextAct = nextActs.get(0);
        }


        if (nextAct == null) return;
        //如果下一环节是 结束节点 判断它是不是子流程内的结束节点
        Map<String, Object> subNextAct = new HashMap<>();

        if ("endEvent".equalsIgnoreCase(nextAct.getType())) {

            ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.
                    getProcessDefinition(task.getProcessDefinitionId());
            ActivityImpl activity = processDefinitionEntity.findActivity(nextAct.getId());
            if (activity.getParent() instanceof ActivityImpl) {
                String assignee = getSubNextAct(activity);
                //去查询记录的用户
                String val = taskMapper.getValByEl(task.getProcessInstanceId(), assignee);
                if (val == null && assignee != null) throw new AIOAException("未为下一环节记录好用户，请检查环节配置");
                if (assignee != null) subNextAct.put(assignee.split("-")[1], val);
                if (vars == null) {
                    taskInfoVO.setVars(subNextAct);
                } else {
                    vars.putAll(subNextAct);
                    taskInfoVO.setVars(vars);
                }
            }

        }


        String processInstanceId = task.getProcessInstanceId();
        String userId = taskInfoVO.getUserId();
        String currentTaskKey = task.getTaskDefinitionKey();


        //当前环节是否需要记录用户
        OaProcActinst currentTaskOaProcActinst = taskMapper.isRecordCurrentUser(currentTaskKey, processDefinitionId);
        if (currentTaskOaProcActinst.getRecordCurrentuser()) {
            String recordKey = currentTaskOaProcActinst.getRecordKey();
            if (null != recordKey) {
                List<String> keys = new ArrayList<>();
                if (recordKey.contains(",")) {
                    String[] split = recordKey.split(",");
                    keys = Arrays.asList(split);
                } else {
                    keys.add(recordKey);
                    taskMapper.deleteOld(processInstanceId, keys);
                    taskMapper.recordUser(processInstanceId, keys, userId);

                }
                taskMapper.deleteOld(processInstanceId, keys);
                taskMapper.recordUser(processInstanceId, keys, userId);
            }

        }

        //下一环节
        String taskDefKeyNext = nextAct.getId();

        //获取下一环节的办理人表达式
        String assignee = nextAct.getAssignee();
        if (assignee == null) return;


        boolean multi = nextAct.isMulti();
        if (multi) return;//选择记录用户的节点不允许多实例

        //下一环节是否使用已经记录的用户
        //Boolean userRecordVal = oaProcActinst.getUserRecordVal();
        //if (userRecordVal != null && userRecordVal) {//是否使用记录的用户
        //使用记录的用户(如果本身有用户的话就使用自己选择的用户)
        if (vars != null && vars.get(assignee) == null) {
            String valByEl = taskMapper.getValByEl(processInstanceId, taskDefKeyNext + "-" + assignee);
            if (StringUtils.isNotBlank(valByEl)) vars.put(assignee, valByEl);
        }
        if (vars == null) {
            vars = new HashMap<String, Object>();
            String valByEl = taskMapper.getValByEl(processInstanceId, taskDefKeyNext + "-" + assignee);
            if (StringUtils.isNotBlank(valByEl)) vars.put(assignee, valByEl);
        }
        //}
    }


    /**
     * 人工终止流程
     */
    @Override
    public void endProcess(TaskInfoVO taskInfoVO) {
        //先存储一份到本地
//        Authentication.setAuthenticatedUserId(taskInfoVO.getUserId());
//
//        runtimeService.deleteProcessInstance(taskInfoVO.getProcessId(), "人工终止");
        haveEndProcess.set("endProcess");

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        String table = taskInfoVO.getTable();
        String id = taskInfoVO.getBusDataId() == null ? "" : taskInfoVO.getBusDataId().toString();
        if (StringUtils.isNotBlank(table) && StringUtils.isNotBlank(id)) {
            afterDoTask(table, id, request, response);
        }

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
        String dataTable = jumpMsg.getTable();
        Integer tableId = jumpMsg.getTableId();


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

        TaskWithDepts taskWithDepts = jumpMsg.getTaskWithDepts();

        //主办部门更新
        String description = task.getDescription();
        //部门问题
        description = updateDeptMsg(jumpMsg, dataTable, tableId, task, deleteReason, taskWithDepts, description);


        //执行重置
        commandExecutor.execute(new JumpTaskCmd(jumpMsg.getTaskId(), jumpMsg.getExecutionId(),
                jumpMsg.getProcessInstanceId(), dest, jumpMsg.getVars(), curr, jumpMsg.getDeleteReason()));


        //查到所有 产生的任务实例
        List<Task> newTasks = taskService.createTaskQuery().processInstanceId(jumpMsg.getProcessInstanceId()).list();
        String type = deleteReason.contains("back") ? "_back" : "_jump";
        if (newTasks.size() > 0) {
            //跳转或回退后更新 描述信息 与父节点
            for (Task newTask : newTasks) {

                newTask.setDescription(description);

                newTask.setParentTaskId(sourceTaskId + type);
                taskMapper.updateParentAndDesc(newTask);
                taskService.saveTask(newTask);
            }
        }


        String table = jumpMsg.getTable() + "_permit";
        Integer functionId = jumpMsg.getFunctionId();
        //4 存储用户信息到 业务数据权限表 - 构造用户信息
        oaBusDataPermitService.save(table, jumpMsg.getAssignee(), functionId, tableId);


        //查看接下来的流程---并将信息记录在部门表里
        Boolean isDept = jumpMsg.getIsDept();
        if (isDept != null && isDept) {
            String processInstanceId = jumpMsg.getProcessInstanceId();
            List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            //存储部门相关信息


            //变量与任务的对应关系----如何区分主办/辐办/传阅
            boolean delFlag = true;
            for (Task t : list) {
                String id = t.getId();
                String taskDefinitionKey = t.getTaskDefinitionKey();
                String parentTaskId = t.getParentTaskId();
                if (parentTaskId.equalsIgnoreCase(sourceTaskId + type)) {
                    taskWithDepts.setTskId(id);
                    taskWithDepts.setTaskDefKey(taskDefinitionKey);
//                    if (delFlag) {
//                        //第一次删除以前记录的数据
//                        departWithTaskMapper.deleteSameTask(processInstanceId, taskDefinitionKey);
//                        delFlag = false;
//                    }

                    //存储下个节点的任务id---主办/辐办/传阅等类型都存储起来(会有重复数据)[数量=用户量*种类]
                    //请求数据库 2-3 次
                    departWithTaskMapper.save(processInstanceId, taskWithDepts);
                }

            }


        }


        //将父节点是它的任务的节点置空
        //update   `act_hi_taskinst` set PARENT_TASK_ID_=null   where PARENT_TASK_ID_='10027' PROC_INST_ID_=2560
        //  update  act_ru_task set PARENT_TASK_ID_=null   where PARENT_TASK_ID_='10027' and PROC_INST_ID_=10022
        if (!deleteReason.contains("back")) {
            String processInstanceId = jumpMsg.getProcessInstanceId();
            taskMapper.deleteHiParent(processInstanceId, sourceTaskId);
            taskMapper.deleteRuParent(processInstanceId, sourceTaskId);
            historyService.deleteHistoricTaskInstance(sourceTaskId);
        }


        return task;

    }


    private String updateDeptMsg(JumpMsg jumpMsg, String dataTable, Integer tableId, Task task, String deleteReason, TaskWithDepts taskWithDepts, String description) {
        boolean needUpdateData = false;

        String destActDefKey = jumpMsg.getDestActDefKey();
        String processDefinitionId = jumpMsg.getProcessDefinitionId();


        //跳转或者撤回 到 部门 或者到部门之前都需要 更新部门信息
        if (StringUtils.isNotBlank(description) && jumpMsg.getIsDept()) {//是部门任务
            String[] split = description.split("@mainDept");
            String s = split[0];
            description = s + "@mainDept:" + taskWithDepts.getMainDept() + "@";

            boolean flag = oaProcActinstMapper.taskHaveMain(destActDefKey, processDefinitionId);
            if (flag) needUpdateData = true;
        } else {
            //判断环节是否在子流程之前  之前就更新/之后就不更新
            ProcessDefinitionEntity proc = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(task.getProcessDefinitionId());
            List<ActivityImpl> activities = proc.getActivities();
            //顺序遍历 并记录遍历过的环节 直到遇到子流程并判断目标环节是否在 子流程之前
            List<String> foreTasks = new ArrayList<>();
            for (ActivityImpl activity : activities) {

                String name = (String) activity.getProperty("name");
                String id = activity.getId();
                String type = (String) activity.getProperty("type");
                foreTasks.add(id);
                if (type.equalsIgnoreCase("subProcess") && foreTasks.contains(destActDefKey)) {
                    boolean flag = oaProcActinstMapper.taskHaveMain(destActDefKey, processDefinitionId);
                    if (flag) needUpdateData = true;
                    break;
                }
            }
        }


        if (needUpdateData) {

            String[] split = description.split("@mainDept");
            String s = split[0];
            if (StringUtils.isBlank(taskWithDepts.getMainDept())) {
                description = s + "@mainDept:@";
            } else {
                description = s + "@mainDept:" + taskWithDepts.getMainDept() + "@";
            }

            Map<String, Object> vars = jumpMsg.getVars();
            vars.put("busMsg", description);
            jumpMsg.setVars(vars);
            if (StringUtils.isNotBlank(dataTable)) {
                Map<String, Object> data = new HashMap<>();
                data.put("table", dataTable);
                data.put("i_id", tableId);
                data.put("s_main_unit_names", taskWithDepts.getMainDept());
                data.put("s_cc_unit_names", taskWithDepts.getFuDept());
                data.put("s_inside_deptnames", taskWithDepts.getCyDept());
                dynamicTableMapper.updateData(data);
            }
        }


        //不符合条件不更新数据
        return description;
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
            if (processDefinitionId == null) {
                //可能是追加的 没有在历史表里产生数据
                Task task1 = taskService.createTaskQuery().taskId(taskId).singleResult();
                if (task1 == null) throw new AIOAException("追加用户信息不完整");
                processDefinitionId = task1.getProcessDefinitionId();
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
            currentAct = getFirstAct(activities);
        }
        Integer count = 0;
        for (ActivityImpl activity : actsAll) {
            String id = activity.getId();
            taskIds.add(id);

            //直接获取第二个节点
            if (null != task && task.getTaskDefinitionKey().equals(id)) {
                currentAct = activity;
            }
            if ((activity.getProperty("type").toString()).endsWith("Gateway")) continue;//网关
            if ((activity.getProperty("type").toString()).endsWith("Event")) continue;//事件排除
            count++;
        }

        //查询业务
        Map<String, Map<String, Object>> acts = ioaProcActinstService.queryActs(key, taskIds);
        if (count != acts.size()) return null;//环节配置信息不完善请检查

        //查询下几个节点
        TaskUtil.searchNextActsInfo(nexts, currentAct, acts, -1, null);

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
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Result departFinish(String taskId, String processInstanceId, SysUser user) {

        HistoricTaskInstance currentTask = null;
        String description = null;
        String procInstId = null;
        List<HistoricTaskInstance> allTAsk = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
        for (HistoricTaskInstance historicTaskInstance : allTAsk) {
            String description1 = historicTaskInstance.getDescription();
            if (StringUtils.isNotBlank(description1)) description = description1;
            procInstId = historicTaskInstance.getProcessInstanceId();

            if (historicTaskInstance.getId().equals(taskId)) {
                currentTask = historicTaskInstance;
//                break;
            }
        }
        if (currentTask == null) throw new AIOAException("未找到要完成的任务");
        //判断是否是主办
        boolean isZhuBan = false;
        OaTaskDept zhuBane = taskMapper.isZhuBane(taskId, user.getId());
        if (zhuBane == null) throw new AIOAException("您不是部门负责人,不可部门完成");
        if (zhuBane.getType() != null && zhuBane.getType().contains("主办")) isZhuBan = true;

        //获取到当前节点
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.
                getProcessDefinition(currentTask.getProcessDefinitionId());
        ActivityImpl activity = processDefinitionEntity.findActivity(currentTask.getTaskDefinitionKey());

        //获取结束节点
        Activity endAct = getEndAct(activity);

        if (endAct == null) return Result.error("该部门环节没有结束节点，请检查流程图设计");
        String assignee = getSubNextAct(activity);
        //去查询记录的用户
        String val = taskMapper.getValByEl(currentTask.getProcessInstanceId(), assignee);
        if (val == null && assignee != null) return Result.error("未为下一环节记录好用户，请检查环节配置");
        Map<String, Object> nextActNeed = new HashMap<>();
        if (assignee != null) nextActNeed.put(assignee.split("-")[1], val);


        //读取到完成条件
        Map vars = endAct.getConditionContext();
        vars.putAll(nextActNeed);//子流程结束时下一环节需要的变量


        //如果是主办 就完成所有的任务 如果不是主办就只完成taskId是自己的任务
//        ArrayList<Future<String>> futures = new ArrayList<>();
//        ExecutorService executorService = Executors.newCachedThreadPool();

        if (isZhuBan) {
            String taskDefinitionKey = currentTask.getTaskDefinitionKey();
            //所有同级任务+所发出任务(未结束)
            List<String> parentTask = new ArrayList<>();
            //先完成第一环节
            for (HistoricTaskInstance historicTaskInstance : allTAsk) {
                String id = historicTaskInstance.getId();
                String deleteReason = historicTaskInstance.getDeleteReason();
                String taskDefinitionKey1 = historicTaskInstance.getTaskDefinitionKey();
                String assignee1 = historicTaskInstance.getAssignee();
                if (taskDefinitionKey.equalsIgnoreCase(taskDefinitionKey1)) {
                    parentTask.add(id);
                    if (StringUtils.isBlank(deleteReason)) {
                        if (StringUtils.isBlank(assignee1)) {
                            taskService.claim(id, user.getId());
                        }
                        taskService.complete(id, vars);

                    }
                }
            }
            //完成第二环节
            for (HistoricTaskInstance historicTaskInstance : allTAsk) {
                String id = historicTaskInstance.getId();
                String parentTaskId = historicTaskInstance.getParentTaskId();
                String deleteReason = historicTaskInstance.getDeleteReason();
                String assignee1 = historicTaskInstance.getAssignee();

                if (StringUtils.isBlank(deleteReason) && parentTask.contains(parentTaskId)) {
                    if (StringUtils.isBlank(assignee1)) {
                        taskService.claim(id, user.getId());
                    }
                    taskService.complete(id, vars);
                }
            }


        } else {
            //只结束自己或者自己发出的 且未结束的
            for (HistoricTaskInstance historicTaskInstance : allTAsk) {
                String id = historicTaskInstance.getId();
                String parentTaskId = historicTaskInstance.getParentTaskId();
                String deleteReason = historicTaskInstance.getDeleteReason();
                String assignee1 = historicTaskInstance.getAssignee();

                if ((id.equals(taskId) || taskId.equalsIgnoreCase(parentTaskId))
                        && StringUtils.isBlank(deleteReason)) {

                    if (StringUtils.isBlank(assignee1)) {
                        taskService.claim(id, user.getId());
                    }
                    taskService.complete(id, vars);
                }
            }
        }


//        //阻塞获取结果  出现activiti的并发错误 一个任务办理过程中,其他任务处于挂起状态
//        try {
//            for (Future<String> future : futures) {
//                String id = future.get();
//                log.info("-------------->>>>>>阻塞获取:完成的任务id:" + (id));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Result.error("部门完成失败");
//        }


//        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
//        String executionId = task.getExecutionId();
//        List<Task> list = new ArrayList<>();
//
//        if (!isZhuBan) {
//            //向上一级结束自己部门的
//            list = taskService.createTaskQuery().executionId(executionId).list();
//            //将这些都完成
//            for (int i = 0; i < list.size(); i++) {
//                Task t = list.get(i);
//
//                if (i == list.size() - 1) {
//                    //检查该任务是否是该excutionId下的唯一一个任务
//                    List<Task> tasks = new ArrayList<>();
//                    queryAllTaskInSubAct(executionId, tasks);
//                    if (tasks.size() == 1 && tasks.get(0) != null && t.getId().equals(tasks.get(0).getId())) {
//                        vars.putAll(nextActNeed);
//                    }
//                }
//                taskService.complete(t.getId(), vars);
//            }
//        }
//        else {
//            queryAllTaskInSubAct(executionId, list);
//
//            for (int i = 0; i < list.size(); i++) {
//                Task t = list.get(i);
//
//                if (i == list.size() - 1) {
//                    vars.putAll(nextActNeed);
//                }
//                taskService.complete(t.getId(), vars);
//            }
//
//        }
        //部门办结后追加描述
        if (StringUtils.isNotBlank(processInstanceId) && StringUtils.isNotBlank(description)) {
            taskMapper.updateTaskDescript(procInstId, description);
        }


        return Result.ok("部门完成成功");
    }

    private Activity getEndAct(PvmActivity activity) {


        List<PvmTransition> outgoingTransitions = activity.getOutgoingTransitions();
        for (PvmTransition outgoingTransition : outgoingTransitions) {
            PvmActivity destination = outgoingTransition.getDestination();
            String type = (String) destination.getProperty("type");
            if ("endEvent".equalsIgnoreCase(type)) {
                Activity activity1 = new Activity();
                Object conditionText = outgoingTransition.getProperty("conditionText");
                if (conditionText != null) {
                    Map<String, String> parse = ElParse.parseCondition((String) conditionText);
                    activity1.getConditionContext().putAll(parse);
                }

                return activity1;
            }
            if (type != null && type.endsWith("Gateway")) {
                return getEndAct(destination);
            }
        }

        return null;
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
     * @return
     */
    private String getSubNextAct(ActivityImpl activity) {
        //父节点的下个节点
        //获取输出
        ActivityImpl parent = (ActivityImpl) activity.getParent();
        //规定只能有一条出去的线
        ActivityImpl destination = (ActivityImpl) parent.getOutgoingTransitions().get(0).getDestination();
        ActivityBehavior activityBehavior1 = destination.getActivityBehavior();
        if (activityBehavior1 instanceof UserTaskActivityBehavior) {
            UserTaskActivityBehavior activityBehavior = (UserTaskActivityBehavior) activityBehavior1;
            TaskDefinition taskDefinition = activityBehavior.getTaskDefinition();
            String expressionText = taskDefinition.getAssigneeExpression().getExpressionText();
            if (StringUtils.isBlank(expressionText)) throw new AIOAException("子流程下以环节未设置办理人,请检查流程设计");
            return taskDefinition.getKey() + "-" + ElParse.parseNormal(expressionText);
        } else {
            return null;
        }

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


            //TODO 部门任务 更新
            TaskInfoVO next = taskInfoVOS.iterator().next();
            String processDefinitionId = next.getProcessDefinitionId();
            String isDeptId = next.getTaskId();
            String taskDefKey = next.getTaskDefKey();
            boolean isDept = taskMapper.isDeptTask(isDeptId, taskDefKey);
            String description = historicTaskInstance.getDescription();
            if (StringUtils.isNotBlank(description) && isDept) {
                TaskCommon taskCommon = new TaskCommon();
                taskCommon.setBusMsg(description);
                String table = taskCommon.getTable();
                String tableId = taskCommon.getTableId();
//                oaProcActinstMapper
                //更新业务数据
                if (StringUtils.isNotBlank(table) && StringUtils.isNotBlank(tableId)) {
                    boolean b = oaProcActinstMapper.taskHaveMain(taskDefKey, processDefinitionId);
                    if (b) {
                        Map<String, Object> data = new HashMap<String, Object>();
                        data.put("table", table);
                        data.put("i_id", tableId);
                        data.put("s_main_unit_names", "");
                        data.put("s_cc_unit_names", "");
                        data.put("s_inside_deptnames", "");
                        dynamicTableMapper.updateData(data);
                    }
                }

                String[] split = description.split("@mainDept");
                String s = split[0];
                description = s + "@mainDept:@";
            }
            Map<String, Object> vars = new HashMap<>();
            if (StringUtils.isNotBlank(description)) {
                vars.put("busMsg", description);
            }


            reCallTaskService.backProcess(next.getTaskId(), historicTaskInstance.getTaskDefinitionKey(),
                    vars, currentUser, "撤回", historicTaskInstance.getName());

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
                taskInfoJsonAble.setProcessDefinitionId(task.getProcessDefinitionId());
                result.setResult(taskInfoJsonAble);
            }

            //更新流程描述
            taskMapper.updateTaskDescript(processInstanceId, description);

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
