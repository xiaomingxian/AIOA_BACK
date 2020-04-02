package com.cfcc.modules.oaTeamwork.controller;
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
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.common.util.oConvertUtils;
import java.util.Date;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.cfcc.modules.oaTeamwork.entity.oaTeamwork;
import com.cfcc.modules.oaTeamwork.entity.oaTeamworkInst;
import com.cfcc.modules.oaTeamwork.entity.oaTeamworkSet;
import com.cfcc.modules.oaTeamwork.service.IoaTeamworkSetService;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.service.ISysDepartService;
import com.cfcc.modules.system.service.ISysUserService;
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
 * @Description: 个人协同办公业务配置明细
 * @Author: jeecg-boot
 * @Date:   2019-12-30
 * @Version: V1.0
 */
@Slf4j
@Api(tags="个人协同办公业务配置明细")
@RestController
@RequestMapping("/oateamwork/oaTeamworkSet")
public class oaTeamworkSetController {
	@Autowired
	private IoaTeamworkSetService oaTeamworkSetService;
	@Autowired
	private ISysDepartService iSysDepartService ;
	@Autowired
	private ISysUserService  iSysUserService;
	@Autowired
	private IOaBusdataService iOaBusdataService;
	
	/**
	  * 分页列表查询
	 * @param oaTeamworkSet
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "个人协同办公业务配置明细-分页列表查询")
	@ApiOperation(value="个人协同办公业务配置明细-分页列表查询", notes="个人协同办公业务配置明细-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<oaTeamworkSet>> queryPageList(oaTeamworkSet oaTeamworkSet,
													  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													  HttpServletRequest req) {
		Result<IPage<oaTeamworkSet>> result = new Result<IPage<oaTeamworkSet>>();
		IPage<oaTeamworkSet> pageList = oaTeamworkSetService.findPage(pageNo, pageSize, oaTeamworkSet);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	 @AutoLog(value = "个人协同办公业务配置分类-分页列表查询")
	 @ApiOperation(value="个人协同办公业务配置分类-分页列表查询", notes="个人协同办公业务配置分类-分页列表查询")
	 @GetMapping(value = "/queryList")
	 public Result<IPage<oaTeamworkSet>> queryList(oaTeamworkSet oaTeamworkSet,
												@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												HttpServletRequest req) {

		 Result<IPage<oaTeamworkSet>> result = new Result<IPage<oaTeamworkSet>>();
		 IPage<oaTeamworkSet> pageList = oaTeamworkSetService.queryList(pageNo, pageSize, oaTeamworkSet);

		 result.setSuccess(true);
		 result.setResult(pageList);
		 return result;
	 }
	 @AutoLog(value = "个人协同办公业务配置分类")
	 @ApiOperation(value="个人协同办公业务配置分类-分页列表查询", notes="个人协同办公业务配置分类-分页列表查询")
	 @GetMapping(value = "/selectNext")
	 public oaTeamworkSet  selectNext(oaTeamworkSet oaTeamworkSet) {
		 oaTeamworkSet teamworkSet = oaTeamworkSetService.select(oaTeamworkSet);
		 return teamworkSet;
	 }
	/**
	  *   添加
	 * @param oaTeamworkSet
	 * @return
	 */
	@AutoLog(value = "个人协同办公业务配置明细-添加")
	@ApiOperation(value="个人协同办公业务配置明细-添加", notes="个人协同办公业务配置明细-添加")
	@PostMapping(value = "/add")
	public Result<oaTeamworkSet> add(@RequestBody oaTeamworkSet oaTeamworkSet,HttpServletRequest request) {
		LoginInfo loginInfo = iSysUserService.getLoginInfo(request);
		Result<oaTeamworkSet> result = new Result<oaTeamworkSet>();
		String UserId = loginInfo.getId();//创建者id
		String  DepartId= loginInfo.getDepart().getId();//创建者部门id
		SysDepart unit = iSysDepartService.getUnitByDeptId(DepartId);
		if(unit == null){
			result.error500("未找到对应实体");
		}
		String parentId = unit.getId();//机构id
		try {
			oaTeamworkSet.setSCreateBy(UserId);
			oaTeamworkSet.setSCreateDeptid(DepartId);
			oaTeamworkSet.setSCreateUnitid(parentId);
			List<Integer> orderlist = oaTeamworkSetService.findorder();
			if(orderlist != null){
				if(orderlist.contains(oaTeamworkSet.getIOrder())){
					result.error500("序号不能重复");
				}else{
					oaTeamworkSetService.insert(oaTeamworkSet);
					result.success("添加成功！");
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param oaTeamworkSet
	 * @return
	 */
	@AutoLog(value = "个人协同办公业务配置明细-编辑")
	@ApiOperation(value="个人协同办公业务配置明细-编辑", notes="个人协同办公业务配置明细-编辑")
	@PutMapping(value = "/edit")
	public Result<oaTeamworkSet> edit(@RequestBody oaTeamworkSet oaTeamworkSet) {
		Result<oaTeamworkSet> result = new Result<oaTeamworkSet>();
		oaTeamworkSet oaTeamworkSetEntity = oaTeamworkSetService.findByid(oaTeamworkSet.getIId());
		if(oaTeamworkSetEntity==null) {
			result.error500("未找到对应实体");
		}else {
			List<Integer> orderlist = oaTeamworkSetService.findorder();
			if(orderlist != null){
				if(orderlist.contains(oaTeamworkSet.getIOrder()) && !oaTeamworkSetEntity.getIOrder().equals(oaTeamworkSet.getIOrder())){
					result.error500("序号不能重复");
				}else{
					boolean ok = oaTeamworkSetService.updateByIid(oaTeamworkSet);
					//TODO 返回false说明什么？
					if(ok) {
						result.success("修改成功!");
					}
				}
			}

		}
		
		return result;
	}
	
	/**
	  *   通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "个人协同办公业务配置明细-通过id删除")
	@ApiOperation(value="个人协同办公业务配置明细-通过id删除", notes="个人协同办公业务配置明细-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=false) String id) {
		try {
			oaTeamworkSetService.deleteById(id);
		} catch (Exception e) {
			log.error("删除失败",e.getMessage());
			return Result.error("删除失败!");
		}
		return Result.ok("删除成功!");
	}
	/**
	  *
	 * @param
	 * @return
	 */
	@AutoLog(value = "个人协同办公业务配置明细")
	@ApiOperation(value="个人协同办公业务配置明细", notes="个人协同办公业务配置明细")
	@GetMapping(value = "/findListByteamworkId")
	public List<oaTeamworkSet> findListByteamworkId(@RequestParam(name="TeamworkName",required=false) String TeamworkName) {
			List<oaTeamworkSet> list = oaTeamworkSetService.findListByteamworkId(TeamworkName);
			return list;
	}
	/**
	  *
	 * @param
	 * @return
	 */
	@AutoLog(value = "个人协同办公业务配置明细")
	@ApiOperation(value="个人协同办公业务配置明细", notes="个人协同办公业务配置明细")
	@GetMapping(value = "/findTableName")
	public String findTableName(@RequestParam(name="ibusModelId",required=false) Integer ibusModelId) {

		String tableName = iOaBusdataService.queryTableName(ibusModelId);
		if(tableName.equals("")){
			tableName = "";
		}
		return tableName;
	}
	 /**
	  *
	  * @param TeamworkId
	  * @return
	  */
	 @AutoLog(value = "个人协同办公业务配置明细")
	 @ApiOperation(value="个人协同办公业务配置明细", notes="个人协同办公业务配置明细")
	 @GetMapping(value = "/findMax")
	 public Result<Integer> findMax(@RequestParam(name="TeamworkId",required=false) Integer TeamworkId) {
		 Result<Integer> result = new Result<Integer>();
		 int maxOrder = oaTeamworkSetService.findMax(TeamworkId);
		 if(maxOrder == 0){
			 return null;
		 }else{
			 result.setResult(maxOrder);
		 }

		 return result;
	 }

	 /**
	  *  批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "个人协同办公业务配置明细-批量删除")
	@ApiOperation(value="个人协同办公业务配置明细-批量删除", notes="个人协同办公业务配置明细-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<oaTeamworkSet> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<oaTeamworkSet> result = new Result<oaTeamworkSet>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			List<String> list = Arrays.asList(ids.split(","));
			for (int i=0;i<list.size();i++)
			{
				this.oaTeamworkSetService.deleteById(list.get(i));
			}
			result.success("删除成功!");
		}
		return result;
	}
	 /**
	  * 下拉列表--模块下拉列表
	  *
	  * @param
	  * @return
	  */
	 @AutoLog(value = "个人协同办公业务配置分类-下拉列表")
	 @ApiOperation(value = "个人协同办公业务配置分类-下拉列表", notes = "个人协同办公业务配置分类-下拉列表")
	 @GetMapping(value = "/findTeamworkSet")
	 public Result<List<oaTeamworkSet>> findTeamworkSet(Integer  iTeamworkId) {
		 Result<List<oaTeamworkSet>> result = new Result<>();
		 try {
			 List<oaTeamworkSet> teamworkSets= oaTeamworkSetService.findTeamworkSet(iTeamworkId);
			 result.setSuccess(true);
			 result.setResult(teamworkSets);
		 } catch (Exception e) {
			 e.printStackTrace();
			 result.setSuccess(false);
		 }
		 return result;
	 }
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "个人协同办公业务配置明细-通过id查询")
	@ApiOperation(value="个人协同办公业务配置明细-通过id查询", notes="个人协同办公业务配置明细-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<oaTeamworkSet> queryById(@RequestParam(name="id",required=true) String id) {
		Result<oaTeamworkSet> result = new Result<oaTeamworkSet>();
		oaTeamworkSet oaTeamworkSet = oaTeamworkSetService.findByid(Integer.valueOf(id));
		if(oaTeamworkSet==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(oaTeamworkSet);
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
      QueryWrapper<oaTeamworkSet> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              oaTeamworkSet oaTeamworkSet = JSON.parseObject(deString, oaTeamworkSet.class);
              queryWrapper = QueryGenerator.initQueryWrapper(oaTeamworkSet, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<oaTeamworkSet> pageList = oaTeamworkSetService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "个人协同办公业务配置明细列表");
      mv.addObject(NormalExcelConstants.CLASS, oaTeamworkSet.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("个人协同办公业务配置明细列表数据", "导出人:Jeecg", "导出信息"));
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
              List<oaTeamworkSet> listoaTeamworkSets = ExcelImportUtil.importExcel(file.getInputStream(), oaTeamworkSet.class, params);
              oaTeamworkSetService.saveBatch(listoaTeamworkSets);
              return Result.ok("文件导入成功！数据行数:" + listoaTeamworkSets.size());
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
