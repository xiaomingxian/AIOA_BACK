package com.cfcc.modules.oaBus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.constant.workflow.RoleScope;
import com.cfcc.common.exception.AIOAException;
import com.cfcc.common.util.workflow.VarsWithBus;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.service.IBusFunctionPermitService;
import com.cfcc.modules.oaBus.service.IBusFunctionService;
import com.cfcc.modules.oaBus.service.TaskInActService;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.service.ISysUserService;
import com.cfcc.modules.workflow.pojo.Activity;
import com.cfcc.modules.workflow.pojo.OaProcActinst;
import com.cfcc.modules.workflow.pojo.TaskInfoJsonAble;
import com.cfcc.modules.workflow.pojo.TaskWithDepts;
import com.cfcc.modules.workflow.service.IoaProcActinstService;
import com.cfcc.modules.workflow.service.TaskCommonService;
import com.cfcc.modules.workflow.vo.TaskInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Slf4j
@Api(tags = "处于流程中的数据")
@RestController
@RequestMapping("oaBus/taskInAct")
public class TaskInActController {

    @Autowired
    private IBusFunctionService busFunctionService;


    @Autowired
    private IBusFunctionPermitService iBusFunctionPermitService;

    @Autowired
    private TaskCommonService taskCommonService;

    @Autowired
    private IoaProcActinstService ioaProcActinstService;


    @Autowired
    private ISysUserService sysUserService;


    @Autowired
    private TaskInActService taskInActService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;


    //TODO 过滤行领导 *****************************************************

    @ApiOperation("查询追加用户")
    @GetMapping("departChuanYueUser")
    public Result departChuanYueUser(String drafterId, String procInstId, String procKey, HttpServletRequest request) {
        try {
            //查询 流程 办理没办理信息(根据用户信息+procId)
            if (StringUtils.isBlank(procInstId)) {
                List<Map<String, Object>> list = nexUserQuery(procKey, procInstId, drafterId, null, request);
                //走普通的下一任务查询
                return Result.ok(list);
            } else {
                //追加视图--查询第一个环节
                List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(procInstId).list();
                if (list.size() == 0) return Result.error("未找到产生的任务信息");
                String processDefinitionId = list.get(0).getProcessDefinitionId();
                ProcessDefinitionEntity proc = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
                List<ActivityImpl> activities = proc.getActivities();
                if (activities.size() == 0) throw new AIOAException("未找到流程环节,请检查流程图是否合法");
                String taskDefKey = activities.get(0).getId();//第一个环节的key
                String hiTaskId = null;
                for (HistoricTaskInstance historicTaskInstance : list) {
                    String taskDefinitionKey = historicTaskInstance.getTaskDefinitionKey();
                    if (taskDefinitionKey.equalsIgnoreCase(taskDefKey)) {
                        hiTaskId = historicTaskInstance.getId();
                        break;
                    }
                }
                return canAddUsers(hiTaskId, true, request);


            }
        } catch (AIOAException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询用户失败");
        }
    }

    //办理(追加走追加,办理走办理)[在前端区分]


    @ApiOperation("查询追加用户")
    @GetMapping("addUsersQuery")
    public Result addUsersQuery(String taskId, HttpServletRequest request) {

        try {
            return canAddUsers(taskId, false, request);
        } catch (AIOAException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询可追加用户失败");
        }
    }

    private Result canAddUsers(String taskId, boolean isChuanyue, HttpServletRequest request) {
        //查找是自己发出的任务
        List<TaskInfoJsonAble> taskJsonAbles = taskCommonService.sendByMe(taskId);
        if (taskJsonAbles == null || (taskJsonAbles != null && taskJsonAbles.size() <= 0)) {
            return Result.error("没有可追加环节");
        }

        ArrayList<String> taskIds = new ArrayList<>();
        //记录taskId去查询已选择的用户
        Map<String, String> idAndKey = new HashMap<>();
        Map<String, String> idAndExecutionId = new HashMap<>();
        //已经选择过的用户
        Map<String, List<String>> choiceIds = new HashMap<>();

        taskJsonAbles.stream().forEach(task -> {
            String id = task.getId();
            String executionId = task.getExecutionId();
            String taskDefinitionKey = task.getTaskDefinitionKey();
            taskIds.add(id);
            idAndKey.put(taskDefinitionKey, id);
            idAndExecutionId.put(taskDefinitionKey, executionId);
            choiceIds.put(id, task.getUsersHaveChoice());
        });


        TaskInfoJsonAble taskInfoJsonAble = taskJsonAbles.get(0);
        String drafterId = taskInfoJsonAble.getDrafterId();
        String key = taskInfoJsonAble.getProDefName();
        String processDefinitionId = taskInfoJsonAble.getProcessDefinitionId();

        Result result = nextUserQuery(key, processDefinitionId, drafterId, taskId, request);
        //查找已经选择的用户 标记出来
        if (result.isSuccess()) {
            List<Map<String, Object>> actMsgs = (List<Map<String, Object>>) result.getResult();
            ListIterator<Map<String, Object>> iterator = actMsgs.listIterator();

            //下n个节点遍历
            while (iterator.hasNext()) {
                Map<String, Object> actMsg = iterator.next();

                Activity activity = (Activity) actMsg.get("actMsg");
                List<Map<String, Object>> nextUsers = (List<Map<String, Object>>) actMsg.get("nextUsers");
                String taskDefKey = activity.getId();
                boolean allowMulti = activity.isAllowMulti();
                OaProcActinst oaProcActinst = (OaProcActinst) actMsg.get("oaProcActinst");

                boolean isDept = false;
                String userOrRole = oaProcActinst.getUserOrRole();
                if ("dept".equalsIgnoreCase(userOrRole)) {
                    isDept = true;
                }

                boolean multAssignee = oaProcActinst.isMultAssignee();
                if ((!allowMulti) || (!multAssignee) && !isDept) {
                    iterator.remove();
                    continue;
                }

                //部门与非部门的处理方式不同
                if (isDept) {
                    String firstSonKey = activity.getFirstSonKey();
                    //去部门记录任务记录表里查询相关信息
                    //查看是否有主办
                    String taskIdRecord = idAndKey.get(firstSonKey);
                    if (taskIdRecord == null) {
                        iterator.remove();
                        continue;
                    }

                    int count = taskCommonService.haveMainDept(taskIdRecord);//主办数量大于就排除主办
                    if (count > 0) {
                        String depts = oaProcActinst.getDepts();
                        if (depts != null && depts.contains(",")) {
                            String[] split = depts.split(",");
                            ArrayList<String> list = new ArrayList<>();
                            for (String s : split) {
                                if (!s.contains("主办")) {
                                    list.add(s);
                                }
                            }
                            oaProcActinst.setDepts(list.toString());
                        }
                    }
                    //查出所有用户,排除他们所在部门
                    List<String> deptUsers = taskCommonService.deptUsers(taskIdRecord);
                    if (deptUsers.size() > 0) {

                        List<String> deptIds = sysUserService.selectDeptsBysUsers(deptUsers);
                        List<Map<String, Object>> deptHaveChoice = new ArrayList<>();//已经选择过的部门
                        Iterator<Map<String, Object>> deptsIterator = nextUsers.iterator();

                        while (deptsIterator.hasNext()) {
                            Map<String, Object> next = deptsIterator.next();
                            Object id = next.get("id");
                            next.put("taskId", idAndKey.get(firstSonKey));
                            next.put("executionId", idAndExecutionId.get(firstSonKey));
                            if (deptIds.contains(id)) {
                                deptHaveChoice.add(next);
                                deptsIterator.remove();
                            }
                        }

                        actMsg.put("deptHaveChoice", deptHaveChoice);
                        if (nextUsers.size() == 0) {
//                            if (!isChuanyue) {
                            iterator.remove();
//                            }
                            continue;
                        } else {
                            activity.setCanAdd(true);
                        }

                    }


                } else {
                    //区分已选和未选
                    //定义是否需要追加用户的标志
                    //根据任务定义key获取记录的流程id(不是部门类型)
                    String taskIdRecord = idAndKey.get(taskDefKey);
                    if (taskIdRecord == null && !isDept) {
                        iterator.remove();
                        continue;
                    }

                    //已经选择的用户id
                    List<String> uids = choiceIds.get(taskIdRecord);


                    boolean donotAddUser = true;
                    List<Map<String, Object>> userHaveChoice = new ArrayList<>();
                    Iterator<Map<String, Object>> iterator1 = nextUsers.iterator();
                    while (iterator1.hasNext()) {
                        Map<String, Object> user = iterator1.next();
                        String uid = (String) user.get("uid");
                        if (uids.contains(uid)) {
                            userHaveChoice.add(user);
                            iterator1.remove();
                        } else {
                            user.put("status", "可追加");
                            donotAddUser = false;
                        }
                        user.put("taskId", idAndKey.get(taskDefKey));
                        user.put("executionId", idAndExecutionId.get(taskDefKey));

                    }
                    actMsg.put("userHaveChoice", userHaveChoice);
                    if (donotAddUser) {
//                        if (!isChuanyue)
                        iterator.remove();
                        continue;
                    } else {
                        activity.setCanAdd(true);

                    }
                }
            }


        }
        List<Map<String, Object>> actMsgs = (List<Map<String, Object>>) result.getResult();
        if (actMsgs.size() == 0) return Result.error("没有可追加环节");
        return result;
    }


    @PostMapping("doAddUsers")
    @ApiOperation("追加办理人")
    public Result doAddUser(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        try {
            LoginInfo loginInfo = sysUserService.getLoginInfo(request);
            List<Map<String, Object>> taskInfoVOs = (List<Map<String, Object>>) map.get("list");
            ArrayList<TaskInfoVO> taskInfoVOS = new ArrayList<>();
            for (Map<String, Object> taskInfoVO : taskInfoVOs) {

                TaskInfoVO taskInfoVO1 = mapToObject(taskInfoVO);
                taskInfoVO1.setUserId(loginInfo.getId());

                Map<String, Object> vars = taskInfoVO1.getVars();
                Collection<Object> values = vars.values();
                boolean flag=false;
                for (Object value : values) {
                    if (value instanceof  String && value!=null && value.toString().contains("no-")){
                        flag=true;
                        break;
                    }
                }
                if (!flag){
                    taskInfoVOS.add(taskInfoVO1);
                }
            }


            if (taskInfoVOS != null && taskInfoVOS.size() > 0) {
                taskInActService.doAddUsers(taskInfoVOS);
            } else {
                return Result.error("信息不完善,拒绝追加");
            }
            return Result.ok("任务追加成功");
        } catch (AIOAException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("追加任务失败" + e.toString());
            return Result.error("追加任务失败");
        }
    }

    @ApiOperation(value = "查询下一办理人")
    @GetMapping("nextUserQuery")
    public Result nextUserQuery(String procDefkey, String drafterId, String processDefinitionId,
                                @RequestParam(required = false) String taskId, HttpServletRequest request) {

        try {

            List<Map<String, Object>> list = nexUserQuery(procDefkey, processDefinitionId, drafterId, taskId, request);

            return Result.ok(list);
        } catch (AIOAException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
            return Result.error("查询失败");
        }
    }


    private List<Map<String, Object>> nexUserQuery(String procDefkey, String processDefinitionId, String drafterId,
                                                   @RequestParam(required = false) String taskId, HttpServletRequest request) {
        if (StringUtils.isBlank(procDefkey)) {
            throw new AIOAException("传入的流程定义为空，请检查此业务是否配置了流程");
        }
        //当前用户
        LoginInfo user = sysUserService.getLoginInfo(request);
        String id = user.getId();
        //查询下n个节点属性
        ActivityImpl current = taskCommonService.currentAct(taskId, processDefinitionId, procDefkey);
        List<Activity> acts = taskCommonService.searchNextActs(taskId, processDefinitionId, procDefkey);
        if (acts == null) throw new AIOAException("环节配置信息不完善请检查");

        //组装代办列表
        List<Map<String, Object>> list = new ArrayList();
        Map<String, String> conditionContext = new HashMap<>();

        //遍历节点 查询相关业务信息(避免循环中查询(但是此数据较少))
        for (Activity i : acts) {
            if (i.getConditionContext() != null) {
                Map<String, String> conditionContext1 = i.getConditionContext();
                for (String key : conditionContext1.keySet()) {
                    String val = conditionContext1.get(key);
                    conditionContext.put(key, val);
                }
            }
            HashMap<String, Object> oneAct = new HashMap<>();
            //查询节点信息(配置的form信息)
            QueryWrapper<OaProcActinst> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("PROC_DEF_KEY", procDefkey)
                    .eq("ACT_ID", i.getId())
                    .eq("ACT_NAME", i.getName())
            ;
            //1 节点信息
            oneAct.put("actMsg", i);
            if (!i.getType().equalsIgnoreCase("endevent")) {

                List<OaProcActinst> oaProcActinsts = ioaProcActinstService.list(queryWrapper);
                OaProcActinst oaProcActinst = null;
                if (oaProcActinsts.size() > 0) {
                    oaProcActinst = oaProcActinsts.get(0);
                }

                if (oaProcActinst == null) {
                    throw new AIOAException("没找到对应的对应环节的配置请检查环节配置:要查询的环节" +
                            "<【" + procDefkey + "】" + i.getId() + "-" + i.getName() + ">不存在[检查是否是拷贝数据]");
                }
                //2 数据库中的节点信息
                oneAct.put("oaProcActinst", oaProcActinst);
                // 同一环节排除自己
                boolean excludeMySelf = false;
                if (current.getId().equals(i.getId())) {
                    excludeMySelf = true;
                }
                //查询具体数据
                List<Map<String, Object>> nextUsers = new ArrayList<>();
                nexUsersQuery(drafterId, id, oaProcActinst, nextUsers, user, excludeMySelf);

                //3 办理人信息
                oneAct.put("nextUsers", nextUsers);
                list.add(oneAct);
            }
            if (i.getType().equalsIgnoreCase("endevent") && acts.size() == 1) {
                list.add(oneAct);
            }
        }

        return list;
    }

    @ApiOperation(value = "查询该节点办理人")
    @GetMapping("currentUserQuery")
    public Result currentUserQuery(String procDefKey, String taskId, String taskName, String drafterId, HttpServletRequest request) {

        try {
            //当前用户
            LoginInfo user = sysUserService.getLoginInfo(request);
            String id = user.getId();
            if (StringUtils.isBlank(procDefKey)) {
                return Result.error("传入的流程定义为空，请检查此业务是否配置了流程");
            }
            QueryWrapper<OaProcActinst> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("PROC_DEF_KEY", procDefKey)
                    .eq("ACT_ID", taskId)
                    .eq("ACT_NAME", taskName);
            //.eq("ACT_TYPE", "userTask");

            List<OaProcActinst> oaProcActinsts = ioaProcActinstService.list(queryWrapper);
            if (oaProcActinsts.size() <= 0) {
                return Result.error("没找到对应的对应环节的配置请检查环节配置");
            }
            List<Map<String, Object>> nextUsers = new ArrayList<>();
            //如果存在数据应该只有一条
            for (OaProcActinst oaProcActinst : oaProcActinsts) {
                nexUsersQuery(drafterId, id, oaProcActinst, nextUsers, user, true);//排除自己
            }

            return Result.ok(nextUsers);
        } catch (AIOAException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败");
        }
    }


    @ApiOperation(value = "部门候选人组装")
    @PostMapping("deptUsersQuery")
    public Result deptUsers(@RequestBody Map<String, Object> param) {


        try {
            String role = (String) param.get("role");
            //key 主办/辅版...
            //value部门集合
            Map<String, List<Map<String, String>>> depts = (Map<String, List<Map<String, String>>>) param.get("depts");


            Set<Map.Entry<String, List<Map<String, String>>>> entries = depts.entrySet();


            //部门与用户id组合串，depart
            HashMap<String, List<SysUser>> depart_userIds = new HashMap<>();

            //循环不会太多 3条左右
            for (Map.Entry<String, List<Map<String, String>>> entry : entries) {
                //
                String key = entry.getKey();
                List<Map<String, String>> value = entry.getValue();

                List<SysUser> users = new ArrayList<>();
                ArrayList<String> deptids = new ArrayList<>();
                if (value.size() > 0) {
                    //查询用户组装
                    for (Map<String, String> depart : value) {
                        deptids.add(depart.get("id"));
                    }
                    users = sysUserService.selectUsersByDeptAndRole(role, deptids);
                }
                depart_userIds.put(key, users);
            }
            Result<Object> ok = Result.ok("查询成功");
            ok.setResult(depart_userIds);
            return ok;

        } catch (Exception e) {
            log.error(e.toString());
            return Result.error("查询失败");
        }

    }


    @PostMapping("doTask")
    @ApiOperation("任务办理")
    public Result doTask(@RequestBody TaskInfoVO taskInfoVO, HttpServletRequest request) {
        try {
            //流程与业务相关数据
            SysUser user = sysUserService.getCurrentUser(request);
            taskInfoVO.setUserId(user.getId());

            busDataSet(taskInfoVO);

            taskInActService.doTask(taskInfoVO, request);
            return Result.ok("任务办理成功");
        } catch (AIOAException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("办理任务失败" + e.toString());
            return Result.error("办理任务失败");
        }
    }

    private void busDataSet(TaskInfoVO taskInfoVO) {
        Map<String, Object> vars = taskInfoVO.getVars();
        Map<String, Object> busData = taskInfoVO.getBusData();
        //根据functionId查出function名称
        String i_bus_function_id = busData.get("i_bus_function_id").toString();
        BusFunction busFunction = busFunctionService.getOneByFunId(i_bus_function_id);
        String sName = busFunction.getSName();
        String sLevel = busFunction.getSLevel();
        busData.put("ilevel", sLevel);
        busData.put("functionName", sName);

        String mainDept = "";
        String s_receive_num = "";
        Object file_num = busData.get("s_file_num");
        String s_file_num = null;
        if (file_num != null) {
            s_file_num = busData.get("s_file_num").toString();
        }

        String sourceFileNum = busData.get("s_file_num") == null ? null : busData.get("s_file_num").toString();
        if (sName.contains("收文")) {//
            if (taskInfoVO.getIsDept()) {
                mainDept = taskInfoVO.getTaskWithDepts().getMainDept();
            }
            busData.put("s_create_dept", mainDept);
            s_receive_num = busData.get("s_receive_num") == null ? "" : busData.get("s_receive_num").toString();
            busData.put("s_file_num", s_receive_num);
        } else {
            mainDept = busData.get("s_create_dept") == null ? "" : busData.get("s_create_dept").toString();
        }
        busData.put("mainDept", mainDept);


        String busMsg = VarsWithBus.getBusMsg(busData);

        busData.put("s_file_num", sourceFileNum);


        if (vars != null) {
            vars.put("busMsg", busMsg);
        }
        taskInfoVO.setVars(vars);
        if (sName.contains("收文")) {
            busData.put("s_file_num", s_file_num);
        }
    }

    @PostMapping("doTaskMore")
    @ApiOperation("任务办理并行/包容网关")
    public Result doTaskMore(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        try {
            LoginInfo loginInfo = sysUserService.getLoginInfo(request);
            List<Map<String, Object>> taskInfoVOs = (List<Map<String, Object>>) map.get("list");
            ArrayList<TaskInfoVO> taskInfoVOS = new ArrayList<>();
            for (Map<String, Object> taskInfoVO : taskInfoVOs) {

                TaskInfoVO taskInfoVO1 = mapToObject(taskInfoVO);
                taskInfoVO1.setUserId(loginInfo.getId());
                taskInfoVOS.add(taskInfoVO1);
            }


            if (taskInfoVOS != null && taskInfoVOS.size() > 0) {
                taskInActService.doTaskMore(taskInfoVOS, request);
            } else {
                return Result.error("信息不完善,拒绝办理");
            }
            return Result.ok("任务办理成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("办理任务失败" + e.toString());
            return Result.error("办理任务失败");
        }
    }

    public TaskInfoVO mapToObject(Map<String, Object> map) throws Exception {
        if (map == null)
            return null;
        TaskInfoVO taskInfoVO = new TaskInfoVO();

        Map<String, Object> taskWithDeptsMap = (Map<String, Object>) map.get("taskWithDepts");
        Map<String, Object> busData = (Map<String, Object>) map.get("busData");
        map.remove("taskWithDepts");
        map.remove("busData");

        org.apache.commons.beanutils.BeanUtils.populate(taskInfoVO, map);
        TaskWithDepts taskWithDepts = new TaskWithDepts();
        if (taskWithDeptsMap != null) {
            Map<String, List<String>> deptMsg = (Map<String, List<String>>) taskWithDeptsMap.get("deptMsg");
            taskWithDeptsMap.remove("deptMsg");
            org.apache.commons.beanutils.BeanUtils.populate(taskWithDepts, taskWithDeptsMap);
            taskWithDepts.setDeptMsg(deptMsg);
        }
        taskInfoVO.setTaskWithDepts(taskWithDepts);
        taskInfoVO.setBusData(busData);

        busDataSet(taskInfoVO);


        return taskInfoVO;
    }


    @ApiOperation(value = "已读任务状态修改")
    @GetMapping("/changeStatus")
    public Result changeStatus(String table, String userId, String functionId, String dataId) {

        try {
            iBusFunctionPermitService.updateReade(table, userId, functionId, dataId);
            return Result.ok("修改状态成功");
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error("修改状态失败");
        }
    }


    @ApiOperation(value = "流程跟踪")
    @GetMapping("/workTrack")
    public Result workTrack(String proInstId) {
        try {
            List<Map<String, Object>> res = taskCommonService.workTrack(proInstId, true);
            if (res == null) return Result.error("流程环节配置不完善请检查");

            Result<Object> result = Result.ok("查询成功");
            result.setResult(res);
            return result;
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error("查询失败");
        }
    }


    private void nexUsersQuery(String drafterId, String id, OaProcActinst oaProcActinst,
                               List<Map<String, Object>> nextUsers, LoginInfo user, boolean excludeMySelf) {
        String roleScope = oaProcActinst.getRoleScope();
        String userOrRole = oaProcActinst.getUserOrRole();
        String candidates = oaProcActinst.getCandidates();
        //排除掉自己？？？？
        if ("dept".equals(userOrRole)) {
            //case RoleScope.ALLDEPT:
            //返回所有部门信息(不跨部门)
            String parentId = user.getDepart().getParentId();
            nextUsers.addAll(sysUserService.selectAllDept(parentId));
            //break;
        } else {
            List<Map<String, Object>> draftMsg = null;
            switch (roleScope) {
                case RoleScope.DRAFTER:
                    //拟稿人
                    draftMsg = sysUserService.getDraftMsg(drafterId);
                    break;
                case RoleScope.DRAFTER_DEPT:
                    //拟稿人所在部门
                    draftMsg = sysUserService.getNextUsersByDept(drafterId, candidates);
                    break;
                case RoleScope.CURRENT_ORG:
                    //当前用户所在 机构
                    draftMsg = sysUserService.getNextUsersByOrg(id, candidates);
                    break;
                case RoleScope.CURRENT_DEPT:
                    //
                    draftMsg = sysUserService.getNextUsersByDept(id, candidates);

                    break;
                case RoleScope.ALLDEPT:
                    //
                    draftMsg = sysUserService.getNextUsersAllDept(candidates);
                    break;

            }
            if (draftMsg == null) throw new AIOAException("为找到匹配用户");
            String userId = user.getId();
            //如果不允许包含自己，就删除
            if (excludeMySelf) {
                ListIterator<Map<String, Object>> iterator = draftMsg.listIterator();
                while (iterator.hasNext()) {
                    Map<String, Object> next = iterator.next();
                    Object uid = next.get("uid");
                    if (uid != null) {
                        if (uid.toString().equals(userId)) {
                            iterator.remove();
                        }
                    }
                }
            }
            nextUsers.addAll(draftMsg);
        }

    }


}
