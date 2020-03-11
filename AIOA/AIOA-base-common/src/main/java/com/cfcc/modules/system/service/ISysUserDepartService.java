package com.cfcc.modules.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.entity.SysUserDepart;
import com.cfcc.modules.system.model.DepartIdModel;

import java.util.List;

/**
 * <p>
 * SysUserDpeart用户组织机构service
 * </p>
 * @Author ZhiLin
 *
 */
public interface ISysUserDepartService extends IService<SysUserDepart> {
	

	/**
	 * 根据指定用户id查询部门信息
	 * @param userId
	 * @return
	 */
	List<DepartIdModel> queryDepartIdsOfUser(String userId);
	

	/**
	 * 根据部门id查询用户信息
	 * @param depId
	 * @return
	 */
	List<SysUser> queryUserByDepId(String depId);

    void deleteUserDepartByUserId(String id);
}
