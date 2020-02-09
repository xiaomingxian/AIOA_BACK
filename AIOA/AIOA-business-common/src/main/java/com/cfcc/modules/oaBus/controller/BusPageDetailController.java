package com.cfcc.modules.oaBus.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.oaBus.entity.BusPageDetail;
import com.cfcc.modules.oaBus.mapper.BusPageDetailMapper;
import com.cfcc.modules.oaBus.service.IBusModelService;
import com.cfcc.modules.oaBus.service.IBusPageDetailService;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Description: 业务页面详情表（实际业务字段含义说明）
 * @Author: jeecg-boot
 * @Date: 2019-10-18
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "业务页面详情表（实际业务字段含义说明）")
@RestController
@RequestMapping("/oaBus/busPageDetail")
public class BusPageDetailController {
    @Autowired
    private IBusPageDetailService busPageDetailService;
    @Autowired
    private IBusModelService iBusModelService;
    @Autowired
    private BusPageDetailMapper busPageDetailMapper;
    @Autowired
    private IOaBusdataService iOaBusdataService;

    /**
     * 分页列表查询
     *
     * @param busPageDetail
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "业务页面详情表（实际业务字段含义说明）-分页列表查询")
    @ApiOperation(value = "业务页面详情表（实际业务字段含义说明）-分页列表查询", notes = "业务页面详情表（实际业务字段含义说明）-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<BusPageDetail>> queryPageList(BusPageDetail busPageDetail,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest req) {
        Result<IPage<BusPageDetail>> result = new Result<IPage<BusPageDetail>>();
		/*QueryWrapper<BusPageDetail> queryWrapper = QueryGenerator.initQueryWrapper(busPageDetail, req.getParameterMap());
		Page<BusPageDetail> page = new Page<BusPageDetail>(pageNo, pageSize);*/
        IPage<BusPageDetail> pageList = busPageDetailService.getBusPageDetailpage(pageNo, pageSize, busPageDetail);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }
    @AutoLog(value = "业务页面详情表（实际业务字段含义说明）-分页列表查询")
    @ApiOperation(value = "业务页面详情表（实际业务字段含义说明）-分页列表查询", notes = "业务页面详情表（实际业务字段含义说明）-分页列表查询")
    @GetMapping(value = "/queryListByName")
    public Result<List<BusPageDetail>> queryListByName(BusPageDetail busPageDetail) {
        Result<List<BusPageDetail>> result = new Result<List<BusPageDetail>>();
        QueryWrapper<BusPageDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(busPageDetail) ;
        List<BusPageDetail> list = busPageDetailService.list(queryWrapper);
        result.setSuccess(true);
        result.setResult(list);
        return result;
    }
    @AutoLog(value = "业务页面详情表（实际业务字段含义说明）-分页列表查询")
    @ApiOperation(value = "业务页面详情表（实际业务字段含义说明）-分页列表查询", notes = "业务页面详情表（实际业务字段含义说明）-分页列表查询")
    @GetMapping(value = "/queryListByFunctionId")
    public Result<List<BusPageDetail>> queryListByFunctionId(int ibusFunctionId,int ibusPageId ,
                                                      HttpServletRequest req) {
        BusPageDetail busPageDetail = new BusPageDetail();
        busPageDetail.setIBusPageId(ibusPageId) ;
        busPageDetail.setIBusFunctionId(ibusFunctionId) ;
        Result<List<BusPageDetail>> result = new Result<List<BusPageDetail>>();
		QueryWrapper<BusPageDetail> queryWrapper = new QueryWrapper<>();
		queryWrapper.setEntity(busPageDetail) ;
		List<BusPageDetail> list = busPageDetailService.list(queryWrapper);
		result.setSuccess(true);
        result.setResult(list);
        return result;
    }
@AutoLog(value = "业务页面详情表（实际业务字段含义说明）-通过functionId查询字段")
    @ApiOperation(value = "业务页面详情表（实际业务字段含义说明）-通过functionId查询字段", notes = "业务页面详情表（实际业务字段含义说明）-通过functionId查询字段")
    @GetMapping(value = "/getConditionByFunId")
    public List<Map<String, String>> getConditionByFunId(int functionId) {
        List<Map<String, String>> colList = iOaBusdataService.getQueryCondition(functionId);
        return colList;
    }

    @AutoLog(value = "业务页面详情表（实际业务字段含义说明）-分页列表查询")
    @ApiOperation(value = "业务页面详情表（实际业务字段含义说明）-分页列表查询", notes = "业务页面详情表（实际业务字段含义说明）-分页列表查询")
    @GetMapping(value = "/queryCount")
    public Result<Boolean> queryCount(String functionId, String pageId, String modelId) {
        Result<Boolean> result = new Result<Boolean>();
        String tableName = iBusModelService.getBusModelById(Integer.parseInt(modelId)).getSBusdataTable();
        int count = busPageDetailService.getBusPageDetailCount(functionId, pageId, tableName);
        if (count > 5) {
            result.setSuccess(false);
        } else {
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 根据pageId查询出对应的数据
     * @param pageId
     * @return
     */
    @AutoLog(value = "业务页面详情表（实际业务字段含义说明）-根据pageId查询出对应的数据")
    @ApiOperation(value = "业务页面详情表（实际业务字段含义说明）-根据pageId查询出对应的数据", notes = "业务页面详情表（实际业务字段含义说明）-根据pageId查询出对应的数据")
    @GetMapping(value = "/queryPageDetailDef")
    public List<BusPageDetail> queryPageDetailDef(int pageId) {
        QueryWrapper<BusPageDetail> queryWrapper = new QueryWrapper<>() ;
        queryWrapper.isNull("i_bus_function_id")
                        .eq("i_bus_page_id",pageId);
        List<BusPageDetail> list = busPageDetailService.list(queryWrapper);
        return list;
    }

    @AutoLog(value = "业务页面详情表（实际业务字段含义说明）-根据pageId查询出对应的数据")
    @ApiOperation(value = "业务页面详情表（实际业务字段含义说明）-根据pageId查询出对应的数据", notes = "业务页面详情表（实际业务字段含义说明）-根据pageId查询出对应的数据")
    @GetMapping(value = "/queryDirect")
    public Result<Map<String,Object>> queryDirect(int pageId) {
        Result<Map<String,Object>> result = new Result<>();
        QueryWrapper<BusPageDetail> queryWrapper = new QueryWrapper<>() ;
        queryWrapper.isNull("i_bus_function_id")
                .eq("i_bus_page_id",pageId);
        List<BusPageDetail> list = busPageDetailService.list(queryWrapper);
        return result;
    }

    /**
     * 添加
     *
     * @param busPageDetail
     * @return
     */
    @AutoLog(value = "业务页面详情表（实际业务字段含义说明）-添加")
    @ApiOperation(value = "业务页面详情表（实际业务字段含义说明）-添加", notes = "业务页面详情表（实际业务字段含义说明）-添加")
    @PostMapping(value = "/add")
    public Result<BusPageDetail> add(@RequestBody BusPageDetail busPageDetail) {
        Result<BusPageDetail> result = new Result<BusPageDetail>();
        try {
            log.info(busPageDetail.toString());
            //busPageDetailService.saveBusPageDetail(busPageDetail);
            QueryWrapper<BusPageDetail> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("s_table_column",busPageDetail.getSTableColumn())
                        .eq("i_bus_page_id",busPageDetail.getIBusPageId())
                        .isNull("i_bus_function_id");
            List<BusPageDetail> list = busPageDetailService.list(queryWrapper);
            if(list == null || list.size() ==0){
                busPageDetail.setIIsDefault(1) ;        //设置为默认
                busPageDetailService.save(busPageDetail);
                result.success("添加成功！");
            }else{
                result.success("该数据已存在，请勿重复添加！") ;
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    @AutoLog(value = "更新全部的字段")
    @ApiOperation(value = "更新全部的字段", notes = "更新全部的字段")
    @PostMapping(value = "/updateDetail")
    @ResponseBody
    public Result<String> updateDetail(@RequestBody BusPageDetail busPageDetail) {
        Result<String> result = new Result<>();
        try {
            boolean res = busPageDetailService.updatePageDetail(busPageDetail);
            result.setSuccess(res);
        } catch (Exception e) {
            log.error(e.toString());
            result.setSuccess(false);
            result.setMessage("业务数据查询失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param busPageDetail
     * @return
     */
    @AutoLog(value = "业务页面详情表（实际业务字段含义说明）-编辑")
    @ApiOperation(value = "业务页面详情表（实际业务字段含义说明）-编辑", notes = "业务页面详情表（实际业务字段含义说明）-编辑")
    @PutMapping(value = "/edit")
    public Result<BusPageDetail> edit(@RequestBody BusPageDetail busPageDetail) {
        Result<BusPageDetail> result = new Result<BusPageDetail>();
        BusPageDetail busPageDetailEntity = busPageDetailService.getById(busPageDetail.getIId());
        if (busPageDetailEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = busPageDetailService.updateById(busPageDetail);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }
        return result;
    }

    /**
     * 编辑
     * functionId: '',
     * pageId: '',
     * modelId: '',
     *
     * @param busPageDetail
     * @return
     */
    @AutoLog(value = "业务页面详情表（实际业务字段含义说明）-更新或添加")
    @ApiOperation(value = "业务页面详情表（实际业务字段含义说明）-更新或添加", notes = "业务页面详情表（实际业务字段含义说明）-更新或添加")
    @PutMapping(value = "/editOrAdd")
    public Result<BusPageDetail> editOrAdd(@RequestBody BusPageDetail busPageDetail) {

        Result<BusPageDetail> result = new Result<BusPageDetail>();
        //这里busPageDetail.getSBusdataTable() 中的存的是modelId，所以需要查询出对应的表名
        String tableName = iBusModelService.getBusModelById(Integer.parseInt(busPageDetail.getSBusdataTable())).getSBusdataTable();
        busPageDetail.setSBusdataTable(tableName);
        /*QueryWrapper<BusPageDetail> queryWrapper = new QueryWrapper<>();
        BusPageDetail pageDetailNew = new BusPageDetail();
        pageDetailNew.setSBusdataTable(tableName);
        pageDetailNew.setIBusFunctionId(busPageDetail.getIBusFunctionId());
        pageDetailNew.setIBusPageId(busPageDetail.getIBusPageId());
        pageDetailNew.setSTableColumn(busPageDetail.getSTableColumn());

        queryWrapper.setEntity(pageDetailNew);
        pageDetailNew = busPageDetailService.getOne(queryWrapper);*/
        //如果不存在的话就插入，存在的话就更新
        if (busPageDetail.getIId() == null) {
            boolean res = busPageDetailService.save(busPageDetail);
        } else {
            //busPageDetail.setIId(pageDetailNew.getIId());
            int res = busPageDetailMapper.updateById(busPageDetail);
            if (res > 0) {
                result.success("保存成功!");
            }
        }
        return result;
    }

    /**
     * 初始化页面模板定义的含义，
     * @param busPageDetail
     * @return
     */
    @AutoLog(value = "业务页面详情表（实际业务字段含义说明）-初始化页面模板定义的含义")
    @ApiOperation(value = "业务页面详情表（实际业务字段含义说明）-初始化页面模板定义的含义", notes = "业务页面详情表（实际业务字段含义说明）-初始化页面模板定义的含义")
    @GetMapping(value = "/initDetail")
    public Result<List<BusPageDetail>> initDetail( int functionId,int pageId ,int modelId) {

        Result<List<BusPageDetail>> result = new Result<List<BusPageDetail>>();
        String tableName = iBusModelService.getBusModelById(modelId).getSBusdataTable();
        QueryWrapper<BusPageDetail> queryWrapper = new QueryWrapper<>() ;
        queryWrapper.isNull("i_bus_function_id")
                .eq("i_bus_page_id",pageId);
        //查询出页面模板定义后的数据
        List<BusPageDetail> list = busPageDetailService.list(queryWrapper);
        QueryWrapper<BusPageDetail> queryWrapperFun = new QueryWrapper<>() ;
        queryWrapperFun.eq("i_bus_function_id",functionId)
                .eq("i_bus_page_id",pageId) ;
        //查询出functionId对应的数据
        List<BusPageDetail> listFun = busPageDetailService.list(queryWrapperFun);
        if(list != null || list.size() > 0){
            Iterator iterator = list.iterator();
            while(iterator.hasNext()){
              BusPageDetail detail = (BusPageDetail) iterator.next();
              listFun.forEach(entry ->{
                  if(entry.getSTableColumn().equals(detail.getSTableColumn())){
                      iterator.remove();
                  }
              });
            }
        }
        //将未模板中定义了却不在functionId对应的数据中的数据，插入到functiong的数据中
        if(list != null ||list.size() > 0){
            list.forEach(entry ->{
                entry.setIBusFunctionId(functionId);
                entry.setSBusdataTable(tableName);
                entry.setIIsDefault(0);
            });
            boolean res = busPageDetailService.saveBatch(list);
            if(res){
                result.success("true") ;
            }else{
                result.error500("批量插入失败！！！") ;
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
    @AutoLog(value = "业务页面详情表（实际业务字段含义说明）-通过id删除")
    @ApiOperation(value = "业务页面详情表（实际业务字段含义说明）-通过id删除", notes = "业务页面详情表（实际业务字段含义说明）-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            busPageDetailService.removeById(id);
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
    @AutoLog(value = "业务页面详情表（实际业务字段含义说明）-批量删除")
    @ApiOperation(value = "业务页面详情表（实际业务字段含义说明）-批量删除", notes = "业务页面详情表（实际业务字段含义说明）-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<BusPageDetail> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<BusPageDetail> result = new Result<BusPageDetail>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.busPageDetailService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "业务页面详情表（实际业务字段含义说明）-通过id查询")
    @ApiOperation(value = "业务页面详情表（实际业务字段含义说明）-通过id查询", notes = "业务页面详情表（实际业务字段含义说明）-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<BusPageDetail> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<BusPageDetail> result = new Result<BusPageDetail>();
        BusPageDetail busPageDetail = busPageDetailService.getById(id);
        if (busPageDetail == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(busPageDetail);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        // Step.1 组装查询条件
        QueryWrapper<BusPageDetail> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                BusPageDetail busPageDetail = JSON.parseObject(deString, BusPageDetail.class);
                queryWrapper = QueryGenerator.initQueryWrapper(busPageDetail, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<BusPageDetail> pageList = busPageDetailService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "业务页面详情表（实际业务字段含义说明）列表");
        mv.addObject(NormalExcelConstants.CLASS, BusPageDetail.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("业务页面详情表（实际业务字段含义说明）列表数据", "导出人:Jeecg", "导出信息"));
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
                List<BusPageDetail> listBusPageDetails = ExcelImportUtil.importExcel(file.getInputStream(), BusPageDetail.class, params);
                busPageDetailService.saveBatch(listBusPageDetails);
                return Result.ok("文件导入成功！数据行数:" + listBusPageDetails.size());
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

}
