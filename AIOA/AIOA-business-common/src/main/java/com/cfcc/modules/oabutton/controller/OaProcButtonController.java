package com.cfcc.modules.oabutton.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.oaBus.entity.BusProcSet;
import com.cfcc.modules.oaBus.service.IBusModelService;
import com.cfcc.modules.oaBus.service.IBusProcSetService;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.cfcc.modules.oabutton.entity.OaButtonSet;
import com.cfcc.modules.oabutton.entity.OaProcButton;
import com.cfcc.modules.oabutton.service.IOaButtonSetService;
import com.cfcc.modules.oabutton.service.IOaProcButtonService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 /**
 * @Description: 发布类按钮管理
 * @Author: jeecg-boot
 * @Date:   2019-10-26
 * @Version: V1.0
 */
@Slf4j
@Api(tags="发布类按钮管理")
@RestController
@RequestMapping("/oaprocbutton/oaProcButton")
public class OaProcButtonController {
	@Autowired
	private IOaProcButtonService oaProcButtonService;

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
	 * @param oaProcButton
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "发布类按钮管理-分页列表查询")
	@ApiOperation(value="发布类按钮管理-分页列表查询", notes="发布类按钮管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<OaProcButton>> queryPageList(OaProcButton oaProcButton,
													 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													 HttpServletRequest req) {
		Result<IPage<OaProcButton>> result = new Result<IPage<OaProcButton>>();
		/*QueryWrapper<OaProcButton> queryWrapper = QueryGenerator.initQueryWrapper(oaProcButton, req.getParameterMap());
		Page<OaProcButton> page = new Page<OaProcButton>(pageNo, pageSize);
		IPage<OaProcButton> pageList = oaProcButtonService.page(page, queryWrapper);*/
		IPage<OaProcButton> pageList = oaProcButtonService.getPage(pageNo,pageSize,oaProcButton);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	 /**
	  *
	  * @return
	  */
	 @AutoLog(value = "业务配置-查询业务配置 流程相关的按钮")
	 @ApiOperation(value="业务配置-查询业务配置 流程相关的按钮", notes="业务配置-查询业务配置 流程相关的按钮")
	 @GetMapping(value = "/queryProcButton")
	 public Result<List<OaProcButton>> queryProcButton(String key) {
		 Result<List<OaProcButton>> result = new Result<List<OaProcButton>>();
		 List<OaProcButton> list = oaProcButtonService.queryProcButtonByProc(key) ;
		 result.setSuccess(true);
		 result.setResult(list);
		 return result;
	 }
	 @AutoLog(value = "按钮配置-查询按钮配置 没有流程相关的按钮")
	 @ApiOperation(value="按钮配置-查询按钮配置 没有流程相关的按钮", notes="按钮配置-查询按钮配置 没有流程相关的按钮")
	 @GetMapping(value = "/queryNoProcButton")
	 public Result<List<OaProcButton>> queryNoProcButton() {
		 Result<List<OaProcButton>> result = new Result<List<OaProcButton>>();
		 List<OaProcButton> list = oaProcButtonService.queryNoProcButtonByProc() ;
		 result.setSuccess(true);
		 result.setResult(list);
		 return result;
	 }


	 /**
	  *   添加
	 * @param oaProcButton
	 * @return
	 */
	@AutoLog(value = "发布类按钮管理-添加")
	@ApiOperation(value="发布类按钮管理-添加", notes="发布类按钮管理-添加")
	@PostMapping(value = "/add")
	public Result<OaProcButton> add(@RequestBody OaProcButton oaProcButton) {
		Result<OaProcButton> result = new Result<OaProcButton>();
		try {
			int count = oaProcButtonService.queryProcButtonBySButtonSetName(oaProcButton.getSButtonSetName());
			if (count>0){
				result.error500("添加失败,配置名称不能重复");
			}
			oaProcButtonService.insertProcOaButton(oaProcButton);
			result.success("添加成功！");
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
	@AutoLog(value = "发布类按钮管理-编辑")
	@ApiOperation(value="发布类按钮管理-编辑", notes="发布类按钮管理-编辑")
	@PutMapping(value = "/edit")
	public Result<OaProcButton> edit(@RequestBody Map<String,Object> map) {
		OaProcButton oaProcButton=new OaProcButton();
		if (map.get("iid")!=null){
			oaProcButton.setIId((Integer)map.get("iid"));		}
		if (map.get("sbuttonSetName")!=null){
			oaProcButton.setSButtonSetName(map.get("sbuttonSetName").toString());
		}
		if (map.get("procDefKey")!=null){
			oaProcButton.setProcDefKey(map.get("procDefKey").toString());
		}
		Result<OaProcButton> result = new Result<OaProcButton>();
		OaProcButton oaProcButtonEntity = oaProcButtonService.queryById(oaProcButton.getIId());
		if(oaProcButtonEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = oaProcButtonService.updateProcOaButtonById(oaProcButton);
			//TODO 返回false说明什么？
			if(ok) {
				result.success("修改成功!");
			}
		}
		
		return result;
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
			 //获取modelid ,funtioni
			 Map<String,Object> mapNow=new HashMap<>();
			 mapNow.put("iProcButtonId",id);
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
	  *   通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "发布类按钮管理-通过id删除")
	@ApiOperation(value="发布类按钮管理-通过id删除", notes="发布类按钮管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			String schema = MycatSchema.getSchema();
			oaProcButtonService.deleteProcOaButtonByID(id);
			List<OaButtonSet> oaButtonSetList = oaButtonSetService.queryByProcButtonId(Integer.valueOf(id));
			if (oaButtonSetList!=null && oaButtonSetList.size()>0){
				oaButtonSetService.deleteOaButtonSetByProcId(id,schema);
			}
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
	@AutoLog(value = "发布类按钮管理-批量删除")
	@ApiOperation(value="发布类按钮管理-批量删除", notes="发布类按钮管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<OaProcButton> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<OaProcButton> result = new Result<OaProcButton>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
//			List<String> idList = Arrays.asList(ids.split(","));
//			for (int i = 0; i < idList.size(); i++) {
				this.oaProcButtonService.removeByIds(Arrays.asList(ids.split(",")));
//			}
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "发布类按钮管理-通过id查询")
	@ApiOperation(value="发布类按钮管理-通过id查询", notes="发布类按钮管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<OaProcButton> queryById(@RequestParam(name="id",required=false) Integer id) {
		Result<OaProcButton> result = new Result<OaProcButton>();
		OaProcButton oaProcButton = oaProcButtonService.queryById(id);
		if(oaProcButton==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(oaProcButton);
			result.setSuccess(true);
		}
		return result;
	}

	 /**
	  * 复制配置-通过id
	  * @param
	  * @return
	  */
	 @AutoLog(value = "发布类按钮管理-通过id复制配置")
	 @ApiOperation(value="发布类按钮管理-复制配置", notes="发布类按钮管理-通过id复制配置")
	 @PostMapping(value = "/copeConfig")
	 public Result<OaProcButton> copeConfig(@RequestBody OaProcButton oaProcButton1) {
		 Result<OaProcButton> result = new Result<OaProcButton>();
		 OaProcButton oaProcButton = oaProcButtonService.queryById(oaProcButton1.getIId());

		 if(oaProcButton==null) {
			 result.error500("未找到对应实体");
		 }else {
			 int countName = oaProcButtonService.queryProcButtonBySButtonSetName(oaProcButton1.getSButtonSetName());
			 if (countName>0){
				 result.error500("流程配置名称重复");
				 return result;
			 }

			 if (oaProcButton1.getSButtonSetName()==null || oaProcButton1.getSButtonSetName().trim().length()<1){
				 result.error500("流程配置名称不能为空");
				 return result;
			 }
			 //重新配置命名
			 oaProcButton.setSButtonSetName(oaProcButton1.getSButtonSetName());
			 int ids = oaProcButtonService.insertProcOaButton(oaProcButton);
//			 测试添加主键返回
			 oaProcButton.getIId();
			 if (ids<=0){
				 result.error500("复制失败");
				 return result;
			 }
			 String schema = MycatSchema.getSchema();
			 List<OaButtonSet> oaButtonSetList = oaButtonSetService.queryByProcButtonId(oaProcButton1.getIId());
			 //判断按钮是否有关联流程，若有复制相关流程
			 if (oaButtonSetList.size()>0){
				 for (int i = 0; i < oaButtonSetList.size(); i++) {
				 	if (oaButtonSetList.get(i).getSRoles()==null || oaButtonSetList.get(i).getSRoles().equalsIgnoreCase("")){
						oaButtonSetList.get(i).setSRoles("0");
//						oaButtonSetList.get(i).setIPermitType(1);
					}
					 oaButtonSetList.get(i).setIProcButtonId(oaProcButton.getIId());
					 oaButtonSetList.get(i).setProcDefKey(oaProcButton.getProcDefKey());
					 oaButtonSetService.insertoaButtonSet(oaButtonSetList.get(i),schema);
				 }
			 }
			 result.setResult(oaProcButton);
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
      QueryWrapper<OaProcButton> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              OaProcButton oaProcButton = JSON.parseObject(deString, OaProcButton.class);
              queryWrapper = QueryGenerator.initQueryWrapper(oaProcButton, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<OaProcButton> pageList = oaProcButtonService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "发布类按钮管理列表");
      mv.addObject(NormalExcelConstants.CLASS, OaProcButton.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("发布类按钮管理列表数据", "导出人:Jeecg", "导出信息"));
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
              List<OaProcButton> listOaProcButtons = ExcelImportUtil.importExcel(file.getInputStream(), OaProcButton.class, params);
              oaProcButtonService.saveBatch(listOaProcButtons);
              return Result.ok("文件导入成功！数据行数:" + listOaProcButtons.size());
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
