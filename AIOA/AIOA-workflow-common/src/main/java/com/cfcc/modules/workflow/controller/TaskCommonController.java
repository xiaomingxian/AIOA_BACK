package com.cfcc.modules.workflow.controller;


import com.cfcc.common.api.vo.Result;
import com.cfcc.common.exception.AIOAException;
import com.cfcc.common.util.FileUtils;
import com.cfcc.common.util.norepeat.NoRepeatSubmit;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysRole;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.service.ISysUserService;
import com.cfcc.modules.utils.IWfConstant;
import com.cfcc.modules.workflow.pojo.Activity;
import com.cfcc.modules.workflow.pojo.BackRecord;
import com.cfcc.modules.workflow.pojo.JumpMsg;
import com.cfcc.modules.workflow.pojo.TaskInfoJsonAble;
import com.cfcc.modules.workflow.service.ActPicService;
import com.cfcc.modules.workflow.service.ProcessManagerService;
import com.cfcc.modules.workflow.service.TaskCommonService;
import com.cfcc.modules.workflow.vo.TaskInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Api(tags = "工作流：通用任务接口")
@RestController
@RequestMapping("wf/task")
@Slf4j
public class TaskCommonController {

    @Autowired
    private TaskCommonService taskCommonService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ProcessManagerService processManagerService;

    @Autowired
    private ActPicService actPicService;

    @Value("${jeecg.path.upload}")
    private String savePath;


    @GetMapping("taskStatus")
    public Result taskStatus(String taskid) {
        String status = taskCommonService.taskStatus(taskid);
        return Result.ok(status);
    }


    @GetMapping("finish")
    @NoRepeatSubmit
    public Result finish(String taskId) {
        taskCommonService.finsh(taskId);
        return Result.ok("完成任务成功");
    }


    @GetMapping("updateTaskStatus")
    @NoRepeatSubmit
    public void updateTaskStatus(String taskId) {
        taskCommonService.updateTaskStatus(taskId,"done");
    }


    @GetMapping("currentUserTodos")
    public Result currentUserTodos(String processInstanceId, HttpServletRequest request) {

        try {
            LoginInfo loginInfo = sysUserService.getLoginInfo(request);
            List<TaskInfoJsonAble> list = taskCommonService.undo(processInstanceId, loginInfo);
            return Result.ok(list);
        } catch (Exception e) {
            log.error("查询待办失败：" + e.getMessage());
            return Result.error("查询失败");
        }
    }


    @GetMapping("queryTask")
    @ApiOperation("任务查询")
    public Result queryTask(TaskInfoVO taskInfoVO, @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                            HttpServletRequest request) {
        try {

            LoginInfo loginInfo = sysUserService.getLoginInfo(request);
            String operstatus = taskInfoVO.getOperstatus();

            //判断查询条件是否有用户(作为查询条件)
            if (taskInfoVO.getUserId() != null) {
                //有任何一个就去填充完整
                if (taskInfoVO.getUserName() == null)
                    taskInfoVO.setUserName(sysUserService.selectUserNameById(taskInfoVO.getUserId()));
            } else {
                //查询当前用户，作为assignee
                if (operstatus != null && !operstatus.equals(IWfConstant.JUMP)) {//重置是管理员权限(不用必须加用户)

                    taskInfoVO.setUserId(loginInfo.getId());
                    taskInfoVO.setUserName(loginInfo.getUsername());
                }
            }


            Result result = null;

            switch (operstatus) {
                //待办(部门类型已加)
                case IWfConstant.TASK_TODO:
                    result = taskCommonService.queryTaskToDo(taskInfoVO, pageNo, pageSize);
                    break;
                //已办
                case IWfConstant.TASK_DONE:
                    result = taskCommonService.queryTaskDone(taskInfoVO, pageNo, pageSize);
                    break;
                //流程监控数据
                case IWfConstant.TASK_MONITOR:
                    //判断是不是超级管理员 是的话展示所有人
                    List<SysRole> roles = loginInfo.getRoles();
                    boolean isAdmin = false;
                    for (SysRole role : roles) {
                        String roleName = role.getRoleName();
                        if ("系统管理员".equalsIgnoreCase(roleName)) {
                            isAdmin = true;
                            break;
                        }
                    }
                    result = taskCommonService.queryTaskMonitor(taskInfoVO, pageNo, pageSize, isAdmin);
                    break;
                //我的委托
                case IWfConstant.MY_AGENT:
                    result = taskCommonService.queryTaskMyAgent(taskInfoVO, pageNo, pageSize);
                    break;
                //重置(所有未完成-不区分用户[查询条件可以用区分])
                case IWfConstant.JUMP:
                    result = taskCommonService.queryTaskToDo(taskInfoVO, pageNo, pageSize);
                    break;
                case IWfConstant.SHHIFT://任务移交-以人为准
                    result = taskCommonService.queryTaskShift(taskInfoVO, pageNo, pageSize);
                    break;
            }
            return result;
        } catch (
                Exception e) {
            log.error("任务查询失败" + e.toString());
            return Result.error("任务查询失败");
        }

    }

    @GetMapping("queryTaskUnDoCurrent")
    @ApiOperation("查看最新的代办信息")
    public Result queryTask(String procInstId) {//流程实例id
        //环节 用户
        try {
            List<Map<String, Object>> tasks = taskCommonService.workTrack(procInstId, false);
            if (tasks == null) return Result.error("环节配置信息不完善请检查");
            return Result.ok(tasks);
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error("查询失败");
        }
    }


    @ApiOperation("终止流程")
    @GetMapping("endProcess")
    @NoRepeatSubmit
    public Result endProcess(TaskInfoVO taskInfoVO, HttpServletRequest request) {
        try {
            //查询当前用户，作为assignee
            SysUser user = sysUserService.getCurrentUser(request);

            taskInfoVO.setUserId(user.getId());
            taskCommonService.endProcess(taskInfoVO);
            return Result.ok("终止流程成功");
        } catch (Exception e) {
            log.error("终止流程失败:" + e.toString());
            return Result.error("终止流程失败");
        }
    }

    @ApiOperation("批量办结")
    @PostMapping("batchEnd")
    @NoRepeatSubmit
    public Result batchEnd(@RequestBody Map<String, String[]> ids, HttpServletRequest request) {
        try {
            //查询当前用户，作为assignee
            SysUser user = sysUserService.getCurrentUser(request);

            taskCommonService.batchEnd(ids.get("ids"), user);

            return Result.ok("批量办结成功");
        } catch (Exception e) {
            log.error("批量办结失败:" + e.toString());
            return Result.error("批量办结失败");
        }
    }

    @ApiOperation("部门完成")
    @PostMapping("departFinish")
    @NoRepeatSubmit
    public Result departFinish(@Param(value = "taskId") String taskId, String processInstanceId, HttpServletRequest request) {
        try {
            SysUser user = sysUserService.getCurrentUser(request);

            return taskCommonService.departFinish(taskId, processInstanceId, user);
        } catch (AIOAException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("部门完成失败");
        }
    }

    @ApiOperation("批量办结/部门完成")
    @PostMapping("batchPiYue")
    @NoRepeatSubmit
    public Result batchChuanYue(@RequestBody List<Map<String, Object>> data, HttpServletRequest request) {
        try {
            //查询当前用户，作为assignee
            SysUser user = sysUserService.getCurrentUser(request);

            return taskCommonService.batchChuanYue(data, user);

        } catch (Exception e) {
            log.error("批量办结失败:" + e.toString());
            return Result.error("批量办结失败");
        }
    }


    @ApiOperation("意见查询")
    @GetMapping("commentQuery")
    public Result commentQuery(String processInstanceId, @RequestParam(required = false, defaultValue = "1") Integer pageNo, @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        try {
            return taskCommonService.commentQuery(processInstanceId, pageNo, pageSize);
        } catch (Exception e) {
            log.error("终止流程失败:" + e.toString());
            return Result.error("终止流程失败");
        }
    }


    @ApiOperation("节点位置查询")
    @GetMapping("pointLocationQuery")
    public Result pointLocationQuery(TaskInfoVO taskInfoVO) {
        try {
            String processId = taskInfoVO.getProcessId();
            //流程是否结束
            boolean flag = taskCommonService.exeHaveDone(processId);
            List<Map> location = null;
            if (flag) {
                location = taskCommonService.getEndLocation(taskInfoVO.getProcessDefinitionId());
            } else {
                location = taskCommonService.getLocationRun(taskInfoVO);
            }
            Result<Object> result = Result.ok("查询成功");
            result.setResult(location);
            return result;
        } catch (Exception e) {
            log.error("节点位置查询:" + e.toString());
            return Result.error("节点位置查询错误");
        }
    }


    @ApiOperation("展示可退回节点")
    @GetMapping("showBackAct")
    public Result showBackAct(String processDefinitionId, String processInstanceId, String taskDefinitionKey, String type) {

        try {
            //不包含本节点
            List<Activity> list = null;
            if (type.equalsIgnoreCase("back")) {
                list = processManagerService.showBackAct(processDefinitionId, processInstanceId, taskDefinitionKey);
            }
            if (type.equalsIgnoreCase("all")) {
                list = processManagerService.actsList(processDefinitionId, false);
                //移除相同节点
                Iterator<Activity> iterator = list.iterator();
                while (iterator.hasNext()) {
                    Activity next = iterator.next();
                    boolean b = next.getId().equalsIgnoreCase(taskDefinitionKey);
                    if (b) {
                        iterator.remove();
                    }
                }
            }
            return Result.ok(list);
        } catch (AIOAException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error("查询失败");
        }
    }


    @ApiOperation("获取指定节点的form信息")
    @GetMapping("getActForm")
    public Result getActForm(String processDefinitionId, String taskdefid) {
        try {
            Map<String, Object> map = taskCommonService.getActForm(processDefinitionId, taskdefid);
            return Result.ok(map);
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error("查询失败");
        }
    }


    @ApiOperation("是否跳转或者回退到某个环节之前")
    @PostMapping("someActFore")
    public Result someActFore(@RequestBody JumpMsg jumpMsg) {

        try {
            boolean flag = taskCommonService.someActFore(jumpMsg);

            return Result.ok(flag);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
            return Result.error("查询状态失败");
        }

    }


    @ApiOperation("跳转")
    @PostMapping("jump")
    @NoRepeatSubmit
    public Result jump(@RequestBody JumpMsg jumpMsg, HttpServletRequest request) {
        try {
            taskCommonService.jump(jumpMsg, request);
            return Result.ok("跳转节点成功");
        } catch (AIOAException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("跳转节点失败");
        }
    }

    @ApiOperation("回退")
    @PostMapping("back")
    @NoRepeatSubmit
    public Result back(@RequestBody JumpMsg jumpMsg, HttpServletRequest request) {
        try {

            taskCommonService.back(jumpMsg, request);
            return Result.ok("回退成功");
        } catch (AIOAException e) {
            return Result.error(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("回退失败");
        }
    }


    @ApiOperation("回退/撤回记录查看")
    @GetMapping("backRecordQuery")
    public Result backRecordQuery(String procInstId, @RequestParam(required = false) String table, @RequestParam(required = false,defaultValue = "") String endTime, HttpServletRequest request) {
        try {
            if (StringUtils.isBlank(endTime)){
                List<BackRecord> list = taskCommonService.backRecord(procInstId, table);
                return Result.ok(list);
            }else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timePath = endTime.replaceAll("-", "/");
                String fullPath = savePath + "/activiti/" + timePath + "/" + procInstId + "_back";
                List<Map> maps = FileUtils.readJSONArrayOneLine(fullPath);
                maps.stream().forEach(m -> {
                    Long endTimeStemp = (Long) m.get("endTime");
                    if (null!=endTimeStemp){
                        String format = simpleDateFormat.format(new Date(endTimeStemp));
                        m.put("endTime", format);
                    }

                });

                return Result.ok(maps);
            }


        } catch (AIOAException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("回退/撤回记录查看失败");
        }
    }


    @ApiOperation("任务移交")
    @GetMapping("taskShift")
    @NoRepeatSubmit
    public Result taskShift(String taskId, String sourceUserId, String destUserId) {
        try {
            String sourceUsername = sysUserService.getById(sourceUserId).getUsername();
            taskCommonService.taskShift(taskId, sourceUserId, sourceUsername, destUserId);
            return Result.ok("任务移交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("任务移交失败");
        }
    }


    @ApiOperation("任务移交")
    @GetMapping("batchShift")
    public Result batchShift(String taskIds, String sourceUserId, String destUserId) {
        try {
            String sourceUsername = sysUserService.getById(sourceUserId).getUsername();
            boolean contains = taskIds.contains(",");
            if (contains) {
                String[] split = taskIds.split(",");
                for (String id : split) {
                    taskCommonService.taskShift(id, sourceUserId, sourceUsername, destUserId);
                }
            } else {
                taskCommonService.taskShift(taskIds, sourceUserId, sourceUsername, destUserId);
            }
            return Result.ok("批量移交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("批量移交失败");
        }
    }


    @ApiOperation("查询所有已办节点")
    @GetMapping("queryAllHaveDone")
    public Result queryAllHaveDone(String assignee, String procInstId) {

        try {
            List<Map<String, String>> maps = taskCommonService.queryAllHaveDone(assignee, procInstId);
            return Result.ok(maps);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询失败：" + e.toString());
            return Result.error("查询失败");
        }
    }


    @ApiOperation("撤回-当前环节撤回到指定环节")
    @GetMapping("reCallTask")
    @NoRepeatSubmit
    public Result reCallTask(String taskId, HttpServletRequest request) {

        System.out.println("--------------------------------");
        try {
            SysUser currentUser = sysUserService.getLoginInfo(request);
            //是否可撤回
            Result reCallAble = taskCommonService.reCallAble(taskId, currentUser);
            return reCallAble;
        } catch (AIOAException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
//            e.printStackTrace();
            log.error("撤回失败：" + e.toString());
            return Result.error("撤回失败");
        }
    }


    @RequestMapping("queryProPlan")
    public void queryProPlan(HttpServletRequest request, HttpServletResponse response, @RequestParam(required = false, defaultValue = "") String endTime) throws IOException {
        String processInstanceId = request.getParameter("ProcessInstanceId");
        if (StringUtils.isBlank(endTime)){
            actPicService.queryProPlan(processInstanceId, response);
        }else {
            //获取历史流程实例
            actPicService.queryProPlanFromLocal(processInstanceId,endTime, response);
        }


    }


}
