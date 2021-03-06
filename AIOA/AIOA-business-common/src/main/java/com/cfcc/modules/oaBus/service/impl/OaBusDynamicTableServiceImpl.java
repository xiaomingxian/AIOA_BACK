package com.cfcc.modules.oaBus.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.system.vo.LoginUser;
import com.cfcc.common.util.HttpClientUtil;
import com.cfcc.common.util.IPUtils;
import com.cfcc.common.util.SpringContextUtils;
import com.cfcc.common.util.workflow.VarsWithBus;
import com.cfcc.modules.oaBus.entity.*;
import com.cfcc.modules.oaBus.mapper.BusFunctionMapper;
import com.cfcc.modules.oaBus.mapper.OaBusDynamicTableMapper;
import com.cfcc.modules.oaBus.mapper.OaFileMapper;
import com.cfcc.modules.oaBus.service.*;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.entity.SysLog;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.mapper.SysDictMapper;
import com.cfcc.modules.system.service.ISysDictService;
import com.cfcc.modules.system.service.ISysLogService;
import com.cfcc.modules.system.service.ISysUserService;
import com.cfcc.modules.workflow.service.ProcessManagerService;
import com.cfcc.modules.workflow.service.TaskCommonService;
import com.cfcc.modules.workflow.vo.TaskInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@Transactional
public class OaBusDynamicTableServiceImpl implements OaBusDynamicTableService {

    @Autowired
    private IBusProcSetService iBusProcSetService;

    @Autowired
    private IOaFileService oaFileService;

    @Autowired
    private OaFileMapper oaFileMapper;
    @Autowired
    private ISysLogService sysLogService;

    @Autowired
    private BusFunctionMapper busFunctionMapper;

    @Autowired
    private OaBusDynamicTableMapper dynamicTableMapper;

    @Autowired
    private ProcessManagerService processManagerService;

    @Autowired
    private TaskCommonService taskCommonService;

    @Autowired
    private ISysDictService sysDictService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysDictMapper sysDictMapper;

    @Autowired
    @Lazy
    private IOaBusdataService oaBusdataService;

    @Autowired
    @Lazy
    private OaBusDynamicTableService dynamicTableService;

    //附件上传地址
    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Override
    public Map<String, Object> getBusProcSet(Integer iprocSetId) {
        return dynamicTableMapper.getBusProcSet(iprocSetId);
    }

    @Override
    public List<ButtonPermit> queryButtonId(String key, String taskDefKey, String btn) {
        return dynamicTableMapper.queryButtonId(key, taskDefKey, btn);
    }

    @Override
    public Map<String, Object> insertData(Map<String, Object> map) {
        dynamicTableMapper.insertData(map);
        return map;
    }

    @Override
    public Map<String, Object> queryDataById(Map<String, Object> map) {
        String tableName = (String) map.get("table");
        if (tableName == null || "".equals(tableName)) {
            log.error("表名为空！！！");
            return null;
        }
        if (map.containsKey("i_id")) {
            String i_id = map.get("i_id") + "";
            if (i_id == null || "".equals(i_id)) {
                log.error("i_id为空！！！");
                return null;
            }
        }
        return dynamicTableMapper.queryDataById(map);
    }

    @Override
    public IPage<Map<String, Object>> queryByEquals(Integer pageNo, Integer pageSize, Map<String, Object> map) {

        String table = (String) map.get("table");
        map.remove("table");

        //封装分页信息
        Page<Map<String, Object>> page = new Page<>(pageNo, pageSize);
        Long total = dynamicTableMapper.queryCountByEquals(map, table);
        page.setTotal(total);
        List<Map<String, Object>> recoders = dynamicTableMapper.queryByEquals((pageNo - 1) * pageSize, pageSize, map, table);
        page.setRecords(recoders);

        return page;
    }

    @Override
    public List<Map<String, Object>> queryOptions(String opt, String iprocSetId, String key, String taskDefKey,
                                                  String optionTable, String busDataId) {
        return dynamicTableMapper.queryOptions(opt, iprocSetId, key, taskDefKey, optionTable, busDataId);
    }

    @Override
    public int updateData(Map<String, Object> map) {
        return dynamicTableMapper.updateData(map);
    }


    @Autowired
    private IBusFunctionService busFunctionService;


    @Override
    public boolean deleteData(Map<String, Object> map) {
        return dynamicTableMapper.deleteData(map);
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
            if (null != taskInfoVO.getIsDept() && taskInfoVO.getIsDept()) {
                mainDept = taskInfoVO.getTaskWithDepts().getMainDept();
            }
            if (StringUtils.isNotBlank(mainDept)) {
                busData.put("s_create_dept", mainDept);
            }
            s_receive_num = busData.get("s_receive_num") == null ? "" : busData.get("s_receive_num").toString();
            busData.put("s_file_num", s_receive_num);
        } else {
            mainDept = busData.get("s_create_dept") == null ? "" : busData.get("s_create_dept").toString();
        }
        busData.put("mainDept", mainDept);


        String busMsg = VarsWithBus.getBusMsg(busData);

        busData.put("s_file_num", sourceFileNum);


        if (vars == null) {
            vars = new HashMap<String, Object>();
        }
        vars.put("busMsg", busMsg);
        taskInfoVO.setVars(vars);
        if (sName.contains("收文")) {
            busData.put("s_file_num", s_file_num);
        }
    }


    @Override
    public void checkProc(Map<String, Object> map) {


        HashMap<String, Object> query = new HashMap<>();
        query.put("i_id", map.get("i_id"));
        query.put("table", map.get("table"));
        Map<String, Object> oneData = queryDataById(query);
        if (oneData == null) return;
        Object i_bus_function_id = oneData.get("i_bus_function_id");
        Object s_cur_proc_name = oneData.get("s_cur_proc_name");

        if (s_cur_proc_name == null) return;//没有流程就返回
        if (s_cur_proc_name != null && StringUtils.isBlank(s_cur_proc_name.toString())) return;//没有流程就返回

        Object s_cur_task_name = oneData.get("s_cur_task_name");//newtask-新建任务
        Object s_create_by = oneData.get("s_create_by");//newtask-新建任务
        boolean claim = false;
        if (s_create_by != null && s_create_by.toString().contains(",")) claim = true;
        //act_show
        String actShow = dynamicTableMapper.queryActShow(i_bus_function_id);
        map.put("act_show", actShow);
        map.put("justStart", true);
        map.put("s_cur_task_name", s_cur_task_name);
        map.put("s_cur_proc_name", s_cur_proc_name);


        //if (s_cur_proc_name == null) return;

        if ("newtask-新建任务".equals(s_cur_task_name)) {

            //没有开启流程-去开启
            TaskInfoVO taskInfoVO = new TaskInfoVO();
            taskInfoVO.setBusData(map);
            //根据functionId查出function名称
            BusFunction busFunction = busFunctionMapper.selectByIid(Integer.parseInt(i_bus_function_id.toString()));
            String sName = busFunction.getSName();
            String sLevel = busFunction.getSLevel();
            map.put("ilevel", sLevel);
            map.put("functionName", sName);
            String sourceFileNum = map.get("s_file_num") == null ? null : map.get("s_file_num").toString();

            if (sName.contains("收文")) {
                map.put("mainDept", "");
                map.put("s_file_num", map.get("s_receive_num") == null ? "" : map.get("s_receive_num").toString());
            } else {
                map.put("mainDept", map.get("s_create_dept") == null ? "" : map.get("s_create_dept").toString());
            }

            busDataSet(taskInfoVO);
            String nextMsg = taskCommonService.doTask(taskInfoVO);
            map.put("s_cur_task_name", nextMsg);
            if (claim) map.put("s_create_by", "");
            if (sName.contains("收文")) {
                Object file_num = map.get("s_file_num");
                String s_file_num = null;
                if (file_num != null) {
                    s_file_num = map.get("s_file_num").toString();
                }
                map.put("s_file_num", s_file_num);
            }
            //更新流程环节
            map.remove("justStart");
            map.remove("act_show");
            map.remove("ilevel");
            map.remove("functionName");
            map.remove("mainDept");
            map.put("s_file_num", sourceFileNum);
            updateData(map);
        } else {
            //更新流程中的名称
            BusFunction busFunction = busFunctionMapper.selectByIid(Integer.parseInt(i_bus_function_id.toString()));
            String sName = busFunction.getSName();
            if (sName.contains("收文")) {
                map.put("s_file_num", map.get("s_receive_num") == null ? "" : map.get("s_receive_num").toString());
            }
            taskCommonService.updateTitle(map);

        }

    }

    @Override
    public Map<String, Object> updateOpinionByTaskId(Map<String, Object> map) {
        Map<String, Object> opinionMap = new HashMap<>();
        opinionMap.put("s_task_id", map.get("s_task_id"));
        opinionMap.put("s_task_name", map.get("s_task_name"));
        opinionMap.put("s_taskdef_key", map.get("s_taskdef_key"));
        opinionMap.put("i_bus_function_id", map.get("i_bus_function_id"));
        opinionMap.put("i_busdata_id", map.get("i_busdata_id"));
        opinionMap.put("s_name", map.get("s_name"));
        opinionMap.put("i_user_id", map.get("i_user_id"));
        opinionMap.put("table", map.get("table"));
        Map<String, Object> data = dynamicTableMapper.queryDataById(opinionMap);
        int mapHaveId = 0;
        if (data != null && data.size() > 0) {
            if (!map.get("s_task_id").equals(data.get("s_task_id"))) {
                mapHaveId = dynamicTableMapper.insertData(map);
            } else {
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("table", map.get("table"));
                updateMap.put("i_id", data.get("i_id"));
                updateMap.put("d_sign", map.get("d_sign"));
                updateMap.put("s_opinion", map.get("s_opinion"));
                mapHaveId = dynamicTableMapper.updateData(updateMap);
            }
        } else {
            if (map.get("i_id") != null) {
                map.put("i_id", 0);
            }
            mapHaveId = dynamicTableMapper.insertData(map);
        }
        return data;
    }

    @Override
    public Map<String, Object> queryOptionsByBusDataIdAndFuncationId(String stable, Integer tableid, Integer funcationid) {
        Map<String, Object> dataMap = new HashMap<>();
        String tableName = stable + "_opinion";
        List<OaBusdataOpinion> opinions = dynamicTableMapper.queryOptionsByBusDataIdAndFuncationId(tableName, tableid, funcationid);

        String dictKey = "type";

        List<Map<String, Object>> mapList = sysDictMapper.getDictByKey(dictKey);

        for (int i = 0; i < mapList.size(); i++) {

            String type = "";
            for (int j = 0; j < opinions.size(); j++) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String format = sdf.format(opinions.get(j).getDSign());
                if (opinions.get(j).getSOpinionType().equals(mapList.get(i).get("dictVal"))) {
                    type += opinions.get(j).getSOpinion() + " " + opinions.get(j).getSName() + " " + format + "\n";
                }

            }
            dataMap.put("opinion" + mapList.get(i).get("dictVal"), type);
        }

        return dataMap;
    }

    @Override
    public List<Map<String, Object>> queryUnitsByUser(String orgCode) {
        List<Map<String, Object>> list = dynamicTableMapper.queryUnitsByUser(orgCode);
        return list;
    }

    @Override
    public Map<String, Object> queryModelByFunctionId(String functionId) {

        return dynamicTableMapper.queryModelByFunctionId(functionId);
    }

    @Override
    public Map<String, List<Map<String, Object>>> downSendData(List<String> unit, HttpServletRequest request) {
        Map<String, List<Map<String, Object>>> users = new HashMap<>();
        //根据机构id、县行管理员角色查询用户
        for (int i = 0; i < unit.size(); i++) {
            List<Map<String, Object>> maps = dynamicTableMapper.queryUsersByUnit(unit.get(i));
            if (maps.size() > 0) {
                users.put(unit.get(i), maps);
            }
        }
        //数据字典获取functionid
        SysUser currentUser = userService.getCurrentUser(request);
        Map<String, Object> allUserMsg = userService.getAllUserMsg(currentUser.getUsername());
        String deptId = allUserMsg.get("deptId") + "";
        String dictKey = "btn_downSend"; //数据字典-下发
        List<Map<String, Object>> list = sysDictService.getDictByKeySer(dictKey, deptId);
        //遍历支行id、functionid查询table
        List<Map<String, Object>> unitTable = new ArrayList<>();
        for (int i = 0; i < unit.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (unit.get(i).equals(list.get(j).get("text"))) {
                    String unitId = unit.get(i);
                    String functionId = list.get(j).get("value") + "";  //functionId
                    Map<String, Object> table = dynamicTableMapper.getTableByUnitAndFunction(unitId, functionId);
                    if (table != null) {
                        unitTable.add(table);
                    }
                }
            }
        }
        users.put("unitTables", unitTable);
        return users;
    }

    @Override
    public Map<String, List<Map<String, Object>>> upSendFile(SysDepart depart, HttpServletRequest request) {
        Map<String, List<Map<String, Object>>> users = new HashMap<>();
        //根据机构id、县行管理员角色查询用户
        List<Map<String, Object>> maps = dynamicTableMapper.queryUsersByUnit(depart.getId());
        users.put(depart.getId(), maps);
        //数据字典获取functionid
        SysUser currentUser = userService.getCurrentUser(request);
        Map<String, Object> allUserMsg = userService.getAllUserMsg(currentUser.getUsername());
        String deptId = allUserMsg.get("deptId") + "";
        String dictKey = "btn_upSend"; //数据字典-上报
        List<Map<String, Object>> list = sysDictService.getDictByKeySer(dictKey, deptId);
        //遍历支行id、functionid查询table
        List<Map<String, Object>> unitTable = new ArrayList<>();
        for (int j = 0; j < list.size(); j++) {
            if (depart.getId().equals(list.get(j).get("text"))) {
                String unitId = depart.getId();
                String functionId = list.get(j).get("value") + "";  //functionId
                Map<String, Object> table = dynamicTableMapper.getTableByUnitAndFunction(unitId, functionId);
                if (table != null) {
                    table.put("unitName", depart.getDepartName());
                    unitTable.add(table);
                }
            }
        }
        users.put("unitTables", unitTable);
        return users;
    }

    @Override
    public boolean insertUserPermit(String param) {
        JSONObject jsonObject = JSONObject.parseObject(param);
        if (jsonObject.containsKey("unitTables")) {
            jsonObject.remove("unitTables");
        }
        JSONArray list = jsonObject.getJSONArray("list");
        String iBusdataId = jsonObject.getString("iBusdataId");
        String functionId = jsonObject.getString("functionId");
        String table = jsonObject.getString("table");
        Map<String, Object> map = new HashMap<>();
        map.put("table", table + "_permit");
        map.put("i_bus_function_id", functionId);
        map.put("i_busdata_id", iBusdataId);
        boolean flag = false;
        for (int i = 0; i < list.size(); i++) {
            JSONObject object = list.getJSONObject(i);
            map.put("s_user_id", object.get("userId"));
            map.put("s_userdept_id", object.get("departId"));
            map.put("s_userunit_id", object.get("unitId"));
            int total = dynamicTableMapper.insertData(map);
            if (i == list.size() - 1) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public Result provinceToCityReceiveFile(Map<String, Object> param, HttpServletRequest request) {
        Result result = new Result();
        JSONObject object = JSONObject.parseObject(JSON.toJSONString(param));
        JSONObject busData = object.getJSONObject("busData");
        Map<String, Object> urlData = (Map<String, Object>) object.get("cityUrl");
        if (urlData == null) {
            result.setSuccess(false);
            result.setCode(500);
            result.setMessage("请配置" + urlData.get("text") + "调用接口地址！");
            return result;
        }
        String fileTable = busData.get("table") + "";
        String fileTableId = busData.get("i_id") + "";
        String fileSfileType = "4,6";

        //查询附件list
        List<OaFile> fileList = oaFileMapper.queryFileListByType(fileTable, fileTableId, fileSfileType);
        List<Map<String, Object>> mapFiles = new ArrayList<>();
        LoginInfo loginInfo = userService.getLoginInfo(request);
        String orgSchema = "";
        if (loginInfo.getOrgSchema() != null && !loginInfo.getOrgSchema().equals("")) {
            orgSchema = loginInfo.getOrgSchema();
        }
        for (OaFile file : fileList) {
            Map<String, Object> map = new HashMap<>();
            map.put("sFilePath", uploadpath + File.separator + orgSchema + File.separator + file.getSFilePath());
//            log.info("附件路径============"+map.get("sFilePath"));
            map.put("sFileName", file.getSFileName());
            mapFiles.add(map);
        }
        busData.put("fileList", mapFiles); //附件key名称固定
        if (urlData.get("value") != null) {
            busData.put("org_schema", urlData.get("value"));
        }
        String url = "http://" + urlData.get("description") + "/AIOA/oaBus/dynamic/provinceToCityReceviceClient";
        result = HttpClientUtil.doPostFileStreamAndJsonObj(url, busData);
        return result;
    }

    @Override
    public Result provinceToCityInsideFile(Map<String, Object> param, HttpServletRequest request) {
        Result result = new Result<>();
        JSONObject object = JSONObject.parseObject(JSON.toJSONString(param));
        JSONObject busData = object.getJSONObject("busData");
        Map<String, Object> urlData = (Map<String, Object>) object.get("cityUrl");
        if (urlData == null) {
            result.setSuccess(false);
            result.setCode(500);
            result.setMessage("请配置" + urlData.get("text") + "调用接口地址！");
            return result;
        }
        String fileTable = busData.get("table") + "";
        String fileTableId = busData.get("i_id") + "";
        String fileSfileType = "4,6";

        //查询附件list
        List<OaFile> fileList = oaFileMapper.queryFileListByType(fileTable, fileTableId, fileSfileType);
        List<Map<String, Object>> mapFiles = new ArrayList<>();
        LoginInfo loginInfo = userService.getLoginInfo(request);
        String orgSchema = "";
        if (loginInfo.getOrgSchema() != null && !loginInfo.getOrgSchema().equals("")) {
            orgSchema = loginInfo.getOrgSchema();
        }
        for (OaFile file : fileList) {
            Map<String, Object> map = new HashMap<>();
            map.put("sFilePath", uploadpath + File.separator + orgSchema + File.separator + file.getSFilePath());
            map.put("sFileName", file.getSFileName());
            mapFiles.add(map);
        }
        busData.put("fileList", mapFiles); //附件key名称固定
        if (urlData.get("value") != null) {
            busData.put("org_schema", urlData.get("value"));
        }
        String url = "http://" + urlData.get("description") + "/AIOA/oaBus/dynamic/provinceToCityInsideClient";
        result = HttpClientUtil.doPostFileStreamAndJsonObj(url, busData);
        return result;
    }

    @Override
    public boolean shareFile(Map<String, Object> map, LoginInfo loginInfo, HttpServletRequest request) {
        boolean flag = false;
        Integer oldId = (Integer) map.get("i_id");
        String oldTable = map.get("table") + "";

        //获取字典值
        List<Map<String, Object>> functonId = sysDictService.getDictByKey("btn_shareSend");
        String functId = functonId.get(0).get("dictVal") + "";

        //获取业务详情
        Map<String, Object> tableData = dynamicTableMapper.queryModelByFunctionId(functId);
        String modelId = tableData.get("i_id") + "";

        //新建临时数据
        Result<Map<String, Object>> result = oaBusdataService.queryDataByModelAndFunctionId(modelId, functId, loginInfo);
        String receiveTable = result.getResult().get("tableName") + "";
        String receiveId = result.getResult().get("busdataId") + "";
        //更新数据
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("table", receiveTable); //标题
        updateMap.put("i_id", receiveId); //标题
        updateMap.put("s_title", map.get("s_title")); //标题
        updateMap.put("s_file_num", map.get("s_file_num")); //文件字号
        updateMap.put("d_sealdate", map.get("d_sealdate"));  //封发日期
        updateMap.put("s_main_dept_names", map.get("s_main_dept_names")); //主送部门
        updateMap.put("s_create_name", map.get("s_create_name")); //拟稿人
        updateMap.put("s_create_dept", map.get("s_create_dept")); //拟稿部门
        updateMap.put("s_main_unit_names", map.get("s_main_unit_names")); //主送单位
        updateMap.put("s_signer", map.get("s_signer")); //签发人
        updateMap.put("s_varchar1", map.get("table"));  //业务数据表
        updateMap.put("s_varchar7", "共享文件"); //传阅类别
        updateMap.put("i_safetylevel", map.get("i_safetylevel")); //密级
        updateMap.put("i_urgency", map.get("i_urgency"));  //缓急
        updateMap.put("i_bigint1", map.get("i_id")); //传阅业务数据id
        updateMap.put("i_bigint2", map.get("i_bus_model_id")); //传阅业务模块id
        updateMap.put("i_bigint3", map.get("i_bus_function_id")); //传阅业务id
        //业务名称
        Map<String, Object> names = oaBusdataService.getFuncitonDataById(map.get("i_bus_function_id") + "");
        updateMap.put("s_varchar5", names.get("mName")); //业务分类
        updateMap.put("s_varchar6", names.get("fName"));  //业务名称
        updateMap.put("i_is_display", "0"); //是否临时
        updateMap.put("i_is_es", 3); //es
//        System.out.println(JSON.toJSONString(updateMap));
        dynamicTableMapper.updateData(updateMap);

        //复制附件
        Map<String, Object> copyFileMap = new HashMap<>();
        copyFileMap.put("sTable", oldTable);
        copyFileMap.put("iTableId", oldId);
        copyFileMap.put("sFileType", "4,6");
        copyFileMap.put("receiveTable", receiveTable);
        copyFileMap.put("receiveId", Integer.valueOf(receiveId));
        List<OaFile> oaFiles = oaFileService.copyFiles(JSON.toJSONString(copyFileMap), request);
        flag = true;
        return flag;
    }

    @Override
    public Result provinceToCityClient(HttpServletRequest request) {
//        log.info("========省会向地市转收文服务开始============");
        Result respData = new Result();
        try {
            Collection<Part> parts = request.getParts();
//            List<Map<String, Object>> fileList = new ArrayList<>();
            JSONObject jsonParam = new JSONObject();
            for (Iterator<Part> iterator = parts.iterator(); iterator.hasNext(); ) {
                Part part = iterator.next();
                if ("JsonBusData".equals(part.getName())) {
                    //解析json参数
                    BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream()));
                    String line = "";
                    String parseString = "";
                    while ((line = reader.readLine()) != null) {
                        parseString += line;
                    }
                    jsonParam = JSONObject.parseObject(parseString);
                    if (jsonParam.get("org_schema") != null) {
                        String sceam = jsonParam.get("org_schema") + "";
                        request.setAttribute("orgSchema", sceam);
                    }
//                    fileList = (List<Map<String, Object>>) jsonParam.get("fileList");
//                    System.out.println("=====业务数据为=======" + jsonParam.toString());
//                    System.out.println("=====附件数据为=======" + fileList.toString());
                }
            }
            //数据字典获取模块和业务id
            List<Map<String, Object>> functonDict = sysDictService.getDictByKey("btn_sdSend");
            if (functonDict.size() < 1) {
                respData.setMessage("请完善收文数据配置！");
                respData.setCode(500);
                respData.setSuccess(false);
                return respData;
            }
            String functionId = functonDict.get(0).get("dictVal") + "";
            String unitId = functonDict.get(0).get("dictItem") + "";
            Map<String, Object> tableData = dynamicTableMapper.queryModelByFunctionId(functonDict.get(0).get("dictVal") + "");
            if (tableData == null) {
                respData.setMessage("请完善模块配置！");
                respData.setCode(500);
                respData.setSuccess(false);
                return respData;
            }
            String table = tableData.get("s_busdata_table") + "";
            String modelId = tableData.get("i_id") + "";
            LoginInfo loginInfo = new LoginInfo();
//        loginInfo.setOrgCode("A01A01");
            Result<Map<String, Object>> result = oaBusdataService.queryDataByModelAndFunctionId(modelId, functionId, loginInfo);
            if (result == null) {
                respData.setMessage("请完善业务配置！");
                respData.setCode(500);
                respData.setSuccess(false);
                return respData;
            }
            //组装业务数据
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("s_title", jsonParam.get("s_title"));
            paramMap.put("d_sealdate", jsonParam.get("d_sealdate"));
            paramMap.put("s_receive_num", jsonParam.get("s_file_num"));
            paramMap.put("s_varchar5", jsonParam.get("s_unit_name"));
            paramMap.put("i_urgency", jsonParam.get("i_urgency"));
            paramMap.put("i_bigint1", jsonParam.get("i_bigint1"));
            paramMap.put("i_bigint2", jsonParam.get("i_bigint2"));
            paramMap.put("s_main_unit_names", jsonParam.get("s_main_unit_names")); //主送单位
            paramMap.put("s_cc_unit_names", jsonParam.get("s_cc_unit_names")); //辅办单位
            paramMap.put("s_inside_deptnames", jsonParam.get("s_inside_deptnames")); //内部传送单位
            paramMap.put("table", result.getResult().get("tableName"));
            paramMap.put("i_id", result.getResult().get("busdataId"));
            paramMap.put("i_is_display", "0");
            paramMap.put("i_is_es", 3);
            //查询收文管理员角色用户（编码710）
            List<Map<String, Object>> userList = dynamicTableMapper.queryUsersByUnit(unitId);
            if (userList.size() < 1) {
                result.setMessage("无管理员角色用户！");
                result.setCode(500);
                result.setSuccess(false);
                return result;
            }
            String sCreateBy = "";
            for (Map<String, Object> map : userList) {
                sCreateBy += map.get("userId") + ",";
            }
            paramMap.put("s_create_by", sCreateBy);

            int num = dynamicTableMapper.updateData(paramMap);
            if (num != 1) {
                result.setMessage("修改数据失败！");
                result.setCode(500);
                result.setSuccess(false);
                return result;
            }
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("i_id", paramMap.get("i_id") + "");
            queryMap.put("table", paramMap.get("table"));
            //开启流程前组装数据
            Map<String, Object> busDataMap = new HashMap<>();
            busDataMap = dynamicTableMapper.queryDataById(queryMap);
            busDataMap.put("table", paramMap.get("table"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = sdf.format(busDataMap.get("d_create_time"));
            busDataMap.put("d_create_time", format);

            //开启流程
            try {
                dynamicTableService.checkProc(busDataMap);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("流程开启失败！");
                result.setCode(500);
                result.setSuccess(false);
            }

            //写入用户权限
            Map<String, Object> permitMap = new HashMap<>();
            permitMap.put("list", userList);
            permitMap.put("iBusdataId", paramMap.get("i_id"));
            permitMap.put("functionId", functionId);
            permitMap.put("table", paramMap.get("table"));
            dynamicTableService.insertUserPermit(JSON.toJSONString(permitMap));

            //处理附件
            MultipartHttpServletRequest multRequest = (MultipartHttpServletRequest) request;
            List<MultipartFile> files = multRequest.getFiles("File");
            int fileTotal = 0;
            HttpServletResponse response = null;
            if (files.size() > 0) {
                while (fileTotal < files.size()) {
                    MultipartFile mf = files.get(fileTotal);
                    String sFileType = "4";
                    String sTable = result.getResult().get("tableName") + "";
                    Integer iTableId = Integer.valueOf(result.getResult().get("busdataId") + "");
                    oaFileService.batchUploads(mf, mf.getOriginalFilename(), sTable, iTableId, sFileType, request, response);
                    fileTotal++;
                }
            }

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        } catch (ServletException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
//        log.info("========省会向地市转收文服务结束============");
        respData.setCode(200);
        respData.setMessage("收文传输完毕！");
        respData.setSuccess(true);
        return respData;
    }


    @Override
    public Result provinceToCityInsideClient(HttpServletRequest request) {
        Result result = new Result();
//        log.info("========省会向地市传阅服务开始============");
        try {
            Collection<Part> parts = request.getParts();
//            List<Map<String, Object>> fileList = new ArrayList<>();
            JSONObject jsonParam = new JSONObject();
            for (Iterator<Part> iterator = parts.iterator(); iterator.hasNext(); ) {
                Part part = iterator.next();
                if ("JsonBusData".equals(part.getName())) {
                    //解析json参数
                    BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream()));
                    String line = "";
                    String parseString = "";
                    while ((line = reader.readLine()) != null) {
                        parseString += line;
                    }
                    jsonParam = JSONObject.parseObject(parseString);
                    if (jsonParam.get("org_schema") != null) {
                        String sceam = jsonParam.get("org_schema") + "";
                        request.setAttribute("orgSchema", sceam);
                    }
//                    fileList = (List<Map<String, Object>>) jsonParam.get("fileList");
//                    System.out.println("=====业务数据为=======" + jsonParam.toString());
//                    System.out.println("=====附件数据为=======" + fileList.toString());
                }
            }
            //数据字典获取模块和业务id
            List<Map<String, Object>> functonDict = sysDictService.getDictByKey("btn_insideSend");
            if (functonDict.size() < 1) {
                result.setMessage("请完善传阅数据配置！");
                result.setCode(500);
                result.setSuccess(false);
                return result;
            }
            String functionId = functonDict.get(0).get("dictVal") + "";
            String unitId = functonDict.get(0).get("dictItem") + "";
            Map<String, Object> tableData = dynamicTableMapper.queryModelByFunctionId(functonDict.get(0).get("dictVal") + "");
            if (tableData == null) {
                result.setMessage("请完善模块配置！");
                result.setCode(500);
                result.setSuccess(false);
                return result;
            }
            Map<String, Object> functionMap = new HashMap<>();
            functionMap.put("i_id", functionId);
            functionMap.put("table", "oa_bus_function");
            Map<String, Object> functionData = dynamicTableMapper.queryDataById(functionMap);
            if (functionData == null) {
                result.setMessage("请完善业务配置！");
                result.setCode(500);
                result.setSuccess(false);
                return result;
            }
            String table = tableData.get("s_busdata_table") + "";
//            String modelId = tableData.get("i_id") + "";
//            LoginInfo loginInfo = new LoginInfo();
//            Result<Map<String, Object>> result = oaBusdataService.queryDataByModelAndFunctionId(modelId, functionId, loginInfo);
            //更新数据
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("table", table); //标题
            updateMap.put("i_bus_model_id", functionData.get("i_bus_model_id"));
            updateMap.put("i_bus_function_id", functionData.get("i_id"));
            updateMap.put("s_left_parameter", functionData.get("s_bus_left_parameter"));
            updateMap.put("s_right_parameter", functionData.get("s_bus_right_parameter"));
            updateMap.put("s_title", jsonParam.get("s_title")); //标题
            updateMap.put("s_file_num", jsonParam.get("s_file_num")); //文件字号
            updateMap.put("d_sealdate", jsonParam.get("d_sealdate"));  //封发日期
//            updateMap.put("s_main_dept_names", jsonParam.get("s_main_dept_names")); //主送部门
            updateMap.put("s_create_name", jsonParam.get("s_create_name")); //拟稿时间
            updateMap.put("s_create_dept", jsonParam.get("s_create_dept")); //拟稿部门
            updateMap.put("s_main_unit_names", jsonParam.get("s_main_unit_names")); //主送单位
            updateMap.put("s_signer", jsonParam.get("s_signer")); //签发人
            updateMap.put("s_varchar1", jsonParam.get("table"));  //业务数据表
//            updateMap.put("s_varchar7", "共享文件"); //传阅类别
            updateMap.put("i_safetylevel", jsonParam.get("i_safetylevel")); //密级
            updateMap.put("i_urgency", jsonParam.get("i_urgency"));  //缓急
            updateMap.put("i_bigint1", jsonParam.get("i_id")); //传阅业务数据id
            updateMap.put("i_bigint2", jsonParam.get("i_bus_model_id")); //传阅业务模块id
            updateMap.put("i_bigint3", jsonParam.get("i_bus_function_id")); //传阅业务id
            //业务名称
            Map<String, Object> names = oaBusdataService.getFuncitonDataById(functionId);
            updateMap.put("s_varchar5", names.get("mName"));
            updateMap.put("s_varchar6", names.get("fName"));
            updateMap.put("i_is_display", "0"); //是否临时
            updateMap.put("i_is_es", 3); //es
            LocalDate nowDate = LocalDate.now();
            updateMap.put("i_create_year", nowDate.getYear());
            updateMap.put("i_create_month", nowDate.getMonthValue());
            updateMap.put("i_create_day", nowDate.getDayOfMonth());
            updateMap.put("d_create_time", new Date());
            updateMap.put("s_create_unitid", unitId);
            BusProcSet busProcSet = iBusProcSetService.getById(functionData.get("i_proc_set_id") + "");
            if (busProcSet == null) return Result.error("无此类数据配置");
            int iVsersion = busProcSet.getIVersion() == null ? 1 : busProcSet.getIVersion();
            updateMap.put("i_fun_version", iVsersion);
            int i = dynamicTableMapper.insertData(updateMap);
            if (i != 1) {
                result.setMessage("写入数据失败！");
                result.setCode(500);
                result.setSuccess(false);
                return result;
            }

            //查询收文管理员角色用户（编码710）
            List<Map<String, Object>> userList = dynamicTableMapper.queryUsersByUnit(unitId);
            if (userList.size() < 1) {
                result.setMessage("无管理员角色用户！");
                result.setCode(500);
                result.setSuccess(false);
                return result;
            }
            String sCreateBy = "";
            for (Map<String, Object> map : userList) {
                sCreateBy += map.get("userId") + ",";
            }
            updateMap.put("s_create_by", sCreateBy);
            int num = dynamicTableMapper.updateData(updateMap);
            if (num != 1) {
                result.setMessage("数据修改失败！");
                result.setCode(500);
                result.setSuccess(false);
                return result;
            }
            //写入用户权限
            Map<String, Object> permitMap = new HashMap<>();
            permitMap.put("list", userList);
            permitMap.put("iBusdataId", updateMap.get("i_id"));
            permitMap.put("functionId", functionId);
            permitMap.put("table", updateMap.get("table"));
            dynamicTableService.insertUserPermit(JSON.toJSONString(permitMap));

            //处理附件
            MultipartHttpServletRequest multRequest = (MultipartHttpServletRequest) request;
            List<MultipartFile> files = multRequest.getFiles("File");
            int fileTotal = 0;
            HttpServletResponse response = null;
            if (files.size() > 0) {
                while (fileTotal < files.size()) {
                    MultipartFile mf = files.get(fileTotal);
                    String sFileType = "4";
                    String sTable = table;
                    Integer iTableId = Integer.valueOf(updateMap.get("i_id") + "");
                    oaFileService.batchUploads(mf, mf.getOriginalFilename(), sTable, iTableId, sFileType, request, response);
                    fileTotal++;
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        } catch (ServletException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
//        log.info("========省会向地市传阅服务结束============");
        result.setSuccess(true);
        result.setCode(200);
        result.setMessage("传阅完毕！");
        return result;
    }

    @Override
    public void insertOaOutLog(Map<String, Object> map, HttpServletRequest request) {
        LoginInfo loginInfo = userService.getLoginInfo(request);
        map.put("s_send_by", loginInfo.getId()); //创建用户
        map.put("d_create_time", new Date());  //发送时间
        dynamicTableMapper.insertData(map);
    }

    @Override
    public List<String> queryOaOutLogById(OaOutLog oaOutLog) {
        List<OaOutLog> oaOutLogs = dynamicTableMapper.queryOaOutLogById(oaOutLog);
        List<String> list = new ArrayList<>();
        for (OaOutLog log : oaOutLogs) {
            list.add(log.getSRecUnitid());
        }
        return list;
    }

    @Override
    public void deleteOaFile(String table, String id, int i) {
        dynamicTableMapper.deleteOaFile(table, id, i);
        Map<String, Object> obj = new HashMap<>();
        obj.put("table", table);
        obj.put("i_id", id);
        obj.put("i_is_typeset", 0);
        dynamicTableMapper.updateData(obj);
    }

    @Override
    public boolean deleteBusdata(Map<String, Object> map) {
        boolean b = false;
        if (map.get("processInstanceId") != null) {
            taskCommonService.del(map.get("processInstanceId") + "");
            map.remove("processInstanceId");
        }
        //删除业务数据
        b = dynamicTableMapper.deleteData(map);
        //添加删除日志
        addSysLog(map);
        //更改文号状态
        Map<String, Object> docManageParam = new HashMap<>();
        docManageParam.put("table", "oa_docnum_manage");
        docManageParam.put("i_busdata_id", map.get("i_id") + "");
        //已删除业务关联文号状态改为1
        try {
            Map<String, Object> docManage = dynamicTableMapper.queryDataById(docManageParam);
            if (docManage != null) {
                docManageParam.put("i_busdata_id", 1);
                docManageParam.put("i_id", docManage.get("i_id") + "");
                dynamicTableMapper.updateData(docManageParam);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("文号状态修改失败！");
        }
        return b;
    }

    /**
     * 添加操作日志
     *
     * @param sTitle
     */
    private void addSysLog(Map<String, Object> map) {
        String sTitle = "";
        sTitle = (String) map.get("s_title");
        int id = (int) map.get("i_id");
        String tableName = (String) map.get("table");
        System.out.println(sTitle);
        SysLog sysLog = new SysLog();
        //操作内容为title
        sysLog.setLogContent(sTitle);
        //0:操作日志;1:登录日志;2:定时任务
        sysLog.setLogType(0);
        sysLog.setMethod("delete");
        //操作类型（1查询，2添加，3修改，4删除,5导入，6导出）
        sysLog.setOperateType(4);
        sysLog.setRequestParam(tableName + ":" + id);
        //获取request
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        //设置IP地址
        sysLog.setIp(IPUtils.getIpAddr(request));

        //获取登录用户信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (sysUser != null) {
            sysLog.setUserid(sysUser.getUsername());
            sysLog.setUsername(sysUser.getRealname());

        }
        sysLog.setCreateTime(new Date());
        //保存系统日志
        sysLogService.save(sysLog);
    }

    @Override
    public void insertDataAndStartPro(Map<String, Object> map, HashMap<String, Object> vars, String keyPro) {
        String idAfterInsert = insertData(map).get("i_id") + "";
        String busMsg = (String) vars.get("busMsg");
        vars.put("busMsg", busMsg.replace("i_id", idAfterInsert));
        //开启流程
        processManagerService.start(keyPro, idAfterInsert, vars);
    }

    @Override
    public List<String> isReader(String busDataId, String table) {
        return dynamicTableMapper.isReader(busDataId, table);
    }

}
