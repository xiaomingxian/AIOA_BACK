package com.cfcc.modules.system.controller;


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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.modules.system.entity.sysUserManagedepts;
import com.cfcc.modules.system.service.ISysUserManagedeptsService;
import com.sun.org.apache.xpath.internal.operations.Bool;
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
import com.alibaba.fastjson.JSON;

/**
 * @Description: 分管领导部门管理
 * @Author: jeecg-boot
 * @Date:   2019-12-28
 * @Version: V1.0
 */
@Slf4j
@Api(tags="分管领导部门管理")
@RestController
@RequestMapping("/sys/sysUserManagedepts")
public class SysUserManagedeptsController {
	@Autowired
	private ISysUserManagedeptsService sysUserManagedeptsService;
	
	/**
	  * 分页列表查询
	 * @param username
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "分管领导部门管理-分页列表查询")
	@ApiOperation(value="分管领导部门管理-分页列表查询", notes="分管领导部门管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Map<String,Object>>> queryPageList( String username,
														   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														   HttpServletRequest req) {
		Result<IPage<Map<String,Object>>> result = new Result<IPage<Map<String,Object>>>();
		IPage<Map<String,Object>> pageList = new Page<Map<String,Object>>();
		pageList = sysUserManagedeptsService.findUserManageDeptsByUserName(username,pageNo, pageSize);
		result.setSuccess(true);
//		IPage<Map<String,Object>> pageList = sysUserManagedeptsService.findUserManageDeptsByUserName(username,pageNo, pageSize);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param
	 * @return
	 */
	@AutoLog(value = "分管领导部门管理-添加")
	@ApiOperation(value="分管领导部门管理-添加", notes="分管领导部门管理-添加")
	@PostMapping(value = "/add")
	public Result<sysUserManagedepts> add(@RequestBody Map<String,Object> map) {
		Result<sysUserManagedepts> result = new Result<sysUserManagedepts>();
		try {
			if (!(map.size() < 1)){

			}
			Boolean flag = sysUserManagedeptsService.saveDepartIdByUserId(map);
			if (flag){
				result.success("添加成功！");
			}else {
				result.error500("添加失败");
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("添加失败");
		}
		return result;
	}

	
	/**
	  *   通过id删除
	 * @param
	 * @return
	 */
	@AutoLog(value = "分管领导部门管理-通过id删除")
	@ApiOperation(value="分管领导部门管理-通过id删除", notes="分管领导部门管理-通过id删除")
	@GetMapping(value = "/delete")
	public Result<?> delete( String userId, String deptId) {
		try {
			//删除成功返回true
			Boolean b = sysUserManagedeptsService.removeByIdAndDeptId(userId, deptId);
			if (!b){
				return Result.error("删除失败!");
			}
		} catch (Exception e) {
			log.error("删除失败",e.getMessage());
			return Result.error("删除失败!");
		}
		return Result.ok("删除成功!");
	}
	
	/**
	  *  批量删除
	 * @param
	 * @return
	 */
	@AutoLog(value = "分管领导部门管理-批量删除")
	@ApiOperation(value="分管领导部门管理-批量删除", notes="分管领导部门管理-批量删除")
	@PostMapping(value = "/deleteList")
	public Result<String> deleteList(@RequestBody Map<String,Object> map ) {
		Result<String> result = new Result<String>();
		if (map.size() >= 1 && map.get("departMapId") != null){
			sysUserManagedeptsService.deleteListById((List<String>)map.get("departMapId"));
			result.setSuccess(true);
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param
	 * @return
	 */
	@AutoLog(value = "分管领导部门管理-通过id查询")
	@ApiOperation(value="分管领导部门管理-通过id查询", notes="分管领导部门管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<IPage<Map<String,Object>>> queryById(String userId,String deptName) {
		Result<IPage<Map<String,Object>>> result = new Result<IPage<Map<String,Object>>>();
		if (userId != null || deptName != null){
			IPage<Map<String,Object>> pageList = sysUserManagedeptsService.getByUserIdAndDeptName(userId,deptName);
			if(pageList==null) {
				result.error500("没有要查询的数据");
			}else {
				result.setResult(pageList);
				result.setSuccess(true);
			}
		}else {
			result.setMessage("请输入查询数据");
		}
		return result;
	}


}
