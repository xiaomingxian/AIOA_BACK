package com.cfcc.modules.oaBus.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.oaBus.entity.BusFunctionPermit;
import com.cfcc.modules.oaBus.entity.BusModelPermit;
import com.cfcc.modules.oaBus.service.IBusModelPermitService;
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
import java.util.List;
import java.util.Map;

/**
 * @Description: 业务模板
 * @Author: jeecg-boot
 * @Date: 2019-10-21
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "业务模板")
@RestController
@RequestMapping("/oaBus/busModelPermit")
public class BusModelPermitController {
    @Autowired
    private IBusModelPermitService busModelPermitService;

    @AutoLog(value = "业务模板-分页列表查询")
    @ApiOperation(value = "业务模板-分页列表查询", notes = "业务模板-分页列表查询")
    @GetMapping(value = "/pagelist")
    public Result<IPage<BusModelPermit>> queryPageList(@RequestParam(name = "ibusmodelId",required=false) Integer ibusmodelId,
                                                        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                        HttpServletRequest req) {
        Result<IPage<BusModelPermit>> result = new Result<IPage<BusModelPermit>>();
        IPage<BusModelPermit> pageList = busModelPermitService.findPage(ibusmodelId, pageNo, pageSize);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @AutoLog(value = "业务类型-分页列表查询")
    @ApiOperation(value = "业务类型-分页列表查询", notes = "业务类型-分页列表查询")
    @GetMapping(value = "/findAllList")
    public Result<IPage<BusModelPermit>> findAllList(BusModelPermit busModelPermit,
                                                        @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                        @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                        HttpServletRequest req) {
        Result<IPage<BusModelPermit>> result = new Result<IPage<BusModelPermit>>();
        IPage<BusModelPermit> pageList = busModelPermitService.findAllList(busModelPermit,pageNo, pageSize);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }
    /**
     * 添加
     *
     * @param busModelPermit
     * @return
     */
    @AutoLog(value = "业务模板-添加")
    @ApiOperation(value = "业务模板-添加", notes = "业务模板-添加")
    @PostMapping(value = "/add")
    public Result<BusModelPermit> add(@RequestBody BusModelPermit busModelPermit, HttpServletRequest request) {
        Result<BusModelPermit> result = new Result<BusModelPermit>();
        String schema = MycatSchema.getSchema();
        try {
            String token = request.getHeader("X-Access-Token");
            String username = JwtUtil.getUsername(token);
            String sPermitType = busModelPermit.getSPermitType();
            List<String> typeIdList = busModelPermitService.getTypeId(Integer.valueOf(busModelPermit.getIBusModelId()));
            switch (sPermitType) {
                case "0":
                    busModelPermit.setITypeId("");
                    busModelPermitService.save(busModelPermit);
                    break;
                case "1":
                    if (typeIdList.contains(busModelPermit.getITypeId())) {
                        result.error500("该角色已选择");
                        return result;
                    }
                    busModelPermitService.save(busModelPermit);
                    break;
                case "2":
                    String[] typeidList = busModelPermit.getITypeId().split(",");
                    for (int j = 0; j < typeidList.length; j++) {
                        if (typeIdList.contains(typeidList[j])) {
                            result.error500("该部门已选择");
                            return result;
                        }
                    }
                    for (int j = 0; j < typeidList.length; j++) {
                        if (!typeIdList.contains(typeidList[j])) {
                            busModelPermit.setITypeId(typeidList[j]);
                            busModelPermitService.save(busModelPermit);
                        }
                    }
                    break;
                case "3":
                    String[] userList = busModelPermit.getITypeId().split(",");
                    for (int j = 0; j <userList.length; j++) {
                        if( typeIdList.contains(userList[j])){
                            result.error500("该用户已选择");
                            return result;
                        }
                    }

                    for (int j = 0; j <userList.length; j++) {
                        if( !typeIdList.contains(userList[j])){
                            busModelPermit.setITypeId(userList[j]);
                            busModelPermitService.save(busModelPermit);
                        }
                    }
                    break;

            }
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
     * @param busModelPermit
     * @return
     */
    @AutoLog(value = "业务模板-编辑")
    @ApiOperation(value = "业务模板-编辑", notes = "业务模板-编辑")
    @PutMapping(value = "/edit")
    public Result<BusModelPermit> edit(@RequestBody BusModelPermit busModelPermit) {
        Result<BusModelPermit> result = null;
        String schema = MycatSchema.getSchema();
        try {
            result = new Result<BusModelPermit>();
            BusModelPermit busModelPermitEntity = busModelPermitService.findById(busModelPermit.getIId());
            if (busModelPermitEntity == null) {
                result.error500("未找到对应实体");
            } else {
                String sPermitType = busModelPermit.getSPermitType();
                List<String> typeIdList = busModelPermitService.getTypeId(Integer.valueOf(busModelPermit.getIBusModelId()));
                switch (sPermitType) {
                    case "0":
                        busModelPermit.setITypeId("");
                        busModelPermitService.updateBYIid(busModelPermit,schema);
                        break;
                    case "1":
                        if (typeIdList.contains(busModelPermit.getITypeId()) && !busModelPermitEntity.getITypeId().equals(busModelPermit.getITypeId())) {
                        result.error500("该角色已选择");
                        return result;
                    }else{
                        busModelPermitService.updateBYIid(busModelPermit,schema);
                    }

                    break;
                    case "2":
                        if (typeIdList.contains(busModelPermit.getITypeId()) && !busModelPermitEntity.getITypeId().equals(busModelPermit.getITypeId())) {
                            result.error500("该部门已选择");
                            return result;
                        }
                        else{
                            busModelPermitService.updateBYIid(busModelPermit,schema);
                        }
                        break;
                    case "3":
                        if (typeIdList.contains(busModelPermit.getITypeId()) && !busModelPermitEntity.getITypeId().equals(busModelPermit.getITypeId())) {
                            result.error500("该用户已选择");
                            return result;
                        }
                        else{
                            busModelPermitService.updateBYIid(busModelPermit,schema);
                        }
                        break;

                }
                result.success("修改成功!");

            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("修改成功");
        }

        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "业务模板-通过id删除")
    @ApiOperation(value = "业务模板-通过id删除", notes = "业务模板-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(value = "id", required = false) String id) {
        String schema = MycatSchema.getSchema();
        try {
            busModelPermitService.deleteById(id,schema);
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
    @AutoLog(value = "业务模板-批量删除")
    @ApiOperation(value = "业务模板-批量删除", notes = "业务模板-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<BusModelPermit> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<BusModelPermit> result = new Result<BusModelPermit>();
        String schema = MycatSchema.getSchema();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            List<String> list = Arrays.asList(ids.split(","));
            for (int i = 0; i < list.size(); i++) {
                this.busModelPermitService.deleteById(list.get(i),schema);
            }
            result.success("删除成功!");
        }
        return result;
    }
    @GetMapping(value = "/findList")
    public Result findList() {
        String schema = MycatSchema.getSchema();
        Result<List<BusModelPermit>> result = new Result<>();
        List<BusModelPermit> ModelPermitList = busModelPermitService.findList(schema);
        if (ModelPermitList.size() == 0) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(ModelPermitList);
            result.setSuccess(true);
        }
        return result;

    }
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "业务模板-通过id查询")
    @ApiOperation(value = "业务模板-通过id查询", notes = "业务模板-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<BusModelPermit> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<BusModelPermit> result = new Result<BusModelPermit>();
        BusModelPermit busModelPermit = busModelPermitService.getById(id);
        if (busModelPermit == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(busModelPermit);
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
        QueryWrapper<BusModelPermit> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                BusModelPermit busModelPermit = JSON.parseObject(deString, BusModelPermit.class);
                queryWrapper = QueryGenerator.initQueryWrapper(busModelPermit, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<BusModelPermit> pageList = busModelPermitService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "业务模板列表");
        mv.addObject(NormalExcelConstants.CLASS, BusModelPermit.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("业务模板列表数据", "导出人:Jeecg", "导出信息"));
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
                List<BusModelPermit> listBusModelPermits = ExcelImportUtil.importExcel(file.getInputStream(), BusModelPermit.class, params);
                busModelPermitService.saveBatch(listBusModelPermits);
                return Result.ok("文件导入成功！数据行数:" + listBusModelPermits.size());
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
