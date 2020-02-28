package com.cfcc.modules.oabutton.controller;

import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.util.oConvertUtils;

import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.oaBus.entity.BusProcSet;
import com.cfcc.modules.oaBus.service.IBusFunctionService;
import com.cfcc.modules.oaBus.service.IBusModelService;
import com.cfcc.modules.oaBus.service.IBusProcSetService;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.cfcc.modules.oabutton.entity.OaButton;
import com.cfcc.modules.oabutton.entity.OaButtonSet;
import com.cfcc.modules.oabutton.service.IOaButtonSetService;
import com.cfcc.modules.workflow.pojo.OaProcActinst;
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
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 发布类按钮描述
 * @Author: jeecg-boot
 * @Date: 2019-10-26
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "发布类按钮描述")
@RestController
@RequestMapping("/oabuttonset/oaButtonSet")
public class OaButtonSetController {
    @Autowired
    private IOaButtonSetService oaButtonSetService;
    @Autowired
    private IBusProcSetService busProcSetService;
    @Autowired
    private IBusModelService busModelService;
    @Autowired
    private IOaBusdataService oaBusdataService;

    /**
     * 分页列表查询
     *
     * @param id
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "发布类按钮描述-分页列表查询")
    @ApiOperation(value = "发布类按钮描述-分页列表查询", notes = "发布类按钮描述-分页列表查询")
    @GetMapping(value = "/findById")
    public Result<IPage<OaButtonSet>> findById(@RequestParam(name = "id", required = true) Integer id,
                                               @RequestParam(name = "buttonId", required = false) Integer buttonId,
                                               @RequestParam(name = "taskDefKey", required = false) String taskDefKey,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
        Result<IPage<OaButtonSet>> result = new Result<IPage<OaButtonSet>>();
		/*QueryWrapper<OaButtonSet> queryWrapper = QueryGenerator.initQueryWrapper(oaButtonSet, req.getParameterMap());
		Page<OaButtonSet> page = new Page<OaButtonSet>(pageNo, pageSize);
		IPage<OaButtonSet> pageList = oaButtonSetService.page(page, queryWrapper);*/
        IPage<OaButtonSet> pageList = oaButtonSetService.getPage(pageNo, pageSize, id,null,null);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 分页列表查询--点击查询按钮查询
     *
     * @param
     * @return
     */
    @AutoLog(value = "发布类按钮描述-分页列表查询")
    @ApiOperation(value = "发布类按钮描述-分页列表查询", notes = "发布类按钮描述-分页列表查询")
    @PostMapping(value = "/findByIf")
    public Result<IPage<OaButtonSet>> findByIf(@RequestBody Map<String,Object> map) {
        Result<IPage<OaButtonSet>> result = new Result<>();
        List<OaButton>  buttonList=null;
        if (map.get("buttonId")!=null){
            buttonList=(List<OaButton>)map.get("buttonId");
        }
        List<OaProcActinst>  taskDefKeyList=null;
        if (map.get("taskDefKey")!=null){
            taskDefKeyList=(List<OaProcActinst>)map.get("taskDefKey");
        }
        IPage<OaButtonSet> pageList = oaButtonSetService.getPage((Integer) map.get("pageNo"),(Integer)map.get("pageSize"),(Integer)map.get("id"),buttonList,taskDefKeyList);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param map
     * @return
     */
    @AutoLog(value = "发布类按钮描述-添加")
    @ApiOperation(value = "发布类按钮描述-添加", notes = "发布类按钮描述-添加")
    @PostMapping(value = "/add")
    public Result<OaButtonSet> add(@RequestBody Map<String,Object> map) {
        Result<OaButtonSet> result = new Result<OaButtonSet>();
        OaButtonSet oaButtonSet=new OaButtonSet();
        oaButtonSet.setIProcButtonId((Integer) map.get("iprocButtonId"));
        oaButtonSet.setIButtonId((Integer) map.get("ibuttonId"));
        if (map.get("taskDefKey")!=null){
            oaButtonSet.setTaskDefKey(map.get("taskDefKey").toString());
        }
        if (map.get("procDefKey")!=null){
            oaButtonSet.setProcDefKey(map.get("procDefKey").toString());
        }

        if (map.get("sroles")!=null){
            oaButtonSet.setSRoles(map.get("sroles").toString());
        }else {
            oaButtonSet.setSRoles("");
        }

        oaButtonSet.setIIsDefault((Integer) map.get("iisDefault"));
        oaButtonSet.setIIsTransactors((Integer) map.get("iisTransactors"));
        oaButtonSet.setIIsLastsender((Integer) map.get("iisLastsender"));
        oaButtonSet.setIIsReader((Integer) map.get("iisReader"));
        oaButtonSet.setIIsCreater((Integer) map.get("iisCreater"));
        oaButtonSet.setIPermitType((Integer) map.get("ipermitType"));
        oaButtonSet.setIOrder((Integer) map.get("iorder"));
        try {
            String schema = MycatSchema.getSchema();
            oaButtonSetService.insertoaButtonSet(oaButtonSet,schema);
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
     * @param oaButtonSet
     * @return
     */
    @AutoLog(value = "发布类按钮描述-编辑")
    @ApiOperation(value = "发布类按钮描述-编辑", notes = "发布类按钮描述-编辑")
    @PutMapping(value = "/edit")
    public Result<OaButtonSet> edit(@RequestBody OaButtonSet oaButtonSet) {
        Result<OaButtonSet> result = new Result<OaButtonSet>();
        OaButtonSet oaButtonSetEntity = oaButtonSetService.queryById(oaButtonSet.getIId());
        if (oaButtonSetEntity == null) {
            result.error500("未找到对应实体");
        } else {
            String schema = MycatSchema.getSchema();
            boolean ok = oaButtonSetService.updateOaButtonSetById(oaButtonSet,schema);
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
    @AutoLog(value = "发布类按钮描述-通过id删除")
    @ApiOperation(value = "发布类按钮描述-通过id删除", notes = "发布类按钮描述-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            String schema = MycatSchema.getSchema();
            oaButtonSetService.deleteOaButtonSetByID(id,schema);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    /**
     * 通过id查看是否有业务数据
     *
     * @param id
     * @return
     */
    @AutoLog(value = "发布类按钮描述-删除前校验是否有业务数据")
    @ApiOperation(value = "发布类按钮描述-删除前校验是否有业务数据", notes = "发布类按钮描述-删除前校验是否有业务数据")
    @GetMapping(value = "/verButtonSetDelete")
    public Result<?> verButtonSetDelete(@RequestParam(name = "id", required = true) String id) {
        try {
            if (id==null||id.trim().length()<1){
                return Result.error("校验id为空!");
            }
            OaButtonSet oaButtonSet = oaButtonSetService.queryById(Integer.valueOf(id));
            if (oaButtonSet==null || oaButtonSet.getIProcButtonId()==null){
                return Result.error("数据查询失败");
            }
            //获取modelid ,funtionid
            Map<String,Object> mapNow=new HashMap<>();
            mapNow.put("iProcButtonId",oaButtonSet.getIProcButtonId());
            List<BusProcSet> procSetByprocbuttonId = busProcSetService.getProcSetByprocbuttonId(mapNow);
            if (procSetByprocbuttonId==null){
                return Result.error("数据获取失败");
            }
            for (int j = 0; j < procSetByprocbuttonId.size(); j++) {
                BusModel busModelById = busModelService.getBusModelById(procSetByprocbuttonId.get(j).getIBusModelId());
                if (busModelById==null){
//                     Result.error("获取失败");
                }else {
                    Map<String,Object> map=new HashMap<>();
                    //拼接参数
                    map.put("tableName",busModelById.getSBusdataTable());
                    map.put("iBusFunctionId",procSetByprocbuttonId.get(j).getIBusFunctionId());
                    map.put("iBusModelId",busModelById.getIId());
                    int i = oaBusdataService.listCountBytableName(map);
                    if (i>0){
                        return Result.ok("校验成功!");
                    }
                }
            }
        } catch (Exception e) {
            log.error("校验失败", e.getMessage());
            return Result.error("校验失败!");
        }
        return Result.ok("校验成功!");
    }


    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "发布类按钮描述-批量删除")
    @ApiOperation(value = "发布类按钮描述-批量删除", notes = "发布类按钮描述-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<OaButtonSet> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<OaButtonSet> result = new Result<OaButtonSet>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
//            List<String> idList = Arrays.asList(ids.split(","));
//            for (int i = 0; i < idList.size(); i++) {
                this.oaButtonSetService.removeByIds(Arrays.asList(ids.split(",")));
//            }
//            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "发布类按钮描述-通过id查询")
    @ApiOperation(value = "发布类按钮描述-通过id查询", notes = "发布类按钮描述-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<OaButtonSet> queryById(@RequestParam(name = "id", required = true) Integer id) {
        Result<OaButtonSet> result = new Result<OaButtonSet>();
        OaButtonSet oaButtonSet = oaButtonSetService.queryById(id);
        if (oaButtonSet == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(oaButtonSet);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 通过任务KEY和按钮ID查询
     * @param
     * @param
     * @return
     */
    @AutoLog(value = "发布类按钮描述-通过任务KEY和按钮ID查询")
    @ApiOperation(value = "发布类按钮描述-通过任务KEY和按钮ID查询", notes = "发布类按钮描述-通过任务KEY和按钮ID查询")
    @PostMapping(value = "/queryByTaskDefKeyAndBtnId")
    public Result<OaButtonSet> queryByTaskDefKeyAndBtnId(@RequestBody Map<String,Object> map) {
        Result<OaButtonSet> result = new Result<OaButtonSet>();
        if (map.get("taskDefKey")==null || map.get("iButtonId")==null
                || map.get("iProcButtonId")==null || map.get("procDefKey")==null){
            result.error500("未找到对应实体");
            return result;
        }
        OaButtonSet oaButtonSet = oaButtonSetService.queryByTaskDefKeyAndBtnId(map);
        if (oaButtonSet == null) {
            result.error500("未找到对应数据");
        } else {
            result.setResult(oaButtonSet);
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
        QueryWrapper<OaButtonSet> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                OaButtonSet oaButtonSet = JSON.parseObject(deString, OaButtonSet.class);
                queryWrapper = QueryGenerator.initQueryWrapper(oaButtonSet, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<OaButtonSet> pageList = oaButtonSetService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "发布类按钮描述列表");
        mv.addObject(NormalExcelConstants.CLASS, OaButtonSet.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("发布类按钮描述列表数据", "导出人:Jeecg", "导出信息"));
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
                List<OaButtonSet> listOaButtonSets = ExcelImportUtil.importExcel(file.getInputStream(), OaButtonSet.class, params);
                oaButtonSetService.saveBatch(listOaButtonSets);
                return Result.ok("文件导入成功！数据行数:" + listOaButtonSets.size());
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
     * 根据oa_proc_button的主键id查询
     * @param id
     * @return
     *//*
    @GetMapping(value = "/findById")
    public List<OaButtonSet> findById(@RequestParam(name = "id", required = true) Integer id) {
        Result<OaButtonSet> result = new Result<OaButtonSet>();
        List<OaButtonSet> oaButtonSet = oaButtonSetService.findById(id);
        if (oaButtonSet == null) {
            result.error500("未找到对应实体");
        }
            return oaButtonSet;
    }*/



}
