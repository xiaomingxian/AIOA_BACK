package com.cfcc.modules.system.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.system.entity.QrtzBackUp;
import com.cfcc.modules.system.service.IQrtzBackUpService;
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
 * @Description: 备份情况
 * @Author: jeecg-boot
 * @Date:   2019-11-28
 * @Version: V1.0
 */
@Slf4j
@Api(tags="备份情况")
@RestController
@RequestMapping("/system/qrtzBackUp")
public class QrtzBackUpController {
	@Autowired
	private IQrtzBackUpService qrtzBackUpService;
	
	/**
	  * 分页列表查询
	 * @param qrtzBackUp
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "备份情况-分页列表查询")
	@ApiOperation(value="备份情况-分页列表查询", notes="备份情况-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<QrtzBackUp>> queryPageList(QrtzBackUp qrtzBackUp,
												   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												   HttpServletRequest req) {
		Result<IPage<QrtzBackUp>> result = new Result<IPage<QrtzBackUp>>();
		IPage<QrtzBackUp> pageList = qrtzBackUpService.queryBackUpList(qrtzBackUp, pageNo,pageSize);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param qrtzBackUp
	 * @return
	 */
	@AutoLog(value = "备份情况-添加")
	@ApiOperation(value="备份情况-添加", notes="备份情况-添加")
	@PostMapping(value = "/add")
	public Result<QrtzBackUp> add(@RequestBody QrtzBackUp qrtzBackUp) {
		Result<QrtzBackUp> result = new Result<QrtzBackUp>();
		try {
			qrtzBackUpService.save(qrtzBackUp);
			result.success("添加成功！");
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
	@AutoLog(value = "备份情况-通过id删除")
	@ApiOperation(value="备份情况-通过id删除", notes="备份情况-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			qrtzBackUpService.deleteById(id);
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
	@AutoLog(value = "备份情况-批量删除")
	@ApiOperation(value="备份情况-批量删除", notes="备份情况-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<QrtzBackUp> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<QrtzBackUp> result = new Result<QrtzBackUp>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			List<String> idList = Arrays.asList(ids.split(","));
			for (int i = 0; i < idList.size(); i++) {
				this.qrtzBackUpService.deleteById(idList.get(i));
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
	@AutoLog(value = "备份情况-通过id查询")
	@ApiOperation(value="备份情况-通过id查询", notes="备份情况-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<QrtzBackUp> queryById(@RequestParam(name="id",required=true) String id) {
		Result<QrtzBackUp> result = new Result<QrtzBackUp>();
		QrtzBackUp qrtzBackUp = qrtzBackUpService.queryById(id);
		if(qrtzBackUp==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(qrtzBackUp);
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
      QueryWrapper<QrtzBackUp> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              QrtzBackUp qrtzBackUp = JSON.parseObject(deString, QrtzBackUp.class);
              queryWrapper = QueryGenerator.initQueryWrapper(qrtzBackUp, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<QrtzBackUp> pageList = qrtzBackUpService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "备份情况列表");
      mv.addObject(NormalExcelConstants.CLASS, QrtzBackUp.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("备份情况列表数据", "导出人:Jeecg", "导出信息"));
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
              List<QrtzBackUp> listQrtzBackUps = ExcelImportUtil.importExcel(file.getInputStream(), QrtzBackUp.class, params);
              qrtzBackUpService.saveBatch(listQrtzBackUps);
              return Result.ok("文件导入成功！数据行数:" + listQrtzBackUps.size());
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
