package com.cfcc.modules.workflow.controller;

import com.cfcc.common.api.vo.Result;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysRole;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.service.ISysUserService;
import com.cfcc.modules.utils.IWfConstant;
import com.cfcc.modules.workflow.service.ProcessManagerService;
import com.cfcc.modules.workflow.service.TaskCommonFoldService;
import com.cfcc.modules.workflow.service.TaskCommonService;
import com.cfcc.modules.workflow.vo.TaskInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Api(tags = "缓急程度折叠列表")
@RestController
@RequestMapping("urgency/degree")
@Slf4j
public class UrgencyDegreeFoldController {
    @Autowired
    private TaskCommonFoldService taskCommonService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ProcessManagerService processManagerService;





    @GetMapping("monitorFoldUrgency")
    @ApiOperation("流程监控一级折叠")
    public Result monitorFoldUrgency(TaskInfoVO taskInfoVO,@RequestParam(required = false) String urgencyDegree, @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                            @RequestParam(required = false, defaultValue = "100") Integer pageSize,
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

            String token = request.getHeader("X-Access-Token");
            String username = JwtUtil.getUsername(token);
            SysUser userByName = sysUserService.getUserByName(username);
            if (userByName==null || userByName.getId()==null){
                return Result.error("查询失败,稍后重试");
            }
            taskInfoVO.setUserId(userByName.getId());
            //默认排序
            taskInfoVO.setTableOrder(true);
            taskInfoVO.setOrederByTime(-1);
            //判断是不是超级管理员 是的话展示所有人
            List<SysRole> roles = loginInfo.getRoles();
            boolean isAdmin=false;
            for (SysRole role : roles) {
                String roleName = role.getRoleName();
                if ("系统管理员".equalsIgnoreCase(roleName)) {
                    isAdmin=true;
                    break;
                }
            }
            Result result = taskCommonService.monitorFoldUrgency(urgencyDegree, taskInfoVO,isAdmin);
            return result;
        } catch (Exception e) {
            log.error("任务查询失败" + e.toString());
            return Result.error("任务查询失败");
        }

    }


    @GetMapping("queryTask")
    @ApiOperation("任务查询")
    public Result queryTask(TaskInfoVO taskInfoVO,@RequestParam(required = false, defaultValue = "0") Integer jY,@RequestParam(required = false) String urgencyDegree, @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                            @RequestParam(required = false, defaultValue = "100") Integer pageSize,
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
            //默认排序
            taskInfoVO.setTableOrder(true);
            taskInfoVO.setOrederByTime(-1);
            switch (operstatus) {
                //待办(部门类型已加)
                case IWfConstant.TASK_TODO:
                    result = taskCommonService.queryTaskToDo(urgencyDegree,taskInfoVO, pageNo, pageSize,jY);
                    break;
                //已办
                case IWfConstant.TASK_DONE:
                    result = taskCommonService.queryTaskDone(urgencyDegree,taskInfoVO, pageNo, pageSize,jY);
                    break;
                //流程监控数据
                case IWfConstant.TASK_MONITOR:
                    //判断是不是超级管理员 是的话展示所有人
                    List<SysRole> roles = loginInfo.getRoles();
                    boolean isAdmin=false;
                    for (SysRole role : roles) {
                        String roleName = role.getRoleName();
                        if ("系统管理员".equalsIgnoreCase(roleName)) {
                            isAdmin=true;
                            break;
                        }
                    }
                    result = taskCommonService.queryTaskMonitor(urgencyDegree,taskInfoVO, pageNo, pageSize,jY,isAdmin);
                    break;
                //我的委托
                case IWfConstant.MY_AGENT:
                    result = taskCommonService.queryTaskMyAgent(urgencyDegree,taskInfoVO, pageNo, pageSize,jY);
                    break;
                //重置(所有未完成-不区分用户[查询条件可以用区分])
                case IWfConstant.JUMP:
                    result = taskCommonService.queryTaskToDo(urgencyDegree,taskInfoVO, pageNo, pageSize,jY);
                    break;
            }
            return result;
        } catch (Exception e) {
            log.error("任务查询失败" + e.toString());
            return Result.error("任务查询失败");
        }

    }
}
