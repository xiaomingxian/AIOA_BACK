package com.cfcc.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.modules.system.entity.SysRole;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
public interface ISysRoleService extends IService<SysRole> {

    IPage<Map<String,Object>> pageOneSysRole(String role,Integer pageNo,Integer pageSize);

}
