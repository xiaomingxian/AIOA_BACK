package com.cfcc.modules.oaBus.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.exception.AIOAException;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.common.system.vo.DictModel;
import com.cfcc.common.util.RedisUtil;
import com.cfcc.common.util.StringUtil;
import com.cfcc.modules.oaBus.entity.*;
import com.cfcc.modules.oaBus.mapper.BusFunctionMapper;
import com.cfcc.modules.oaBus.mapper.OaBusDynamicTableMapper;
import com.cfcc.modules.oaBus.mapper.OaBusdataMapper;
import com.cfcc.modules.oaBus.service.*;
import com.cfcc.modules.system.entity.*;
import com.cfcc.modules.system.mapper.SysDictItemMapper;
import com.cfcc.modules.system.service.ISysDepartService;
import com.cfcc.modules.system.service.ISysDictService;
import com.cfcc.modules.system.service.ISysUserRoleService;
import com.cfcc.modules.system.service.ISysUserService;
import com.cfcc.modules.workflow.mapper.DepartWithTaskMapper;
import com.cfcc.modules.workflow.pojo.Activity;
import com.cfcc.modules.workflow.pojo.TaskWithDepts;
import com.cfcc.modules.workflow.service.ProcessManagerService;
import com.cfcc.modules.workflow.service.TaskCommonService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 业务数据表
 * @Author: jeecg-boot
 * @Date: 2019-10-21
 * @Version: V1.0
 */
@Slf4j
@Service
public class OaBusdataServiceImpl extends ServiceImpl<OaBusdataMapper, OaBusdata> implements IOaBusdataService {

    @Autowired
    OaBusdataMapper oaBusdataMapper;
    @Autowired
    private IBusFunctionService busFunctionService;
    @Autowired
    private IBusPageDetailService ibusPageDetailService;
    @Autowired
    private IBusModelService ibusModelService;
    @Autowired
    private IBusFunctionViewService ibusFunctionViewService;
    @Autowired
    private ISysDepartService sysDepartService;
    @Autowired
    private ISysUserRoleService sysUserRoleService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IBusProcSetService iBusProcSetService;
    @Autowired
    private OaBusDynamicTableService dynamicTableService;
    @Autowired
    private ISysDictService sysDictService;
    @Resource
    private BusFunctionMapper functionMapper;
    @Resource
    private SysDictItemMapper sysDictItemMapper;
    @Resource
    private IBusFunctionUnitService iBusFunctionUnitService;
    @Autowired
    private ProcessManagerService processManagerService;
    @Autowired
    private ButtonPermissionService ButtonPermissionService;

    @Autowired
    private IBusFunctionPermitService iBusFunctionPermitService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private IBusPageService iBusPageService;
    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ButtonPermissionService buttonPermissionService;

    @Autowired
    private DepartWithTaskMapper departWithTaskMapper;

    @Autowired
    private OaBusDynamicTableMapper dynamicTableMapper;

    //private Logger logger = LoggerFactory.getLogger(OaBusdataServiceImpl.class);
    @Override
    public IPage<OaBusdata> getBusdataPage(Integer pageNo, Integer pageSize, OaBusdata oaBusdata) {
        int total = oaBusdataMapper.queryBusdataCount(oaBusdata);
        List<OaBusdata> busdataList = oaBusdataMapper.queryBusdata((pageNo - 1) * pageSize, pageSize, oaBusdata);
        IPage<OaBusdata> pageList = new Page<OaBusdata>();
        pageList.setRecords(busdataList);
        pageList.setTotal(total);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;
    }

    @Override
    public OaBusdata getBusdataById(Integer iId) {
        return oaBusdataMapper.getBusdataByIdDao(iId);
    }


    /**
     * 获取表中查询出列的名字和注释，然后返回，如果已经在detail表中定义，则将该数据移除
     *
     * @param iPageId
     * @param functionId
     * @return
     */
    @Override
    public IPage<TableCol> getTableColList(int iPageId, int modelId, int functionId) {
        BusPageDetail busPageDetail = new BusPageDetail();
        busPageDetail.setIBusPageId(iPageId);
        busPageDetail.setIBusFunctionId(functionId);
        QueryWrapper<BusPageDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(busPageDetail);
        //查询出已经定义好的functionId的list数据
        List<BusPageDetail> list = ibusPageDetailService.list(queryWrapper);
        //没有定义原型,则根据modelId查询出表名
        String tableName = oaBusdataMapper.queryTabeleNameByModelId(modelId);
        List<TableCol> busdataList = oaBusdataMapper.getTableColListDao(tableName);
        Map<String, String> map = list.stream()
                .collect(Collectors.toMap(
                        BusPageDetail::getSTableColumn
                        , BusPageDetail::getSColumnName
                        , (entry1, entry2) -> entry2));
        //进行处理，如果list里有值的话就将busdatalist中的值去除，
        List<TableCol> busPageDetails = busdataList.stream()
                .filter(dtail -> !(map.containsKey(dtail.getSTableColumn())))
                .collect(Collectors.toList());
        IPage<TableCol> pageList = new Page<TableCol>();
        pageList.setRecords(busPageDetails);
        log.info(busPageDetails.toString());
        return pageList;
    }

    @Override
    public List<Map> selectByTable(String table, String cols) {
        return oaBusdataMapper.selectByTable(table, cols);
    }

    @Override
    public Map<String, Object> getSqlCodeDictAllSelect(List<BusPageDetail> busPageDetailList) {
        Map<String, Object> optionMap = new TreeMap<>();
        Map<String, String> map = new TreeMap<>();
        return selOptionByDtailList(optionMap, map, busPageDetailList, null);
    }

    /**
     * {i_id=598, i_bus_function_id=64, s_title=会计师；了不懂法, s_create_name=管理员, s_create_dept=科技科,
     * d_create_time=2019-12-27 18:03:38.0, s_remarks=哈哈哈, table_name=oa_busdata30, i_safetylevel=1}
     *
     * @param oaFileList
     */


    @Override
    public void updateIsES(List<Map<String, Object>> oaFileList, String DBvalue) {
        for (Map<String, Object> map : oaFileList) {
            //System.out.println("****************");
            String id = map.get("i_id").toString();
            Object table_name = map.get("table_name");
            //System.out.println(table_name);
            //System.out.println(map);
            String tableName = map.get("table_name").toString();

            Integer count = oaBusdataMapper.updateIsESByid(tableName, id, DBvalue);
            while (count != 1) {
                count = oaBusdataMapper.updateIsESByid(tableName, id, DBvalue);
            }
        }
    }

    /**
     * 根据modelId和functionId以及相应的权限查出对应的busdata数据列表
     *
     * @param json
     * @param realName
     * @param username
     * @return
     * @throws Exception
     */
    @Override
    public Result<IPage<Map<String, Object>>> getByModelId(String json, String realName, String username) {
        log.info(json);
        Result<IPage<Map<String, Object>>> result = new Result<>();
        Map maps = (Map) JSONObject.parse(json);

        int pageNo = 1;
        if (maps.containsKey("pageNo")) {
            pageNo = (int) maps.get("pageNo");
        }
        int pageSize = 10;
        if (maps.containsKey("pageSize")) {
            pageSize = (int) maps.get("pageSize");
        }
        Page<Map<String, Object>> page = new Page<>(pageNo, pageSize);
        String modelId = maps.get("modelId") + "";
        QueryWrapper<BusModel> queryWrapper = new QueryWrapper<BusModel>();
        BusModel busModel = new BusModel();
        busModel.setIId(Integer.parseInt(modelId));
        queryWrapper.setEntity(busModel);
        busModel = ibusModelService.getOne(queryWrapper);

        Map<String, Object> condition = (Map<String, Object>) maps.get("condition");
        String schema = MycatSchema.getSchema();
        Optional<Integer> optional;
        int functionId = 0;
        if (condition.containsKey("function_id")) {
            String funcId = condition.get("function_id") + "";
            if ((funcId != null) && !("".equals(funcId))) {
                optional = Optional.of(Integer.parseInt(funcId));     //业务id
            } else {
                optional = busFunctionService.findList(schema).stream()
                        .filter(busFunction -> modelId.equals(busFunction.getIBusModelId().toString()))
                        .map(BusFunction::getIId)
                        .findFirst();
            }
            if (!optional.isPresent()) {
                result.setMessage("function_id 不能为空！");
                return result;
            }
            functionId = optional.get();
            condition.put("function_id", functionId + "");
            //System.out.println("fuctionId:" + functionId);
        } else {
            result.setMessage("查询条件中必须包含 function_id！！");
            return result;
        }
        String orderFlag = "";     //排序标志
        if (condition.containsKey("orderFlag")) {
            orderFlag = condition.get("orderFlag") + "";
            log.info(orderFlag);
            condition.remove("orderFlag");
        }
        BusFunction busFunction = busFunctionService.getById(functionId);
        List<Map<String, String>> listColumns = ibusPageDetailService.getConColums(functionId, busFunction.getIPageId());//查询出对应的列数据
        if (listColumns.size() == 0 || listColumns == null) {
            result.setMessage("查出列为空！");
            return result;
        }
        StringBuffer column = new StringBuffer("");
        for (Map<String, String> map : listColumns) {
            column.append(map.get("s_table_column") + ",");
        }
        String col = column.substring(0, column.length() - 1);
        String tableName = busModel.getSBusdataTable();

        if (!col.contains("i_id")) {
            col = "i_id," + col;
        }

        //处理是否为查询 由我创建
        String selType = "1";       //默认为1
        if (condition.containsKey("selType")) {
            selType = condition.get("selType") + "";
            condition.remove("selType");
        }
        if ("0".equals(selType)) {      // 0 查询为由我创建
            int count = oaBusdataMapper.getBusdataByCreateNameCount(tableName, condition, realName);
            List<Map<String, Object>> dataList = oaBusdataMapper.getBusdataByCreateName((pageNo - 1) * pageSize,
                    pageSize, col, tableName, condition, realName, orderFlag);
            page.setRecords(dataList);
            page.setTotal(count);
            result.setResult(page);

        } else {
            Map<String, String> permitData = permit(functionId, tableName, username);       //根据权限，查询出对应的查询条件
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
            //System.out.println(dataList);
            page.setRecords(dataList);
            page.setTotal(total);
            result.setResult(page);
        }


        String tableHead = JSON.toJSON(listColumns).toString();
        //System.out.println("tableHead:" + tableHead);
        Map<String, Object> mapHead = new HashMap<>();
        mapHead.put("tableHead", tableHead);
        result.setMessage(tableHead);
        return result;
    }


    @Override
    public Map<String, Object> getBusDataById(String tableName, Integer tableId) {

        return oaBusdataMapper.getBusdataMapByIdDao(tableName, tableId.toString());
    }

    @Override
    public Boolean updateApproveById(String sTable, Integer iTableId) {

        return oaBusdataMapper.updateApproveById(sTable, iTableId);
    }

    /**
     * 查询到要查询的条件
     *
     * @param funcId
     * @return
     */
    @Override
    public List<Map<String, Object>> getQueryCondition(int funcId) {
        return oaBusdataMapper.getQueryCOnditionDao(funcId);
    }

    /**
     * 获取对model对应的function数据
     *
     * @param modelId
     * @return
     */
    @Override
    public List<Map<String, Object>> getSelFun(String modelId) {

        return oaBusdataMapper.getSelFunDao(modelId);
    }


    /**
     * 查询出对应的业务数据，并且将流程意见对应的中间数据查询出来
     *
     * @return
     */
    @Override
    public Map<String, Object> getBusDataAndDetailById(Map<String, Object> param, LoginInfo loginInfo) {

        System.out.println("===================>>>>查详情开始<<<<==========================");
        long l1 = System.currentTimeMillis();
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
        oaBusdata.put("table", tableName);
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
            Map<String, Object> pageMap = getPageUrlSer(functionId);
            result.put("pageRef", pageMap.get("pageRef"));
            result.put("actShow", pageMap.get("actShow"));
        } else { // 不为空的话
            String iPageId = busProcSet1.getIPageId() + "";
            busPageDetailList = ibusPageDetailService.getListByFunIDAndIPageId(functionId, iPageId);
            BusPage busPage = iBusPageService.getById(iPageId);
            result.put("pageRef", busPage.getSPagePath());
            result.put("actShow", busPage.getActShow());
        }
        Map<String, String> map = new HashMap<>();
        Map<String, Object> optionMap = new HashMap<>();
        //存放校验规则,字典数据，detail数据
        long aaa = System.currentTimeMillis();
        optionMap = selOptionByDtailList(optionMap, map, busPageDetailList, loginInfo.getDepart().getId());
        result.put("optionMap", optionMap);
        log.info(map.toString());
        long bbb = System.currentTimeMillis();
        System.out.println("查询配置用时：" + (bbb - aaa));
        result.put("detailList", map);
        // 业务function信息
        BusFunction busFunction = busFunctionService.getOneByFunId(functionId);
        result.put("busFunction", busFunction);
        // 获取富文本内容
        Map<String, Object> busText = functionMapper.getEdtorText(tableName, oaBusdata.get("i_id") + "");
        result.put("busTextData", busText);

        SysUser user = loginInfo;


        if (busProcSet1 == null) {
            throw new AIOAException("数据版本与设置版本不一致请检查版本");
        }


        long l2 = System.currentTimeMillis();
        String userId = user.getId();

        System.out.println("================>>>业务详情查询时间::" + (l2 - l1));
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
                    && (taskDefData == null || (taskDefData != null && taskDefData.contains("newtask")))) {//新建
                ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionKey(proKey).latestVersion().singleResult();
                if (processDefinition == null) throw new AIOAException("未找到流程信息请检查流程是否部署");
                ProcessDefinitionEntity proc = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinition.getId());
                List<ActivityImpl> activities = proc.getActivities();
                if (activities.size() == 0) throw new AIOAException("未找到流程环节,请检查流程图是否合法");
                taskDef = activities.get(0).getId();
            } else {//已经产生流程
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
        result.put("taskDefKey", taskDef);
        result.put("optionTable", optionTable);
        result.put("taskId", taskId);
        result.put("processDefinitionId", processDefinitionId);
        result.put("processInstanceId", processInstanceId);
        result.put("taskDefName", taskDefName);
        result.put("status", status);
        result.put("table", tableName);
        result.put("executionId", executionId);
        result.put("functionId", functionId);


        //******************************************   更新状态为已读
        //只是待办状态时修改
        if (StringUtils.isNotBlank(status) && "todo".equalsIgnoreCase(status)) {
            iBusFunctionPermitService.updateReade(tableName + "_permit", loginInfo.getId(), functionId, busdataId);
        }
        long lstatus = System.currentTimeMillis();

        //******************************************   按钮/意见
        Map<String, Boolean> currentUserPermission =
                buttonPermissionService.currentUserPermission(proKey, oaBusdata, loginInfo,
                        taskDef, proInstanId, taskId, status);


        Map<String, Object> btnAndOpt = ButtonPermissionService.getBtnAndOpt(result, currentUserPermission);
        result.put("btnAndOpt", btnAndOpt);


        Set<Integer> deptOptTypes = new HashSet<>();
        //自己是部门类型
        if (StringUtils.isNotBlank(processInstanceId) && StringUtils.isNotBlank(taskId) && StringUtils.isNotBlank(taskDef)) {
            List<String> types = departWithTaskMapper.selectMyType(processInstanceId, taskId, taskDef, userId);
            types.stream().forEach(i -> {
                if (StringUtils.isNotBlank(i) && i.contains("主办")) deptOptTypes.add(8);
                if (StringUtils.isNotBlank(i) && i.contains("辅办")) deptOptTypes.add(9);
                if (StringUtils.isNotBlank(i) && i.contains("传阅")) deptOptTypes.add(10);
            });

        }
        HistoricTaskInstance historicTaskInstance2 = null;
        if (parentTaskId != null) {
            historicTaskInstance2 = historyService.createHistoricTaskInstanceQuery().taskId(parentTaskId).singleResult();
        }
        //父任务是部门类型
        if (StringUtils.isNotBlank(processInstanceId) && StringUtils.isNotBlank(parentTaskId) && historicTaskInstance2 != null) {

            if (historicTaskInstance2.getAssignee() != null) {
                List<String> types = departWithTaskMapper.selectMyParentType(processInstanceId, parentTaskId, historicTaskInstance2.getAssignee());
                types.stream().forEach(i -> {
                    if (StringUtils.isNotBlank(i) && i.contains("主办")) deptOptTypes.add(8);
                    if (StringUtils.isNotBlank(i) && i.contains("辅办")) deptOptTypes.add(9);
                    if (StringUtils.isNotBlank(i) && i.contains("传阅")) deptOptTypes.add(10);
                });
            }
        }

        //查询是主板/辅办/传阅
        result.put("deptOptTypes", deptOptTypes);


        // 查询意见
        if (busProcSet1 != null) {
            String opt = busProcSet1.getIProcOpinionId() == null ? null : busProcSet1.getIProcOpinionId().toString();
            List<Map<String, Object>> optList = dynamicTableMapper.queryOptions(opt, busProcSet1.getIId().toString(), proKey,
                    (String) result.get("taskDefKey"), (String) result.get("optionTable"), (String) result.get("busdataId"));
            btnAndOpt.put("opt", optList);
        } else {
            log.error("===================>>>>流程配置信息为空,可能是redis数据错误");
            result.put("opt", new ArrayList<>());
        }


        long l4 = System.currentTimeMillis();
        System.out.println("================>>>总共查询时间：" + (l4 - l1));


        return result;
    }


    @Override
    public Map<String, Object> getFuncitonDataById(String functionId) {
        Map<String, Object> map = oaBusdataMapper.queryFuncitonDataById(functionId);
        return map;
    }

    @Override
    public Map<String, Object> queryStateById(String stable, Integer tableid) {
        Map<String, Object> map = oaBusdataMapper.getBusdataById(stable, tableid);
        return map;
    }

    /**
     * 查询当前用户是否有查看数据的权限
     *
     * @param tableName
     * @param id
     * @param userName
     * @return
     */
    @Override
    public boolean checkBusDataSer(String tableName, String id, String userName) {
        boolean res = false;
        if (tableName == null || "".equals(tableName)) {
            log.info("查询失败：表名为空");
            return false;
        }
        if (id == null || "".equals(id)) {
            log.info("查询失败：id为空");
            return false;
        }
        Map<String, Object> oaBusdata = oaBusdataMapper.getBusdataMapByIdDao(tableName, id);
        if (null == oaBusdata) {
            // 数据不存在
            log.info("数据不存在可能已被删除");
            return false;
        }
        String functionId = oaBusdata.get("i_bus_function_id") + "";
        int funId = Integer.parseInt(functionId);
        Map<String, String> permitData = permit(funId, tableName, userName);
        String userId = permitData.get("userId");
        String userUnit = permitData.get("userUnit");
        String userDepart = permitData.get("userDepart");
        permitData.remove("userId");
        permitData.remove("userUnit");
        permitData.remove("userDepart");
       /* List<Map<String, Object>> dataList = oaBusdataMapper.getBusdataByMap((pageNo - 1) * pageSize,
                pageSize, col, tableName, condition, permitData, userId, userUnit, userDepart, orderFlag);
*/
        List<Map<String, Object>> list = oaBusdataMapper.getCheckData(tableName, functionId, permitData, userId, userUnit, userDepart);
        for (Map map : list) {
            String i_id = map.get("i_id") + "";
            if (id.equals(i_id)) {
                res = true;
                break;
            }
        }
        return res;
    }

    @Override
    public List<Map<String, Object>> getOaBusdataList(String columnLists, BusFunction busFunction, String DBvalue) {
        return oaBusdataMapper.getBusdataByTable(columnLists, busFunction, DBvalue);
    }


    /**
     * 查询出对应的下拉框数据
     * feng
     *
     * @param optionMap
     * @param map
     * @param busPageDetailList
     * @return
     */
    private Map<String, Object> selOptionByDtailList(Map<String, Object> optionMap,
                                                     Map<String, String> map,
                                                     List<BusPageDetail> busPageDetailList, String departId) {
        List<BusPageDetail> checkList = new ArrayList<>();
        //查询出对应的下拉列表数据
        long aaa = System.currentTimeMillis();
        busPageDetailList.forEach(entry -> {
            map.put(entry.getSTableColumn(), entry.getSColumnName());
            if (entry.getICheckIsNull() != null && entry.getICheckIsNull() == 1) {
                checkList.add(entry);
            }
            //如果配置字典的话，就将数据查出来放到下拉框中，然后放到map
            if (entry.getSDictId() != null && !"".equals(entry.getSDictId())) {
                List<DictModel> dictMOdelList = sysDictService.getDictByCode(entry.getSDictId());
                optionMap.put(entry.getSTableColumn() + "_option", dictMOdelList);
            }
            //如果字典sql中有值的话，就先查出sql，再执行sql取查
            else if (entry.getSDictSqlKey() != null && !"".equals(entry.getSDictSqlKey())) {
                SysDictItem itemByCode = sysDictService.getDictItemByCode("sql", entry.getSDictSqlKey());
                if (itemByCode != null || !"".equals(itemByCode)) {
                    List<DictModel> dictMOdelList = sysDictService.getSqlValue(itemByCode.getDescription());
                    optionMap.put(entry.getSTableColumn() + "_option", dictMOdelList);
                }
            }
        });
        long bbb = System.currentTimeMillis();
        System.out.println("AAAA查询配置用时：" + (bbb - aaa));
        aaa = System.currentTimeMillis();
       /* //查询秘密等级
        List<DictModel> secretDegreeList = sysDictService.getDictByCode("secretDegree");
        List<DictModel> regulars = sysDictService.getDescribeDictCode("regular_expressions");
        //查询对应的信息公开，字典数据
        List<Map<String, Object>> xxgk = sysDictService.getDictByKeySer("xxgk", departId);
        //查询对应的不公开理由，字典数据
        List<Map<String, Object>> bgkly = sysDictService.getDictByKeySer("bgkly", departId);
        //查询缓急
        List<Map<String, Object>> urgencyList = sysDictService.getDictByKeySer("urgencyDegree", departId);
        optionMap.put("secretDegree", secretDegreeList);
        //将数据校验规则取出
        optionMap.put("xxgk", xxgk);
        optionMap.put("urgencyList", urgencyList);
        optionMap.put("bgkly", bgkly);
        optionMap.put("regulars", regulars);*/
        optionMap.put("checkList", checkList);
        bbb = System.currentTimeMillis();
        System.out.println("AAAA查询其他配置用时：" + (bbb - aaa));
        return optionMap;
    }

    /**
     * 查询出用户对应的部门和机构
     *
     * @return
     */
    @Override
    public Map<String, Object> getBusDataUserDepartSer() {
        String userName = (String) redisUtil.get("userName");
        return oaBusdataMapper.getBusDataUserDepartDao(userName);
    }

    @Override
    public boolean updateMiddleById(String table, OaBusdata oaBusdata) {
        return oaBusdataMapper.updateMiddleById(table, oaBusdata);
    }

    /**
     * 根据权限，查询出对应的查询条件
     * 先查oa_bus_function_view这张表，根据i_function_id匹配查看权限类型 （1、角色、2、角色+机构3、角色+部门4、角色+分管部门）',
     * 1、先判断角色符不符合，只要当前角色符合的话就直接break，设置is1为true，说明有权看所有的数据
     *
     * @param funid
     * @return
     */
    public Map<String, String> permit(int funid, String tableName, String userName) {

        Map<String, String> result = new HashMap<>();
        List<String> deptList = new ArrayList<>();
        SysUser sysUser = sysUserService.getUserByName(userName);

        //用户对应的部门
        List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
        List<SysDepart> unitList = new ArrayList<>();
        String[] depts = new String[departs.size()];
        for (int i = 0; i < departs.size(); i++) {
            depts[i] = "'" + departs.get(i).getId() + "'";
        }
        departs.forEach(sysDepart -> {
            unitList.add(sysDepartService.getUnitByDeptId(sysDepart.getId()));
        });
        List<String> unitSet = unitList.stream().map(SysDepart::getId).distinct().collect(Collectors.toList());
        //unitSet.stream().forEach(s -> log.info(s));
        String[] units = new String[unitSet.size()];
        for (int i = 0; i < unitSet.size(); i++) {
            units[i] = "'" + unitSet.get(i) + "'";
        }
        String unit = String.join(",", units);     //机构对应的字符串，使用in查询
        result.put("userId", sysUser.getId());
        result.put("userUnit", unit);
        result.put("userDepart", String.join(",", depts));

        //用户的角色
        List<SysUserRole> userRole = sysUserRoleService.list(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, sysUser.getId()));
        String roleids = "";
        boolean is1 = false;
        boolean is2 = false;
        boolean is3 = false;
        boolean is4 = false;
        List<BusFunctionView> functionViewList = ibusFunctionViewService.getFunViewListByFunId(funid);
        for (BusFunctionView entry : functionViewList) {
            int type = entry.getIType();
            String subfunid = "";
            String funroleid = "";
            //如果角色id不在当前用户的角色里的话，就直接跳过本次循环，进行下次循环判断
            if (!isInRoleList(userRole, entry.getIRoleId())) {
                continue;
            }
            if (type == 1) {        //查看权限类型为角色，对比角色id是否包含在当前用户对应的角色中，如果有，则将is1设为true
                is1 = true;
                break;  //如果1类型角色匹配上的话，就不用再进行循环了，直接根据funcitonid去查去全部
            } else if (!is2) {
                if (type == 2) {
                    is2 = true;
                    continue;
                } else if (!is3 && type == 3) {
                    is3 = true;
                    departs.forEach(sysDepart -> deptList.add(sysDepart.getId()));
                    continue;
                } else if (!is4 && type == 4) {
                    is4 = true;
                    List<String> list1 = oaBusdataMapper.queryUserManagedepts(sysUser.getId());
                    list1.forEach(s -> deptList.add(s));
                    continue;
                }
            }
        }

        if (is1) {
            result.put("2", "2");
        } else {
            if (is2) {
                result.put("s_create_unitid", unit);
            } else if (is3 || is4) {
                for (int i = 0; i < deptList.size(); i++) {
                    deptList.set(i, "'" + deptList.get(i) + "'");
                }
                String str = String.join(",", deptList);
                result.put("s_create_deptid", str);
            }
            /*else{
                tableName = tableName + "_permit";
                String dataIdList = oaBusdataMapper.getDataIdfromPermit(funid,tableName,sysUser.getId(),unit);
                result.put("i_id",dataIdList) ;
            }*/
        }
        //System.out.println(result);
        return result;
    }


    public boolean isInRoleList(List<SysUserRole> list, String roleId) {
        /*for(SysUserRole sysUserRole : list){        //判断是否角色匹配
            if(sysUserRole.getRoleId().equals(roleId)){
                result = true;
                break;
            }
        }*/
        Optional<SysUserRole> userRole = list.stream().filter(sysUserRole -> sysUserRole.getRoleId().equals(roleId)).findFirst();
        log.info("查询出的userRole：{}", userRole.toString());
        return userRole.isPresent();
    }

    @Override
    public IPage<OaBusdata> selectDocList(OaBusdata oaBusdata, String sBusdataTable, Integer pageNo, Integer pageSize) {
        int total = oaBusdataMapper.getDocListTotal(oaBusdata, sBusdataTable);
        List<OaBusdata> list = oaBusdataMapper.selectDocList(oaBusdata, sBusdataTable, (pageNo - 1) * pageSize, pageSize);
        IPage<OaBusdata> pageList = new Page<OaBusdata>();
        pageList.setTotal(total);
        pageList.setCurrent(pageNo);
        pageList.setCurrent(pageSize);
        pageList.setRecords(list);
        return pageList;
    }

    /**
     * 查询出对应的页面路径地址
     *
     * @return
     */
    @Override
    public Map<String, Object> getPageUrlSer(String functionId) {

        return oaBusdataMapper.getPageUrlDao(functionId);
    }

    @Override
    public IPage<Map<String, Object>> selBusDataListByModelId(String modelId, String userName, Integer pageNo, Integer pageSize) {
        Page<Map<String, Object>> page = new Page<>(pageNo, pageSize);
        String schema = MycatSchema.getSchema();
        Optional<String> tableName = ibusModelService.findList(schema).stream()
                .filter(model -> modelId.equals(model.getIId() + ""))
                .map(BusModel::getSBusdataTable)
                .findFirst();


        return page;
    }


    @Override
    public Result queryDataByModelAndFunctionId(String modelId, String functionId, LoginInfo loginInfo) {

        Map<String, Object> result = new HashMap<>();

        BusModel busModel = ibusModelService.getById(Integer.parseInt(modelId));
        String tableName = busModel.getSBusdataTable();
        BusFunction busFunction = functionMapper.selectByIid(Integer.parseInt(functionId));
        if (busFunction == null) throw new AIOAException("此业务不存在，检查数据库是否为迁移数据");

        //使用busfunction 表中的iprocId查询出对应的数据，在查出他的版本号
        BusProcSet busProcSet = iBusProcSetService.getById(busFunction.getIProcSetId());
        if (busProcSet == null) return Result.error("无此类数据配置,检查数据库是否为迁移数据");
        int iVsersion = busProcSet.getIVersion() == null ? 1 : busProcSet.getIVersion();


        result.put("proSetId", busProcSet.getIId());

        Boolean have = functionMapper.haveProc(functionId);
        //读取该流程的第一个环节
        String proKey = null;
        Object proKey1 = busProcSet.getProcDefKey();
        if (proKey1 == null || (proKey1 != null && "".equals(proKey1))) {//没有流程
            proKey = null;
            result.put("taskDefKey", null);//新建任务
            result.put("optionTable", null);
        } else {//有流程
            proKey = proKey1 == null ? "" : proKey1 + "";
            List<Activity> list = processManagerService.actsListPic(null, proKey, true);
            if (list.size() <= 0) throw new AIOAException("未查到对应的流程信息,请检查是否为迁移数据");
            String taskDef = list.get(0).getId();
            taskDef = have == null || !have ? null : taskDef;
            result.put("taskDefKey", taskDef);//新建任务
            result.put("optionTable", busModel.getSBusdataTable() + "_opinion");
        }

        //如果版本号为空的话，则换为0，否则就原来的处理
        Map<String, Object> oaBusdata = insertAndSel(modelId, busFunction, proKey, loginInfo, iVsersion);
        Object i_id = oaBusdata.get("i_id");
        result.put("busdataId", i_id);
        result.put("tableName", tableName);

        return Result.ok(result);
    }

    /**
     * 根据modelId和functionId和当前用户，生成一条业务数据到数据库
     *
     * @param modelId
     * @param function
     * @param iVsersion function对应的字段
     * @return
     */
    private Map<String, Object> insertAndSel(String modelId, BusFunction function,
                                             String proKey, LoginInfo loginInfo, int iVsersion) {

        Map<String, Object> map = new HashMap<>();
        String tableName = ibusModelService.getBusModelById(Integer.parseInt(modelId)).getSBusdataTable();
        map.put("table", tableName);

        map.put("i_bus_model_id", modelId);
        map.put("i_is_display", "1");
        map.put("s_title", "");
        map.put("i_bus_function_id", function.getIId());
        map.put("i_safetylevel", 1);    //密级    默认为1  一般
        map.put("i_urgency", 4);        //缓急    默认为4  一般
        map.put("s_left_parameter", function.getSBusLeftParameter());

        if (function.getIIsDept() != null && function.getIIsDept() == 1) {     //插入部门
            String departName = loginInfo.getDepart().getDepartName();
            map.put("s_dept_name", departName);
        }
        if (function.getIIsUnit() != null && function.getIIsUnit() == 1) {     //插入机构
            SysDepart depart = loginInfo.getDepart();
            SysDepart unit = sysDepartService.getUnitByDeptId(depart.getId());
            map.put("s_unit_name", unit.getDepartName());
        }
        map.put("s_right_parameter", function.getSBusRightParameter());
        map.put("i_bus_model_id", modelId);
        LocalDate nowDate = LocalDate.now();
        map.put("i_create_year", nowDate.getYear());
        map.put("i_create_month", nowDate.getMonthValue());
        map.put("i_create_day", nowDate.getDayOfMonth());
        map.put("s_create_name", loginInfo.getRealname());
        map.put("s_create_by", loginInfo.getId());
        if (loginInfo.getOrgCode() != null) {
            SysDepart sysDepart = sysDepartService.queryUserDepartByOrgCode(loginInfo.getOrgCode());
            SysDepart unit = sysDepartService.getUnitByDeptId(sysDepart.getId() + "");
            map.put("s_create_dept", sysDepart.getDepartName());
            map.put("s_create_deptid", sysDepart.getId());
            map.put("s_create_unitid", unit.getId());
        }

        LocalDateTime dateTime = LocalDateTime.now();
        map.put("d_create_time", dateTime);
        map.put("i_fun_version", iVsersion);    //新建时将function对应的流程按钮版本号插入
        map.put("s_cur_proc_name", proKey);//当前流程名称
        map.put("s_cur_task_name", "newtask-新建任务");//当前任务名称
        Map<String, Object> mapHaveId = dynamicTableService.insertData(map);
        mapHaveId.put("i_id", mapHaveId.get("i_id"));
        //mapHaveId.remove("i_id");
        //log.info(mapHaveId.toString());
        return mapHaveId;
    }

    @Override
    public String queryTableName(Integer modelId) {
        String tableName = oaBusdataMapper.queryTabeleNameByModelId(modelId);
        return tableName;
    }

    @Override
    public boolean commitPer(String json, SysUser currentUser) {
        Map map = (Map) JSONObject.parse(json);
        String busdataId = map.get("busdataId") + "";
        String departId = map.get("departId") + "";
        String iIsLimits = map.get("iIsLimits") + "";
        String tableName = map.get("tableName") + "_permit";
        String userRealName = map.get("userRealName") + "";
        String functionId = map.get("functionId") + "";
        if ("1".equals(iIsLimits)) {
            //插入全行
            SysDepart sysDepart = sysDepartService.queryUserDepartByOrgCode(currentUser.getOrgCode());
            //全行的机构数据
            SysDepart maxUnitByDeptId = sysDepartService.getMaxUnitByDeptId(sysDepart.getId());
            /*BusdataPermit busdataPermit = new BusdataPermit() ;
            busdataPermit.set*/
            Map<String, Object> mapPermit = new HashMap<>();
            mapPermit.put("table", tableName);
            mapPermit.put("i_bus_function_id", functionId);
            mapPermit.put("i_busdata_id", busdataId);
            mapPermit.put("s_userunit_id", maxUnitByDeptId.getId());
            dynamicTableService.insertData(mapPermit);
        } else if ("2".equals(iIsLimits)) {    //插入部门数据
            String depts[] = departId.split(",");
            for (int i = 0; i < depts.length; i++) {
                SysDepart unitByDept = sysDepartService.getUnitByDeptId(depts[i]);
                Map<String, Object> mapPermit = new HashMap<>();
                mapPermit.put("table", tableName);
                mapPermit.put("i_bus_function_id", functionId);
                mapPermit.put("i_busdata_id", busdataId);
                mapPermit.put("s_userunit_id", unitByDept.getId());
                mapPermit.put("s_userdept_id", depts[i]);
                dynamicTableService.insertData(mapPermit);
            }
        } else if ("3".equals(iIsLimits)) {
            //插入用户数据
            String userName[] = userRealName.split(",");
            for (int i = 0; i < userName.length; i++) {
                SysUser sysUser = sysUserService.getUserByName(userName[i]);
                //SysDepart sysDepart = sysDepartService.queryUserDepartByOrgCode(sysUser.getOrgCode());
                List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
                //全行的机构数据
                departs.forEach(dept -> {
                    SysDepart unitByDept = sysDepartService.getUnitByDeptId(dept.getId());
                    Map<String, Object> mapPermit = new HashMap<>();
                    mapPermit.put("table", tableName);
                    mapPermit.put("i_bus_function_id", functionId);
                    mapPermit.put("i_busdata_id", busdataId);
                    mapPermit.put("s_userunit_id", unitByDept.getId());
                    mapPermit.put("s_userdept_id", dept.getId());
                    mapPermit.put("s_user_id", sysUser.getId());
                    dynamicTableService.insertData(mapPermit);
                });

            }
        }
        return true;
    }

    //查询某一条具体业务数据
    @Override
    public List<Map<String, Object>> getModifyFieldDataOne(String column, String tableName, Integer iid) {
        return oaBusdataMapper.getModifyFieldDataOne(column, tableName, iid);
    }

   /* @Override
    public Map<String, Object> getBusDataById(String tableName, String tableId) {
        return oaBusdataMapper.getBusdataById(tableName,tableId);
    }*/

    //   --通过去重获取业务数据表中任务环节列表
    @Override
    public List<String> getTaskNameList(String tableName) {
        List<String> taskNameList = oaBusdataMapper.getTaskNameList(tableName);
        return taskNameList;
    }

    @Override
    public int updateAllOaData(Map<String, Object> map) {
        return oaBusdataMapper.updateAllOaData(map);
    }

    /**
     * 折叠列表-------------通过任务环节获取------------------------
     * 通过model_id，查询，返回map
     *
     * @param
     * @param json
     * @param userName -----------------无分页--------------------
     * @return
     */
    @Override
    public Result<Map<String, Object>> getByModelIdAndTaskName(String json, String userName) throws Exception {
        Map<String, Object> page = new TreeMap<>();
        Result<Map<String, Object>> result = new Result<>();
        Map maps = (Map) JSONObject.parse(json);
        //任务环节
        String taskName = (String) maps.get("taskName");
        String modelId = maps.get("modelId") + "";
        QueryWrapper<BusModel> queryWrapper = new QueryWrapper<BusModel>();
        BusModel busModel = new BusModel();
        busModel.setIId(Integer.parseInt(modelId));
        queryWrapper.setEntity(busModel);
        busModel = ibusModelService.getOne(queryWrapper);
        String schema = MycatSchema.getSchema();

        Map<String, Object> condition = (Map<String, Object>) maps.get("condition");

        Optional<Integer> optional;
        int functionId = 0;
        if (condition.containsKey("function_id")) {
            String funcId = condition.get("function_id") + "";
            if ((funcId != null) && !("".equals(funcId))) {
                optional = Optional.of(Integer.parseInt(funcId));     //业务id
            } else {
                optional = busFunctionService.findList(schema).stream()
                        .filter(busFunction -> modelId.equals(busFunction.getIBusModelId().toString()))
                        .map(BusFunction::getIId)
                        .findFirst();
            }
            if (!optional.isPresent()) {
                result.setMessage("function_id 不能为空！");
                return result;
            }
            functionId = optional.get();
            condition.put("function_id", functionId + "");
        } else {
            result.setMessage("查询条件中必须包含 function_id！！");
            return result;
        }

        String orderFlag = "";     //排序标志
        if (condition.containsKey("orderFlag")) {
            orderFlag = condition.get("orderFlag") + "";
            log.info(orderFlag);
            condition.remove("orderFlag");
        }

        BusFunction busFunction = busFunctionService.getById(functionId);
        List<Map<String, String>> listColumns = ibusPageDetailService.getConColums(functionId, busFunction.getIPageId());//查询出对应的列数据
        if (listColumns.size() == 0 || listColumns == null) {
            result.setMessage("查出列为空！");
            return result;
        }
        StringBuffer column = new StringBuffer("");
        for (Map<String, String> map : listColumns) {
            column.append(map.get("s_table_column") + ",");
        }
        String col = column.substring(0, column.length() - 1);
        String tableName = busModel.getSBusdataTable();

        if (!col.contains("i_id")) {
            col = "i_id," + col;
        }

        //处理是否为查询 由我创建
        String selType = "1";
        if (condition.containsKey("selType")) {
            selType = condition.get("selType") + "";
            condition.remove("selType");
        }
        if ("0".equals(selType)) {//判断是否需要权限
            List<Map<String, Object>> dataList = oaBusdataMapper.getBusdataByCreateNameNoPages(
                    col, tableName, taskName, condition, userName, orderFlag);
            page.put("dataList", dataList);
            result.setResult(page);
        } else {
            Map<String, String> permitData = permit(functionId, tableName, userName);       //根据权限，查询出对应的查询条件
            String userId = permitData.get("userId");
            String userUnit = permitData.get("userUnit");
            String userDepart = permitData.get("userDepart");
            permitData.remove("userId");
            permitData.remove("userUnit");
            permitData.remove("userDepart");
            List<Map<String, Object>> dataList = oaBusdataMapper.getBusdataByMapNoPages(
                    col, tableName, taskName, condition, permitData, userId, userUnit, userDepart, orderFlag);
            page.put("dataList", dataList);
            result.setResult(page);
        }
        String tableHead = JSON.toJSON(listColumns).toString();
        Map<String, Object> mapHead = new HashMap<>();
        mapHead.put("tableHead", tableHead);
        result.setMessage(tableHead);
        return result;
    }

    //运维工具列表-无权限 全部查询业务数据表
    @Override
    public IPage<Map<String, Object>> getModifyFieldList(int pageNo, int pageSize, String column, String tableName, Map<String, Object> map) {
        map.remove("selType");
        int total = oaBusdataMapper.getModifyFieldListTotals(tableName, map);
        List<Map<String, Object>> busdataList = oaBusdataMapper.getModifyFieldList((pageNo - 1) * pageSize, pageSize, column, tableName, map);
        IPage<Map<String, Object>> pageList = new Page<Map<String, Object>>();
        pageList.setRecords(busdataList);
        pageList.setTotal(total);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;
    }

    @Override
    public String getBusdataValueByIdAndFiled(String stable, Integer tableid, String sTableColumn) {
        String value = oaBusdataMapper.queryValue(stable, tableid, sTableColumn);
        return value;
    }

    @Override
    public String getDictText(String sDictId, String itemValue) {
        String iSafetyLevelText = sysDictItemMapper.queryItemTextByDicIdAndValue(sDictId, itemValue);
        return iSafetyLevelText;
    }


    /**
     * 根据业务机构过滤对应的funList ，有些fun只能特定的部门能看到
     * feng
     *
     * @param busFunctionList
     * @param depart
     * @return
     */
    @Override
    public List<BusFunction> getFunListByFunUnit(List<BusFunction> busFunctionList, SysDepart depart) {
        List<String> strList = new ArrayList<>();

        List<BusFunction> list1 = busFunctionList.stream().filter(busFunction -> {
            QueryWrapper<BusFunctionUnit> wrapper = new QueryWrapper<>();
            BusFunctionUnit functionUnit = new BusFunctionUnit();
            functionUnit.setIBusFunctionId(busFunction.getIId());
            wrapper.setEntity(functionUnit);
            List<BusFunctionUnit> busFunctionUnitList = iBusFunctionUnitService.list(wrapper);
            if (busFunctionUnitList != null && busFunctionUnitList.size() > 0) {
                for (BusFunctionUnit funUnit : busFunctionUnitList) {
                    if ((funUnit.getSDeptId() != null && funUnit.getSDeptId().equals(depart.getId()))
                            || (funUnit.getSUnitId().equals(depart.getParentId()) && funUnit.getSDeptId() == null)) {
                        return true;
                    } else {
                        List<String> unitList = sysDepartService.getUnitList(depart.getParentId());
                        for (String str : unitList) {
                            if (funUnit.getSUnitId().equals(str)) {
                                return true;
                            }
                        }
                        return false;
                    }
                }

            }
            return true;

        }).collect(Collectors.toList());


        /*busFunctionList.forEach(entry -> {
            QueryWrapper<BusFunctionUnit> wrapper = new QueryWrapper<>();
            BusFunctionUnit functionUnit = new BusFunctionUnit();
            functionUnit.setIBusFunctionId(entry.getIId());
            wrapper.setEntity(functionUnit);
            List<BusFunctionUnit> busFunctionUnitList = iBusFunctionUnitService.list(wrapper);
            if (busFunctionUnitList != null && busFunctionUnitList.size() > 0) {
                busFunctionUnitList.forEach(funUnit -> {
                    if (funUnit.getSDeptId() == depart.getId()
                            || (funUnit.getSUnitId() == depart.getParentId() && funUnit.getSDeptId() == null)) {
                        strList.add(funUnit.getIBusFunctionId() + "");
                    }
                });
            }
        });
        Iterator<BusFunction> iterator = busFunctionList.iterator();
        while (iterator.hasNext()) {
            boolean b = false;
            String str = iterator.next().getIId() + "";
            for (int i = 0; i < strList.size(); i++) {
                if (str.equals(strList.get(i))) {
                    b = true;
                    break;
                }
            }
            if (b) {
                iterator.remove();
            }
        }*/
        return list1;
    }


    //按表名，条件获取条数
    @Override
    public int listCountBytableName(Map<String, Object> map) {
        return oaBusdataMapper.listCountBytableName(map);
    }
}
