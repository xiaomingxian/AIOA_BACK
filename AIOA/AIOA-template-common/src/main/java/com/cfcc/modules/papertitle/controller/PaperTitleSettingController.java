package com.cfcc.modules.papertitle.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.papertitle.entity.PaperTitleSetting;
import com.cfcc.modules.papertitle.service.IPaperTitleSettingService;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

 /**
 * @Description: 稿纸头配置
 * @Author: jeecg-boot
 * @Date:   2019-10-14
 * @Version: V1.0
 */
@Slf4j
@Api(tags="稿纸头配置")
@RestController
@RequestMapping("/papertitle/paperTitleSetting")
public class PaperTitleSettingController {
	 @Autowired
	 private IPaperTitleSettingService paperTitleSettingService;

	 /**
	  * 分页列表查询
	  *
	  * @param paperTitleSetting
	  * @param pageNo
	  * @param pageSize
	  * @return
	  */
	 @AutoLog(value = "稿纸头配置-分页列表查询")
	 @ApiOperation(value = "稿纸头配置-分页列表查询", notes = "稿纸头配置-分页列表查询")
	 @GetMapping(value = "/list")
	 public Result<IPage<PaperTitleSetting>> queryPageList(PaperTitleSetting paperTitleSetting,
														   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
														   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		 Result<IPage<PaperTitleSetting>> result = new Result<IPage<PaperTitleSetting>>();
		 IPage<PaperTitleSetting> pageList = paperTitleSettingService.queryPaperList(paperTitleSetting, pageNo, pageSize);
		 result.setSuccess(true);
		 result.setResult(pageList);
		 return result;
	 }

	 /**
	  * 添加
	  *
	  * @param paperTitleSetting
	  * @return
	  */
	 @AutoLog(value = "稿纸头配置-添加")
	 @ApiOperation(value = "稿纸头配置-添加", notes = "稿纸头配置-添加")
	 @PostMapping(value = "/add")
	 public Result<PaperTitleSetting> add(@RequestBody PaperTitleSetting paperTitleSetting, HttpServletRequest request) {
		 Result<PaperTitleSetting> result = new Result<PaperTitleSetting>();
		 try {
			 //获取当前用户名称
			 String token = request.getHeader("X-Access-Token");
			 String username = JwtUtil.getUsername(token);
			 paperTitleSetting.setSCreateBy(username);
			 paperTitleSetting.setDCreateTime(new Date());
			 paperTitleSettingService.save(paperTitleSetting);
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
	  * @param paperTitleSetting
	  * @return
	  */
	 @AutoLog(value = "稿纸头配置-编辑")
	 @ApiOperation(value = "稿纸头配置-编辑", notes = "稿纸头配置-编辑")
	 @PutMapping(value = "/edit")
	 public Result<PaperTitleSetting> edit(@RequestBody PaperTitleSetting paperTitleSetting, HttpServletRequest request) {
		 Result<PaperTitleSetting> result = new Result<PaperTitleSetting>();
		 PaperTitleSetting paperTitleSettingEntity = paperTitleSettingService.queryById(paperTitleSetting.getIId());
		 if (paperTitleSettingEntity == null) {
			 result.error500("未找到对应实体");
		 } else {
			 //获取当前用户名称
			 String token = request.getHeader("X-Access-Token");
			 String username = JwtUtil.getUsername(token);
			 paperTitleSetting.setSUpdateBy(username);
			 paperTitleSetting.setDUpdateTime(new Date());
			 boolean ok = paperTitleSettingService.updatePaperById(paperTitleSetting);
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
	 @AutoLog(value = "稿纸头配置-通过id删除")
	 @ApiOperation(value = "稿纸头配置-通过id删除", notes = "稿纸头配置-通过id删除")
	 @DeleteMapping(value = "/delete")
	 public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		 try {
			 paperTitleSettingService.deletePaperTitleByIID(id);
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
	 @AutoLog(value = "稿纸头配置-批量删除")
	 @ApiOperation(value = "稿纸头配置-批量删除", notes = "稿纸头配置-批量删除")
	 @DeleteMapping(value = "/deleteBatch")
	 public Result<PaperTitleSetting> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		 Result<PaperTitleSetting> result = new Result<PaperTitleSetting>();
		 if (ids == null || "".equals(ids.trim())) {
			 result.error500("参数不识别！");
		 } else {
			 List<String> idList = Arrays.asList(ids.split(","));
			 for (int i = 0; i < idList.size(); i++) {
				 this.paperTitleSettingService.deletePaperTitleByIID(idList.get(i));
			 }
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
	 @AutoLog(value = "稿纸头配置-通过id查询")
	 @ApiOperation(value = "稿纸头配置-通过id查询", notes = "稿纸头配置-通过id查询")
	 @GetMapping(value = "/queryById")
	 public Result<PaperTitleSetting> queryById(@RequestParam(name = "id", required = true) Integer id) {
		 Result<PaperTitleSetting> result = new Result<PaperTitleSetting>();
		 PaperTitleSetting paperTitleSetting = paperTitleSettingService.queryById(id);
		 if (paperTitleSetting == null) {
			 result.error500("未找到对应实体");
		 } else {
			 result.setResult(paperTitleSetting);
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
		 QueryWrapper<PaperTitleSetting> queryWrapper = null;
		 try {
			 String paramsStr = request.getParameter("paramsStr");
			 if (oConvertUtils.isNotEmpty(paramsStr)) {
				 String deString = URLDecoder.decode(paramsStr, "UTF-8");
				 PaperTitleSetting paperTitleSetting = JSON.parseObject(deString, PaperTitleSetting.class);
				 queryWrapper = QueryGenerator.initQueryWrapper(paperTitleSetting, request.getParameterMap());
			 }
		 } catch (UnsupportedEncodingException e) {
			 e.printStackTrace();
		 }

		 //Step.2 AutoPoi 导出Excel
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 List<PaperTitleSetting> pageList = paperTitleSettingService.list(queryWrapper);
		 //导出文件名称
		 mv.addObject(NormalExcelConstants.FILE_NAME, "稿纸头配置列表");
		 mv.addObject(NormalExcelConstants.CLASS, PaperTitleSetting.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("稿纸头配置列表数据", "导出人:Jeecg", "导出信息"));
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
				 List<PaperTitleSetting> listPaperTitleSettings = ExcelImportUtil.importExcel(file.getInputStream(), PaperTitleSetting.class, params);
				 paperTitleSettingService.saveBatch(listPaperTitleSettings);
				 return Result.ok("文件导入成功！数据行数:" + listPaperTitleSettings.size());
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

	 @AutoLog(value = "稿纸头配置-稿纸头下拉选列表")
	 @ApiOperation(value = "稿纸头配置-稿纸头下拉选列表", notes = "稿纸头配置-稿纸头下拉选列表")
	 @GetMapping(value = "/paperTitleList")
	 public Result paperTitleList() {
		 Result<List<PaperTitleSetting>> result = new Result<>();
		 List<PaperTitleSetting> paperList = paperTitleSettingService.paperTitleList();
		 if (paperList.size()==0) {
			 result.error500("未找到对应实体");
		 } else {
			 result.setResult(paperList);
			 result.setSuccess(true);
		 }
		 return result;
	 }
}
