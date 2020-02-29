package com.cfcc.modules.docnum.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.common.system.vo.LoginUser;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.docnum.entity.DocNumExport;
import com.cfcc.modules.docnum.entity.DocNumSet;
import com.cfcc.modules.docnum.mapper.DocNumSetMapper;
import com.cfcc.modules.docnum.service.IDocNumSetService;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.oaBus.entity.oaCalendar;
import com.cfcc.modules.shiro.vo.DefContants;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.mapper.SysUserRoleMapper;
import com.cfcc.modules.system.model.DepartIdModel;
import com.cfcc.modules.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 文号配置
 * @Author: jeecg-boot
 * @Date:   2019-10-16
 * @Version: V1.0
 */
@Slf4j
@Api(tags="文号配置")
@RestController
@RequestMapping("/papertitle/docNumSet")
public class DocNumSetController {
	@Autowired
	private IDocNumSetService docNumSetService;

	@Resource
	private DocNumSetMapper docNumSetMapper;
	@Resource
	private ISysUserService sysUserService;

	@Autowired
	private SysUserRoleMapper sysUserRoleMapper;
	/**
	  * 分页列表查询
	 * @param docNumSet
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@AutoLog(value = "文号配置-分页列表查询")
	@ApiOperation(value="文号配置-分页列表查询", notes="文号配置-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<DocNumSet>> queryPageList(DocNumSet docNumSet,
												  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Result<IPage<DocNumSet>> result = new Result<IPage<DocNumSet>>();
		IPage<DocNumSet> pageList = docNumSetService.queryDocNumList(docNumSet,pageNo,pageSize);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param docNumSet
	 * @return
	 */
	@AutoLog(value = "文号配置-添加")
	@ApiOperation(value="文号配置-添加", notes="文号配置-添加")
	@PostMapping(value = "/add")
	public Result<DocNumSet> add(@RequestBody DocNumSet docNumSet,HttpServletRequest request) {
		Result<DocNumSet> result = new Result<DocNumSet>();
		try {
			//获取当前用户名称
			String token = request.getHeader("X-Access-Token");
			String username = JwtUtil.getUsername(token);
			docNumSet.setSCreateBy(username);
			docNumSetService.insertDocNum(docNumSet);
			String[] departList = docNumSet.getSelecteddeparts().split(",");
			for (int i = 0; i <departList.length; i++) {
				boolean ok = docNumSetService.insertDepart(docNumSet.getIId(), departList[i]);
			}
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	/**
	 * 复制文号
	 * @param docNumSet
	 * @return
	 */
	@AutoLog(value = "文号配置-复制文号")
	@ApiOperation(value="文号配置-复制文号", notes="文号配置-复制文号")
	@PostMapping(value = "/addDocNum")
	public Result<DocNumSet> addDocNum(@RequestBody DocNumSet docNumSet,HttpServletRequest request) {
		Result<DocNumSet> result = new Result<DocNumSet>();
		try {
			//获取当前用户名称
			String token = request.getHeader("X-Access-Token");
			String username = JwtUtil.getUsername(token);
			docNumSet.setSCreateBy(username);
			Integer docId = docNumSet.getIId();
			docNumSet.setIId(0);
			docNumSetService.insertDocNum(docNumSet);
			String[] departList = docNumSetMapper.selectDepartIds(docId+"").split(",");
			for (int i = 0; i <departList.length; i++) {
				boolean ok = docNumSetService.insertDepart(docNumSet.getIId(), departList[i]);
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
	 * @param docNumSet
	 * @return
	 */
	@AutoLog(value = "文号配置-编辑")
	@ApiOperation(value="文号配置-编辑", notes="文号配置-编辑")
	@PutMapping(value = "/edit")
	public Result<DocNumSet> edit(@RequestBody DocNumSet docNumSet,HttpServletRequest request) {
		Result<DocNumSet> result = new Result<DocNumSet>();
		DocNumSet docNumSetEntity = docNumSetService.queryById(docNumSet.getIId());
		if(docNumSetEntity==null) {
			result.error500("未找到对应实体");
		}else {

			//获取当前用户名称
			String token = request.getHeader("X-Access-Token");
			String username = JwtUtil.getUsername(token);
			docNumSet.setSUpdateBy(username);
			boolean ok = docNumSetService.updateDocNumById(docNumSet);
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
	@AutoLog(value = "文号配置-通过id删除")
	@ApiOperation(value="文号配置-通过id删除", notes="文号配置-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			docNumSetService.deleteDocNumTitleByIID(id);
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
	@AutoLog(value = "文号配置-批量删除")
	@ApiOperation(value="文号配置-批量删除", notes="文号配置-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<DocNumSet> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<DocNumSet> result = new Result<DocNumSet>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			List<String> idList = Arrays.asList(ids.split(","));
			for (int i = 0; i < idList.size(); i++) {
				this.docNumSetService.deleteDocNumTitleByIID(idList.get(i));
			}			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "文号配置-通过id查询")
	@ApiOperation(value="文号配置-通过id查询", notes="文号配置-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<DocNumSet> queryById(@RequestParam(name="id",required=true) Integer id) {
		Result<DocNumSet> result = new Result<DocNumSet>();
		DocNumSet docNumSet = docNumSetService.queryById(id);
		if(docNumSet==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(docNumSet);
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
	@RequestMapping(value = "/docNumExportXls")
	public ModelAndView docNumExportXls(HttpServletRequest request, HttpServletResponse response) {
		String iid = request.getParameter("iid");
		String s_create_by = request.getParameter("s_create_by");
		List<Map<String, Object>> functionData = docNumSetMapper.selectBusdataLIstsByDocId(Integer.valueOf(iid));
		List<DocNumExport> exportsData = new ArrayList<>();
		int i = 0;
		for (Map<String,Object> table:functionData) {
			i++;
			DocNumExport docNumExport = docNumSetMapper.selectBusdataByIid(table.get("s_busdata_table") + "", (Integer) table.get("i_busdata_id"));
			docNumExport.setIId(i);
			exportsData.add(docNumExport);
		}
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "文号配置列表");
		mv.addObject(NormalExcelConstants.CLASS, DocNumExport.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("文号使用情况", "导出人:"+s_create_by, "文号使用列表"));
		mv.addObject(NormalExcelConstants.DATA_LIST, exportsData);
		return mv;
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
      QueryWrapper<DocNumSet> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              DocNumSet docNumSet = JSON.parseObject(deString, DocNumSet.class);
              queryWrapper = QueryGenerator.initQueryWrapper(docNumSet, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<DocNumSet> pageList = docNumSetService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "文号配置列表");
      mv.addObject(NormalExcelConstants.CLASS, DocNumSet.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("文号配置列表数据", "导出人:Jeecg", "导出信息"));
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
              List<DocNumSet> listDocNumSets = ExcelImportUtil.importExcel(file.getInputStream(), DocNumSet.class, params);
              docNumSetService.saveBatch(listDocNumSets);
              return Result.ok("文件导入成功！数据行数:" + listDocNumSets.size());
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
	 @AutoLog(value = "文号配置-模块下拉选列表")
	 @ApiOperation(value="文号配置-模块下拉选列表", notes="文号配置-模块下拉选列表")
	 @GetMapping(value = "/busModelList")
	 public Result busModelList() {
		 Result<List<BusModel>> result = new Result<>();
		 List<BusModel> busModelList = docNumSetService.busModelList();
		 if (busModelList.size() == 0) {
			 result.error500("未找到对应实体");
		 } else {
			 result.setResult(busModelList);
			 result.setSuccess(true);
		 }
		 return result;
	 }

	 @AutoLog(value = "文号配置-业务下拉选列表")
	 @ApiOperation(value="文号配置-业务下拉选列表", notes="文号配置-业务下拉选列表")
	 @GetMapping(value = "/busFunctionList")
	 public Result busFunctionList(@RequestParam(name="ibusModelId",required=true) Integer ibusModelId) {
		 Result<List<BusFunction>> result = new Result<>();
		 List<BusFunction> busModelList = docNumSetService.busFunctionList(ibusModelId);
		 if (busModelList.size() == 0) {
			 result.error500("未找到对应实体");
		 } else {
			 result.setResult(busModelList);
			 result.setSuccess(true);
		 }
		 return result;
	 }


	/**
	 * 查询文号和部门关联的数据
	 *
	 * @param docId
	 * @return
	 */
	@RequestMapping(value = "/getDocDepartsList", method = RequestMethod.GET)
	public Result<List<DepartIdModel>> getDocDepartsList(@RequestParam(name = "userId", required = true) String docId) {
		Result<List<DepartIdModel>> result = new Result<>();
		try {
			List<DepartIdModel> depIdModelList = docNumSetService.queryDepartIdsOfDocNum(docId);
			if (depIdModelList != null && depIdModelList.size() > 0) {
				result.setSuccess(true);
				result.setMessage("查找成功");
				result.setResult(depIdModelList);
			} else {
				result.setSuccess(false);
				result.setMessage("查找失败");
			}
			return result;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.setSuccess(false);
			result.setMessage("查找过程中出现了异常: " + e.getMessage());
			return result;
		}

	}

	/**
	 * 根据业务id查询文号字号列表
	 * @param iBusFunctionId
	 * @param sDeptId
	 * @return
	 */
	@AutoLog(value = "业务按钮-查询文号字号列表")
	@ApiOperation(value="业务按钮-查询文号字号列表", notes="业务按钮-查询文号字号列表")
	@GetMapping(value = "/getDocNumList")
	public Result getDocNumList(@RequestParam(name="iBusFunctionId",required=true) Integer iBusFunctionId
								,HttpServletRequest request) {
		String token = request.getHeader(DefContants.X_ACCESS_TOKEN);
		String userName = JwtUtil.getUsername(token);
		SysUser user = sysUserService.getUserByName(userName);

		//根据编码查询部门id
		SysDepart depart = sysUserRoleMapper.getDeptById(user.getOrgCode());
		Result<List<DocNumSet>> result = new Result<>();
		List<DocNumSet> button = docNumSetService.getDocNumNameListByBf(iBusFunctionId,depart.getId());
		if(button==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(button);
			result.setSuccess(true);
		}
		return result;
	}

}
