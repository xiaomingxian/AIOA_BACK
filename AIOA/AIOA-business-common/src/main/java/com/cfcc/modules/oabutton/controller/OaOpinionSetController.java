package com.cfcc.modules.oabutton.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.util.oConvertUtils;

import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.oaBus.entity.BusProcSet;
import com.cfcc.modules.oaBus.service.IBusModelService;
import com.cfcc.modules.oaBus.service.IBusProcSetService;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.cfcc.modules.oabutton.entity.OaButtonSet;
import com.cfcc.modules.oabutton.entity.OaOpinionSet;
import com.cfcc.modules.oabutton.service.IOaOpinionSetService;
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
 * @Description: 意见配置按钮
 * @Author: jeecg-boot
 * @Date:   2019-10-27
 * @Version: V1.0
 */
@Slf4j
@Api(tags="意见配置按钮")
@RestController
@RequestMapping("/oaopinionset/oaOpinionSet")
public class OaOpinionSetController {
	@Autowired
	private IOaOpinionSetService oaOpinionSetService;

	 @Autowired
	 private IBusProcSetService busProcSetService;
	 @Autowired
	 private IBusModelService busModelService;
	 @Autowired
	 private IOaBusdataService oaBusdataService;
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
			 OaOpinionSet oaOpinionSet = oaOpinionSetService.queryById(Integer.valueOf(id));
			 if (oaOpinionSet==null || oaOpinionSet.getIProcOpinionId()==null){
				 return Result.error("数据查询失败");
			 }
			 //获取modelid ,funtionid
			 Map<String,Object> mapNow=new HashMap<>();
			 mapNow.put("iProcOpinionId",oaOpinionSet.getIProcOpinionId());
             List<BusProcSet>  procSetByprocbuttonId = busProcSetService.getProcSetByprocbuttonId(mapNow);
			 if (procSetByprocbuttonId==null){
				 return Result.error("数据获取失败");
			 }
             int m=0;
             for (int i = 0; i < procSetByprocbuttonId.size(); i++) {
                 BusModel busModelById = busModelService.getBusModelById(procSetByprocbuttonId.get(i).getIBusModelId());
                 if (busModelById==null){
//                     return Result.error("获取失败");
                 }else {
                     Map<String,Object> map=new HashMap<>();
                     //拼接参数
                     map.put("tableName",busModelById.getSBusdataTable());
                     map.put("iBusFunctionId",procSetByprocbuttonId.get(i).getIBusFunctionId());
                     map.put("iBusModelId",busModelById.getIId());
                     m = oaBusdataService.listCountBytableName(map);
                     if (m>0){
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
	  * 分页列表查询
	 * @param oaOpinionSet
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "意见配置按钮-分页列表查询")
	@ApiOperation(value="意见配置按钮-分页列表查询", notes="意见配置按钮-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<OaOpinionSet>> queryPageList(OaOpinionSet oaOpinionSet,
													 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													 HttpServletRequest req) {
		Result<IPage<OaOpinionSet>> result = new Result<IPage<OaOpinionSet>>();
		/*QueryWrapper<OaOpinionSet> queryWrapper = QueryGenerator.initQueryWrapper(oaOpinionSet, req.getParameterMap());
		Page<OaOpinionSet> page = new Page<OaOpinionSet>(pageNo, pageSize);
		IPage<OaOpinionSet> pageList = oaOpinionSetService.page(page, queryWrapper);*/
		IPage<OaOpinionSet> pageList = oaOpinionSetService.getPage(pageNo,pageSize,oaOpinionSet);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param oaOpinionSet
	 * @return
	 */
	@AutoLog(value = "意见配置按钮-添加")
	@ApiOperation(value="意见配置按钮-添加", notes="意见配置按钮-添加")
	@PostMapping(value = "/add")
	public Result<OaOpinionSet> add(@RequestBody OaOpinionSet oaOpinionSet) {
		Result<OaOpinionSet> result = new Result<OaOpinionSet>();
//		if(oaOpinionSet.getIProcSetId()==null) {
//			result.error500("请选择业务");
//			return result;
//		}
		if (oaOpinionSet==null|| oaOpinionSet.getIProcOpinionId()==null){
			result.error500("当前意见不存在");
			return result;
		}
		if (oaOpinionSet!=null && oaOpinionSet.getType()!=null && oaOpinionSet.getType().equals("9999")){
			oaOpinionSet.setType(null);
		}
		List<OaOpinionSet> oaOpinionSets = oaOpinionSetService.queryByType(oaOpinionSet.getType(), oaOpinionSet.getIProcOpinionId());
		if (oaOpinionSet.getType()!=null&&oaOpinionSet.getType().trim().length()>0&&oaOpinionSets.size()>0){
			result.error500("当前意见类型已存在");
			return result;
		}
		try {
			oaOpinionSetService.insertOaButtonSet(oaOpinionSet);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param oaOpinionSet
	 * @return
	 */
	@AutoLog(value = "意见配置按钮-编辑")
	@ApiOperation(value="意见配置按钮-编辑", notes="意见配置按钮-编辑")
	@PutMapping(value = "/edit")
	public Result<OaOpinionSet> edit(@RequestBody OaOpinionSet oaOpinionSet) {
		Result<OaOpinionSet> result = new Result<OaOpinionSet>();
//		if(oaOpinionSet.getIProcSetId()==null) {
//			result.error500("请选择业务");
//			return result;
//		}
		if (oaOpinionSet==null|| oaOpinionSet.getIProcOpinionId()==null){
			result.error500("当前意见不存在");
			return result;
		}
		if (oaOpinionSet!=null && oaOpinionSet.getType()!=null && oaOpinionSet.getType().equals("9999")){
			oaOpinionSet.setType(null);
		}
//		List<OaOpinionSet> oaOpinionSets = oaOpinionSetService.queryByType(oaOpinionSet.getType(), oaOpinionSet.getIProcOpinionId());
//		if (oaOpinionSet.getType()!=null&&oaOpinionSet.getType().trim().length()>0&&oaOpinionSets.size()>0&&oaOpinionSets.get(0).getIId()!=oaOpinionSet.getIId()){
//			result.error500("当前意见类型已存在");
//			return result;
//		}
		OaOpinionSet oaOpinionSetEntity = oaOpinionSetService.queryById(oaOpinionSet.getIId());
		if(oaOpinionSetEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = oaOpinionSetService.updateOaButtonById(oaOpinionSet);
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
	@AutoLog(value = "意见配置按钮-通过id删除")
	@ApiOperation(value="意见配置按钮-通过id删除", notes="意见配置按钮-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			oaOpinionSetService.deleteOaOpinionSetByID(id);
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
	@AutoLog(value = "意见配置按钮-批量删除")
	@ApiOperation(value="意见配置按钮-批量删除", notes="意见配置按钮-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<OaOpinionSet> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<OaOpinionSet> result = new Result<OaOpinionSet>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			List<String> idList = Arrays.asList(ids.split(","));
			for (int i = 0; i < idList.size(); i++) {
				this.oaOpinionSetService.deleteOaOpinionSetByID(idList.get(i));
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
	@AutoLog(value = "意见配置按钮-通过id查询")
	@ApiOperation(value="意见配置按钮-通过id查询", notes="意见配置按钮-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<OaOpinionSet> queryById(@RequestParam(name="id",required=true) Integer id) {
		Result<OaOpinionSet> result = new Result<OaOpinionSet>();
		OaOpinionSet oaOpinionSet = oaOpinionSetService.queryById(id);
		if(oaOpinionSet==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(oaOpinionSet);
			result.setSuccess(true);
		}
		return result;
	}


	 /**
	  * 通过任务KEY查询
	  * @param
	  * @param
	  * @return
	  */
	 @AutoLog(value = "发布类按钮描述-通过任务KEY和按钮ID查询")
	 @ApiOperation(value = "发布类按钮描述-通过任务KEY和按钮ID查询", notes = "发布类按钮描述-通过任务KEY和按钮ID查询")
	 @PostMapping(value = "/queryByTaskDefKey")
	 public Result<OaOpinionSet> queryByTaskKey(@RequestBody Map<String,Object> map) {
		 Result<OaOpinionSet> result = new Result<OaOpinionSet>();
		 if (map.get("taskDefKey")==null || map.get("iProcOpinionId")==null ||map.get("procDefKey")==null){
			 result.error500("未找到对应实体");
			 return result;
		 }
		 if(map.get("type")!=null && map.get("type")==" "|| map.get("type")!=null && map.get("type").equals("9999")){
			 map.put("type",null);
		 }
		 OaOpinionSet oaOpinionSet = oaOpinionSetService.queryByTaskKey(map);
		 if (oaOpinionSet == null) {
			 result.error500("未找到对应数据");
		 } else {
			 result.setResult(oaOpinionSet);
			 result.setSuccess(true);
		 }
		 return result;
	 }

	 /**
	  * 通过任务KEY查询
	  * @param
	  * @param
	  * @return
	  */
	 @AutoLog(value = "意见配置-根据序号校验名字")
	 @ApiOperation(value = "意见配置-根据序号校验名字", notes = "意见配置-根据序号校验名字")
	 @PostMapping(value = "/queryByOrderAndKey")
	 public Result<List<OaOpinionSet>> queryByOrderAndKey(@RequestBody Map<String,Object> map) {
		 Result<List<OaOpinionSet>> result = new Result<>();
		 if (map.get("itaskOpinionOrder")==null
				 ||map.get("procDefKey")==null){
			 result.error500("未找到对应实体");
			 return result;
		 }
		 List<OaOpinionSet> oaOpinionSet = oaOpinionSetService.queryByOrderAndKey(map);
		 if (oaOpinionSet == null || oaOpinionSet.size()<1) {
			 result.setResult(null);
			 result.setSuccess(true);
		 }else if(oaOpinionSet.size()==1 && map.get("iid")!=null && map.get("iid").toString()!="" && oaOpinionSet.get(0).getIId().equals(map.get("iid"))){
			 result.setResult(null);
			 result.setSuccess(true);
		 }else {
			 result.setResult(oaOpinionSet);
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
      QueryWrapper<OaOpinionSet> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              OaOpinionSet oaOpinionSet = JSON.parseObject(deString, OaOpinionSet.class);
              queryWrapper = QueryGenerator.initQueryWrapper(oaOpinionSet, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<OaOpinionSet> pageList = oaOpinionSetService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "意见配置按钮列表");
      mv.addObject(NormalExcelConstants.CLASS, OaOpinionSet.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("意见配置按钮列表数据", "导出人:Jeecg", "导出信息"));
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
              List<OaOpinionSet> listOaOpinionSets = ExcelImportUtil.importExcel(file.getInputStream(), OaOpinionSet.class, params);
              oaOpinionSetService.saveBatch(listOaOpinionSets);
              return Result.ok("文件导入成功！数据行数:" + listOaOpinionSets.size());
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
