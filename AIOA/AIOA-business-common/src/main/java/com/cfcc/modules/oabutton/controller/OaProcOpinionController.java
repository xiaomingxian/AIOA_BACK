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
import com.cfcc.modules.oabutton.entity.OaProcButton;
import com.cfcc.modules.oabutton.entity.OaProcOpinion;
import com.cfcc.modules.oabutton.service.IOaOpinionSetService;
import com.cfcc.modules.oabutton.service.IOaProcOpinionService;
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
@RequestMapping("/oaprocopinion/oaProcOpinion")
public class OaProcOpinionController {
	@Autowired
	private IOaProcOpinionService oaProcOpinionService;
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
	 @AutoLog(value = "意见配置按钮-删除前校验是否有业务数据")
	 @ApiOperation(value = "意见配置按钮-删除前校验是否有业务数据", notes = "意见配置按钮-删除前校验是否有业务数据")
	 @GetMapping(value = "/verButtonSetDelete")
	 public Result<?> verButtonSetDelete(@RequestParam(name = "id", required = true) String id) {
		 try {
			 if (id==null||id.trim().length()<1){
				 return Result.error("校验id为空!");
			 }
			 Map<String,Object> mapNow=new HashMap<>();
			 mapNow.put("iProcOpinionId",id);
			 //获取modelid ,funtionid
             List<BusProcSet> procSetByprocbuttonId = busProcSetService.getProcSetByprocbuttonId(mapNow);
			 if (procSetByprocbuttonId==null){
				 return Result.error("数据获取失败");
			 }
             for (int j = 0; j < procSetByprocbuttonId.size(); j++) {
                 BusModel busModelById = busModelService.getBusModelById(procSetByprocbuttonId.get(j).getIBusModelId());
                 if (busModelById==null){
//                     return Result.error("获取失败");
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
	  * 分页列表查询
	 * @param oaProcOpinion
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "意见配置按钮-分页列表查询")
	@ApiOperation(value="意见配置按钮-分页列表查询", notes="意见配置按钮-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<OaProcOpinion>> queryPageList(OaProcOpinion oaProcOpinion,
													  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													  HttpServletRequest req) {
		Result<IPage<OaProcOpinion>> result = new Result<IPage<OaProcOpinion>>();
	/*	QueryWrapper<OaProcOpinion> queryWrapper = QueryGenerator.initQueryWrapper(oaProcOpinion, req.getParameterMap());
		Page<OaProcOpinion> page = new Page<OaProcOpinion>(pageNo, pageSize);
		IPage<OaProcOpinion> pageList = oaProcOpinionService.page(page, queryWrapper);*/
		IPage<OaProcOpinion> pageList = oaProcOpinionService.getPage(pageNo,pageSize,oaProcOpinion);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param map
	 * @return
	 */
	@AutoLog(value = "意见配置按钮-添加")
	@ApiOperation(value="意见配置按钮-添加", notes="意见配置按钮-添加")
	@PostMapping(value = "/add")
	public Result<OaProcOpinion> add(@RequestBody Map<String,Object> map) {
		OaProcOpinion oaProcOpinion=new OaProcOpinion();
		if (map.get("procDefKey")!=null){
			oaProcOpinion.setProcDefKey(map.get("procDefKey").toString());
		}
		if (map.get("sprocOpinionName")!=null){
			oaProcOpinion.setSProcOpinionName(map.get("sprocOpinionName").toString());
		}
		Result<OaProcOpinion> result = new Result<OaProcOpinion>();
		try {
			oaProcOpinionService.insertOaProcOpinion(oaProcOpinion);
			result.success("添加成功！");
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param map
	 * @return
	 */
	@AutoLog(value = "意见配置按钮-编辑")
	@ApiOperation(value="意见配置按钮-编辑", notes="意见配置按钮-编辑")
	@PutMapping(value = "/edit")
	public Result<OaProcOpinion> edit(@RequestBody Map<String,Object> map) {
		OaProcOpinion oaProcOpinion=new OaProcOpinion();
		if (map.get("iid")!=null){
			oaProcOpinion.setIId((Integer)map.get("iid"));		}
		if (map.get("sprocOpinionName")!=null){
			oaProcOpinion.setSProcOpinionName(map.get("sprocOpinionName").toString());
		}
		if (map.get("procDefKey")!=null){
			oaProcOpinion.setProcDefKey(map.get("procDefKey").toString());
		}
		Result<OaProcOpinion> result = new Result<OaProcOpinion>();
		OaProcOpinion oaProcOpinionEntity = oaProcOpinionService.queryById(oaProcOpinion.getIId());
		if(oaProcOpinionEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = oaProcOpinionService.updateOaProcOpinionById(oaProcOpinion);
			//TODO 返回false说明什么？
			if(ok) {
				result.success("修改成功!");
				result.setSuccess(true);
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
			oaProcOpinionService.deleteOaProcOpinionByID(id);
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
	public Result<OaProcOpinion> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<OaProcOpinion> result = new Result<OaProcOpinion>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			List<String> idList = Arrays.asList(ids.split(","));
			for (int i = 0; i < idList.size(); i++) {
				this.oaProcOpinionService.deleteOaProcOpinionByID(idList.get(i));
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
	public Result<OaProcOpinion> queryById(@RequestParam(name="id",required=true) Integer id) {
		Result<OaProcOpinion> result = new Result<OaProcOpinion>();
		OaProcOpinion oaProcOpinion = oaProcOpinionService.queryById(id);
		if(oaProcOpinion==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(oaProcOpinion);
			result.setSuccess(true);
		}
		return result;
	}

	 /**
	  * 复制配置-通过id
	  * @param
	  * @return
	  */
	 @AutoLog(value = "意见配置按钮-通过id复制配置")
	 @ApiOperation(value="意见配置按钮-复制配置", notes="意见配置按钮-通过id复制配置")
	 @PostMapping(value = "/copeConfig")
	 public Result<OaProcOpinion> copeConfig(@RequestBody OaProcOpinion oaProcOpinion1) {
		 Result<OaProcOpinion> result = new Result<OaProcOpinion>();
		 OaProcOpinion oaProcOpinion = oaProcOpinionService.queryById(oaProcOpinion1.getIId());
		 if(oaProcOpinion==null) {
			 result.error500("未找到对应实体");
		 }else {
			 int countName = oaProcOpinionService.queryBysProcOpinionName(oaProcOpinion1.getSProcOpinionName());
			 if (countName>0){
				 result.error500("意见配置名称重复");
				 return result;
			 }

			 if (oaProcOpinion1.getSProcOpinionName()==null || oaProcOpinion1.getSProcOpinionName().trim().length()<1){
				 result.error500("流程配置名称不能为空");
				 return result;
			 }
			 //重新配置命名
			 oaProcOpinion.setSProcOpinionName(oaProcOpinion1.getSProcOpinionName());
			 int ids = oaProcOpinionService.insertOaProcOpinion(oaProcOpinion);
			 if (ids<=0){
				 result.error500("复制失败");
				 return result;
			 }

			 List<OaOpinionSet> OaOpinionSetList = oaOpinionSetService.queryListByOpinionId(oaProcOpinion1.getIId());
			 //判断按钮是否有关联流程，若有复制相关流程
			 if (OaOpinionSetList.size()>0){
				 for (int i = 0; i < OaOpinionSetList.size(); i++) {
					 OaOpinionSetList.get(i).setIProcOpinionId(oaProcOpinion.getIId());
					 OaOpinionSetList.get(i).setProcDefKey(oaProcOpinion.getProcDefKey());
					 oaOpinionSetService.insertOaButtonSet(OaOpinionSetList.get(i));
				 }
			 }
			 result.setResult(oaProcOpinion);
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
      QueryWrapper<OaProcOpinion> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              OaProcOpinion oaProcOpinion = JSON.parseObject(deString, OaProcOpinion.class);
              queryWrapper = QueryGenerator.initQueryWrapper(oaProcOpinion, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<OaProcOpinion> pageList = oaProcOpinionService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "意见配置按钮列表");
      mv.addObject(NormalExcelConstants.CLASS, OaProcOpinion.class);
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
              List<OaProcOpinion> listOaProcOpinions = ExcelImportUtil.importExcel(file.getInputStream(), OaProcOpinion.class, params);
              oaProcOpinionService.saveBatch(listOaProcOpinions);
              return Result.ok("文件导入成功！数据行数:" + listOaProcOpinions.size());
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
