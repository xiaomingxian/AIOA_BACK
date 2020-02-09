package com.cfcc.modules.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.modules.system.service.ISysRoleService;
import com.cfcc.modules.system.entity.SysRole;
import com.cfcc.modules.system.mapper.SysRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public IPage<Map<String,Object>> pageOneSysRole(String role, Integer pageNo, Integer pageSize){
        IPage<Map<String,Object>> pageList = new Page<Map<String,Object>>();
        Long total = sysRoleMapper.pageOneTotal(role);
        List<Map<String,Object>> sysRoleList = sysRoleMapper.pageOneSysRole(role,(pageNo - 1)*pageSize,pageSize);
        pageList.setRecords(sysRoleList);
        pageList.setTotal(total);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;

    }

}
