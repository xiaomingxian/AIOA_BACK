package com.cfcc.modules.system.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.system.entity.SysUserSet;

import java.util.List;

/**
 * @Description: 权限设置
 * @Author: jeecg-boot
 * @Date:   2019-10-17
 * @Version: V1.0
 */
public interface ISysUserSetService extends IService<SysUserSet> {

    SysUserSet findById(Integer iId);

    boolean updateBYIid(SysUserSet sysUserSet);

    boolean deleteByIidd(String id);

    IPage<SysUserSet> findPage(Integer pageNo, Integer pageSize, SysUserSet sysUserSet);

    List<SysUserSet> findList();

    int insert(SysUserSet sysUserSet);

    SysUserSet findByUserId(String userId);

    List<SysUserSet> findByIId(Integer iId);

    SysUserSet HomeAndDay(SysUserSet sysUserSet);
}
