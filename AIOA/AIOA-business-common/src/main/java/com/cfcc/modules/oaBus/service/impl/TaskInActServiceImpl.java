package com.cfcc.modules.oaBus.service.impl;

import com.cfcc.common.constant.workflow.TaskConstant;
import com.cfcc.common.util.workflow.VarsWithBus;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.mapper.OaBusDynamicTableMapper;
import com.cfcc.modules.oaBus.service.IBusFunctionService;
import com.cfcc.modules.oaBus.service.TaskInActService;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.service.ISysUserService;
import com.cfcc.modules.utils.AddUserCmd;
import com.cfcc.modules.utils.JumpTaskCmd;
import com.cfcc.modules.workflow.mapper.DepartWithTaskMapper;
import com.cfcc.modules.workflow.pojo.TaskWithDepts;
import com.cfcc.modules.workflow.service.OaBusDataPermitService;
import com.cfcc.modules.workflow.service.TaskCommonService;
import com.cfcc.modules.workflow.vo.TaskInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.TaskServiceImpl;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
@Slf4j
public class TaskInActServiceImpl implements TaskInActService {

    @Autowired
    private TaskCommonService taskCommonService;
    @Resource
    private OaBusDynamicTableMapper oaBusDynamicTableMapper;
    @Autowired
    private OaBusDataPermitService oaBusDataPermitService;

    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private TaskServiceImpl taskServiceImpl;
    @Autowired
    private DepartWithTaskMapper departWithTaskMapper;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;


    @Override
    public void doTask(TaskInfoVO taskInfoVO, HttpServletRequest request) {

        //1 流程办理
        String nextTaskMsg = taskCommonService.doTask(taskInfoVO);
        //2 业务相关
        if (nextTaskMsg.endsWith("  ")) {
            Map<String, Object> busData = taskInfoVO.getBusData();
            LoginInfo loginInfo = sysUserService.getLoginInfo(request);
            busData.put("s_signer", loginInfo.getUsername());

            busData.put("d_date1", new Date());//new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//
        }
        busAbout(taskInfoVO, nextTaskMsg);
    }

    private void busAbout(TaskInfoVO taskInfoVO, String nextTaskMsg) {
        //2 更新流程对应的业务数据......
        //排除掉多余的字段
        //移除多余字段
        Map<String, Object> busData = taskInfoVO.getBusData();
        //更新当前节点信息
        busData.put("s_cur_task_name", nextTaskMsg);
        if ("end-已结束".equals(nextTaskMsg)) {
            busData.put("i_is_state", 1);
        }
        for (String remove : TaskConstant.REMOVEFILEDS) {
            busData.remove(remove);
        }
        busData.put("i_is_display", '0');
        //********************* 写入参与人 *********************
        String table = busData.get("table") + "_permit";
        oaBusDynamicTableMapper.updateData(busData);

        //4 存储用户信息到 业务数据权限表 - 构造用户信息
        saveDataPermit(taskInfoVO, table);
    }


    @Override
    public void doTaskMore(List<TaskInfoVO> taskInfoVOs) {


        String nextTaskMsg = taskCommonService.doTasksMore(taskInfoVOs);
        //TODO 业务信息
        //if (nextTaskMsg.endsWith("  ")) {
        //    Map<String, Object> busData = taskInfoVO.getBusData();
        //    LoginInfo loginInfo = sysUserService.getLoginInfo(request);
        //    busData.put("s_signer", loginInfo.getUsername());
        //
        //    busData.put("d_date1", new Date());//new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//
        //}
        //busAbout(taskInfoVO, nextTaskMsg);

    }



    /**
     * 并行/包容 追加用户
     *
     * @param taskInfoVOS
     */
    @Override
    public void doAddUsers(ArrayList<TaskInfoVO> taskInfoVOS) {
        CommandExecutor commandExecutor = taskServiceImpl.getCommandExecutor();


        String descript = null;
        for (TaskInfoVO taskInfoVO : taskInfoVOS) {

            String taskId = taskInfoVO.getTaskId();

            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            String processInstanceId = task.getProcessInstanceId();
            String parentTaskId = task.getParentTaskId();
            if (descript == null) {
                descript = task.getDescription();
            }

            String executionId = taskInfoVO.getExecutionId();
            if (taskInfoVO.getIsDept() != null && !taskInfoVO.getIsDept()) {
                List<String> assignee = (List<String>) taskInfoVO.getAssignee();

                for (String userId : assignee) {
                    commandExecutor.execute(new AddUserCmd(executionId, userId, descript, parentTaskId
                            , runtimeService, taskService, false));
                }
                taskCommonService.updateHisAct(task);

            } else {
                List<List<String>> assignee = (List<List<String>>) taskInfoVO.getAssignee();
                String randomParent = UUID.randomUUID().toString().replaceAll("-", "");
                commandExecutor.execute(new AddUserCmd(executionId, null, descript, randomParent
                        , runtimeService, taskService, true));


                List<Task> addDeptTask = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
                TaskWithDepts taskWithDepts = taskInfoVO.getTaskWithDepts();

                if (addDeptTask.size() > 0) {
                    for (Task taskDept : addDeptTask) {
                        //部门用户 抢签
                        taskId = taskDept.getId();
                        if (randomParent.equalsIgnoreCase(taskDept.getParentTaskId())){
                            //TODO 同一类型 多个部门如何区分
                            for (List<String> list : assignee) {
                                for (String uid : list) {
                                    taskService.addCandidateUser(taskId, uid);
                                }
                                String taskDefinitionKey = taskDept.getTaskDefinitionKey();
                                taskWithDepts.setTaskDefKey(taskDefinitionKey);
                                //存储下个节点的任务id---主办/辐办/传阅等类型都存储起来(会有重复数据)[数量=用户量*种类]
                                taskWithDepts.setTskId(taskId);
                                //请求数据库 2-3 次
                                departWithTaskMapper.save(processInstanceId, taskWithDepts);
                            }
                        }
                    }


                }
                taskCommonService.updateHisActDept(task, randomParent);
                taskCommonService.updateRuActDept(task, randomParent);


            }
            //********************* 写入参与人 *********************
            Map<String, Object> busData = taskInfoVO.getBusData();
            String table = busData.get("table") + "_permit";

            //4 存储用户信息到 业务数据权限表 - 构造用户信息
            saveDataPermit(taskInfoVO, table);
        }


    }

    private void saveDataPermit(TaskInfoVO taskInfoVO, String table) {
        ArrayList<String> uids = new ArrayList<>();
        Boolean isDept = taskInfoVO.getIsDept();
        if (null != isDept && isDept) {
            Map<String, List<String>> deptMsg = taskInfoVO.getTaskWithDepts().getDeptMsg();

            for (Map.Entry<String, List<String>> entry : deptMsg.entrySet()) {
                List<String> ids = entry.getValue();
                ids.stream().forEach(id -> {
                    uids.add(id);
                });
            }

        } else {
            List<String> assignee = (List<String>) taskInfoVO.getAssignee();
            assignee.stream().forEach(id -> {
                uids.add(id);
            });
        }

        //5 构造业务数据权限表数据
        Integer functionId = taskInfoVO.getFunctionId();
        Integer busDataId = taskInfoVO.getBusDataId();
        oaBusDataPermitService.save(table, uids, functionId, busDataId);
    }

}
