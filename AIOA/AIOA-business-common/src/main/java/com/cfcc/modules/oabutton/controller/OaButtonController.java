package com.cfcc.modules.oabutton.controller;

import java.util.Arrays;
import java.util.Date;
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
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.common.util.oConvertUtils;

import com.cfcc.modules.oabutton.entity.OaButton;
import com.cfcc.modules.oabutton.entity.OaButtonSet;
import com.cfcc.modules.oabutton.service.IOaButtonService;
import com.cfcc.modules.oabutton.service.IOaButtonSetService;
import com.sun.org.apache.regexp.internal.RE;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 按钮管理
 * @Author: jeecg-boot
 * @Date:   2019-10-24
 * @Version: V1.0
 */
@Slf4j
@Api(tags="按钮管理")
@RestController
@RequestMapping("/oabutton/oaButton")
public class OaButtonController {
	@Autowired
	private IOaButtonService oaButtonService;
	 @Autowired
	 private IOaButtonSetService oaButtonSetService;
	
	/**
	  * 分页列表查询
	 * @param oaButton
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "按钮管理-分页列表查询")
	@ApiOperation(value="按钮管理-分页列表查询", notes="按钮管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<OaButton>> queryPageList(OaButton oaButton,
												 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												 HttpServletRequest req) {
		Result<IPage<OaButton>> result = new Result<IPage<OaButton>>();
		/*QueryWrapper<OaButton> queryWrapper = QueryGenerator.initQueryWrapper(oaButton, req.getParameterMap());
		Page<OaButton> page = new Page<OaButton>(pageNo, pageSize);
		IPage<OaButton> pageList = oaButtonService.page(page, queryWrapper);*/
		try {
			IPage<OaButton> pageList = oaButtonService.getPage(pageNo,pageSize,oaButton);
			result.setSuccess(true);
			result.setResult(pageList);
		} catch (Exception e) {
			log.error("按钮列表查询失败",e.getMessage());
		}
		return result;
	}

	
	/**
	  *   添加
	 * @param oaButton
	 * @return
	 */
	@AutoLog(value = "按钮管理-添加")
	@ApiOperation(value="按钮管理-添加", notes="按钮管理-添加")
	@PostMapping(value = "/add")
	public Result<OaButton> add(@RequestBody OaButton oaButton) {
		Result<OaButton> result = new Result<OaButton>();
		if (oaButton!=null && oaButton.getSBtnValue()!=null ){
			oaButton.setSMethod(oaButton.getSBtnValue());
		}
		try {
			oaButtonService.save(oaButton);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param oaButton
	 * @return
	 */
	@AutoLog(value = "按钮管理-编辑")
	@ApiOperation(value="按钮管理-编辑", notes="按钮管理-编辑")
	@PutMapping(value = "/edit")
	public Result<OaButton> edit(@RequestBody OaButton oaButton) {
		Result<OaButton> result = new Result<OaButton>();
		try {
			if (oaButton!=null && oaButton.getSBtnValue()!=null ){
                oaButton.setSMethod(oaButton.getSBtnValue());
            }
			List<OaButton> oaButtonEntity = oaButtonService.queryById(oaButton.getIId(),null);
			if(oaButtonEntity==null ||oaButtonEntity.size()!=1) {
                result.error500("未找到对应实体");
            }else {
                boolean ok = oaButtonService.updateOaButtonById(oaButton);
                //TODO 返回false说明什么？
                if(ok) {
                    result.success("修改成功!");
                }
            }
		} catch (Exception e) {
			log.error("edit失败",e.getMessage());
		}

		return result;
	}


	/**
	  *   通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "按钮管理-通过id删除")
	@ApiOperation(value="按钮管理-通过id删除", notes="按钮管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			List<OaButtonSet> oaButtonSets = oaButtonSetService.queryByButtonId(Integer.valueOf(id));
			if (oaButtonSets!=null && oaButtonSets.size()>0){
				return Result.ok("删除失败，请先删除按钮权限中与当前按钮相关的配置!");
			}
			oaButtonService.deleteOaButtonByID(id);
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
	@AutoLog(value = "按钮管理-批量删除")
	@ApiOperation(value="按钮管理-批量删除", notes="按钮管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<OaButton> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<OaButton> result = new Result<OaButton>();
		try {
			if(ids==null || "".equals(ids.trim())) {
                result.error500("参数不识别！");
            }else {
                List<String> idList = Arrays.asList(ids.split(","));
                for (int i = 0; i < idList.size(); i++) {
                    this.oaButtonService.deleteOaButtonByID(idList.get(i));
                }
                result.success("删除成功!");
            }
		} catch (Exception e) {
//			e.printStackTrace();
			log.error("删除失败",e.getMessage());
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "按钮管理-通过id查询或名称")
	@ApiOperation(value="按钮管理-通过id查询或名称", notes="按钮管理-通过id查询或名称")
	@GetMapping(value = "/queryById")
	public Result<OaButton> queryById(@RequestParam(name="id",required=false) Integer id,
									  @RequestParam(name="sbtnName",required=false) String sBtnName,
									  @RequestParam(value = "orgSchema", required = false) String orgSchema,
									  HttpServletRequest request) {
		try {
			if (orgSchema!=null && !orgSchema.equals("")){
				request.setAttribute("orgSchema",orgSchema);
			}
			Result<OaButton> result = new Result<>();
			List<OaButton> oaButton = oaButtonService.queryById(id,sBtnName);
			if(oaButton==null||oaButton.size()<1) {
                result.error500("未找到对应实体");
            }else {
                result.setResult(oaButton.get(0));
                result.setSuccess(true);
            }
			return result;
		} catch (Exception e) {
			log.error("按钮管理通过id查询失败",e.getMessage());
		}
		return null;
	}

	 /**
	  * 通过按钮名称查询查询
	  * @param sBtnName
	  * @return
	  */
	 @AutoLog(value = "按钮管理-通过id查询或名称")
	 @ApiOperation(value="按钮管理-通过id查询或名称", notes="按钮管理-通过id查询或名称")
	 @GetMapping(value = "/queryBySbtnName")
	 public Result<List<OaButton>> queryBySbtnName(@RequestParam(name="sbtnName",required=false) String sBtnName) {
		 try {
			 Result<List<OaButton>> result = new Result<>();
			 List<OaButton> oaButton = oaButtonService.queryById(null,sBtnName.trim());
			 if(oaButton==null||oaButton.size()==0) {
			 	OaButton oatest=new OaButton();
				 oatest.setIId(1000000);
				 oaButton.add(oatest);
				 result.setResult(oaButton);
				 result.setSuccess(true);
//				 result.error500("未找到对应实体");
			 }else {
				 result.setResult(oaButton);
				 result.setSuccess(true);
			 }
			 return result;
		 } catch (Exception e) {
			 log.error("按钮管理通过按钮名称查询失败",e.getMessage());
		 }
		 return null;
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
      QueryWrapper<OaButton> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              OaButton oaButton = JSON.parseObject(deString, OaButton.class);
              queryWrapper = QueryGenerator.initQueryWrapper(oaButton, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
		  log.error("按钮管理导出失败",e.getMessage());
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<OaButton> pageList = oaButtonService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "按钮管理列表");
      mv.addObject(NormalExcelConstants.CLASS, OaButton.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("按钮管理列表数据", "导出人:Jeecg", "导出信息"));
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
              List<OaButton> listOaButtons = ExcelImportUtil.importExcel(file.getInputStream(), OaButton.class, params);
              oaButtonService.saveBatch(listOaButtons);
              return Result.ok("文件导入成功！数据行数:" + listOaButtons.size());
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

	 /**
	  * 按钮列表查询
	  */
	 @AutoLog(value = "按钮管理-分页列表查询")
	 @ApiOperation(value="按钮管理-分页列表查询", notes="按钮管理-分页列表查询")
	 @GetMapping(value = "/buttonList")
	 public Result<List<OaButton>> querySelectList() {
		 Result<List<OaButton>> result = new Result<List<OaButton>>();
		 List<OaButton> buttonList = null;
		 try {
			 buttonList = oaButtonService.buttonList();
		 } catch (Exception e) {
			 log.error("按钮管理buttonList查询失败",e.getMessage());
		 }
		 result.setSuccess(true);
		 result.setResult(buttonList);
		 return result;
	 }

}
