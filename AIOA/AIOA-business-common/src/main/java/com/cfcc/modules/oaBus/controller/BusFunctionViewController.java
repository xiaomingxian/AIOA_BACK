package com.cfcc.modules.oaBus.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.oaBus.entity.BusFunctionView;
import com.cfcc.modules.oaBus.service.IBusFunctionViewService;
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
 * @Description: 业务功能数据查看权限表
 * @Author: jeecg-boot
 * @Date:   2019-10-21
 * @Version: V1.0
 */
@Slf4j
@Api(tags="业务功能数据查看权限表")
@RestController
@RequestMapping("/oaBus/busFunctionView")
public class BusFunctionViewController {
	@Autowired
	private IBusFunctionViewService busFunctionViewService;
	
	/**
	  * 分页列表查询
	 * @param busFunctionView
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "业务功能数据查看权限表-分页列表查询")
	@ApiOperation(value="业务功能数据查看权限表-分页列表查询", notes="业务功能数据查看权限表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<BusFunctionView>> queryPageList(BusFunctionView busFunctionView,
														@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														HttpServletRequest req) {
		Result<IPage<BusFunctionView>> result = new Result<IPage<BusFunctionView>>();
		QueryWrapper<BusFunctionView> queryWrapper = QueryGenerator.initQueryWrapper(busFunctionView, req.getParameterMap());
		Page<BusFunctionView> page = new Page<BusFunctionView>(pageNo, pageSize);
		IPage<BusFunctionView> pageList = busFunctionViewService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param busFunctionView
	 * @return
	 */
	@AutoLog(value = "业务功能数据查看权限表-添加")
	@ApiOperation(value="业务功能数据查看权限表-添加", notes="业务功能数据查看权限表-添加")
	@PostMapping(value = "/add")
	public Result<BusFunctionView> add(@RequestBody BusFunctionView busFunctionView) {
		Result<BusFunctionView> result = new Result<BusFunctionView>();
		try {
			busFunctionViewService.save(busFunctionView);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param busFunctionView
	 * @return
	 */
	@AutoLog(value = "业务功能数据查看权限表-编辑")
	@ApiOperation(value="业务功能数据查看权限表-编辑", notes="业务功能数据查看权限表-编辑")
	@PutMapping(value = "/edit")
	public Result<BusFunctionView> edit(@RequestBody BusFunctionView busFunctionView) {
		Result<BusFunctionView> result = new Result<BusFunctionView>();
		BusFunctionView busFunctionViewEntity = busFunctionViewService.getById(busFunctionView.getIId());
		if(busFunctionViewEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = busFunctionViewService.updateById(busFunctionView);
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
	@AutoLog(value = "业务功能数据查看权限表-通过id删除")
	@ApiOperation(value="业务功能数据查看权限表-通过id删除", notes="业务功能数据查看权限表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			busFunctionViewService.removeById(id);
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
	@AutoLog(value = "业务功能数据查看权限表-批量删除")
	@ApiOperation(value="业务功能数据查看权限表-批量删除", notes="业务功能数据查看权限表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<BusFunctionView> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<BusFunctionView> result = new Result<BusFunctionView>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.busFunctionViewService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "业务功能数据查看权限表-通过id查询")
	@ApiOperation(value="业务功能数据查看权限表-通过id查询", notes="业务功能数据查看权限表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<BusFunctionView> queryById(@RequestParam(name="id",required=true) String id) {
		Result<BusFunctionView> result = new Result<BusFunctionView>();
		BusFunctionView busFunctionView = busFunctionViewService.getById(id);
		if(busFunctionView==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(busFunctionView);
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
      QueryWrapper<BusFunctionView> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              BusFunctionView busFunctionView = JSON.parseObject(deString, BusFunctionView.class);
              queryWrapper = QueryGenerator.initQueryWrapper(busFunctionView, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<BusFunctionView> pageList = busFunctionViewService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "业务功能数据查看权限表列表");
      mv.addObject(NormalExcelConstants.CLASS, BusFunctionView.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("业务功能数据查看权限表列表数据", "导出人:Jeecg", "导出信息"));
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
              List<BusFunctionView> listBusFunctionViews = ExcelImportUtil.importExcel(file.getInputStream(), BusFunctionView.class, params);
              busFunctionViewService.saveBatch(listBusFunctionViews);
              return Result.ok("文件导入成功！数据行数:" + listBusFunctionViews.size());
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
