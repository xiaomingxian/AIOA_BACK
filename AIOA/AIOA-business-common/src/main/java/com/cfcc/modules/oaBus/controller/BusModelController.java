package com.cfcc.modules.oaBus.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.constant.CacheConstant;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.oaBus.entity.BusModelPermit;
import com.cfcc.modules.oaBus.entity.BusPage;
import com.cfcc.modules.oaBus.service.*;
import com.cfcc.modules.workflow.pojo.TaskCommon;
import com.cfcc.modules.workflow.service.TaskCommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 业务模块表（业务分类表）
 * @Author: jeecg-boot
 * @Date: 2019-10-12
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "业务模块表（业务分类表）")
@RestController
@RequestMapping("/oaBus/busModel")
public class BusModelController {
    @Autowired
    private IBusModelService busModelService;
    @Autowired
    private IBusFunctionService busFunctionService;
    @Autowired
    private IBusPageDetailService ibusPageDetailService;
    @Autowired
    private IBusPageService iBusPageService;
    @Autowired
    private IBusModelPermitService iBusModelPermitService;


    /**
     * 分页列表查询
     *
     * @param busModel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "业务模块表（业务分类表）-分页列表查询")
    @ApiOperation(value = "业务模块表（业务分类表）-分页列表查询", notes = "业务模块表（业务分类表）-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<BusModel>> queryPageList(BusModel busModel,
                                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                 HttpServletRequest req) {
        Result<IPage<BusModel>> result = new Result<IPage<BusModel>>();
		/*QueryWrapper<BusModel> queryWrapper = QueryGenerator.initQueryWrapper(busModel, req.getParameterMap());
		//queryWrapper.eq("i_order",busModel.getIOrder()) ;
		Page<BusModel> page = new Page<BusModel>(pageNo, pageSize);
		System.out.println(queryWrapper.toString());
		IPage<BusModel> pageList = busModelService.page(page, queryWrapper);*/

        IPage<BusModel> pageList = busModelService.getPage(pageNo, pageSize, busModel);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }
    @AutoLog(value = "业务模块表（业务分类表）-分页列表查询")
    @ApiOperation(value = "业务模块表（业务分类表）-分页列表查询", notes = "业务模块表（业务分类表）-分页列表查询")
    @GetMapping(value = "/getModelIdByUrl")
    public String getModelIdByUrl(String str) {
        return busModelService.getModelIdByUrlSer(str);
    }

    /**
     * 下拉列表--模块下拉列表
     *
     * @param
     * @return
     */
    @AutoLog(value = "业务模块表（业务分类表）-下拉列表")
    @ApiOperation(value = "业务模块表（业务分类表）-下拉列表", notes = "业务模块表（业务分类表）-下拉列表")
    @GetMapping(value = "/findList")
    public Result<List<BusModel>> findList() {
        Result<List<BusModel>> result = new Result<>();
        String schema = MycatSchema.getSchema();
        try {
            List<BusModel> busModelList = busModelService.findList(schema);
            result.setSuccess(true);
            result.setResult(busModelList);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 添加
     *
     * @param busModel
     * @return
     */
    @AutoLog(value = "业务模块表（业务分类表）-添加")
    @ApiOperation(value = "业务模块表（业务分类表）-添加", notes = "业务模块表（业务分类表）-添加")
    @PostMapping(value = "/add")
    @CacheEvict(value = CacheConstant.MODEL_CACHE,allEntries=true)
    public Result<BusModel> add(@RequestBody BusModel busModel) {
        Result<BusModel> result = new Result<BusModel>();
        try {
            BusModelPermit busModelPermit =  new BusModelPermit();
            busModelPermit.setSDisplay("1");//可见
            busModelPermit.setSPermitType("0");//说有人
            busModelPermit.setIBusModelId(busModel.getIId());//模块的id
            iBusModelPermitService.save(busModelPermit);
            busModelService.save(busModel);
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
     * @param busModel
     * @return
     */
    @AutoLog(value = "业务模块表（业务分类表）-编辑")
    @ApiOperation(value = "业务模块表（业务分类表）-编辑", notes = "业务模块表（业务分类表）-编辑")
    @PutMapping(value = "/edit")
    @CacheEvict(value = CacheConstant.MODEL_CACHE,allEntries=true)
    public Result<BusModel> edit(@RequestBody BusModel busModel) {
        Result<BusModel> result = new Result<BusModel>();
        BusModel busModelEntity = busModelService.getBusModelById(busModel.getIId());
        if (busModelEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = busModelService.updateById(busModel);
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
    @AutoLog(value = "业务模块表（业务分类表）-通过id删除")
    @ApiOperation(value = "业务模块表（业务分类表）-通过id删除", notes = "业务模块表（业务分类表）-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        String schema = MycatSchema.getSchema();
        try {
            busModelService.removeBusModelById(id,schema);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    @AutoLog(value = "稿纸头配置-稿纸头下拉选列表")
    @ApiOperation(value = "稿纸头配置-稿纸头下拉选列表", notes = "稿纸头配置-稿纸头下拉选列表")
    @GetMapping(value = "/modelList")
    public Result<Map<String,Object>> paperTitleList() {
        Result<Map<String,Object>> result = new Result<>();
        List<BusModel> paperList = busModelService.getModelList();
        List<BusPage> busPageList = iBusPageService.list();
        Map<String,Object> map = new HashMap<>() ;
        map.put("modelList",paperList);
        map.put("pageList",busPageList);
        if (paperList.size() == 0) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(map);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "业务模块表（业务分类表）-批量删除")
    @ApiOperation(value = "业务模块表（业务分类表）-批量删除", notes = "业务模块表（业务分类表）-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<BusModel> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<BusModel> result = new Result<BusModel>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.busModelService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "业务模块表（业务分类表）-通过id查询")
    @ApiOperation(value = "业务模块表（业务分类表）-通过id查询", notes = "业务模块表（业务分类表）-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<BusModel> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<BusModel> result = new Result<BusModel>();
        BusModel busModel = busModelService.getById(id);
        if (busModel == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(busModel);
            result.setSuccess(true);
        }
        return result;
    }

    /*
     * 通过model_id查询出对应的查询条件（busPageDetail）
     *
     * @param id
     * @return*/
   /* @AutoLog(value = "业务模块表-通过id查询出列条件")
    @ApiOperation(value = "业务模块表-通过id查询出列条件", notes = "业务模块表-通过id查询出列条件")
    @GetMapping(value = "/queryConditonById")
    public List<BusPageDetail> queryConditionById(@RequestParam(name = "id", required = true) String id) {
        List<BusFunction> busFunctionList = busFunctionService.findList();
        List<BusPageDetail> busPageDetailList = null;
        for (BusFunction busFunction : busFunctionList) {
            if (id.equals(busFunction.getIBusModelId())) {
                busPageDetailList = ibusPageDetailService.queryConditionById(busFunction.getIId());
                break;
            }
        }
        return busPageDetailList;
    }*/



    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        // Step.1 组装查询条件
        QueryWrapper<BusModel> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                BusModel busModel = JSON.parseObject(deString, BusModel.class);
                queryWrapper = QueryGenerator.initQueryWrapper(busModel, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<BusModel> pageList = busModelService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "业务模块表（业务分类表）列表");
        mv.addObject(NormalExcelConstants.CLASS, BusModel.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("业务模块表（业务分类表）列表数据", "导出人:Jeecg", "导出信息"));
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
                List<BusModel> listBusModels = ExcelImportUtil.importExcel(file.getInputStream(), BusModel.class, params);
                busModelService.saveBatch(listBusModels);
                return Result.ok("文件导入成功！数据行数:" + listBusModels.size());
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
     * 查询文件类别
     * @return
     */
    @AutoLog(value = "业务按钮-查询文件类别")
    @ApiOperation(value="业务按钮-查询文件类别", notes="业务按钮-查询文件类别")
    @GetMapping(value = "/getDocType")
    public Result getDocType() {
        Result<List<BusModel>> result = new Result<>();
        List<BusModel> button = busModelService.selectDocType();
        if(button==null) {
            result.error500("未找到对应实体");
        }else {
            result.setResult(button);
            result.setSuccess(true);
        }
        return result;
    }

}
