package com.cfcc.modules.oaBus.controller;


import com.cfcc.common.api.vo.Result;
import com.cfcc.common.constant.workflow.TaskConstant;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.common.util.workflow.VarsWithBus;
import com.cfcc.modules.oaBus.entity.*;
import com.cfcc.modules.oaBus.service.*;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.service.ISysDepartService;
import com.cfcc.modules.system.service.ISysUserService;
import com.cfcc.modules.utils.IWfConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Api(tags = "新建任务")
@Slf4j
@RestController
@RequestMapping("/oaBus/newTask")
public class NewTaskController {


    @Autowired
    private OaBusDynamicTableService dynamicTableService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysDepartService sysDepartService;

    @Autowired
    private IBusModelService iBusModelService;

    @Autowired
    private IBusFunctionService iBusFunctionService;

    @Autowired
    private IBusModelPermitService iBusModelPermitService;

    @Autowired
    private IBusFunctionPermitService iBusFunctionPermitService;

    @Autowired
    private IoaCalendarService ioaCalendarService;



    //
    ///**
    // * 一级下拉选
    // * 二级下拉选走function的接口
    // */
    //@GetMapping(value = "/findModelPermit")
    //public Result findModelPermit(HttpServletRequest request) {
    //
    //    try {
    //        String token = request.getHeader("X-Access-Token");
    //        String username = JwtUtil.getUsername(token);
    //        List<Map<String, Object>> msgs = sysUserService.getCurrentUserMsg(username);
    //
    //        //查询当前用户的 角色,部门信息//遍历(数据不会太多-一般一条数据)
    //        //去重
    //        HashSet<BusModel> busModelsAll = new HashSet<>();
    //        msgs.stream().forEach(msg -> {
    //            Object uId = msg.get("uId");
    //            msg.get("uName");
    //            Object rId = msg.get("rId");
    //            msg.get("rName");
    //            Object dId = msg.get("dId");
    //            msg.get("dName");
    //            //查询所有人都可见的model
    //            List<BusModel> busModels = iBusModelService.allPeopleSee();
    //            busModelsAll.addAll(busModels);
    //            //查询当前角色可见的model
    //            List<BusModel> busModels2 = iBusModelService.currentRoleSee("1", rId);
    //            busModelsAll.addAll(busModels2);
    //            //查询当前部门可见的model
    //
    //            List<BusModel> busModels3 = iBusModelService.currentDeptSee("2", dId);
    //            busModelsAll.addAll(busModels3);
    //            //查询当前用户可见的model
    //            List<BusModel> busModels4 = iBusModelService.currentUserSee("3", uId);
    //            busModelsAll.addAll(busModels4);
    //        });
    //
    //        return Result.ok(busModelsAll);
    //    } catch (Exception e) {
    //        log.error(e.toString());
    //        return Result.error("查询model失败");
    //    }
    //}
    //
    ////
    /////**
    //// * 查询按钮
    //// * 查询意见框
    //// */
    ////@ApiOperation("查询意见与按钮")
    ////@GetMapping("optionAndBtn")
    ////public Result btnQuery(Integer iprocSetId,
    ////                       @RequestParam(required = false) String taskDefKey,
    ////                       @RequestParam(required = false) String proInstanId,
    ////                       @RequestParam(required = false) String taskId,
    ////                       String busDataId, String optionTable, HttpServletRequest request) {
    ////
    ////    Map<String, Object> data = new HashMap<>();
    ////    //查询当前用户拥有的权限
    ////    SysUser currentUser = sysUserService.getCurrentUser(request);
    ////
    ////    try {
    ////        //按钮需要判断类型
    ////        Map<String, Object> map = dynamicTableService.getBusProcSet(iprocSetId);
    ////        String btn = map.get("btn") + "";
    ////        String opt = map.get("opt") == null ? null : map.get("opt") + "";
    ////        String key = map.get("pkey") == null || "".equals(map.get("pkey")) ? null : map.get("pkey") + "";
    ////
    ////        String tableName = map.get("tableName") + "";
    ////        //流程定义key--开启任务使用
    ////        data.put("key", key);
    ////        //************************ 当前用户权限 ***************************************************
    ////        Map<String, Object> query = new HashMap<>();
    ////        query.put("table", tableName);
    ////        query.put("i_id", busDataId);
    ////
    ////        //校验当前用户所拥有的权限
    ////        Map<String, Boolean> currentUserPermission = buttonPermissionService.
    ////                currentUserPermission(key, query, currentUser, taskDefKey, proInstanId, taskId);
    ////        //****************************************************************************************
    ////        //TODO 校验是否是追加的参与人
    ////
    ////        //查询按钮所有id oa_button_set ,再查具体的按钮信息 oa_button
    ////        List<ButtonPermit> btnList = dynamicTableService.queryButtonId(key, taskDefKey, btn);
    ////        //TODO 校验互斥按钮 待定
    ////        //Map<Integer, ButtonPermit> buttonPermitHashMap = new HashMap<>();
    ////        //btnList.stream().forEach(b -> {
    ////        //    buttonPermitHashMap.put(b.getIId(), b);
    ////        //});
    ////
    ////        //按钮分类
    ////        Map<String, List<ButtonPermit>> buttons = new HashMap<>();
    ////        buttons.put("isDefend", new ArrayList<ButtonPermit>());
    ////        buttons.put("isNotDefend", new ArrayList<ButtonPermit>());
    ////        Iterator<ButtonPermit> iterator = btnList.iterator();
    ////        while (iterator.hasNext()) {
    ////            ButtonPermit buttonPermit = iterator.next();
    ////            //判断当前按钮是否符合按钮规则
    ////            boolean havePermission = buttonPermissionService.havePermission(buttonPermit, currentUserPermission);
    ////            //判断当前用户是否有-对应权限--没有权限就不展示
    ////            if (!havePermission) {
    ////                iterator.remove();
    ////                continue;
    ////            }
    ////
    ////            //是否是维护按钮
    ////            Boolean isBase = buttonPermit.getIIsBase() == null ? true : buttonPermit.getIIsBase();
    ////            if (isBase) {
    ////                buttons.get("isDefend").add(buttonPermit);
    ////            } else {
    ////                buttons.get("isNotDefend").add(buttonPermit);
    ////            }
    ////        }
    ////        //按钮
    ////        data.put("btn", buttons);
    ////
    ////        List<Map<String, Object>> optList = dynamicTableService.queryOptions(opt, iprocSetId, key, taskDefKey, optionTable, busDataId);
    ////        //查询意见
    ////        data.put("opt", optList);
    ////
    ////        return Result.ok(data);
    ////    } catch (Exception e) {
    ////        log.error(e.toString());
    ////        return Result.error("查询失败");
    ////    }
    ////}
    //
    //
    //@ApiOperation("插入数据与开启流程")
    //@PostMapping("insertDataAndStartPro")
    //public Result dataInsert(@RequestBody Map<String, Object> map, HttpServletRequest request) {
    //    String keyPro = null;
    //    HashMap<String, Object> vars = null;
    //    try {
    //        //获取当前用户
    //        SysUser user = sysUserService.getCurrentUser(request);
    //        String userId = user.getId();
    //
    //        String busMsg = VarsWithBus.getBusMsg(map);
    //
    //        keyPro = (String) map.get("key");
    //        //开启流程
    //        vars = new HashMap<>();
    //        vars.put("busMsg", busMsg);
    //        vars.put(IWfConstant.WF_TASK_STARTER, userId);
    //
    //        //移除多余字段
    //        for (String remove : TaskConstant.REMOVEFILEDS) {
    //            map.remove(remove);
    //        }
    //
    //        dynamicTableService.insertDataAndStartPro(map, vars, keyPro);
    //
    //    } catch (Exception e) {
    //        log.error(e.toString());
    //        return Result.error("保存任务并开启流程失败");
    //    }
    //
    //    return Result.ok("保存任务并开启流程成功");
    //}

    /**
     * 新建任务
     * 菜单名称数据查询
     */
    @GetMapping(value = "/findModelPermit1")
    public Result<BusModelPermit> findModelPermit1(HttpServletRequest request) {
        Result result = new Result();
        List<String> list1 = new ArrayList<>();  //可见的

        List<String> list2 = new ArrayList<>();  //不可见的

        String token = request.getHeader("X-Access-Token");
        String username = JwtUtil.getUsername(token);
        String schema = MycatSchema.getSchema();
        List<BusModelPermit> ModelPermitList = iBusModelPermitService.findList(schema);
        SysUser user = sysUserService.getUserByName(username);
        String sysUserId = user.getId();//查出当前用户的id
        String departId = ioaCalendarService.getDepartId(user.getId()); //查出当前登陆用户的部门id
        List<String> roleIds = iBusFunctionPermitService.getroleId(sysUserId);
        if (ModelPermitList == null) {
            result.error500("未找到对应实体");

        } else {

            for (BusModelPermit modelpermit : ModelPermitList) {
                String sPermitType = modelpermit.getSPermitType();
                if (StringUtils.isBlank(sPermitType)) {
                    continue;
                }

                String sDisplay = modelpermit.getSDisplay();
                if (StringUtils.isBlank(sDisplay)) {
                    continue;
                }
                if (sDisplay.equals("0")) {//不可见
                    queryall(modelpermit, list2, sysUserId,departId,roleIds);
                }
                if (sDisplay.equals("1")) {//可见
                    queryall(modelpermit, list1, sysUserId,departId,roleIds);
                }
            }
            //过滤掉不可见的
            list1.removeAll(list2);
            List<BusModel> ModelList = iBusModelService.findList(schema);
            if (ModelList == null) {
                result.error500("未找到对应实体");
            }
            List<Map<String, String>> listAll = new ArrayList<>();
           /* for (BusModel model : ModelList) {
                Map<String, String> map = new HashMap<>();
                String modelid = model.getIId().toString();
                String modelSName = model.getSName();
                if (list1.contains(modelid)) {
                    map.put("modelid", modelid);
                    map.put("modelSName", modelSName);
                    listAll.add(map);
                }
            }*/
            ModelList.forEach(model -> {
                Map<String, String> map = new HashMap<>();
                String modelid = model.getIId().toString();
                String modelSName = model.getSName();
                if (list1.contains(modelid)) {
                    map.put("modelid", modelid);
                    map.put("modelSName", modelSName);
                    listAll.add(map);
                }
            });
            result.setResult(listAll);

        }

        return result;

    }

    //
    private void queryall(BusModelPermit model, List<String> list, String sysUserId, String departId,List<String> roleIds) {
       /* SysUser user = sysUserService.getUserByName(username);
        String sysUserId = user.getId();//查出当前用户的id
        String departId = ioaCalendarService.getDepartId(user.getId()); //查出当前登陆用户的部门id
        List<String> roleIds = iBusFunctionPermitService.getroleId(sysUserId);*/
        String sPermitType = model.getSPermitType();
        if (sPermitType.equals("0")) { //是所有人\
            String modelid = model.getIBusModelId() + "";
            list.add(modelid);
        } else if (sPermitType.equals("1")) { //角色
            if (roleIds.contains(model.getITypeId())) {
                String modelid = model.getIBusModelId().toString();
                list.add(modelid);
            }

        } else if (sPermitType.equals("2")) {//部门
            String iTypeId = model.getITypeId();
            String orgType = sysDepartService.getById(iTypeId).getOrgType();
            if(orgType.equals("1")){ //表示是机构
                List<String>  ParentIdList=iBusFunctionPermitService.getParentId(iTypeId);
               if(ParentIdList.contains(departId)){
                   String modelid = model.getIBusModelId().toString();
                   list.add(modelid);
               }
            }else{//表示是部门
                if (departId.equals(model.getITypeId())) {
                    String modelid = model.getIBusModelId().toString();
                    list.add(modelid);
                }
            }

        } else if (sPermitType.equals("3")) {//人员
            if (sysUserId.equals(model.getITypeId())) {
                String modelid = model.getIBusModelId().toString();
                list.add(modelid);
            }
        }
    }

    private void queryFunctionall(BusFunctionPermit functionPermit, List<String> list, String sysUserId, String departId,List<String> roleIds) {

        String sPermitType = functionPermit.getSPermitType();

        if (sPermitType.equals("0")) { //是所有人\
            String modelid = functionPermit.getIBusId() + "";
            list.add(modelid);
        } else if (sPermitType.equals("1")) { //角色
            if (roleIds.contains(functionPermit.getITypeId())) {
                String modelid = functionPermit.getIBusId() + "";
                list.add(modelid);
            }

        } else if (sPermitType.equals("2")) {//部门

            String iTypeId = functionPermit.getITypeId();

            String orgType = sysDepartService.getById(iTypeId).getOrgType();

            if(orgType.equals("1")){ //表示是机构
                List<String>  ParentIdList=iBusFunctionPermitService.getParentId(iTypeId);
                if(ParentIdList.contains(departId)){
                    String modelid = functionPermit.getIBusId().toString();
                    list.add(modelid);
                }
            }else{//表示是部门
                if (departId.equals(functionPermit.getITypeId())) {
                    String modelid = functionPermit.getIBusId().toString();
                    list.add(modelid);
                }
            }

        } else if (sPermitType.equals("3")) {//人员
            if (sysUserId.equals(functionPermit.getITypeId())) {
                String modelid = functionPermit.getIBusId().toString();
                list.add(modelid);
            }
        }
    }

    /**
     * 新建任务
     * 功能名称数据查询
     */
    @GetMapping(value = "/findFunctionPermit")
    public Result<BusFunctionPermit> findFunctionPermit(HttpServletRequest request, Integer modelId) {
        Result result = new Result();
        List<String> list1 = new ArrayList<>();  //可见的

        List<String> list2 = new ArrayList<>();  //不可见的

        String token = request.getHeader("X-Access-Token");
        String schema = MycatSchema.getSchema();
        String username = JwtUtil.getUsername(token);
        List<BusFunctionPermit> functionPermitList = iBusFunctionPermitService.findList(schema);
        SysUser user = sysUserService.getUserByName(username);
        String sysUserId = user.getId();//查出当前用户的id
        String departId = ioaCalendarService.getDepartId(user.getId()); //查出当前登陆用户的部门id
        List<String> roleIds = iBusFunctionPermitService.getroleId(sysUserId);
        if (functionPermitList == null) {
            result.error500("未找到对应实体");

        } else {
            for (BusFunctionPermit functionPermit : functionPermitList) {
                String sPermitType = functionPermit.getSPermitType();
                if (StringUtils.isBlank(sPermitType)) {
                    continue;
                }
                String sDisplay = functionPermit.getSDisplay();
                if (StringUtils.isBlank(sDisplay)) {
                    continue;
                }
                if (sDisplay.equals("0")) {//不可见
                    queryFunctionall(functionPermit, list2,sysUserId,departId,roleIds);
                }
                if (sDisplay.equals("1")) {//可见
                    queryFunctionall(functionPermit, list1,sysUserId,departId,roleIds);
                }
            }
            //过滤掉不可见的
            list1.removeAll(list2);
            List<BusFunction> FunctionList = iBusFunctionService.findList(schema);
            if (FunctionList == null) {
                result.error500("未找到对应实体");
            }
            List<Map<String, String>> listAll = new ArrayList<>();
            /*for (BusFunction function : FunctionList) {
                Map<String, String> map = new HashMap<>();
                if (modelId == function.getIBusModelId()) {
                    String functionid = function.getIId().toString();
                    String functionSName = function.getSName();
                    if (list1.contains(functionid)) {
                        map.put("functionid", functionid);
                        map.put("functionSName", functionSName);
                        listAll.add(map);
                    }
                }
            }*/
            FunctionList.forEach(function -> {
                Map<String, String> map = new HashMap<>();
                if (modelId == function.getIBusModelId()) {
                    String functionid = function.getIId().toString();
                    String functionSName = function.getSName();
                    if (list1.contains(functionid)) {
                        map.put("functionid", functionid);
                        map.put("functionSName", functionSName);
                        listAll.add(map);
                    }
                }
            });
            result.setResult(listAll);
        }


        return result;

    }

}
