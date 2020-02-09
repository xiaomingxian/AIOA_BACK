package com.cfcc.modules.oaBus.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.oaBus.entity.BusFunctionPermit;
import com.cfcc.modules.oaBus.entity.BusProcSet;
import com.cfcc.modules.oaBus.service.IBusProcSetService;
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
 * @Description: 业务和流程关联配置表
 * @Author: jeecg-boot
 * @Date:   2019-11-05
 * @Version: V1.0
 */
@Slf4j
@Api(tags="业务和流程关联配置表")
@RestController
@RequestMapping("/oaBus/busProcSet")
public class BusProcSetController {
	@Autowired
	private IBusProcSetService busProcSetService;
	
	/**
	  * 分页列表查询
	 * @param busProcSet
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "业务和流程关联配置表-分页列表查询")
	@ApiOperation(value="业务和流程关联配置表-分页列表查询", notes="业务和流程关联配置表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<BusProcSet>> queryPageList(BusProcSet busProcSet,
												   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												   HttpServletRequest req) {
		Result<IPage<BusProcSet>> result = new Result<IPage<BusProcSet>>();
		QueryWrapper<BusProcSet> queryWrapper = QueryGenerator.initQueryWrapper(busProcSet, req.getParameterMap());
		Page<BusProcSet> page = new Page<BusProcSet>(pageNo, pageSize);
		IPage<BusProcSet> pageList = busProcSetService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param busProcSet
	 * @return
	 */
	@AutoLog(value = "业务和流程关联配置表-添加")
	@ApiOperation(value="业务和流程关联配置表-添加", notes="业务和流程关联配置表-添加")
	@PostMapping(value = "/add")
	public Result<BusProcSet> add(@RequestBody BusProcSet busProcSet) {
		Result<BusProcSet> result = new Result<BusProcSet>();
		try {
			busProcSetService.save(busProcSet);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param busProcSet
	 * @return
	 */
	@AutoLog(value = "业务和流程关联配置表-编辑")
	@ApiOperation(value="业务和流程关联配置表-编辑", notes="业务和流程关联配置表-编辑")
	@PutMapping(value = "/edit")
	public Result<BusProcSet> edit(@RequestBody BusProcSet busProcSet) {
		Result<BusProcSet> result = new Result<BusProcSet>();
		BusProcSet busProcSetEntity = busProcSetService.getById(busProcSet.getIId());
		if(busProcSetEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = busProcSetService.updateById(busProcSet);
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
	@AutoLog(value = "业务和流程关联配置表-通过id删除")
	@ApiOperation(value="业务和流程关联配置表-通过id删除", notes="业务和流程关联配置表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			busProcSetService.removeById(id);
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
	@AutoLog(value = "业务和流程关联配置表-批量删除")
	@ApiOperation(value="业务和流程关联配置表-批量删除", notes="业务和流程关联配置表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<BusProcSet> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<BusProcSet> result = new Result<BusProcSet>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.busProcSetService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	 public static void main(String[] args) {
		 String str1 = "select i_id,i_bus_function_id,PROC_INST_ID,s_main_unit_names,s_cc_unit_names,i_is_state from oa_busdata10 b where s_create_name like concat(concat(\"%\",?),\"%\") and i_bus_function_id = ? and s_create_name = ? order by b.d_create_time desc limit 0,10" ;
		 String str2 = "是(String), 22(String), admin(String)" ;
		 str2 = str2.replace("(String)","");
		 StringBuffer sql = new StringBuffer("");
		 String[] str1s = str1.split("\\?") ;
		 String[] str2s = str2.split(",") ;
		 for(int i = 0 ; i < str2s.length  ; i ++){
		 	sql.append( str1s[i] + str2s[i]) ;
		 }
		 /*for(int i= 0; i < str1s.length ; i ++){
			 System.out.println(str1s[i]);
		 }*/
		 sql.append(str1s[str1s.length-1]);
		 System.out.println(sql.toString());

	 }

	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "业务和流程关联配置表-通过id查询")
	@ApiOperation(value="业务和流程关联配置表-通过id查询", notes="业务和流程关联配置表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<BusProcSet> queryById(@RequestParam(name="id",required=true) String id) {
		Result<BusProcSet> result = new Result<BusProcSet>();
		BusProcSet busProcSet = busProcSetService.getById(id);
		if(busProcSet==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(busProcSet);
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
      QueryWrapper<BusProcSet> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              BusProcSet busProcSet = JSON.parseObject(deString, BusProcSet.class);
              queryWrapper = QueryGenerator.initQueryWrapper(busProcSet, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<BusProcSet> pageList = busProcSetService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "业务和流程关联配置表列表");
      mv.addObject(NormalExcelConstants.CLASS, BusProcSet.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("业务和流程关联配置表列表数据", "导出人:Jeecg", "导出信息"));
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
              List<BusProcSet> listBusProcSets = ExcelImportUtil.importExcel(file.getInputStream(), BusProcSet.class, params);
              busProcSetService.saveBatch(listBusProcSets);
              return Result.ok("文件导入成功！数据行数:" + listBusProcSets.size());
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

	 @GetMapping(value = "/findList")
	 public Result findList() {
		 String schema = MycatSchema.getSchema();
		 Result<List<BusProcSet>> result = new Result<>();
		 List<BusProcSet> busProcSets = busProcSetService.findList(schema);
		 if (busProcSets.size() == 0) {
			 result.error500("未找到对应实体");
		 } else {
			 result.setResult(busProcSets);
			 result.setSuccess(true);
		 }
		 return result;

	 }

}
