package com.cfcc.modules.oaBus.service.impl;

import com.cfcc.common.constant.workflow.TaskConstant;
import com.cfcc.modules.oaBus.mapper.OaBusDynamicTableMapper;
import com.cfcc.modules.oaBus.service.TaskInActService;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.service.ISysUserService;
import com.cfcc.modules.utils.AddUserCmd;
import com.cfcc.modules.workflow.mapper.DepartWithTaskMapper;
import com.cfcc.modules.workflow.pojo.TaskWithDepts;
import com.cfcc.modules.workflow.service.OaBusDataPermitService;
import com.cfcc.modules.workflow.service.TaskCommonService;
import com.cfcc.modules.workflow.vo.TaskInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.TaskServiceImpl;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Transactional(readOnly = false)
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
    private TaskService taskService;

    @Autowired
    private com.cfcc.modules.oaBus.mapper.oaCalendarMapper oaCalendarMapper;


    @Override
    public void doTask(TaskInfoVO taskInfoVO, HttpServletRequest request) {
        LoginInfo loginInfo = sysUserService.getLoginInfo(request);
        //2 业务相关
        Map<String, Object> busData = taskInfoVO.getBusData();
        //3 如果是抢签(可能会没有创建者-需要把抢到签的用户作为创建者)
        String userId = busData.get("s_create_by") == null ? null : busData.get("s_create_by").toString();
        if (StringUtils.isBlank(userId)) {
            busData.put("s_create_by", loginInfo.getId());
        }
        //1 流程办理
        String nextTaskMsg = taskCommonService.doTask(taskInfoVO);
        //2 更新日程信息
        oaCalendarMapper.updateByTaskUserId(taskInfoVO.getTaskId()+userId);

        if (nextTaskMsg.endsWith("  ")) {
            busData.put("s_signer", loginInfo.getUsername());

            busData.put("d_date1", new Date());//new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//
        }
        busData.put("s_varchar10", taskInfoVO.getProcessId());

        busAbout(taskInfoVO, nextTaskMsg);

    }


    private void busAbout(TaskInfoVO taskInfoVO, String nextTaskMsg) {
        //2 更新流程对应的业务数据......
        //排除掉多余的字段
        //移除多余字段
        Map<String, Object> busData = taskInfoVO.getBusData();
        //更新当前节点信息
        busData.put("s_cur_task_name", nextTaskMsg);
        if (nextTaskMsg != null && nextTaskMsg.contains("已结束")) {
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
    public void doTaskMore(List<TaskInfoVO> taskInfoVOs, HttpServletRequest request) {

        Map<String, Object> busData = taskInfoVOs.get(0).getBusData();

        String nextTaskMsg = taskCommonService.doTasksMore(taskInfoVOs);
        if (nextTaskMsg.endsWith("  ")) {
            LoginInfo loginInfo = sysUserService.getLoginInfo(request);
            busData.put("s_signer", loginInfo.getUsername());

            busData.put("d_date1", new Date());//new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//
        }
        busData.put("s_varchar10", taskInfoVOs.get(0).getProcessId());
        busAboutMore(taskInfoVOs, nextTaskMsg);

    }

    private void busAboutMore(List<TaskInfoVO> taskInfoVOs, String nextTaskMsg) {
        //2 更新流程对应的业务数据......
        //处理部门记录问题
        Map<String, Object> busData = taskInfoVOs.get(0).getBusData();


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
        for (TaskInfoVO taskInfoVO : taskInfoVOs) {
            saveDataPermit(taskInfoVO, table);
        }
    }


    /**
     * 并行/包容 追加用户
     *
     * @param taskInfoVOS
     */
    @Override
    public void doAddUsers(ArrayList<TaskInfoVO> taskInfoVOS) {
        CommandExecutor commandExecutor = taskServiceImpl.getCommandExecutor();
        Map<String, Object> busData = taskInfoVOS.get(0).getBusData();
        String descript = null;
        if (taskInfoVOS.get(0).getVars() != null) {
            descript = taskInfoVOS.get(0).getVars().get("busMsg") == null ? null : taskInfoVOS.get(0).getVars().get("busMsg").toString();
        }


        String randomParent = UUID.randomUUID().toString().replaceAll("-", "");
        List<Task> tasks = new ArrayList<>();


        for (TaskInfoVO taskInfoVO : taskInfoVOS) {

            String taskId = taskInfoVO.getTaskId();

            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            //用来记录产生的属性
            TaskEntity taskAfter = new TaskEntity();
            taskAfter.setExecutionId(task.getExecutionId());
            taskAfter.setProcessInstanceId(task.getProcessInstanceId());
            taskAfter.setProcessDefinitionId(task.getProcessDefinitionId());
            taskAfter.setParentTaskId(task.getParentTaskId());

            String processInstanceId = task.getProcessInstanceId();
            if (descript == null) {
                descript = task.getDescription();
            }

            String executionId = taskInfoVO.getExecutionId();
            if (taskInfoVO.getIsDept() != null && !taskInfoVO.getIsDept()) {
                List<String> assignee = (List<String>) taskInfoVO.getAssignee();

                commandExecutor.execute(new AddUserCmd(executionId, assignee, null, descript, randomParent
                        , false, taskAfter, tasks));
//                taskCommonService.updateHisAct(task);

            } else {
                //部门任务
                List<List<String>> assignee = (List<List<String>>) taskInfoVO.getAssignee();

                commandExecutor.execute(new AddUserCmd(executionId, null, assignee, descript, randomParent
                        , true, taskAfter, tasks));


                List<Task> addDeptTask = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
                TaskWithDepts taskWithDepts = taskInfoVO.getTaskWithDepts();


                //为追加的部门 添加用户
                for (int i = 0; i < addDeptTask.size(); i++) {
                    Task taskdept = addDeptTask.get(i);
                    String id = taskdept.getId();
                    String parentTaskIdDept = taskdept.getParentTaskId();

                    if (parentTaskIdDept.equalsIgnoreCase(randomParent)) {
                        String taskDefinitionKey = taskdept.getTaskDefinitionKey();
                        taskWithDepts.setTaskDefKey(taskDefinitionKey);
                        //存储下个节点的任务id---主办/辐办/传阅等类型都存储起来(会有重复数据)[数量=用户量*种类]
                        taskWithDepts.setTskId(id);
                        //请求数据库 2-3 次
                        departWithTaskMapper.save(processInstanceId, taskWithDepts);
                    }

                }


            }


            for (Task task1 : tasks) {
                taskCommonService.updateHisActDept(task1, null);
                taskCommonService.updateRuActDept(task1, null);
            }
            //********************* 写入参与人 *********************
            String table = busData.get("table") + "_permit";

            //4 存储用户信息到 业务数据权限表 - 构造用户信息
            saveDataPermit(taskInfoVO, table);
        }
        //更新业务表(部门数据)
        for (TaskInfoVO taskInfoVO : taskInfoVOS) {

            if (taskInfoVO.getIsDept()){

                for (String remove : TaskConstant.REMOVEFILEDS) {
                    busData.remove(remove);
                }
                oaBusDynamicTableMapper.updateData(busData);
                break;
            }
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
