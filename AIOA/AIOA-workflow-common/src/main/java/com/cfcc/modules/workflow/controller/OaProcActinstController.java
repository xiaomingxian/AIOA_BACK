package com.cfcc.modules.workflow.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.system.service.CommonDynamicTableService;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.workflow.pojo.OaProcActinst;
import com.cfcc.modules.workflow.pojo.OaProcActinst;
import com.cfcc.modules.workflow.service.IoaProcActinstService;
import com.cfcc.modules.workflow.service.TaskCommonFoldService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.apache.commons.lang3.StringUtils;
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

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 流程节点配置
 * @Author: jeecg-boot
 * @Date: 2019-11-01
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "流程节点配置")
@RestController
@RequestMapping("/workflow/oaProcActinst")
public class OaProcActinstController {
    @Autowired
    private IoaProcActinstService OaProcActinstService;

    /**
     * 分页列表查询
     *
     * @param OaProcActinst
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "流程节点配置-分页列表查询")
    @ApiOperation(value = "流程节点配置-分页列表查询", notes = "流程节点配置-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage> queryPageList(OaProcActinst OaProcActinst,
                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                       HttpServletRequest req) {

        Result<IPage> iPageResult = new Result<>();
        IPage iPage = new Page(pageNo, pageSize);


        long count = OaProcActinstService.selectCount(OaProcActinst);
        List<com.cfcc.modules.workflow.pojo.OaProcActinst> oaProcActinsts = OaProcActinstService.selectData(OaProcActinst, pageNo, pageSize);

        oaProcActinsts.stream().forEach(oaProcActinst -> {
            String recordKey = oaProcActinst.getRecordKey();
            if (StringUtils.isNotBlank(recordKey)) {
                List<String> keys = new ArrayList<>();
                if (recordKey.contains(",")) {
                    for (String key : Arrays.asList(recordKey.split(","))) {
                        keys.add(key);
                    }
                } else {
                    keys.add(recordKey);
                }
                oaProcActinst.setRecordKeys(keys);
            }

        });

        //分页参数
        iPageResult.setSuccess(true);
        iPage.setTotal(count);
        iPage.setCurrent(pageNo);
        iPage.setRecords(oaProcActinsts);
        iPage.setSize(pageSize);
        iPageResult.setResult(iPage);

        return iPageResult;
    }

    /**
     * 添加
     *
     * @param OaProcActinst
     * @return
     */
    @AutoLog(value = "流程节点配置-添加")
    @ApiOperation(value = "流程节点配置-添加", notes = "流程节点配置-添加")
    @PostMapping(value = "/add")
    public Result add(@RequestBody OaProcActinst OaProcActinst) {
        Result result = new Result<OaProcActinst>();
        try {
            //先判断相同的key和环节有没有数据-
            Boolean b = OaProcActinstService.isExist(OaProcActinst);

            if (b) return Result.error("该流程的此环节已有数据请勿重复配置");

            OaProcActinstService.save(OaProcActinst);
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
     * @param OaProcActinst
     * @return
     */
    @AutoLog(value = "流程节点配置-编辑")
    @ApiOperation(value = "流程节点配置-编辑", notes = "流程节点配置-编辑")
    @PutMapping(value = "/edit")
    public Result<OaProcActinst> edit(@RequestBody OaProcActinst OaProcActinst) {
        Result<OaProcActinst> result = new Result<OaProcActinst>();
        OaProcActinst OaProcActinstEntity = OaProcActinstService.getById(OaProcActinst.getIId());
        if (OaProcActinstEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = OaProcActinstService.updateById(OaProcActinst);
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
    @AutoLog(value = "流程节点配置-通过id删除")
    @ApiOperation(value = "流程节点配置-通过id删除", notes = "流程节点配置-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            OaProcActinstService.removeById(id);
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
    @AutoLog(value = "流程节点配置-批量删除")
    @ApiOperation(value = "流程节点配置-批量删除", notes = "流程节点配置-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<OaProcActinst> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<OaProcActinst> result = new Result<OaProcActinst>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.OaProcActinstService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "流程节点配置-通过id查询")
    @ApiOperation(value = "流程节点配置-通过id查询", notes = "流程节点配置-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<OaProcActinst> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<OaProcActinst> result = new Result<OaProcActinst>();
        OaProcActinst OaProcActinst = OaProcActinstService.getById(id);
        if (OaProcActinst == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(OaProcActinst);
            result.setSuccess(true);
        }
        return result;
    }


    @Autowired
    private TaskCommonFoldService taskCommonFoldService;
    /**
     * 根据流程key和任务名称查询
     *作者：lvjian
     * @param oaProcActinst
     * @return
     */
    @AutoLog(value = "流程节点配置-根据流程key和任务名称查询")
    @ApiOperation(value = "流程节点配置-根据流程key和任务名称查询", notes = "流程节点配置-根据流程key和任务名称查询")
    @GetMapping(value = "/queryByKeyAndName")
    public Result<List<OaProcActinst>> queryByKeyAndName(OaProcActinst oaProcActinst) {
        Result<List<OaProcActinst>> result = new Result<>();
        if (oaProcActinst.getActName()!=null &&oaProcActinst.getActName().trim().length()>0){
            oaProcActinst.setActName(oaProcActinst.getActName().trim());
        }
        List<OaProcActinst> OaProcActinst1 = taskCommonFoldService.queryByKeyAndName(oaProcActinst);
        if (OaProcActinst1 == null ||OaProcActinst1.size()<1) {
            OaProcActinst oaProcActinstTest=new OaProcActinst();
            oaProcActinstTest.setActId("100dsaf1223aadfsaf");
            OaProcActinst1.add(oaProcActinstTest);
            result.setResult(OaProcActinst1);
            result.setSuccess(true);
//            result.error500("未找到对应实体");
        } else {
            result.setResult(OaProcActinst1);
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
        QueryWrapper<OaProcActinst> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                OaProcActinst OaProcActinst = JSON.parseObject(deString, OaProcActinst.class);
                queryWrapper = QueryGenerator.initQueryWrapper(OaProcActinst, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<OaProcActinst> pageList = OaProcActinstService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "流程节点配置列表");
        mv.addObject(NormalExcelConstants.CLASS, OaProcActinst.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("流程节点配置列表数据", "导出人:Jeecg", "导出信息"));
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
                List<OaProcActinst> listOaProcActinsts = ExcelImportUtil.importExcel(file.getInputStream(), OaProcActinst.class, params);
                OaProcActinstService.saveBatch(listOaProcActinsts);
                return Result.ok("文件导入成功！数据行数:" + listOaProcActinsts.size());
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
