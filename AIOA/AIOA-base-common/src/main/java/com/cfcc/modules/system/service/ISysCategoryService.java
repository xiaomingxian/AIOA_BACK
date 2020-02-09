package com.cfcc.modules.system.service;

import java.util.List;

import com.cfcc.common.exception.AIOAException;
import com.cfcc.modules.system.entity.SysCategory;
import com.cfcc.modules.system.model.TreeSelectModel;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 分类字典
 * @Author: jeecg-boot
 * @Date:   2019-05-29
 * @Version: V1.0
 */
public interface ISysCategoryService extends IService<SysCategory> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";

	void addSysCategory(SysCategory sysCategory);
	
	void updateSysCategory(SysCategory sysCategory);
	
	/**
	  * 根据父级编码加载分类字典的数据
	 * @param pcode
	 * @return
	 */
	public List<TreeSelectModel> queryListByCode(String pcode) throws AIOAException;
	
	/**
	  * 根据pid查询子节点集合
	 * @param pid
	 * @return
	 */
	public List<TreeSelectModel> queryListByPid(String pid);
	
}
