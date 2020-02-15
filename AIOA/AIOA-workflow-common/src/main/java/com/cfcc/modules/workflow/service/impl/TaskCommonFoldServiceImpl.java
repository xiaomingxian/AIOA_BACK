package com.cfcc.modules.workflow.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.constant.workflow.TaskActType;
import com.cfcc.common.system.service.CommonDynamicTableService;
import com.cfcc.common.util.workflow.VarsWithBus;
import com.cfcc.modules.system.entity.SysDictItem;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.entity.SysUserAgent;
import com.cfcc.modules.system.mapper.SysUserAgentMapper;
import com.cfcc.modules.system.mapper.SysUserMapper;
import com.cfcc.modules.utils.*;
import com.cfcc.modules.workflow.mapper.DepartWithTaskMapper;
import com.cfcc.modules.workflow.mapper.TaskFoldMapper;
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

import java.util.*;

@Service
@Transactional
@Slf4j
public class TaskCommonFoldServiceImpl implements TaskCommonFoldService {


    @Autowired
    private SysUserAgentMapper userAgentMapper;

    @Autowired
    private TaskFoldMapper taskFoldMapper;

    @Autowired
    private SysUserMapper userMapper;

//流程监控一级折叠
    @Override
    public Result monitorFoldUrgency(String urgencyDegree, TaskInfoVO taskInfoVO,boolean isAdmin) {
        Result<List<SysDictItem>> SysDictResult = new Result<>();
        List<SysDictItem> sysDictItems = taskFoldMapper.monitorFoldUrgency(urgencyDegree, taskInfoVO,isAdmin);
        SysDictResult.setResult(sysDictItems);
        return SysDictResult;
    }

    /**
     * 根据用户id 查询自己的待办+委托给我的
     */
    @Override
    public Result queryTaskToDo(String urgencyDegree,TaskInfoVO taskInfoVO, Integer pageNo, Integer pageSize,Integer jY) {

        /**
         * 判断是否是查部门的
         */
        if (taskInfoVO.getIsDept() != null && taskInfoVO.getIsDept()) {
            return deptTask(urgencyDegree,taskInfoVO, taskInfoVO.getDeptType(), pageNo, pageSize, jY);
        }


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


        if (jY>0){
            long count = taskFoldMapper.queryTaskToDoCountFold(urgencyDegree,taskInfoVO, ids);
            iPage.setTotal(count);
            iPageResult.setResult(iPage);
            return iPageResult;
        }
        //1 作为assignee与候选人
        List<TaskInfoJsonAble> taskInfoJsonAbles = taskFoldMapper.queryTaskToDoFold(urgencyDegree,taskInfoVO, ids, (pageNo - 1) * pageSize, pageSize);
        String agentUserName = taskInfoVO.getAgentUserName();
        if (StringUtils.isNotBlank(agentUserName)) {
            taskInfoJsonAbles.stream().forEach(i -> {
                i.setAgentUserName(agentUserName);
            });
        }
        //分页参数
        iPageResult.setSuccess(true);
//        iPage.setTotal(count);
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
    public Result queryTaskMonitor(String urgencyDegree,TaskInfoVO taskInfoVO, Integer pageNo, Integer pageSize,Integer jY,boolean isAdmin) {


        //判断是否是部门类型
        Boolean isDept = taskInfoVO.getIsDept();
        if (isDept != null && isDept) {
            //查询部门类型
            return deptTaskMonitor(urgencyDegree,taskInfoVO, taskInfoVO.getDeptType(), pageNo, pageSize,isAdmin);
        }


        Result<IPage> iPageResult = new Result<>();
        IPage iPage = new Page(pageNo, pageSize);

        int count = taskFoldMapper.monitorCountFold(urgencyDegree,taskInfoVO,isAdmin);

        List<TaskInfoJsonAble> list = taskFoldMapper.monitorDataFold(urgencyDegree,taskInfoVO, (pageNo - 1) * pageSize, pageSize,isAdmin);

        //分页参数
        iPage.setTotal(count);
        iPageResult.setSuccess(true);
        iPage.setRecords(list);
        iPage.setCurrent(pageNo);
        iPage.setSize(pageSize);
        iPageResult.setResult(iPage);
        return iPageResult;
    }

    private Result deptTaskMonitor(String urgencyDegree,TaskInfoVO taskInfoVO, String type, Integer pageNo, Integer pageSize,boolean isAdmin) {

        Result<IPage> iPageResult = new Result<>();
        IPage iPage = new Page(pageNo, pageSize);

        Long count = taskFoldMapper.deptTaskMonitorCountFold(urgencyDegree,taskInfoVO, type,isAdmin);
        count = count == null ? 0 : count;

        List<TaskInfoJsonAble> list = taskFoldMapper.deptTaskMonitorQueryFold(urgencyDegree,taskInfoVO, type, (pageNo - 1) * pageSize, pageSize,isAdmin);

        //分页参数
        iPage.setTotal(count);
        iPageResult.setSuccess(true);
        iPage.setRecords(list);
        iPage.setCurrent(pageNo);
        iPageResult.setResult(iPage);
        iPage.setSize(pageSize);
        return iPageResult;
    }





    @Override
    public Result queryTaskMyAgent(String urgencyDegree,TaskInfoVO taskInfoVO, Integer pageNo, Integer pageSize ,Integer jY) {
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
        Result<IPage> result = queryTaskToDo(urgencyDegree,taskInfoVO, pageNo, pageSize,jY);

        return result;
    }


    /**
     * 待办部门类型
     */
    @Override
    public Result deptTask(String urgencyDegree,TaskInfoVO taskInfoVO, String type, Integer pageNo, Integer pageSize,Integer jY) {
        Result<IPage> iPageResult = new Result<>();
        IPage iPage = new Page(pageNo, pageSize);

        Long count = taskFoldMapper.deptTaskCountFold(urgencyDegree,taskInfoVO, type);
        count = count == null ? 0 : count;

        List<TaskInfoJsonAble> list = taskFoldMapper.deptTaskQueryFold(urgencyDegree,taskInfoVO, type, (pageNo - 1) * pageSize, pageSize);

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
    public Result deptTaskHaveDone(String urgencyDegree,TaskInfoVO taskInfoVO, String type, Integer pageNo, Integer pageSize,Integer jY) {


        Result<IPage> iPageResult = new Result<>();
        IPage iPage = new Page(pageNo, pageSize);

        Long count = taskFoldMapper.deptTaskHaveDoneCountFold(urgencyDegree,taskInfoVO, type);
        count = count == null ? 0 : count;


        List<TaskInfoJsonAble> list = taskFoldMapper.deptTaskHaveDoneFold(urgencyDegree,taskInfoVO, type, (pageNo - 1) * pageSize, pageSize);

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
    public Result queryTaskDone(String urgencyDegree,TaskInfoVO taskInfoVO, Integer pageNo, Integer pageSize ,Integer jY) {

        //如果是部门任务
        if (taskInfoVO.getIsDept() != null && taskInfoVO.getIsDept()) {
            return deptTaskHaveDone(urgencyDegree,taskInfoVO, taskInfoVO.getDeptType(), pageNo, pageSize, jY);
        }

        IPage iPage = new Page(pageNo, pageSize);
        Result<IPage> iPageResult = new Result<>();
        if (jY>0){
            Long count = taskFoldMapper.queryTaskDoneCountFold(urgencyDegree,taskInfoVO);
            iPage.setTotal(count);
            iPageResult.setResult(iPage);
            return iPageResult;
        }

        List<HisTaskJsonAble> list = taskFoldMapper.queryTaskDoneFold(urgencyDegree,taskInfoVO, (pageNo - 1) * pageSize, pageSize);
        //数据后续组装*************
        ArrayList<String> ids = new ArrayList<>();
        for (HisTaskJsonAble hisTaskJsonAble : list) {
            if (hisTaskJsonAble.getIsDone() == null) {
                hisTaskJsonAble.setTaskDefinitionKey("end");
                hisTaskJsonAble.setTaskDefinitionKeyName("已结束");
            }
            ids.add(hisTaskJsonAble.getProcessInstanceId());
        }
        //分页


        //分页参数
        iPageResult.setSuccess(true);
//        iPage.setTotal(count);
        iPage.setRecords(list);
        iPage.setSize(pageSize);
        iPage.setCurrent(pageNo);
        iPageResult.setResult(iPage);
        return iPageResult;
    }


}
