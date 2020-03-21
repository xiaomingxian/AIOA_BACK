package com.cfcc.modules.oaBus.service.impl;

import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.oaBus.entity.BusProcSet;
import com.cfcc.modules.oaBus.entity.ButtonPermit;
import com.cfcc.modules.oaBus.mapper.OaBusDynamicTableMapper;
import com.cfcc.modules.oaBus.service.ButtonPermissionService;
import com.cfcc.modules.oaBus.service.IBusModelService;
import com.cfcc.modules.oaBus.service.IBusProcSetService;
import com.cfcc.modules.oabutton.entity.OaButton;
import com.cfcc.modules.oabutton.entity.OaButtonSet;
import com.cfcc.modules.oabutton.service.IOaButtonService;
import com.cfcc.modules.oabutton.service.IOaButtonSetService;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysRole;
import com.cfcc.modules.workflow.mapper.TaskMapper;
import com.cfcc.modules.workflow.service.IoaProcActinstService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private TaskMapper taskMapper;

    @Value("${canAddUserRoles}")
    private String canAddUserRoles;


    @Override
    public Map<String, Object> getBtnAndOpt(Map<String, Object> result, Map<String, Boolean> currentUserPermission) {


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
        boolean isShow = false;
        String[] excfields = sExcfield.split(";");
        if (excfields.length > 0) {
            for (int i = 0; i < excfields.length; i++) {
                String[] strs = excfields[i].split("=");
                if (strs.length > 1 && strs[1].equals(String.valueOf(oaBusdata.get(strs[0])))) {
                    if (i == excfields.length - 1) {
                        isShow = true;
                    }
                } else {
                    break;
                }
            }
        }
        return isShow;
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
        //追加
        if ("addUserOrDepart".equals(buttonPermit.getSMethod()) && currentUserPermission.get("canAddUser")) return true;


        return false;
    }


    /**
     * 当前用户所拥有的权限
     */
    @Override
    public Map<String, Boolean> currentUserPermission(String key, Map<String, Object> busData,
                                                      LoginInfo currentUser,
                                                      String taskDefKey, String proInstanId,
                                                      String taskId,
                                                      String status) {

        String userId = currentUser.getId();
        String busdataId = busData.get("i_id").toString();
        //当前用户权限
        HashMap<String, Boolean> currentUserPermission = new HashMap<>();
        String createBy = busData.get("s_create_by") == null ? "" : busData.get("s_create_by") + "";
        //创建者
        currentUserPermission.put("isCreate", createBy.equals(currentUser.getId()));
//        List<String> reader = dynamicTableMapper.isReader(busdataId, busData.get("table") + "_permit");

        //已办用户(有流程)
        boolean isLastsender = false;
        boolean isTransactors = false;//代办
        boolean isReader = false;//代办
        Boolean deptFinsh = false;//部门完成
        if (taskDefKey != null) {
            //查询是流程已办(已办与待办不可同时出现)
            if (StringUtils.isNotBlank(status) && status.equalsIgnoreCase("todo")) isTransactors = true;
            if (StringUtils.isNotBlank(status) && status.equalsIgnoreCase("newtask")) isTransactors = true;
            if (StringUtils.isNotBlank(status) && status.equalsIgnoreCase("done")) isLastsender = true;
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
        //是否可追加
        boolean canAddUser = false;
        boolean havaAddRole = false;
        if (canAddUserRoles != null) {
            List<SysRole> roles = currentUser.getRoles();
            for (SysRole role : roles) {
                String roleName = role.getRoleName();
                if (canAddUserRoles.contains(roleName)) {
                    havaAddRole = true;
                    break;
                }
            }
        }

        if (isLastsender || havaAddRole) {//是已办或者
            canAddUser = true;
        }

        if (!isLastsender&&!isTransactors)isReader=true;
        //参与者
        currentUserPermission.put("isReader", isReader);
        currentUserPermission.put("isLastsender", isLastsender);
        currentUserPermission.put("isTransactors", isTransactors);
        currentUserPermission.put("deptFinsh", deptFinsh);
        currentUserPermission.put("canAddUser", canAddUser);

        return currentUserPermission;
    }

    @Override
    public List<Map<String, Object>> reloadOpinionList(Map<String, Object> map) {
        String proSetId = map.get("proSetId") + "";
        String taskDefKey = map.get("taskDefKey") + "";
        String opinionTable = map.get("opinionTable") + "";
        String busdataId = map.get("busdataId") + "";
        Map<String, Object> procsetParam = new HashMap<>();
        procsetParam.put("table", "oa_bus_proc_set");
        procsetParam.put("i_id", proSetId);
        Map<String, Object> procsetData = dynamicTableMapper.queryDataById(procsetParam);
        String procDefKey = procsetData.get("PROC_DEF_KEY_") + "";
        String opt = procsetData.get("i_proc_opinion_id") + "";
        List<Map<String, Object>> maps = dynamicTableMapper.queryOptions(opt, proSetId, procDefKey, taskDefKey, opinionTable, busdataId);
        return maps;
    }


}
