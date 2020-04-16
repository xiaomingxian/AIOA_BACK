package com.cfcc.modules.oadata.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.oadata.entity.oaDatadetailedInst;
import com.cfcc.modules.oadata.service.IoaDatadetailedInstService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
 * @Description: 增加明细配置和存储
 * @Author: jeecg-boot
 * @Date:   2020-04-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags="增加明细配置和存储")
@RestController
@RequestMapping("/oadata/oaDatadetailedInst")
public class oaDatadetailedInstController {
	@Autowired
	private IoaDatadetailedInstService oaDatadetailedInstService;
	
	/**
	  * 分页列表查询
	 * @param oaDatadetailedInst
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "增加明细配置和存储-分页列表查询")
	@ApiOperation(value="增加明细配置和存储-分页列表查询", notes="增加明细配置和存储-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<oaDatadetailedInst>> queryPageList(oaDatadetailedInst oaDatadetailedInst,
														   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														   HttpServletRequest req) {
		Result<IPage<oaDatadetailedInst>> result = new Result<IPage<oaDatadetailedInst>>();
		QueryWrapper<oaDatadetailedInst> queryWrapper = QueryGenerator.initQueryWrapper(oaDatadetailedInst, req.getParameterMap());
		Page<oaDatadetailedInst> page = new Page<oaDatadetailedInst>(pageNo, pageSize);
		IPage<oaDatadetailedInst> pageList = oaDatadetailedInstService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param oaDatadetailedInst
	 * @return
	 */
	@AutoLog(value = "增加明细配置和存储-添加")
	@ApiOperation(value="增加明细配置和存储-添加", notes="增加明细配置和存储-添加")
	@PostMapping(value = "/add")
	public Result<oaDatadetailedInst> add(@RequestBody oaDatadetailedInst oaDatadetailedInst) {
		Result<oaDatadetailedInst> result = new Result<oaDatadetailedInst>();
		try {
			oaDatadetailedInstService.save(oaDatadetailedInst);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param oaDatadetailedInst
	 * @return
	 */
	@AutoLog(value = "增加明细配置和存储-编辑")
	@ApiOperation(value="增加明细配置和存储-编辑", notes="增加明细配置和存储-编辑")
	@PutMapping(value = "/edit")
	public Result<oaDatadetailedInst> edit(@RequestBody oaDatadetailedInst oaDatadetailedInst) {
		Result<oaDatadetailedInst> result = new Result<oaDatadetailedInst>();
		oaDatadetailedInst oaDatadetailedInstEntity = oaDatadetailedInstService.getById(oaDatadetailedInst.getIId());
		if(oaDatadetailedInstEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = oaDatadetailedInstService.updateById(oaDatadetailedInst);
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
	@AutoLog(value = "增加明细配置和存储-通过id删除")
	@ApiOperation(value="增加明细配置和存储-通过id删除", notes="增加明细配置和存储-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			oaDatadetailedInstService.removeById(id);
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
	@AutoLog(value = "增加明细配置和存储-批量删除")
	@ApiOperation(value="增加明细配置和存储-批量删除", notes="增加明细配置和存储-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<oaDatadetailedInst> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<oaDatadetailedInst> result = new Result<oaDatadetailedInst>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.oaDatadetailedInstService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "增加明细配置和存储-通过id查询")
	@ApiOperation(value="增加明细配置和存储-通过id查询", notes="增加明细配置和存储-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<oaDatadetailedInst> queryById(@RequestParam(name="id",required=true) String id) {
		Result<oaDatadetailedInst> result = new Result<oaDatadetailedInst>();
		oaDatadetailedInst oaDatadetailedInst = oaDatadetailedInstService.getById(id);
		if(oaDatadetailedInst==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(oaDatadetailedInst);
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
      QueryWrapper<oaDatadetailedInst> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              oaDatadetailedInst oaDatadetailedInst = JSON.parseObject(deString, oaDatadetailedInst.class);
              queryWrapper = QueryGenerator.initQueryWrapper(oaDatadetailedInst, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<oaDatadetailedInst> pageList = oaDatadetailedInstService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "增加明细配置和存储列表");
      mv.addObject(NormalExcelConstants.CLASS, oaDatadetailedInst.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("增加明细配置和存储列表数据", "导出人:Jeecg", "导出信息"));
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
              List<oaDatadetailedInst> listoaDatadetailedInsts = ExcelImportUtil.importExcel(file.getInputStream(), oaDatadetailedInst.class, params);
              oaDatadetailedInstService.saveBatch(listoaDatadetailedInsts);
              return Result.ok("文件导入成功！数据行数:" + listoaDatadetailedInsts.size());
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
