package com.cfcc.modules.oaTeamwork.controller;

import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.service.IBusFunctionService;
import com.cfcc.modules.oaTeamwork.entity.oaTeamwork;
import com.cfcc.modules.oaTeamwork.entity.oaTeamworkInst;
import com.cfcc.modules.oaTeamwork.service.IoaTeamworkInstService;
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
 * @Description: 个人协同办公业务实例
 * @Author: jeecg-boot
 * @Date:   2020-01-02
 * @Version: V1.0
 */
@Slf4j
@Api(tags="个人协同办公业务实例")
@RestController
@RequestMapping("/oaTea/oaTeamworkInst")
public class oaTeamworkInstController {
	@Autowired
	private IoaTeamworkInstService oaTeamworkInstService;

	@Autowired
	private IBusFunctionService iBusFunctionService;
	/**
	  * 分页列表查询
	 * @param oaTeamworkInst
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "个人协同办公业务实例-分页列表查询")
	@ApiOperation(value="个人协同办公业务实例-分页列表查询", notes="个人协同办公业务实例-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<oaTeamworkInst>> queryPageList(oaTeamworkInst oaTeamworkInst,
													   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													   HttpServletRequest req) {
		Result<IPage<oaTeamworkInst>> result = new Result<IPage<oaTeamworkInst>>();
		IPage<oaTeamworkInst> pageList = oaTeamworkInstService.findPage(pageNo, pageSize, oaTeamworkInst);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	 @AutoLog(value = "个人协同办公业务实例-分页列表查询")
	 @ApiOperation(value="个人协同办公业务实例-分页列表查询", notes="个人协同办公业务实例-分页列表查询")
	 @GetMapping(value = "/querylist")
	 public Result<List<oaTeamworkInst>> querylist(oaTeamworkInst oaTeamworkInst,
														@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														HttpServletRequest req) {
		 Result<List<oaTeamworkInst>> result = new Result<List<oaTeamworkInst>>();
		 List<oaTeamworkInst> pageList = oaTeamworkInstService.findList(oaTeamworkInst);
		 result.setSuccess(true);
		 result.setResult(pageList);
		 return result;
	 }
	 @AutoLog(value = "个人协同办公业务实例-分页列表查询")
	 @ApiOperation(value="个人协同办公业务实例-分页列表查询", notes="个人协同办公业务实例-分页列表查询")
	 @GetMapping(value = "/findlist")
	 public Result<List<BusFunction>> findlist() {
		 Result<List<BusFunction>> result = new Result<List<BusFunction>>();
		 String schema = MycatSchema.getSchema();
		 List<BusFunction> pageList = iBusFunctionService.findList(schema);
		 result.setSuccess(true);
		 result.setResult(pageList);
		 return result;
	 }


	 /**
	  *   添加
	 * @param
	 * @return
	 */
	@AutoLog(value = "个人协同办公业务实例-添加")
	@ApiOperation(value="个人协同办公业务实例-添加", notes="个人协同办公业务实例-添加")
	@PostMapping(value = "/add")
	public Result<oaTeamworkInst> add(@RequestBody String dataList) {
		Result<oaTeamworkInst> result = new Result<oaTeamworkInst>();
		try {
			Map map = (Map) JSONObject.parse(dataList);
			JSONObject jsonObject1 = (JSONObject) map.get("data1");
			JSONObject jsonObject2 = (JSONObject) map.get("data2");
            oaTeamworkInst oaTeamworkInst1 = JSONObject.toJavaObject( jsonObject1,oaTeamworkInst.class);
            oaTeamworkInst oaTeamworkInst2 = JSONObject.toJavaObject( jsonObject2,oaTeamworkInst.class);
			if(oaTeamworkInst1 != null){
				oaTeamworkInstService.Insert(oaTeamworkInst1);
			}

			if(oaTeamworkInst2 != null){
				oaTeamworkInstService.Insert(oaTeamworkInst2);
			}


            result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param oaTeamworkInst
	 * @return
	 */
	@AutoLog(value = "个人协同办公业务实例-编辑")
	@ApiOperation(value="个人协同办公业务实例-编辑", notes="个人协同办公业务实例-编辑")
	@PutMapping(value = "/edit")
	public Result<oaTeamworkInst> edit(@RequestBody oaTeamworkInst oaTeamworkInst) {
		Result<oaTeamworkInst> result = new Result<oaTeamworkInst>();
		oaTeamworkInst oaTeamworkInstEntity = oaTeamworkInstService.findById(oaTeamworkInst.getIId());
		if(oaTeamworkInstEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = oaTeamworkInstService.updateByIid(oaTeamworkInst);
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
	@AutoLog(value = "个人协同办公业务实例-通过id删除")
	@ApiOperation(value="个人协同办公业务实例-通过id删除", notes="个人协同办公业务实例-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			oaTeamworkInstService.deleteById(id);
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
	@AutoLog(value = "个人协同办公业务实例-批量删除")
	@ApiOperation(value="个人协同办公业务实例-批量删除", notes="个人协同办公业务实例-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<oaTeamworkInst> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<oaTeamworkInst> result = new Result<oaTeamworkInst>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			List<String> list = Arrays.asList(ids.split(","));
			for (int i=0;i<list.size();i++)
			{
				this.oaTeamworkInstService.deleteById(list.get(i));
			}
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "个人协同办公业务实例-通过id查询")
	@ApiOperation(value="个人协同办公业务实例-通过id查询", notes="个人协同办公业务实例-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<oaTeamworkInst> queryById(@RequestParam(name="id",required=true) String id) {
		Result<oaTeamworkInst> result = new Result<oaTeamworkInst>();
		oaTeamworkInst oaTeamworkInst = oaTeamworkInstService.findById(Integer.valueOf(id));
		if(oaTeamworkInst==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(oaTeamworkInst);
			result.setSuccess(true);
		}
		return result;
	}
   /**
	 * 查询最大的步骤值
	 * @return
	 */
	@AutoLog(value = "个人协同办公业务实例")
	@ApiOperation(value="个人协同办公业务实例", notes="个人协同办公业务实例")
	@GetMapping(value = "/findMax")
	public Result<Integer> findMax(Integer iteamworkId) {
		Result<Integer> result = new Result<Integer>();
		Integer maxOrder = oaTeamworkInstService.findMax(iteamworkId);
		if(maxOrder == null){
			return null ;
		}else{
			result.setResult(maxOrder);
		}

		return result;
	}
	/**
	 * 查询最大的步骤值
	 * @return
	 */
	@AutoLog(value = "个人协同办公业务实例")
	@ApiOperation(value="个人协同办公业务实例", notes="个人协同办公业务实例")
	@GetMapping(value = "/findOrder")
	public Result<List<Integer>> findOrder(Integer iteamworkId) {
		Result<List<Integer>> result = new Result<List<Integer>>();
		List<Integer> orderList = oaTeamworkInstService.findOrder(iteamworkId);
		if(orderList == null){
			return null ;
		}else{
			result.setResult(orderList);
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
      QueryWrapper<oaTeamworkInst> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              oaTeamworkInst oaTeamworkInst = JSON.parseObject(deString, oaTeamworkInst.class);
              queryWrapper = QueryGenerator.initQueryWrapper(oaTeamworkInst, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<oaTeamworkInst> pageList = oaTeamworkInstService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "个人协同办公业务实例列表");
      mv.addObject(NormalExcelConstants.CLASS, oaTeamworkInst.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("个人协同办公业务实例列表数据", "导出人:Jeecg", "导出信息"));
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
              List<oaTeamworkInst> listoaTeamworkInsts = ExcelImportUtil.importExcel(file.getInputStream(), oaTeamworkInst.class, params);
              oaTeamworkInstService.saveBatch(listoaTeamworkInsts);
              return Result.ok("文件导入成功！数据行数:" + listoaTeamworkInsts.size());
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
