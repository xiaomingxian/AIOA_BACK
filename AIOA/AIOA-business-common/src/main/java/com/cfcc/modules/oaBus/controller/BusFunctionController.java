package com.cfcc.modules.oaBus.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.constant.CacheConstant;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.BusPageDetail;
import com.cfcc.modules.oaBus.service.IBusFunctionService;
import com.cfcc.modules.oaBus.vo.BusFunctionPage;
import com.cfcc.modules.oabutton.entity.OaProcOpinion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: 业务配置表
 * @Author: jeecg-boot
 * @Date:   2019-10-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags="业务配置表")
@RestController
@RequestMapping("/oaBus/busFunction")
public class BusFunctionController {
	@Autowired
	private IBusFunctionService busFunctionService;
	
	/**
	  * 分页列表查询
	 * @param busFunction
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "业务配置表-分页列表查询")
	@ApiOperation(value="业务配置表-分页列表查询", notes="业务配置表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<BusFunction>> queryPageList(BusFunction busFunction,
													@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													HttpServletRequest req) {
		Result<IPage<BusFunction>> result = new Result<IPage<BusFunction>>();
		Map<String, String[]> parameterMap = req.getParameterMap();

		String column = "";
		if(parameterMap.containsKey("column")){
			column = parameterMap.get("column")[0];
			column = transFormSecToUpperCase(column) ;
			//column = camel2Underline(column) ;
		}
		String order = "";
		if(parameterMap.containsKey("order")){
			order = parameterMap.get("order")[0];
		}
		IPage<BusFunction> pageList = busFunctionService.getPage(pageNo, pageSize,busFunction,column,order);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	private String camel2Underline(String column) {

		if(column == null || "".equals(column)){
			return "" ;
		}
		StringBuffer str = new StringBuffer() ;
		Pattern pattern = Pattern.compile("[A-Z]([a-z\\d]+)?") ;
		Matcher matcher = pattern.matcher(column);
		while (matcher.find()){
			String word = matcher.group() ;
			str.append(matcher.end() == column.length() ? "":"_") ;
		}
		return str.toString() ;
	}

	/**
	 *	将第二个字母转化为大写
	 */
	private String transFormSecToUpperCase(String column) {
		StringBuffer result = new StringBuffer() ;
		String str1 = column.substring(0,1) ;
		String str2 = column.substring(1,2) ;
		String str3 = column.substring(2) ;
		if( str1.equals("i")|| str1.equals("s")){
			str2 = str2.toUpperCase() ;
		}
		return result.append(str1+str2+str3).toString();
	}

	/**
	  * 分页列表查询
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "业务配置表-通过modelId查询出对应FunctionList")
	 @ApiOperation(value="业务配置表-通过modelId查询出对应FunctionList", notes="业务配置表-通过modelId查询出对应FunctionList")
	 @GetMapping(value = "/queryListByModelId")
	 public Result<IPage<BusFunction>> queryFunListByModelId(@RequestParam(name="id") String id)
													  {
		 Result<IPage<BusFunction>> result = new Result<IPage<BusFunction>>();
		 IPage<BusFunction> pageList = busFunctionService.getFunByModelId(id);
		 result.setSuccess(true);
		 result.setResult(pageList);
		 return result;
	 }

	 /**
	  * 查询业务的搜索条件
	  * @param id
	  * @return
	  */
	 @AutoLog(value = "业务配置表-查询业务的搜索条件")
	 @ApiOperation(value="业务配置表-查询业务的搜索条件", notes="业务配置表-查询业务的搜索条件")
	 @GetMapping(value = "/queryCondition")
	 public Result<List<BusPageDetail>> queryCondition(@RequestParam(name="id") int id) {
		 Result<List<BusPageDetail>> result = new Result<List<BusPageDetail>>();
		/*QueryWrapper<BusFunction> queryWrapper = QueryGenerator.initQueryWrapper(busFunction, req.getParameterMap());
		Page<BusFunction> page = new Page<BusFunction>(pageNo, pageSize);*/
		 List<BusPageDetail> conditionList = busFunctionService.queryConditionSer(id);
		 result.setSuccess(true);
		 result.setResult(conditionList);
		 return result;
	 }

	 @AutoLog(value = "业务配置-查询业务中流程对应的意见配置")
	 @ApiOperation(value="业务配置-查询业务中流程对应的意见配置", notes="业业务配置-查询业务中流程对应的意见配置")
	 @GetMapping(value = "/queryProcOpinion")
	 public Result<List<OaProcOpinion>> queryProcOpinion(String key) {
		 Result<List<OaProcOpinion>> result = new Result<List<OaProcOpinion>>();
		 List<OaProcOpinion> oaProcOpinionList = busFunctionService.queryoaProcOpinionSer(key);
		 result.setSuccess(true);
		 result.setResult(oaProcOpinionList);
		 return result;
	 }



	 /**
	  *   添加
	 * @param
	 * @return
	 */
	@AutoLog(value = "业务配置表-添加")
	@ApiOperation(value="业务配置表-添加", notes="业务配置表-添加")
	@PostMapping(value = "/add")
	public Result<BusFunction> add(@RequestBody BusFunctionPage busFunctionPage,HttpServletRequest request) {
		Result<BusFunction> result = new Result<BusFunction>();
		String token = request.getHeader("X-Access-Token");
		String userName = JwtUtil.getUsername(token);
		String schema = MycatSchema.getSchema();
		try {
			BusFunction busFunction = new BusFunction();
			BeanUtils.copyProperties(busFunctionPage , busFunction);
			busFunction.setSCreateBy(userName) ;

			String str = busFunctionService.saveMain(busFunction,
                    busFunctionPage.getBusFunctionUnit(),
                    busFunctionPage.getBusProcSet(),
                    busFunctionPage.getBusFunctionView(),
					schema);
			result.success("添加成功！");
			if("".equals(str)){
				result.setMessage("添加成功！");
			}else{
				result.setMessage(str);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}



	
	/**
	  *  编辑
	 * @param busFunction
	 * @return
	 */
	@AutoLog(value = "业务配置表-编辑")
	@ApiOperation(value="业务配置表-编辑", notes="业务配置表-编辑")
	@PutMapping(value = "/edit")
	public Result<BusFunction> edit(@RequestBody BusFunctionPage busFunctionPage,HttpServletRequest request) {
		Result<BusFunction> result = new Result<BusFunction>();
		String token = request.getHeader("X-Access-Token");
		String userName = JwtUtil.getUsername(token);
		String schema = MycatSchema.getSchema();
		try {
			BusFunction busFunction = new BusFunction();
			BeanUtils.copyProperties(busFunctionPage , busFunction);
			busFunction.setSCreateBy(userName) ;

			busFunctionService.updateFunction(busFunction,
					busFunctionPage.getBusFunctionUnit(),
					busFunctionPage.getBusProcSet(),
					busFunctionPage.getBusFunctionView(),
					schema
			);
			result.success("修改成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *   通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "业务配置表-通过id删除")
	@ApiOperation(value="业务配置表-通过id删除", notes="业务配置表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		String schema = MycatSchema.getSchema();
		try {
			busFunctionService.removeBusFunctionById(id,schema);
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
	@AutoLog(value = "业务配置表-批量删除")
	@ApiOperation(value="业务配置表-批量删除", notes="业务配置表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<BusFunction> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<BusFunction> result = new Result<BusFunction>();
		String schema = MycatSchema.getSchema();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.busFunctionService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "业务配置表-通过id查询")
	@ApiOperation(value="业务配置表-通过id查询", notes="业务配置表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<BusFunction> queryById(@RequestParam(name="id",required=true) String id) {
		Result<BusFunction> result = new Result<BusFunction>();
		BusFunction busFunction = busFunctionService.getById(id);
		if(busFunction==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(busFunction);
			result.setSuccess(true);
		}
		return result;
	}
	/**
	 * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "业务配置表-通过id查询")
	@ApiOperation(value="业务配置表-通过id查询", notes="业务配置表-通过id查询")
	@GetMapping(value = "/queryInit")
	public Result<Map<String,Object>> queryInit(@RequestParam(name="functionId",required=true) int functionId) {
		Result<Map<String,Object>> result = new Result<Map<String,Object>>();
		Map<String,Object> map = busFunctionService.getEditInit(functionId);
		if(map == null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(map);
			result.setSuccess(true);
		}
		return result;
	}
	@AutoLog(value = "业务配置表-通过id查询")
	@ApiOperation(value="业务配置表-通过id查询", notes="业务配置表-通过id查询")
	@GetMapping(value = "/queryRoleAndDepart")
	public Result<Map<String,Object>> queryRoleAndDepart() {
		Result<Map<String,Object>> result = new Result<Map<String,Object>>();
		Map<String,Object> map = busFunctionService.queryRoleAndDepartSer();
		if(map == null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(map);
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
      QueryWrapper<BusFunction> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              BusFunction busFunction = JSON.parseObject(deString, BusFunction.class);
              queryWrapper = QueryGenerator.initQueryWrapper(busFunction, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<BusFunction> pageList = busFunctionService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "业务配置表列表");
      mv.addObject(NormalExcelConstants.CLASS, BusFunction.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("业务配置表列表数据", "导出人:Jeecg", "导出信息"));
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
              List<BusFunction> listBusFunctions = ExcelImportUtil.importExcel(file.getInputStream(), BusFunction.class, params);
              busFunctionService.saveBatch(listBusFunctions);
              return Result.ok("文件导入成功！数据行数:" + listBusFunctions.size());
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
