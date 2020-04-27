package com.cfcc.modules.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cfcc.common.exception.AIOAException;
import com.cfcc.common.system.vo.DictModel;
import com.cfcc.modules.app.service.IAppDataListService;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.oaBus.entity.BusPage;
import com.cfcc.modules.oaBus.entity.BusPageDetail;
import com.cfcc.modules.oaBus.entity.BusProcSet;
import com.cfcc.modules.oaBus.mapper.OaBusdataMapper;
import com.cfcc.modules.oaBus.service.*;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysDictItem;
import com.cfcc.modules.system.service.ISysDictService;
import com.cfcc.modules.workflow.mapper.DepartWithTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AppDataListService implements IAppDataListService {

    @Autowired
    private OaBusdataMapper oaBusdataMapper;
    @Autowired
    private IBusModelService iBusModelService;
    @Autowired
    private IOaBusdataService iOaBusdataService;
    @Autowired
    private IBusProcSetService iBusProcSetService;
    @Autowired
    private IBusPageDetailService ibusPageDetailService;
    @Autowired
    private IBusPageService iBusPageService;
    @Autowired
    private ISysDictService sysDictService;


    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    @Autowired
    private DepartWithTaskMapper departWithTaskMapper;

    @Autowired
    private IBusFunctionPermitService iBusFunctionPermitService;

    @Override
    public Map<String, Object> queryBusDataByFunctionId(Integer functionId, Integer modelId, LoginInfo loginInfo, Integer pageNo, Integer pageSize, Map<String, Object> condition) {

        Map<String, Object> result = new HashMap<>();
        String col = "i_id,s_title,d_create_time";
        String orderFlag = "";
        //Map<String, Object> condition = new HashMap<>() ;

        BusModel busModel = iBusModelService.getById(modelId);
        String tableName = busModel.getSBusdataTable();
        Map<String, String> permitData = iOaBusdataService.permit(functionId, tableName, loginInfo.getUsername());       //根据权限，查询出对应的查询条件
        String userId = permitData.get("userId");
        String userUnit = permitData.get("userUnit");
        String userDepart = permitData.get("userDepart");
        permitData.remove("userId");
        permitData.remove("userUnit");
        permitData.remove("userDepart");
        log.info(permitData.toString());
        List<Map<String, Object>> dataList = oaBusdataMapper.getBusdataByMap((pageNo - 1) * pageSize,
                pageSize, col, tableName, condition, permitData, userId, userUnit, userDepart, orderFlag);
        int total = oaBusdataMapper.getBusdataByMapTotal(tableName, condition, permitData, userId, userUnit, userDepart);

        result.put("list", dataList);
        result.put("tatal", total);
        //System.out.println(dataList);
        return result;
    }

    /**
     * 获取详情的数据
     *
     * @param loginInfo
     * @param json
     * @return
     */
    @Override
    public Map<String, Object> getDetailSer(LoginInfo loginInfo, String json) {
        Map<String, Object> param = (Map<String, Object>) JSONObject.parse(json);
        // 业务数据表名
        String tableName = param.get("tableName") == null ? null : param.get("tableName") + "";
        if (tableName == null) {
            throw new AIOAException("查询失败：表名为空");
        }
        // 业务数据id
        String busdataId = param.get("busdataId") == null ? null : param.get("busdataId") + "";
        if (busdataId == null) {
            throw new AIOAException("查询失败：数据id为空");
        }

        // 任务实例id
        String taskId = param.get("taskId") == null ? null : param.get("taskId") + "";
        // 流程实例id
        String proInstanId = param.get("proInstanId") == null ? null : param.get("proInstanId") + "";
        // 待办、已办...
        String status = param.get("status") == null ? null : param.get("status") + "";


        Map<String, Object> result = new HashMap<>();
        result.put("busdataId", busdataId);
        result.put("loginInfo", loginInfo);
        result.put("proInstanId", proInstanId);

        // 查询业务数据详情
        Map<String, Object> oaBusdata = oaBusdataMapper.getBusdataMapByIdDao(tableName, busdataId);
        if (null == oaBusdata) {
            // 数据不存在
            throw new AIOAException("数据不存在可能已被删除");
        }

        result.put("oaBusdata", oaBusdata);
        // 查出对应列的名字数据
        String functionId = oaBusdata.get("i_bus_function_id") + "";
        //获取对应的版本号
        String iFunVersion = oaBusdata.get("i_fun_version") + "";
        //增加根据版本查询对应点的IPageID，通过IPageId，和FunctionID查询出对应的字段含义
        //为兼容以前的版本，先查询iPageId，如果有iPageId的话，则用iPageId查询，如果没有的话，就直接查
        QueryWrapper<BusProcSet> queryWrapper = new QueryWrapper<>();
        BusProcSet busProcSet1 = new BusProcSet();
        busProcSet1.setIBusFunctionId(Integer.parseInt(functionId));
        busProcSet1.setIVersion(Integer.parseInt(iFunVersion));
        queryWrapper.setEntity(busProcSet1);
        busProcSet1 = iBusProcSetService.getOne(queryWrapper);
        List<BusPageDetail> busPageDetailList = new ArrayList<BusPageDetail>();
        if (busProcSet1.getIPageId() == null) {
            busPageDetailList = ibusPageDetailService.getListByFunID(functionId);
            Map<String, Object> pageMap = oaBusdataMapper.getPageUrlDao(functionId);
            ;
            result.put("pageRef", pageMap.get("pageRef"));
            result.put("actShow", pageMap.get("actShow"));
        } else { // 不为空的话
            String iPageId = busProcSet1.getIPageId() + "";
            busPageDetailList = ibusPageDetailService.getListByFunIDAndIPageId(functionId, iPageId);
            BusPage busPage = iBusPageService.getById(iPageId);
            result.put("pageRef", busPage.getSPagePath());
            result.put("actShow", busPage.getActShow());
        }
        result.put("busPageDetailList", busPageDetailList);

        Map<String, Object> optionMap = new HashMap<>();
        busPageDetailList.forEach(entry -> {

            //如果配置字典的话，就将数据查出来放到下拉框中，然后放到map
            if (entry.getSDictId() != null && !"".equals(entry.getSDictId())) {
                List<DictModel> dictMOdelList = sysDictService.getDictByCode(entry.getSDictId());
                optionMap.put(entry.getSTableColumn() + "_option", dictMOdelList);
            }
            //如果字典sql中有值的话，就先查出sql，再执行sql取查
            else if (entry.getSDictSqlKey() != null && !"".equals(entry.getSDictSqlKey())) {
                SysDictItem itemByCode = sysDictService.getDictItemByCode("sql", entry.getSDictSqlKey());
                if (itemByCode != null || !"".equals(itemByCode)) {
                    List<DictModel> dictMOdelList = sysDictService.getSqlValue(itemByCode.getDescription(),
                            loginInfo.getId(), loginInfo.getDepart().getId(), loginInfo.getDepart().getParentId());
                    optionMap.put(entry.getSTableColumn() + "_option", dictMOdelList);
                }
            }
        });
        result.put("optionMap", optionMap);

        //流程相关
        activiti(oaBusdata, busProcSet1, status, taskId, loginInfo, tableName,functionId, result);


        return result;
    }


    public void activiti(Map<String, Object> oaBusdata, BusProcSet busProcSet1,
                         String status, String taskId, LoginInfo loginInfo,
                         String tableName,String functionId,
                         Map<String, Object> result) {
        String busdataId = oaBusdata.get("i_id") == null ? null : oaBusdata.get("i_id") + "";
        String userId = loginInfo.getId();
        String endTime = (String) oaBusdata.get("s_varchar9");
        //********************************************************************* 流程信息查询
        //读取该流程的第一个环节
        String proKey = busProcSet1.getProcDefKey();
        String processInstanceId = null;
        String processDefinitionId = null;
        String taskDef = null;
        String executionId = null;
        String taskDefName = null;
        String parentTaskId = null;
        if (StringUtils.isBlank(proKey)) {//没有流程
            result.put("optionTable", null);
        } else {//有流程
            processInstanceId = oaBusdata.get("s_varchar10") == null ? null : oaBusdata.get("s_varchar10").toString();
            String taskDefData = oaBusdata.get("s_cur_task_name") == null ? null : oaBusdata.get("s_cur_task_name").toString();
            if (StringUtils.isNotBlank(status) && status.equalsIgnoreCase("newtask")
                    && (taskDefData == null || (taskDefData != null && taskDefData.contains("newtask")))) {
                //新建
                ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionKey(proKey).latestVersion().singleResult();
                if (processDefinition == null) throw new AIOAException("未找到流程信息请检查流程是否部署");
                ProcessDefinitionEntity proc = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinition.getId());
                List<ActivityImpl> activities = proc.getActivities();
                if (activities.size() == 0) throw new AIOAException("未找到流程环节,请检查流程图是否合法");
                //获取开始事件的后一个节点
                for (ActivityImpl activity : activities) {
                    String type = (String) activity.getProperty("type");
                    if ("startevent".equalsIgnoreCase(type)) {
                        List<PvmTransition> outgoingTransitions = activity.getOutgoingTransitions();
                        if (outgoingTransitions.size() < 0) throw new AIOAException("开始环节后没有连线,请参照手册修改流程图");
                        PvmActivity destination = outgoingTransitions.get(0).getDestination();
                        if (destination == null) throw new AIOAException("开始环节后没有连接环节,请参照手册修改流程图");
                        ActivityBehavior activityBehavior = ((ActivityImpl) destination).getActivityBehavior();
                        if (activityBehavior instanceof UserTaskActivityBehavior) {
                            taskDef = destination.getId();
                            taskDefName = (String) destination.getProperty("name");
                            break;
                        } else {
                            throw new AIOAException("第一环节类型有误,请参照手册修改流程图");
                        }

                    }
                }

            } else {
                //已经产生流程
                Task task = null;
                HistoricTaskInstance historicTaskInstance = null;
                if (taskId != null) {//从待办已办等进来
                    task = taskService.createTaskQuery().processInstanceBusinessKey(proKey)
                            .taskId(taskId).singleResult();
                    if (task != null && StringUtils.isBlank(status)) status = "todo";
                    if (task == null) {
                        historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
                        if (historicTaskInstance != null && StringUtils.isBlank(status)) status = "done";
                    }

                } else {//从业务页面进来


                    if (StringUtils.isBlank(endTime)) {

                        TaskQuery taskQuery = taskService.createTaskQuery().processDefinitionKey(proKey)
                                .processInstanceBusinessKey(busdataId).taskCandidateOrAssigned(userId);
                        if (StringUtils.isNotBlank(processInstanceId)) taskQuery.processInstanceId(processInstanceId);


                        List<Task> list = taskQuery.list();
                        if (list.size() > 0) {
                            //最新待办
                            task = list.get(list.size() - 1);
                            if (task != null && StringUtils.isBlank(status)) status = "todo";

                        } else {
                            //最新已办\
                            HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery().processDefinitionKey(proKey)
                                    .processInstanceBusinessKey(busdataId).taskAssignee(userId);
                            if (StringUtils.isNotBlank(processInstanceId))
                                historicTaskInstanceQuery.processInstanceId(processInstanceId);

                            List<HistoricTaskInstance> hiList = historicTaskInstanceQuery.list();
                            if (hiList.size() > 0) {
                                historicTaskInstance = hiList.get(hiList.size() - 1);
                                if (historicTaskInstance != null && StringUtils.isBlank(status)) status = "done";
                            }
                        }

                    }

                }
                if (task != null) {
                    taskId = task.getId();
                    processInstanceId = task.getProcessInstanceId();
                    processDefinitionId = task.getProcessDefinitionId();
                    taskDef = task.getTaskDefinitionKey();
                    executionId = task.getExecutionId();
                    taskDefName = task.getName();
                    parentTaskId = task.getParentTaskId();
                } else if (historicTaskInstance != null) {
                    taskId = historicTaskInstance.getId();
                    processInstanceId = historicTaskInstance.getProcessInstanceId();
                    processDefinitionId = historicTaskInstance.getProcessDefinitionId();
                    taskDef = historicTaskInstance.getTaskDefinitionKey();
                    executionId = historicTaskInstance.getExecutionId();
                    taskDefName = historicTaskInstance.getName();
                    parentTaskId = historicTaskInstance.getParentTaskId();
                } else {
                    HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery().processDefinitionKey(proKey)
                            .processInstanceBusinessKey(busdataId);
                    if (StringUtils.isNotBlank(processInstanceId))
                        historicTaskInstanceQuery.processInstanceId(processInstanceId);

                    List<HistoricTaskInstance> hiList = historicTaskInstanceQuery.list();
                    if (hiList.size() > 0) {
                        historicTaskInstance = hiList.get(hiList.size() - 1);
                        //不是待办 也不是已办
                        status = null;
                        taskId = historicTaskInstance.getId();
                        processInstanceId = historicTaskInstance.getProcessInstanceId();
                        processDefinitionId = historicTaskInstance.getProcessDefinitionId();
                        taskDef = historicTaskInstance.getTaskDefinitionKey();
                        executionId = historicTaskInstance.getExecutionId();
                        taskDefName = historicTaskInstance.getName();
                        parentTaskId = historicTaskInstance.getParentTaskId();
                    }
                }
            }
        }


        String optionTable = tableName + "_opinion";
        Integer busProcSetIId = busProcSet1.getIId();
        result.put("proSetId", busProcSetIId);
        if (org.apache.commons.lang.StringUtils.isNotBlank(endTime)) taskDef = "end";
        result.put("taskDefKey", taskDef);
        result.put("optionTable", optionTable);
        result.put("taskId", taskId);
        result.put("processDefinitionId", processDefinitionId);
        result.put("processInstanceId", processInstanceId);
        result.put("taskDefName", taskDefName);
        result.put("status", status);
        result.put("executionId", executionId);
        result.put("table", tableName);
        result.put("functionId", functionId);


        //******************************************   更新状态为已读
        //只是待办状态时修改
//        if (org.apache.commons.lang.StringUtils.isNotBlank(status) && "todo".equalsIgnoreCase(status)) {
//            iBusFunctionPermitService.updateReade(tableName + "_permit", loginInfo.getId(), functionId, busdataId);
//        }

        //******************************************   按钮/意见
//        Map<String, Boolean> currentUserPermission =
//                buttonPermissionService.currentUserPermission(proKey, oaBusdata, loginInfo,
//                        taskDef, proInstanId, taskId, status);
//
//
//        Map<String, Object> btnAndOpt = ButtonPermissionService.getBtnAndOpt(result, currentUserPermission);
//        result.put("btnAndOpt", btnAndOpt);


//        Set<Integer> deptOptTypes = new HashSet<>();
//        //自己是部门类型
//        if (org.apache.commons.lang.StringUtils.isNotBlank(processInstanceId) && org.apache.commons.lang.StringUtils.isNotBlank(taskId) && org.apache.commons.lang.StringUtils.isNotBlank(taskDef)) {
//            List<String> types = departWithTaskMapper.selectMyType(processInstanceId, taskId, taskDef, userId);
//            types.stream().forEach(i -> {
//                if (org.apache.commons.lang.StringUtils.isNotBlank(i) && i.contains("主办")) deptOptTypes.add(8);
//                if (org.apache.commons.lang.StringUtils.isNotBlank(i) && i.contains("辅办")) deptOptTypes.add(9);
//                if (org.apache.commons.lang.StringUtils.isNotBlank(i) && i.contains("传阅")) deptOptTypes.add(10);
//            });
//
//        }
//        HistoricTaskInstance historicTaskInstance2 = null;
//        if (parentTaskId != null) {
//            historicTaskInstance2 = historyService.createHistoricTaskInstanceQuery().taskId(parentTaskId).singleResult();
//        }
//        //父任务是部门类型
//        if (org.apache.commons.lang.StringUtils.isNotBlank(processInstanceId) && org.apache.commons.lang.StringUtils.isNotBlank(parentTaskId) && historicTaskInstance2 != null) {
//
//            if (historicTaskInstance2.getAssignee() != null) {
//                List<String> types = departWithTaskMapper.selectMyParentType(processInstanceId, parentTaskId, historicTaskInstance2.getAssignee());
//                types.stream().forEach(i -> {
//                    if (org.apache.commons.lang.StringUtils.isNotBlank(i) && i.contains("主办")) deptOptTypes.add(8);
//                    if (org.apache.commons.lang.StringUtils.isNotBlank(i) && i.contains("辅办")) deptOptTypes.add(9);
//                    if (org.apache.commons.lang.StringUtils.isNotBlank(i) && i.contains("传阅")) deptOptTypes.add(10);
//                });
//            }
//        }
//
//        //查询是主板/辅办/传阅
//        result.put("deptOptTypes", deptOptTypes);


    }
}
