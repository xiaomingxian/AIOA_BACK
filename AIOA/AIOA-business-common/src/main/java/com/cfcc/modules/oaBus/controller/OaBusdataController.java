package com.cfcc.modules.oaBus.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.exception.AIOAException;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.OaBusdata;
import com.cfcc.modules.oaBus.entity.TableCol;
import com.cfcc.modules.oaBus.service.*;
import com.cfcc.modules.shiro.vo.DefContants;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.service.ISysUserService;
import com.cfcc.modules.workflow.vo.OaBusdataPermitRead;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.*;

/**
 * @Description: 业务数据表
 * @Author: jeecg-boot
 * @Date: 2019-10-21
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "业务数据表")
@RestController
@RequestMapping("/oaBus/oaBusdata")
public class OaBusdataController {
    @Autowired
    private IOaBusdataService oaBusdataService;
    @Autowired
    private IBusModelService ibusModelService;
    @Autowired
    private ISysUserService isysUserService;
    @Autowired
    private IOaBusdataPermitService oaBusdataPermitService;
    @Autowired
    private IBusFunctionService ibusFunctionService;
    @Autowired
    private ButtonPermissionService buttonPermissionService;
    @Value("${system.runDate}")
    private String runDate;


    //动态字段拼接
    @Autowired
    private IBusPageDetailService busPageDetailService;



    /**
     * 分页列表查询
     *
     * @param oaBusdata
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "业务数据表-分页列表查询")
    @ApiOperation(value = "业务数据表-分页列表查询", notes = "业务数据表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<OaBusdata>> queryPageList(OaBusdata oaBusdata,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                  HttpServletRequest req) {
        Result<IPage<OaBusdata>> result = new Result<IPage<OaBusdata>>();
		/*QueryWrapper<OaBusdata> queryWrapper = QueryGenerator.initQueryWrapper(oaBusdata, req.getParameterMap());
		Page<OaBusdata> page = new Page<OaBusdata>(pageNo, pageSize);*/
        IPage<OaBusdata> pageList = oaBusdataService.getBusdataPage(pageNo, pageSize, oaBusdata);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 分页列表查询
     *
     * @param req
     * @return
     */
    @AutoLog(value = "业务数据表-查询busdata表中的列名和含义")
    @ApiOperation(value = "业务数据表-查询busdata表中的列名和含义", notes = "业务数据表-查询busdata表中的列名和含义")
    @GetMapping(value = "/getPageDataForm")
    public Result<IPage<TableCol>> getPageDataForm(@RequestParam(name = "pageId") int pageId,
                                                   @RequestParam(name = "modelId") int modelId,
                                                   @RequestParam(name = "functionId") int functionId,
                                                   HttpServletRequest req) {
        Result<IPage<TableCol>> result = new Result<IPage<TableCol>>();
        IPage<TableCol> pageList = oaBusdataService.getTableColList(pageId, modelId, functionId);
        result.setResult(pageList);
        return result;
    }


    /**
     * 添加
     *
     * @param oaBusdata
     * @return
     */
    @AutoLog(value = "业务数据表-添加")
    @ApiOperation(value = "业务数据表-添加", notes = "业务数据表-添加")
    @PostMapping(value = "/add")
    public Result<OaBusdata> add(@RequestBody OaBusdata oaBusdata) {
        Result<OaBusdata> result = new Result<OaBusdata>();
        try {
            oaBusdataService.save(oaBusdata);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param oaBusdata
     * @return
     */
    @AutoLog(value = "业务数据表-编辑")
    @ApiOperation(value = "业务数据表-编辑", notes = "业务数据表-编辑")
    @PutMapping(value = "/edit")
    public Result<OaBusdata> edit(@RequestBody OaBusdata oaBusdata) {
        Result<OaBusdata> result = new Result<OaBusdata>();
        OaBusdata oaBusdataEntity = oaBusdataService.getBusdataById(oaBusdata.getIId());
        if (oaBusdataEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = oaBusdataService.updateById(oaBusdata);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "业务数据表-通过id删除")
    @ApiOperation(value = "业务数据表-通过id删除", notes = "业务数据表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            oaBusdataService.removeById(id);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "业务数据表-批量删除")
    @ApiOperation(value = "业务数据表-批量删除", notes = "业务数据表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<OaBusdata> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<OaBusdata> result = new Result<OaBusdata>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.oaBusdataService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "业务数据表-通过id查询")
    @ApiOperation(value = "业务数据表-通过id查询", notes = "业务数据表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<OaBusdata> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<OaBusdata> result = new Result<OaBusdata>();
        OaBusdata oaBusdata = oaBusdataService.getById(id);
        if (oaBusdata == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(oaBusdata);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 提交发布范围
     *
     * @param json
     * @return
     */
    @AutoLog(value = "业务数据表-提交发布范围")
    @ApiOperation(value = "业务数据表-提交发布范围", notes = "业务数据表-提交发布范围")
    @PostMapping(value = "/commitPer")
    @ResponseBody
    public String commitPer(@RequestBody String json, HttpServletRequest request) {

        SysUser currentUser = isysUserService.getCurrentUser(request);
        boolean res = oaBusdataService.commitPer(json, currentUser);
        String result = "";
        if (res) {
            result = "success";
        } else {
            result = "success";
        }
        return result;
    }

    /**
     * 查询页面路径地址
     *
     * @param json
     * @return
     */
    @AutoLog(value = "业务数据表-查询页面路径地址")
    @ApiOperation(value = "业务数据表-查询页面路径地址", notes = "业务数据表-查询页面路径地址")
    @PostMapping(value = "/getPageUrl")
    @ResponseBody
    public String getPageUrl(@RequestBody String json) {


        Map map = (Map) JSONObject.parse(json);
        //String modelId =  map.get("modelId") + "";
        String functionId = map.get("functionId") + "";
        //String busDataId = map.get("busDataId")+ "" ;
        String result = oaBusdataService.getPageUrlSer(functionId).get("pageRef") + "";
        log.info("-----:" + result);
        return result;
    }


    /**
     * 通过model_id查询出对应的oa_busdata中的数据，简单查询，不带条件
     *
     * @param json
     * @return
     */
    @AutoLog(value = "业务数据表-modelId查询出对应的busdata列表")
    @ApiOperation(value = "业务数据表-modelId查询出对应的busdata列表", notes = "业务数据表-modelId查询出对应的busdata列表")
    @PostMapping(value = "/queryByModelId")
    @ResponseBody
    public Result<IPage<Map<String, Object>>> queryByModelId(@RequestBody String json, HttpServletRequest request) {
        log.info(json);
        //查询当前用户，作为assignee
        LoginInfo loginInfo = isysUserService.getLoginInfo(request);
        String realname = loginInfo.getRealname();
        String username = loginInfo.getUsername();

        Result<IPage<Map<String, Object>>> result = new Result<>();
        try {
            result = oaBusdataService.getByModelId(json, realname,username);
        } catch (Exception e) {
            log.error(e.toString());
            result.setSuccess(false);
            result.setMessage("业务数据查询失败");
        }
        return result;
    }


    /**
     * 根据modelId查询出对应的列表表头以及function列表作为下拉框中的选项
     *
     * @param json
     * @return
     */
    @AutoLog(value = "业务数据表-根据modelId和functionId查询出对应的列表表头")
    @ApiOperation(value = "业务数据表-根据modelId和functionId查询出对应的列表表头", notes = "业务数据表-根据modelId和functionId查询出对应的列表表头")
    @PostMapping(value = "/queryFunSelByModelId")
    @ResponseBody
    public Map<String, Object> queryFunSelByModelId(@RequestBody String json, HttpServletRequest request) {
        LoginInfo loginInfo = isysUserService.getLoginInfo(request);
        SysDepart depart = loginInfo.getDepart();
        Map<String, Object> result = new HashMap<>();
        //System.out.println(json);
        Map maps = (Map) JSONObject.parse(json);
        String modelId = maps.get("modelId") + "";
        String functionId = maps.get("function_id") + "";

        //List<Map<String, Object>> funList = oaBusdataService.getSelFun(modelId);
        /*QueryWrapper<BusFunction> queryWrapper = new QueryWrapper<>();
        BusFunction busFunction = new BusFunction();
        busFunction.setIBusModelId(Integer.parseInt(modelId));
        queryWrapper.setEntity(busFunction);*/
        List<BusFunction> busFunctionList = ibusFunctionService.queryByModelId(modelId);
        //权限过滤，有些fun只能特定的部门能看到
        busFunctionList = oaBusdataService.getFunListByFunUnit(busFunctionList, depart);


        result.put("funList", busFunctionList);

        if (functionId == null || "".equals(functionId.trim())) {
            if (busFunctionList.size() > 0) {
                functionId = busFunctionList.get(0).getIId() + "";
            } else {
                return null;
            }
        }
        List<Map<String, Object>> colList = oaBusdataService.getQueryCondition(Integer.parseInt(functionId));
        result.put("colList", colList);
        String tableName = ibusModelService.getById(Integer.parseInt(modelId)).getSBusdataTable();
        int year = LocalDate.now().getYear();
        int res = year - Integer.parseInt(runDate);
        List<String> list = new ArrayList<>();
        for (int i = 0; i <= res; i++) {
            list.add((Integer.parseInt(runDate) + i) + "");
        }
        result.put("d_create_time", list);
        result.put("tableName", tableName);
        return result;
    }

    /**
     * 查询出具体的业务数据
     *
     * @param
     * @return
     */
    @AutoLog(value = "业务数据表-查询出具体的业务数据")
    @ApiOperation(value = "业务数据表-查询出具体的业务数据", notes = "业务数据表-查询出具体的业务数据")
    @PostMapping(value = "/queryBusdataById")
    @ResponseBody
    public Result queryBusdataById(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        try {
            // 查询当前用户，作为assignee
            LoginInfo loginInfo = isysUserService.getLoginInfo(request);

            // 查询结果
            Map<String, Object> result = oaBusdataService.getBusDataAndDetailById(map, loginInfo);
            if (result.containsKey("error")) {
                return Result.error(result.get("error").toString());
            }
            return Result.ok(result);
        } catch (AIOAException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.toString());
            e.printStackTrace();
            return Result.error("查询业务详情失败");
        }

    }



    @AutoLog(value = "新建临时任务")
    @ApiOperation(value = "新建临时任务", notes = "新建临时任务")
    @PostMapping(value = "/queryNewTaskMsg")
    public Result queryDataByModelAndFunctionId(@RequestBody Map<String, Object> map, HttpServletRequest request) {

        try {
            String modelId = map.get("modelId") + "";
            String functionId = map.get("functionId") + "";
            //SysUser currentUser = isysUserService.getCurrentUser(request);
            LoginInfo loginInfo = isysUserService.getLoginInfo(request);
            Result res = oaBusdataService.queryDataByModelAndFunctionId(modelId, functionId, loginInfo);

            return res;
        } catch (AIOAException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("新增数据失败:" + e.toString());
            return Result.error("新增数据失败");
        }
    }


    /**
     * 通过model_id查询出对应的oa_busdata中的数据，简单查询，不带条件
     *
     * @param
     * @return
     */
    @AutoLog(value = "业务数据表-查询出具体的业务数据")
    @ApiOperation(value = "业务数据表-查询出具体的业务数据", notes = "业务数据表-查询出具体的业务数据")
    @PostMapping(value = "/getBusDataUserDepart")
    @ResponseBody
    public Map<String, Object> getBusDataUserDepart() {
        Map<String, Object> result = oaBusdataService.getBusDataUserDepartSer();
        return result;
    }


    /**
     * 导出excel
     * x
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        // Step.1 组装查询条件
        QueryWrapper<OaBusdata> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                OaBusdata oaBusdata = JSON.parseObject(deString, OaBusdata.class);
                queryWrapper = QueryGenerator.initQueryWrapper(oaBusdata, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<OaBusdata> pageList = oaBusdataService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "业务数据表列表");
        mv.addObject(NormalExcelConstants.CLASS, OaBusdata.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("业务数据表列表数据", "导出人:Jeecg", "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<OaBusdata> listoaBusdata = ExcelImportUtil.importExcel(file.getInputStream(), OaBusdata.class, params);
                oaBusdataService.saveBatch(listoaBusdata);
                return Result.ok("文件导入成功！数据行数:" + listoaBusdata.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入失败！");
    }

    /**
     * 修改文头
     *
     * @param
     * @return
     */
    @AutoLog(value = "业务按钮-修改文头")
    @ApiOperation(value = "业务按钮-修改文头", notes = "业务按钮-修改文头")
    @GetMapping(value = "/editTitle")
    public Result<OaBusdata> edit(OaBusdata oaBusdata, @RequestParam(name = "table") String table, HttpServletRequest request) {
        Result<OaBusdata> result = new Result<OaBusdata>();
        if (oaBusdata.getIId() == null || table == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = oaBusdataService.updateMiddleById(table, oaBusdata);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }
        return result;
    }


    /**
     * 分页条件查询公文链接
     *
     * @param oaBusdata
     * @param sBusdataTable
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "文号配置-分页条件查询公文链接")
    @ApiOperation(value = "文号配置-分页条件查询公文链接", notes = "文号配置-分页条件查询公文链接")
    @GetMapping(value = "/getOfficeList")
    public Result<IPage<OaBusdata>> getOfficeList(OaBusdata oaBusdata,
                                                  @RequestParam(name = "sBusdataTable") String sBusdataTable,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Result<IPage<OaBusdata>> result = new Result<>();
        IPage<OaBusdata> numList = oaBusdataService.selectDocList(oaBusdata, sBusdataTable, pageNo, pageSize);
        result.setSuccess(true);
        result.setResult(numList);
        return result;
    }

    /**
     * 已读情况的查询
     *
     * @param BusdataTable
     * @param pageNo
     * @param pageSize
     * @return
     */

    @GetMapping(value = "/getReadCase")
    public Result<IPage<OaBusdataPermitRead>> getReadCase(@RequestParam(name = "sBusdataTable") String BusdataTable,
                                                          @RequestParam(name = "iBusFunctionId") Integer iBusFunctionId,
                                                          @RequestParam(name = "iBusdataId") Integer iBusdataId,
                                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Result<IPage<OaBusdataPermitRead>> result = new Result<>();
        String sBusdataTable = BusdataTable + "_permit";
        IPage<OaBusdataPermitRead> userList = oaBusdataPermitService.queryUserList(sBusdataTable, iBusFunctionId, iBusdataId, pageNo, pageSize);
        result.setSuccess(true);
        result.setResult(userList);
        return result;
    }


    /**
     * 根据id查询model名称
     *
     * @param functionId
     * @return
     */
    @AutoLog(value = "业务数据表-通过id查询")
    @ApiOperation(value = "业务数据表-通过id查询", notes = "业务数据表-通过id查询")
    @GetMapping(value = "/getModelSname")
    public Result getModelSname(@RequestParam(name = "id", required = true) String functionId) {
        Result<Map<String, Object>> result = new Result<>();
        Map<String, Object> names = oaBusdataService.getFuncitonDataById(functionId);
        result.setResult(names);
        return result;
    }
}
