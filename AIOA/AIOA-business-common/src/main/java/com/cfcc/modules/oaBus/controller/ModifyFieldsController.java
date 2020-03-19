package com.cfcc.modules.oaBus.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.modules.oaBus.entity.*;
import com.cfcc.modules.oaBus.service.*;
import com.cfcc.modules.oaBus.service.impl.OaFileServiceImpl;
import com.cfcc.modules.shiro.vo.DefContants;
import com.cfcc.modules.system.service.ISysUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * @Description: 业务数据表
 * @Author: jeecg-boot
 * @Date: 2019-12-05
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "运维工具-业务数据表")
@RestController
@RequestMapping("/modify/fields")
public class ModifyFieldsController {

    @Autowired
    private IOaBusdataService oaBusdataService;
    @Autowired
    private IBusModelService ibusModelService;
    @Autowired
    private IBusFunctionService busFunctionService;
    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private IOaFileService oaFileService;
    @Autowired
    private IBusProcSetService busProcSetService;

    //动态字段拼接
    @Autowired
    private IBusPageDetailService busPageDetailService;


    /**
     * 通过id查询
     * @param modelId,functionId
     * @return--暂不使用
     */
    @AutoLog(value = "业务和流程关联配置表-通过modelId,functionId查询")
    @ApiOperation(value="业务和流程关联配置表-通过modelId,functionId查询", notes="业务和流程关联配置表-通过modelId,functionId查询")
    @GetMapping(value = "/queryBusProcSetById")
    public Result<BusProcSet> queryBusProcSetById(@RequestParam(name="modelId",required=true) Integer modelId, @RequestParam(name="functionId",required=true) String functionId) {
        Result<BusProcSet> result = new Result<BusProcSet>();
        BusProcSet busProcSet = busProcSetService.getProcSet(modelId,functionId,null);
        if(busProcSet==null) {
            result.error500("未找到对应实体");
        }else {
            result.setResult(busProcSet);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 通过查询业务下拉列表
     * @param
     * @return
     */
    @AutoLog(value = "业务和流程关联配置表-查询全部业务下拉列表")
    @ApiOperation(value="业务和流程关联配置表-查询全部业务下拉列表", notes="业务和流程关联配置表-查询全部业务下拉列表")
    @GetMapping(value = "/queryBusList")
    public Result<List<BusFunction>> queryBusList() {
        Result<List<BusFunction>> result = new Result<List<BusFunction>>();
        String schema = MycatSchema.getSchema();
        List<BusFunction> list = busFunctionService.findList(schema);
        if(list==null || list.size()<1) {
            result.error500("未找到对应实体");
        }else {
            result.setResult(list);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 查询含义表所有列
     * @param
     */
    @AutoLog(value = "业务数据表-根据条件查询业务数据列表")
    @ApiOperation(value = "业务数据表-根据条件查询业务数据列表", notes = "业务数据表-根据条件查询业务数据列表")
    @PostMapping(value = "/queryOaBusPageDetailColumns")
    @ResponseBody
    public Result<Map<String,Object>> queryOaBusPageDetailColumns(@RequestBody Map<String,Object> map) {
        Result<Map<String,Object>> result = new Result<Map<String,Object>>();
        Map<String,Object> objectMap=new TreeMap<>();
        if (map.get("functionId")==null){
            result.error500("请传入正确的参数");
            return result;
        }
        Integer functionId=(Integer) map.get("functionId");
        BusFunction busFunction = busFunctionService.getOneByFunId(functionId.toString());
        if (busFunction==null|| busFunction.getIId()==null || busFunction.getIPageId()==null){
            result.error500("数据查询失败");
            return result;
        }
        List<BusPageDetail> allColumsListPageDtail = busPageDetailService.getAllColumsListPageDtail(busFunction.getIId(), busFunction.getIPageId());
        if (allColumsListPageDtail==null||allColumsListPageDtail.size()<1){
            result.error500("无数据");
            return result;
        }
        Map<String, Object> sqlCodeDictAllSelect = oaBusdataService.getSqlCodeDictAllSelect(allColumsListPageDtail);
        if (sqlCodeDictAllSelect==null){
            result.error500("数据查询失败");
            return result;
        }
        List<Map<String,String>> allColumsList = busPageDetailService.getAllColumsList(busFunction.getIId(), busFunction.getIPageId());
        if(allColumsList==null || allColumsList.size()<1) {
            result.error500("暂无数据");
        }else {
            String s = listColumns(allColumsList);
            if (s==null){
                result.setSuccess(false);
                result.setMessage("查询失败");
                return result;
            }
            List<Map<String, Object>> list = oaBusdataService.getModifyFieldDataOne(s,map.get("tableName").toString(),(Integer)map.get("iid"));
            if (list==null || allColumsList.size()<1){
                result.setSuccess(false);
                result.setMessage("查询数据失败");
                return result;
            }
            objectMap.put("allColumsList",allColumsList);
            objectMap.put("columnAlldata",sqlCodeDictAllSelect);
            objectMap.put("data",list);
            result.setResult(objectMap);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * @Description: 业务数据表
     * 运维工具----列表页----------
     */
    @AutoLog(value = "业务数据表-根据条件查询业务数据列表")
    @ApiOperation(value = "业务数据表-根据条件查询业务数据列表", notes = "业务数据表-根据条件查询业务数据列表")
    @PostMapping(value = "/queryOaBusdataList")
    @ResponseBody
    public Result<Map<String, Object>> queryOaBusdataListByTableId(
                    @RequestBody Map<String,Object> map) {
        Result<Map<String, Object>> result = new Result<>();
        Map<String, Object> res=new HashMap<>();
        Integer pageNo=(Integer) map.get("pageNo");
        Integer pageSize=(Integer) map.get("pageSize");
        if (pageNo==null || pageNo<1){
            pageNo=1;
        }
        if (pageSize==null || pageSize<1){
            pageSize=10;
        }
        String modelId =map.get("modelId").toString();
        Map<String,Object> maps = (Map<String,Object>) map.get("map");

        BusModel busModelById=null;
        try {
            if (StringUtils.isEmpty(maps.get("function_id").toString())){
                result.setSuccess(false);
                result.setMessage("业务查询失败");
                return result;
            }
            String functionId =  maps.get("function_id").toString();
            BusFunction oneByFunId = busFunctionService.getOneByFunId(functionId);
            if (StringUtils.isEmpty(modelId)){
                if (oneByFunId==null || oneByFunId.getIBusModelId()==null){
                    result.setSuccess(false);
                    result.setMessage("业务数据查询失败");
                    return result;
                }else {
                     busModelById = ibusModelService.getBusModelById(oneByFunId.getIBusModelId());
                }
            }else {
                 busModelById = ibusModelService.getBusModelById(Integer.valueOf(modelId));
            }
//            ----------------------------------------------------------
            if (busModelById==null || busModelById.getSBusdataTable()==null){
                result.setSuccess(false);
                result.setMessage("业务数据查询失败");
                return result;
            }
            //根据tableName查询列和表头名称
            List<Map<String, String>> listColumns = busPageDetailService.getConColums(Integer.valueOf(functionId),oneByFunId.getIPageId());
            boolean b=false;
            for (Map<String, String> mt : listColumns) {
                if (mt.get("s_table_column").equals("s_title")){
                    b=true;
                }
            }
            Map<String, String> addtitel=new HashMap<>();
            addtitel.put("s_table_column","s_title");
            addtitel.put("s_column_name","标题");
            List<Map<String, String>> listColumnsNew =new ArrayList<>();
            if (b==false){
                listColumnsNew.add(addtitel);
            }
            for (int i = 0; i < listColumns.size(); i++) {
                listColumnsNew.add(listColumns.get(i));
            }
            String s = listColumns(listColumnsNew);
//            List<BusPageDetail> listColumns = busPageDetailService.getListByFunID(functionId);
//            String s = listColumnsBusPageDetail(listColumns);
            if (s==null){
                result.setSuccess(false);
                result.setMessage("查询失败");
                return result;
            }
            res.put("colList", listColumnsNew);//表头及名称
            IPage<Map<String, Object>> list = oaBusdataService.getModifyFieldList(pageNo,pageSize , s, busModelById.getSBusdataTable(),maps);
            res.put("dataList",list);//表数据
            res.put("tableName",busModelById.getSBusdataTable());
            result.setResult(res);
        } catch (Exception e) {
            log.error(e.toString());
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("业务数据查询失败");
        }
        return result;
    }


    /**
     * @Description: 业务数据表
     * 运维工具----根据表明和ID获取某一条的信息----------
     */
    @AutoLog(value = "业务数据表-根据表面和ID查询详细信息")
    @ApiOperation(value = "业务数据表-根据表面和ID查询详细信息", notes = "业务数据表-根据表面和ID查询详细信息")
    @PostMapping(value = "/queryOaBusdataByTableId")
    @ResponseBody
    public Result<Map<String, Object>> queryOaBusdataByTableId(@RequestBody Map<String,Object> map) {
       String tableName = (String)map.get("tableName");
        String iId = (String)map.get("iId");
        Integer tableId = Integer.valueOf(iId);
        //查询当前用户，作为assignee
//        String token = request.getHeader(DefContants.X_ACCESS_TOKEN);
//        String username = JwtUtil.getUsername(token);
        Result<Map<String, Object>> result = new Result<>();
        try {
            Map<String, Object> data = oaBusdataService.getBusDataById(tableName, tableId);
            result.setResult(data);
            result.setMessage(tableName);
        } catch (Exception e) {
            log.error(e.toString());
            result.setSuccess(false);
            result.setMessage("业务数据查询失败");
        }
        return result;
    }


    /**
     * @Description: 业务数据表
     * 运维工具----动态修改数据----------
     */
    @PostMapping("/updateAll")
    @ApiOperation("根据ID更新业务数据")
    public Result updateAll(@RequestBody Map<String, Object> map) {
        Result<Map<String, Object>> result=new Result<>();
        try {
            if(map.size() <= 0){
                return result.error("请确实数据的完整性！！！");
            }
            Map<String, Object> formData =(Map<String, Object>) map.get("formData");
            if(map.containsKey("tableName") && formData.containsKey("i_id")){
                formData.put("tableName",map.get("tableName"));
                int data = oaBusdataService.updateAllOaData(formData);
                if (data==0){
                    return result.error("修改数据失败,请确实数据的完整性！");
                }
                return result.ok(data);
            }else {
                return result.error("更新数据失败,请确实数据的完整性！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return result.error("更新数据失败");
        }
    }


    public String listColumns(List<Map<String, String>> listColumns) {
        if (listColumns.size() == 0 || listColumns == null) {
            return null;
        }
        StringBuffer column = new StringBuffer("");
        for (Map<String, String> map : listColumns) {
            column.append(map.get("s_table_column") + ",");
        }
        String col = column.substring(0, column.length() - 1);
        return col;
    }

    public String listColumnsBusPageDetail(List<BusPageDetail> listColumns) {
        if (listColumns.size() == 0 || listColumns == null) {
            return null;
        }
        StringBuffer column = new StringBuffer("");
        for (BusPageDetail map : listColumns) {
            column.append(map.getSTableColumn() + ",");
        }
        String col = column.substring(0, column.length() - 1);
        return col;
    }

    /**
     * 折叠 进入前校验是否存在业务
     * @param
     * @return
     */
    @AutoLog(value = "业务和流程关联配置表-进入前校验是否存在业务")
    @ApiOperation(value="业务和流程关联配置表-进入前校验是否存在业务", notes="业务和流程关联配置表-进入前校验是否存在业务")
    @GetMapping(value = "/getProcDefKey")
    public Result<?> getProcDefKey(String functionId) {
        Result<String> result = new Result<>();
        try {
            Map<String,Object> map=new HashMap<>();
            map.put("functionId",functionId);
            List<BusProcSet>  busProcSetList= busProcSetService.getProcSetByprocbuttonId(map);
            for (int i = 0; i < busProcSetList.size(); i++) {
                if (busProcSetList.get(i).getProcDefKey()!=null){
                    result.setResult("1");
                    return result;
                }
            }
            result.setResult("0");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过id查询
     *----------------------------折叠一级--------------------
     * @param
     * @return
     */
    @AutoLog(value = "业务数据表-通过id查询")
    @ApiOperation(value = "业务数据表-通过id查询", notes = "业务数据表-通过id查询")
    @PostMapping(value = "/queryTaskList")
    public Result<List<Map<String,Object>>> queryTaskList(@RequestBody String json) {
        Result<List<Map<String,Object>>> result = new Result<>();
        Map maps = (Map) JSONObject.parse(json);
        String modelId = maps.get("modelId") + "";
        QueryWrapper<BusModel> queryWrapper = new QueryWrapper<BusModel>();
        BusModel busModel = new BusModel();
        busModel.setIId(Integer.parseInt(modelId));
        queryWrapper.setEntity(busModel);
        busModel = ibusModelService.getOne(queryWrapper);
        String tableName = busModel.getSBusdataTable();
        List<String> taskNameList = oaBusdataService.getTaskNameList(tableName);
        //处理数据-拆分
        List<Map<String,Object>> mapList=new LinkedList<>();
        String[] split;
        for (int i = 0; i < taskNameList.size(); i++) {
            Map<String,Object> map=new TreeMap<>();
             split = taskNameList.get(i).split("-");
            map.put("taskKey",split[0]);
            if (split[0]!=null && split[0].equalsIgnoreCase("newtask")){
                map.put("taskName","无任务");
            }else {
                map.put("taskName",split[split.length-1]);
            }
            mapList.add(map);
//            map.remove("taskKey");
//            map.remove("taskName");
        }
        if (taskNameList == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(mapList);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * ------------------------------------------------折叠二级---------
     *通过model_id查询出对应的oa_busdata中的数据，简单查询，不带条件
     * @param json
     * @return
     */
    @AutoLog(value = "业务数据表-modelId查询出对应的busdata列表")
    @ApiOperation(value = "业务数据表-modelId查询出对应的busdata列表", notes = "业务数据表-modelId查询出对应的busdata列表")
    @PostMapping(value = "/queryByModelIdNoPage")
    @ResponseBody
    public Result<Map<String, Object>> queryByModelIdNoPage(@RequestBody String json, HttpServletRequest request) {
        log.info(json);
        //查询当前用户，作为assignee
        String token = request.getHeader(DefContants.X_ACCESS_TOKEN);
        String username = JwtUtil.getUsername(token);
        Result<Map<String, Object>> result = new Result<>();
        try {
            result = oaBusdataService.getByModelIdAndTaskName(json, username);
        } catch (Exception e) {
            log.error(e.toString());
            result.setSuccess(false);
            result.setMessage("业务数据查询失败");
        }
        return result;
    }

    //上传文件地址
    //单文件
    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;
    //多文件
    @Value(value = "${jeecg.path.uploadfile}")
    private String uploadfile;


    /**
     * 通过iid-获取文件后缀名
     * @param
     * @return
     */
    @AutoLog(value = "业务和流程关联配置表-获取文件后缀名")
    @ApiOperation(value="业务和流程关联配置表-获取文件后缀名", notes="业务和流程关联配置表-获取文件后缀名")
    @GetMapping(value = "/getFileSuffixName")
    public Result<?> getFileSuffixName(String iid) {
        Result<String> result = new Result<>();
        //查询当前文件地址
        try {
            OaFile oaFile1 = oaFileService.queryById(Integer.valueOf(iid));
            String substring = oaFile1.getSFilePath().substring(oaFile1.getSFilePath().lastIndexOf("."));
            result.setResult(substring);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    /**
     * 单文件上传
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/upload/")
    public Result<?> upload( HttpServletRequest request, HttpServletResponse response) {
        Result<OaFile> result = new Result<>();
        try {
            //获取 文件表Id
            String iId = request.getParameter("iid");
            OaFile oaFile1 = oaFileService.queryById(Integer.valueOf(iId));
            //            截取后缀名比较
            String suffixName = oaFile1.getSFilePath().substring(oaFile1.getSFilePath().lastIndexOf("."));
            //截取数据库文件名
            String newName = oaFile1.getSFilePath().substring(oaFile1.getSFilePath().lastIndexOf(File.separator )+1);

//            String ctxPath = uploadpath;
//            String fileName = null;
//            Calendar calendar = Calendar.getInstance();
//            String path = ctxPath.replace("//", "/" +
//                    "") + "/" + calendar.get(Calendar.YEAR) +
//                    "/" + (calendar.get(Calendar.MONTH) + 1) +
//                    "/" + calendar.get(Calendar.DATE) + "/";

            File file = new File(oaFile1.getSFilePath());
            if (!file.exists()) {
                file.mkdirs();// 创建文件根目录
            }
            // 获取上传文件对象
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile mf = multipartRequest.getFile("file");
            String orgName = mf.getOriginalFilename();// 获取文件名
            String newSuffixName = orgName.substring(orgName.lastIndexOf("."));
            if (!newSuffixName.equalsIgnoreCase(suffixName)){
                result.setSuccess(false);
                result.setMessage("不能替换不同类型的文件");
                return result;
            }

//            fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.indexOf("."));
//            String savePath = file.getPath() + File.separator + fileName;
//            File savefile = new File(savePath);
            //读取老文件
            File oldSavefile = new File(oaFile1.getSFilePath());
//            if (oldSavefile.exists() && oldSavefile.isFile()){
                //将savefile改名为oldSavefile
//            savefile.renameTo(oldSavefile);
                //删除老文件
            if (oldSavefile.exists()){
                oldSavefile.delete();
            }
                //存放新文件
                FileCopyUtils.copy(mf.getBytes(), oldSavefile);
//            }else {
//                result.setSuccess(false);
//                result.setMessage("上传失败");
//                return result;
//            }
//            OaFile oaFile = new OaFile();
//            oaFile.setSFileType("7");        // 附件类型为 4 附件
//            oaFile.setSFileName(orgName);        //设置附件名字
//            oaFile.setSFilePath(savePath);        //设置文件路径
//            oaFile.setSCreateBy(username);
//            oaFile.setDCreateTime(new Date());
//            oaFileService.save(oaFile);
//            QueryWrapper<OaFile> c = new QueryWrapper<>();
//            c.setEntity(oaFile);
//            OaFile ad = oaFileService.getOne(c);        //查询刚刚插入的那条数据的id
//            result.setResult(ad);
            result.setMessage(oldSavefile.toString());
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            log.error(e.getMessage(), e);
        }
        return result;
    }

}
