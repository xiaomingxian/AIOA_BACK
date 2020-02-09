package com.cfcc.modules.oaBus.service.impl;

import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.oaBus.entity.BusProcSet;
import com.cfcc.modules.oaBus.entity.ButtonPermit;
import com.cfcc.modules.oaBus.entity.OaBusdata;
import com.cfcc.modules.oaBus.mapper.OaBusDynamicTableMapper;
import com.cfcc.modules.oaBus.service.ButtonPermissionService;
import com.cfcc.modules.oaBus.service.IBusModelService;
import com.cfcc.modules.oaBus.service.IBusProcSetService;
import com.cfcc.modules.oabutton.entity.OaButton;
import com.cfcc.modules.oabutton.entity.OaButtonSet;
import com.cfcc.modules.oabutton.service.IOaButtonService;
import com.cfcc.modules.oabutton.service.IOaButtonSetService;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.workflow.mapper.TaskMapper;
import com.cfcc.modules.workflow.service.IoaProcActinstService;
import com.cfcc.modules.workflow.service.TaskCommonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@Transactional
@Slf4j
public class ButtonPermissionServiceImpl implements ButtonPermissionService {

    @Autowired
    private IoaProcActinstService procActinstService;

    @Autowired
    private IBusModelService iBusModelService;

    @Autowired
    private IBusProcSetService iBusProcSetService;

    @Autowired
    private IOaButtonService iOaButtonService;

    @Autowired
    private IOaButtonSetService iOaButtonSetService;

    @Autowired
    private OaBusDynamicTableMapper dynamicTableMapper;

    @Autowired
    private TaskCommonService taskCommonService;

    @Autowired
    private TaskMapper taskMapper;


    @Override
    public Map<String, Object> getBtnAndOpt(Map<String, Object> result) {
        HashMap<String, Object> data = new HashMap<>();
        String schema = MycatSchema.getSchema();
        // 获取Redis中缓存的表数据
        List<BusProcSet> busProcSets = iBusProcSetService.findList(schema);
        List<BusModel> busModels = iBusModelService.findList(schema);
        List<OaButtonSet> oaButtonSets = iOaButtonSetService.findList(schema);
        List<OaButton> oaButtons = iOaButtonService.findList();
        // 获取当前业务需要的数据
        BusProcSet busProcSet = null;
        BusModel busModel = null;
        if (busProcSets != null) {
            for (BusProcSet one : busProcSets) {
                if (one.getIId().equals(result.get("proSetId"))) {
                    busProcSet = one;
                    break;
                }
            }
        }

        Map<String, List<ButtonPermit>> buttons = new HashMap<>();
        // 流程定义key--开启任务使用
        String procDefKey = null;
        if (busProcSet != null) {
            procDefKey = StringUtils.isBlank(busProcSet.getProcDefKey()) ? null : busProcSet.getProcDefKey();
            data.put("key", procDefKey);

            if (busModels != null) {
                for (BusModel one : busModels) {
                    if (one.getIId().equals(busProcSet.getIBusModelId())) {
                        busModel = one;
                        break;
                    }
                }
            }
            if (busModel != null) {
                // 将要显示的按钮分类
                buttons.put("isDefend", new ArrayList<ButtonPermit>());
                buttons.put("isNotDefend", new ArrayList<ButtonPermit>());
                // 将要显示的按钮id拼接成的字符串（去重用）
                String buttonids = ",";
                if (oaButtonSets != null && oaButtons != null) {
                    // 当前用户权限
                    Map<String, Object> query = new HashMap<>();
                    query.put("table", busModel.getSBusdataTable());
                    query.put("i_id", result.get("busdataId"));
                    Map<String, Object> oaBusdata = (Map<String, Object>) result.get("oaBusdata");
                    //判断流程是新建的而且自己是拟稿人，就认为是待办
                    LoginInfo loginInfo = (LoginInfo) result.get("loginInfo");
                    Boolean isDaiBan = null;
                    Object status = result.get("status");
                    if (status != null ) {
                        String s = status.toString();
                        if ("todo".equalsIgnoreCase(s)) isDaiBan = true;
                        if ("newtask".equalsIgnoreCase(s)) isDaiBan = true;
                        if ("done".equalsIgnoreCase(s)) isDaiBan = false;
                    }


                    Map<String, Boolean> currentUserPermission = currentUserPermission(procDefKey, query, loginInfo,
                            (String) result.get("taskDefKey"), (String) result.get("proInstanId"), (String) result.get("taskId"),
                            isDaiBan);
                    // 选出需要显示的按钮
                    for (OaButtonSet one : oaButtonSets) {
                        // 去除非本业务的按钮
                        if (!one.getIProcButtonId().equals(busProcSet.getIProcButtonId())) {
                            continue;
                        }
                        // 按钮去重
                        if (buttonids.contains("," + one.getIButtonId().toString() + ",")) {
                            continue;
                        }
                        // 获取按钮信息
                        ButtonPermit buttonPermit = new ButtonPermit();
                        for (OaButton two : oaButtons) {
                            if (!two.getIId().equals(one.getIButtonId())) {
                                continue;
                            }
                            BeanUtils.copyProperties(two, buttonPermit);
                            buttonPermit.setPermitType(one.getIPermitType() == 0 ? false : true);
                            buttonPermit.setIsCreate(one.getIIsCreater() == 0 ? false : true);
                            buttonPermit.setIsReader(one.getIIsReader() == 0 ? false : true);
                            buttonPermit.setIsLastsender(one.getIIsLastsender() == 0 ? false : true);
                            buttonPermit.setIsTransactors(one.getIIsTransactors() == 0 ? false : true);
                            buttonPermit.setSRoles(one.getSRoles());
                            break;
                        }
                        // 维护角色过滤按钮
                        if (!rolePermission(buttonPermit.getSRoles(), ((LoginInfo) result.get("loginInfo")).getRoleIds())) {
                            // 流程环节过滤
                            if (StringUtils.isNotBlank(one.getProcDefKey()) && !one.getTaskDefKey().equals(result.get("taskDefKey"))) {
                                continue;
                            }
                            // 用户权限过滤按钮
                            if (!havePermission(buttonPermit, currentUserPermission)) {
                                continue;
                            }
                            // 互斥状态字段过滤按钮
                            if (!statePermission(buttonPermit.getSExcfield(), (Map<String, Object>) result.get("oaBusdata"))) {
                                continue;
                            }
                        }
                        // 是否基础操作按钮拆分
                        if (buttonPermit.getIIsBase() == null ? true : buttonPermit.getIIsBase()) {
                            buttons.get("isDefend").add(buttonPermit);
                        } else {
                            buttons.get("isNotDefend").add(buttonPermit);
                        }
                        buttonids += buttonPermit.getIId().toString() + ",";
                    }
                }
            }

        }

        data.put("btn", buttons);

        // 查询意见
        if (busProcSet != null) {
            String opt = busProcSet.getIProcOpinionId() == null ? null : busProcSet.getIProcOpinionId().toString();
            List<Map<String, Object>> optList = dynamicTableMapper.queryOptions(opt, busProcSet.getIId().toString(), procDefKey,
                    (String) result.get("taskDefKey"), (String) result.get("optionTable"), (String) result.get("busdataId"));
            data.put("opt", optList);
        } else {
            log.error("===================>>>>流程配置信息为空,可能是redis数据错误");
            data.put("opt", new ArrayList<>());
        }


        return data;
    }


    @Override
    public Map<String, Object> getBtn(Map<String, Object> result) {
        HashMap<String, Object> data = new HashMap<>();
        // 获取Redis中缓存的表数据
        String schema = MycatSchema.getSchema();
        List<BusProcSet> busProcSets = iBusProcSetService.findList(schema);
        List<BusModel> busModels = iBusModelService.findList(schema);
        List<OaButtonSet> oaButtonSets = iOaButtonSetService.findList(schema);
        List<OaButton> oaButtons = iOaButtonService.findList();
        // 获取当前业务需要的数据
        BusProcSet busProcSet = null;
        BusModel busModel = null;
        if (busProcSets != null) {
            for (BusProcSet one : busProcSets) {
                if (one.getIId().equals(result.get("proSetId"))) {
                    busProcSet = one;
                    break;
                }
            }
        }

        Map<String, List<ButtonPermit>> buttons = new HashMap<>();
        // 流程定义key--开启任务使用
        String procDefKey = null;
        if (busProcSet != null) {
            procDefKey = StringUtils.isBlank(busProcSet.getProcDefKey()) ? null : busProcSet.getProcDefKey();
            data.put("key", procDefKey);

            if (busModels != null) {
                for (BusModel one : busModels) {
                    if (one.getIId().equals(busProcSet.getIBusModelId())) {
                        busModel = one;
                        break;
                    }
                }
            }
            if (busModel != null) {
                // 将要显示的按钮分类
                buttons.put("isDefend", new ArrayList<ButtonPermit>());
                buttons.put("isNotDefend", new ArrayList<ButtonPermit>());
                // 将要显示的按钮id拼接成的字符串（去重用）
                String buttonids = ",";
                if (oaButtonSets != null && oaButtons != null) {
                    // 当前用户权限
                    Map<String, Object> query = new HashMap<>();
                    query.put("table", busModel.getSBusdataTable());
                    query.put("i_id", result.get("busdataId"));
                    Map<String, Object> oaBusdata = (Map<String, Object>) result.get("oaBusdata");
                    //判断流程是新建的而且自己是拟稿人，就认为是待办
                    //Integer iIsDisplay = oaBusdata.getIIsDisplay();
                    //String sCreateBy = oaBusdata.getSCreateBy();
                    LoginInfo loginInfo = (LoginInfo) result.get("loginInfo");
                    String id = loginInfo.getId();
                    Integer iIsDisplay = oaBusdata.get("i_is_display") == null ? null : Integer.parseInt(oaBusdata.get("i_is_display").toString());
                    String sCreateBy = oaBusdata.get("s_create_by") == null ? "" : oaBusdata.get("s_create_by").toString();
                    boolean isDaiBan = false;
                    if (iIsDisplay != null && iIsDisplay == 1 && id.equals(sCreateBy)) {
                        isDaiBan = true;
                    }


                    Map<String, Boolean> currentUserPermission = currentUserPermission(procDefKey, query, loginInfo,
                            (String) result.get("taskDefKey"), (String) result.get("proInstanId"), (String) result.get("taskId"), isDaiBan);
                    // 选出需要显示的按钮
                    for (OaButtonSet one : oaButtonSets) {
                        // 去除非本业务的按钮
                        if (!one.getIProcButtonId().equals(busProcSet.getIProcButtonId())) {
                            continue;
                        }
                        // 按钮去重
                        if (buttonids.contains("," + one.getIButtonId().toString() + ",")) {
                            continue;
                        }
                        // 获取按钮信息
                        ButtonPermit buttonPermit = new ButtonPermit();
                        for (OaButton two : oaButtons) {
                            if (!two.getIId().equals(one.getIButtonId())) {
                                continue;
                            }
                            BeanUtils.copyProperties(two, buttonPermit);
                            buttonPermit.setPermitType(one.getIPermitType() == 0 ? false : true);
                            buttonPermit.setIsCreate(one.getIIsCreater() == 0 ? false : true);
                            buttonPermit.setIsReader(one.getIIsReader() == 0 ? false : true);
                            buttonPermit.setIsLastsender(one.getIIsLastsender() == 0 ? false : true);
                            buttonPermit.setIsTransactors(one.getIIsTransactors() == 0 ? false : true);
                            buttonPermit.setSRoles(one.getSRoles());
                            break;
                        }
                        // 维护角色过滤按钮
                        if (!rolePermission(buttonPermit.getSRoles(), ((LoginInfo) result.get("loginInfo")).getRoleIds())) {
                            // 流程环节过滤
                            if (StringUtils.isNotBlank(one.getProcDefKey()) && !one.getTaskDefKey().equals(result.get("taskDefKey"))) {
                                continue;
                            }
                            // 用户权限过滤按钮
                            if (!havePermission(buttonPermit, currentUserPermission)) {
                                continue;
                            }
                            // 互斥状态字段过滤按钮
                            if (!statePermission(buttonPermit.getSExcfield(), (Map<String, Object>) result.get("oaBusdata"))) {
                                continue;
                            }
                        }
                        // 是否基础操作按钮拆分
                        if (buttonPermit.getIIsBase() == null ? true : buttonPermit.getIIsBase()) {
                            buttons.get("isDefend").add(buttonPermit);
                        } else {
                            buttons.get("isNotDefend").add(buttonPermit);
                        }
                        buttonids += buttonPermit.getIId().toString() + ",";
                    }
                }
            }

        }

        data.put("btn", buttons);

        return data;
    }

    /**
     * taskDefKey
     * optionTable
     * busdataId
     * oaBusdata
     * proInstanId
     * taskId
     * loginInfo
     * proSetId
     *
     * @param result
     * @return
     */
    @Override
    public Map<String, Object> getOpt(Map<String, Object> result) {
        String schema = MycatSchema.getSchema();
        HashMap<String, Object> data = new HashMap<>();
        // 获取Redis中缓存的表数据
        List<BusProcSet> busProcSets = iBusProcSetService.findList(schema);
        List<BusModel> busModels = iBusModelService.findList(schema);
        List<OaButtonSet> oaButtonSets = iOaButtonSetService.findList(schema);
        List<OaButton> oaButtons = iOaButtonService.findList();
        // 获取当前业务需要的数据
        BusProcSet busProcSet = null;
        BusModel busModel = null;
        if (busProcSets != null) {
            for (BusProcSet one : busProcSets) {
                if (one.getIId().equals(result.get("proSetId"))) {
                    busProcSet = one;
                    break;
                }
            }
        }

        Map<String, List<ButtonPermit>> buttons = new HashMap<>();
        // 流程定义key--开启任务使用
        String procDefKey = null;
        if (busProcSet != null) {
            procDefKey = StringUtils.isBlank(busProcSet.getProcDefKey()) ? null : busProcSet.getProcDefKey();
            data.put("key", procDefKey);

            if (busModels != null) {
                for (BusModel one : busModels) {
                    if (one.getIId().equals(busProcSet.getIBusModelId())) {
                        busModel = one;
                        break;
                    }
                }
            }
            if (busModel != null) {
                // 将要显示的按钮分类
                buttons.put("isDefend", new ArrayList<ButtonPermit>());
                buttons.put("isNotDefend", new ArrayList<ButtonPermit>());
                // 将要显示的按钮id拼接成的字符串（去重用）
                String buttonids = ",";
                if (oaButtonSets != null && oaButtons != null) {
                    // 当前用户权限
                    Map<String, Object> query = new HashMap<>();
                    query.put("table", busModel.getSBusdataTable());
                    query.put("i_id", result.get("busdataId"));
                    Map<String, Object> oaBusdata = (Map<String, Object>) result.get("oaBusdata");
                    //判断流程是新建的而且自己是拟稿人，就认为是待办
                    //Integer iIsDisplay = oaBusdata.getIIsDisplay();
                    //String sCreateBy = oaBusdata.getSCreateBy();
                    LoginInfo loginInfo = (LoginInfo) result.get("loginInfo");
                    String id = loginInfo.getId();
                    //刚进来认为是待办
                    Integer iIsDisplay = oaBusdata.get("i_is_display") == null ? null : Integer.parseInt(oaBusdata.get("i_is_display").toString());
                    String sCreateBy = oaBusdata.get("s_create_by") == null ? "" : oaBusdata.get("s_create_by").toString();
                    Boolean isDaiBan = null;
                    if (iIsDisplay != null && iIsDisplay == 1 && id.equals(sCreateBy)) {
                        isDaiBan = true;
                    }
                    if (!isDaiBan) {
                        //看是不是已办
                        String status = result.get("status") == null ? "" : result.get("status").toString();
                        if (StringUtils.isBlank(status)) {
                            isDaiBan = null;
                        }
                        //前端选择的环节性质
                        if (status.equalsIgnoreCase("todo")) isDaiBan = true;
                        if (status.equalsIgnoreCase("done")) isDaiBan = false;
                    }


                    Map<String, Boolean> currentUserPermission = currentUserPermission(procDefKey, query, loginInfo,
                            (String) result.get("taskDefKey"), (String) result.get("proInstanId"),
                            (String) result.get("taskId"), isDaiBan);
                    // 选出需要显示的按钮
                    for (OaButtonSet one : oaButtonSets) {
                        // 去除非本业务的按钮
                        if (!one.getIProcButtonId().equals(busProcSet.getIProcButtonId())) {
                            continue;
                        }
                        // 按钮去重
                        if (buttonids.contains("," + one.getIButtonId().toString() + ",")) {
                            continue;
                        }
                        // 获取按钮信息
                        ButtonPermit buttonPermit = new ButtonPermit();
                        for (OaButton two : oaButtons) {
                            if (!two.getIId().equals(one.getIButtonId())) {
                                continue;
                            }
                            BeanUtils.copyProperties(two, buttonPermit);
                            buttonPermit.setPermitType(one.getIPermitType() == 0 ? false : true);
                            buttonPermit.setIsCreate(one.getIIsCreater() == 0 ? false : true);
                            buttonPermit.setIsReader(one.getIIsReader() == 0 ? false : true);
                            buttonPermit.setIsLastsender(one.getIIsLastsender() == 0 ? false : true);
                            buttonPermit.setIsTransactors(one.getIIsTransactors() == 0 ? false : true);
                            buttonPermit.setSRoles(one.getSRoles());
                            break;
                        }
                        // 维护角色过滤按钮
                        if (!rolePermission(buttonPermit.getSRoles(), ((LoginInfo) result.get("loginInfo")).getRoleIds())) {
                            // 流程环节过滤
                            if (StringUtils.isNotBlank(one.getProcDefKey()) && !one.getTaskDefKey().equals(result.get("taskDefKey"))) {
                                continue;
                            }
                            // 用户权限过滤按钮
                            if (!havePermission(buttonPermit, currentUserPermission)) {
                                continue;
                            }
                            // 互斥状态字段过滤按钮
                            if (!statePermission(buttonPermit.getSExcfield(), (Map<String, Object>) result.get("oaBusdata"))) {
                                continue;
                            }
                        }
                        // 是否基础操作按钮拆分
                        if (buttonPermit.getIIsBase() == null ? true : buttonPermit.getIIsBase()) {
                            buttons.get("isDefend").add(buttonPermit);
                        } else {
                            buttons.get("isNotDefend").add(buttonPermit);
                        }
                        buttonids += buttonPermit.getIId().toString() + ",";
                    }
                }
            }

        }

        //data.put("btn", buttons);

        // 查询意见
        String opt = busProcSet.getIProcOpinionId() == null ? null : busProcSet.getIProcOpinionId().toString();
        List<Map<String, Object>> optList = dynamicTableMapper.queryOptions(opt, busProcSet.getIId().toString(), procDefKey,
                (String) result.get("taskDefKey"), (String) result.get("optionTable"), (String) result.get("busdataId"));
        data.put("opt", optList);

        return data;
    }

    /**
     * 用户是否拥有可以查看按钮的角色
     */
    private boolean rolePermission(String btnRoles, List<String> userRoleIds) {
        if (StringUtils.isNotBlank(btnRoles) && userRoleIds != null) {
            for (String userRoleId : userRoleIds) {
                if (StringUtils.isBlank(userRoleId)) {
                    continue;
                }
                if (StringUtils.equals(btnRoles, userRoleId) || btnRoles.startsWith(userRoleId + ",") || btnRoles.endsWith("," + userRoleId) || btnRoles.contains("," + userRoleId + ",")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 用户是否拥有可以查看按钮的角色
     */
    private boolean statePermission(String sExcfield, Map<String, Object> oaBusdata) {
        if (StringUtils.isBlank(sExcfield)) {
            return true;
        }
        String[] strs = sExcfield.split("=");
        if (strs.length > 1 && strs[1].equals(String.valueOf(oaBusdata.get(strs[0])))) {
            return true;
        }
        return false;
    }

    /**
     * 某个按钮是否符合相关权限
     */
    @Override
    public boolean havePermission(ButtonPermit buttonPermit, Map<String, Boolean> currentUserPermission) {

        //是否有部门完成权限[即使不控制也要校验](没有就直接false,有就在进行以下判断)
        String sBtnValue = buttonPermit.getSBtnValue();
        if (null != sBtnValue && sBtnValue.equals("deptFinish")) {//如果按钮要求部门完成
            //但是用户没有完成部门权限
            if (!currentUserPermission.get("deptFinsh")) return false;
        }


        Boolean permitType = buttonPermit.getPermitType();
        if (permitType == null || !permitType) {//如果不区分权限,直接安返回true,否则进行下面一系列判断
            return true;
        }


        //判断是否符合任意一个条件
        Boolean isCreate = buttonPermit.getIsCreate();
        Boolean isReader = buttonPermit.getIsReader();
        Boolean isLastsender = buttonPermit.getIsLastsender();
        Boolean isTransactors = buttonPermit.getIsTransactors();

        if (null != isCreate && isCreate && currentUserPermission.get("isCreate")) return true;
        if (null != isReader && isReader && currentUserPermission.get("isReader")) return true;
        if (null != isLastsender && isLastsender && currentUserPermission.get("isLastsender")) return true;
        if (null != isTransactors && isTransactors && currentUserPermission.get("isTransactors")) return true;


        return false;
    }



    /**
     * 当前用户所拥有的权限
     */
    @Override
    public Map<String, Boolean> currentUserPermission(String key, Map<String, Object> query,
                                                      SysUser currentUser,
                                                      String taskDefKey, String proInstanId,
                                                      String taskId,
                                                      Boolean isDaiBan) {

        String busDataId = query.get("i_id") + "";
        String tableName = query.get("table") + "";

        String userId = currentUser.getId();
        Map<String, Object> busData = dynamicTableMapper.queryDataById(query);
        //当前用户权限
        HashMap<String, Boolean> currentUserPermission = new HashMap<>();
        String createBy = busData.get("s_create_by") == null ? "" : busData.get("s_create_by") + "";
        //创建者
        currentUserPermission.put("isCreate", createBy.equals(userId));
        List<String> reader = dynamicTableMapper.isReader(busDataId, tableName + "_permit");
        //参与者
        currentUserPermission.put("isReader", reader.contains(userId));
        //TODO 追加
        //已办用户(有流程)
        boolean isLastsender = false;
        boolean isTransactors = false;//代办
        Boolean deptFinsh = false;//部门完成
        if (taskDefKey != null) {
            //查询是流程已办(已办与待办不可同时出现)
            isLastsender = taskCommonService.isLastsender(userId, proInstanId, key, busDataId);
            if (isDaiBan == null) {
                //如果前台没有传入 已办/状态 (有待办就以待办为准)
                isTransactors = taskCommonService.isTransactors(userId, proInstanId, key, busDataId);
                if (isLastsender && isTransactors) {
                    isTransactors = true;
                    isLastsender = false;
                }
            }else {
                if (isDaiBan){
                    isTransactors = true;
                    isLastsender = false;
                }else {
                    isTransactors = false;
                }
            }
            //判断是否是可部门完成环节
            //查询当前节点是否是可部门完成的节点[如果当前环节不是那就不能部门完成]
            deptFinsh = procActinstService.isDeptFinish(key, taskDefKey);
            deptFinsh = deptFinsh == null ? false : deptFinsh;
            //查询当前用户是否是部门办理参与人
            if (deptFinsh) {
                //查询部门类型的任务记录 看当前用户是否是参与者
                boolean b = taskMapper.isDeptTaskUser(proInstanId, taskId, currentUser.getId());
                deptFinsh = deptFinsh && b;
            }
        }
        currentUserPermission.put("isLastsender", isLastsender);
        currentUserPermission.put("isTransactors", isTransactors);
        currentUserPermission.put("deptFinsh", deptFinsh);
        return currentUserPermission;
    }

    @Autowired
    private HistoryService historyService;

    @Override
    public boolean haveSavePermission(Object proKey1, HashMap<String, Object> map, LoginInfo loginInfo,
                                      String taskDef, String taskId, boolean isDaiBan) {


        String processInstanceId = null;

        Object iProcButtonId = map.get("iProcButtonId");
        map.remove("iProcButtonId");

        if (proKey1 != null && taskId != null) {
            HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            processInstanceId = historicTaskInstance.getProcessInstanceId();
        }


        Map<String, Boolean> permission = currentUserPermission(proKey1.toString(), map, loginInfo, taskDef,
                processInstanceId, taskId, isDaiBan);

        List<ButtonPermit> btnList = dynamicTableMapper.querySaveButton(proKey1.toString(), taskDef, iProcButtonId.toString());


        if (btnList.size() <= 0) return false;
        ButtonPermit buttonPermit = btnList.get(0);

        boolean havePermission = havePermission(buttonPermit, permission);

        return havePermission;
    }

}
