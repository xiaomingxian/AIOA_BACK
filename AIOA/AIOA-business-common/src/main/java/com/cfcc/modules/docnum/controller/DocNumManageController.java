package com.cfcc.modules.docnum.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.modules.docnum.entity.DocNumManage;
import com.cfcc.modules.docnum.service.IDocNumManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 文号管理表
 * @Author: jeecg-boot
 * @Date:   2019-11-18
 * @Version: V1.0
 */
@Slf4j
@Api(tags="文号管理表")
@RestController
@RequestMapping("/docnum/docNumManage")
public class DocNumManageController {
	@Autowired
	private IDocNumManageService docNumManageService;

	 	/**
	 * 查询空号列表
	 * @param docNumManage
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */

	@AutoLog(value = "文号配置-分页列表查询")
	@ApiOperation(value="文号配置-分页列表查询", notes="文号配置-分页列表查询")
	@GetMapping(value = "/getNumList")
	public Result<IPage<DocNumManage>> getNumList(DocNumManage docNumManage,
												  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		Result<IPage<DocNumManage>> result = new Result<IPage<DocNumManage>>();
		IPage<DocNumManage> numList = docNumManageService.getNumList(docNumManage, pageNo, pageSize);
		result.setSuccess(true);
		result.setResult(numList);
		return result;
	}


	/**
	  * 登记文号
	 * @param button
	 * @return
	 */
	@AutoLog(value = "业务按钮-添加")
	@ApiOperation(value="业务按钮-添加", notes="业务按钮-添加")
	@GetMapping(value = "/setNum")
	public Result<DocNumManage> add(DocNumManage button) {
		Result<DocNumManage> result = new Result<DocNumManage>();
		try {
			docNumManageService.addDocNum(button);
			result.success("添加成功！");
			result.setResult(button);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	  *  选择使用
	 * @param button i_busdata_id (0:选择使用，-1:线下占用)
	 * @return
	 */
	@AutoLog(value = "业务按钮-选择使用")
	@ApiOperation(value="业务按钮-选择使用", notes="业务按钮-选择使用")
	@GetMapping(value = "/selectNum")
	public Result<DocNumManage> edit( DocNumManage button) {
		Result<DocNumManage> result = new Result<DocNumManage>();
		try {
			docNumManageService.updateDocNumStatus(button);
			result.success("修改成功!");
			result.setResult(button);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("文号设置失败！");
		}
		return result;
	}

	@AutoLog(value = "业务按钮-选择使用")
	@ApiOperation(value="业务按钮-选择使用", notes="业务按钮-选择使用")
	@GetMapping(value = "/checkDocNum")
	public Result checkDocNum( DocNumManage docNumManage) {
		Result result = new Result<>();
		try {
			List<DocNumManage> docNumManages = docNumManageService.checkDocNum(docNumManage);
			result.setResult(docNumManages);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage("查询失败！");
		}
		return result;
	}

}
