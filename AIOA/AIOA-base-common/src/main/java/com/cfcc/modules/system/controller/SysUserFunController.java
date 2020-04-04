package com.cfcc.modules.system.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.util.oConvertUtils;

import com.cfcc.modules.system.entity.SysUserFun;
import com.cfcc.modules.system.service.ISysUserFunService;
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
* @Description: 用户收藏功能表
* @Author: jeecg-boot
* @Date:   2020-04-02
* @Version: V1.0
*/
@Slf4j
@Api(tags="用户收藏功能表")
@RestController
@RequestMapping("/sys/SysUserFun")
public class SysUserFunController {
   @Autowired
   private ISysUserFunService sysUserFunService;

   /**
     * 分页列表查询
    * @param sysUserFun
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "用户收藏功能表-分页列表查询")
   @ApiOperation(value="用户收藏功能表-分页列表查询", notes="用户收藏功能表-分页列表查询")
   @GetMapping(value = "/list")
   public Result<IPage<SysUserFun>> queryPageList(SysUserFun sysUserFun,
                                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                  HttpServletRequest req) {
       Result<IPage<SysUserFun>> result = new Result<IPage<SysUserFun>>();
       QueryWrapper<SysUserFun> queryWrapper = QueryGenerator.initQueryWrapper(sysUserFun, req.getParameterMap());
       Page<SysUserFun> page = new Page<SysUserFun>(pageNo, pageSize);
       IPage<SysUserFun> pageList = sysUserFunService.page(page, queryWrapper);
       result.setSuccess(true);
       result.setResult(pageList);
       return result;
   }

    @AutoLog(value = "用户收藏功能表-分页列表查询")
    @ApiOperation(value="用户收藏功能表-分页列表查询", notes="用户收藏功能表-分页列表查询")
    @GetMapping(value = "/queryListByUserId")
    public Result<List<SysUserFun>> queryListByUserId(String userId) {
        Result<List<SysUserFun>> result = new Result<List<SysUserFun>>();
        List<SysUserFun> pageList = sysUserFunService.queryListByUserIdSer(userId) ;
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

   /**
     *   添加
    * @param sysUserFun
    * @return
    */
   @AutoLog(value = "用户收藏功能表-添加")
   @ApiOperation(value="用户收藏功能表-添加", notes="用户收藏功能表-添加")
   @PostMapping(value = "/add")
   public Result<SysUserFun> add(@RequestBody SysUserFun sysUserFun) {
       Result<SysUserFun> result = new Result<SysUserFun>();
       try {
           sysUserFunService.save(sysUserFun);
           result.success("添加成功！");
       } catch (Exception e) {
           log.error(e.getMessage(),e);
           result.error500("操作失败");
       }
       return result;
   }

   /**
     *  编辑
    * @param SysUserFun
    * @return
    */
   @AutoLog(value = "用户收藏功能表-编辑")
   @ApiOperation(value="用户收藏功能表-编辑", notes="用户收藏功能表-编辑")
   @PutMapping(value = "/edit")
   public Result<SysUserFun> edit(@RequestBody SysUserFun SysUserFun) {
       Result<SysUserFun> result = new Result<SysUserFun>();
       SysUserFun SysUserFunEntity = sysUserFunService.getById(SysUserFun.getIId());
       if(SysUserFunEntity==null) {
           result.error500("未找到对应实体");
       }else {
           boolean ok = sysUserFunService.updateById(SysUserFun);
           //TODO 返回false说明什么？
           if(ok) {
               result.success("修改成功!");
           }
       }

       return result;
   }

   /**
     *   通过id删除
    * @param id
    * @return
    */
   @AutoLog(value = "用户收藏功能表-通过id删除")
   @ApiOperation(value="用户收藏功能表-通过id删除", notes="用户收藏功能表-通过id删除")
   @DeleteMapping(value = "/delete")
   public Result<?> delete(@RequestParam(name="id",required=true) String id) {
       try {
           sysUserFunService.removeById(id);
       } catch (Exception e) {
           log.error("删除失败",e.getMessage());
           return Result.error("删除失败!");
       }
       return Result.ok("删除成功!");
   }

   /**
     *  批量删除
    * @param ids
    * @return
    */
   @AutoLog(value = "用户收藏功能表-批量删除")
   @ApiOperation(value="用户收藏功能表-批量删除", notes="用户收藏功能表-批量删除")
   @DeleteMapping(value = "/deleteBatch")
   public Result<SysUserFun> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       Result<SysUserFun> result = new Result<SysUserFun>();
       if(ids==null || "".equals(ids.trim())) {
           result.error500("参数不识别！");
       }else {
           this.sysUserFunService.removeByIds(Arrays.asList(ids.split(",")));
           result.success("删除成功!");
       }
       return result;
   }

   /**
     * 通过id查询
    * @param id
    * @return
    */
   @AutoLog(value = "用户收藏功能表-通过id查询")
   @ApiOperation(value="用户收藏功能表-通过id查询", notes="用户收藏功能表-通过id查询")
   @GetMapping(value = "/queryById")
   public Result<SysUserFun> queryById(@RequestParam(name="id",required=true) String id) {
       Result<SysUserFun> result = new Result<SysUserFun>();
       SysUserFun SysUserFun = sysUserFunService.getById(id);
       if(SysUserFun==null) {
           result.error500("未找到对应实体");
       }else {
           result.setResult(SysUserFun);
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
     QueryWrapper<SysUserFun> queryWrapper = null;
     try {
         String paramsStr = request.getParameter("paramsStr");
         if (oConvertUtils.isNotEmpty(paramsStr)) {
             String deString = URLDecoder.decode(paramsStr, "UTF-8");
             SysUserFun SysUserFun = JSON.parseObject(deString, SysUserFun.class);
             queryWrapper = QueryGenerator.initQueryWrapper(SysUserFun, request.getParameterMap());
         }
     } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
     }

     //Step.2 AutoPoi 导出Excel
     ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
     List<SysUserFun> pageList = sysUserFunService.list(queryWrapper);
     //导出文件名称
     mv.addObject(NormalExcelConstants.FILE_NAME, "用户收藏功能表列表");
     mv.addObject(NormalExcelConstants.CLASS, SysUserFun.class);
     mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("用户收藏功能表列表数据", "导出人:Jeecg", "导出信息"));
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
             List<SysUserFun> listSysUserFuns = ExcelImportUtil.importExcel(file.getInputStream(), SysUserFun.class, params);
             sysUserFunService.saveBatch(listSysUserFuns);
             return Result.ok("文件导入成功！数据行数:" + listSysUserFuns.size());
         } catch (Exception e) {
             log.error(e.getMessage(),e);
             return Result.error("文件导入失败:"+e.getMessage());
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
