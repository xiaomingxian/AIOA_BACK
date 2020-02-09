package com.cfcc.modules.system.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.system.entity.SysUserOpinion;

import java.util.List;

/**
 * @Description: 快捷意见
 * @Author: jeecg-boot
 * @Date:   2019-10-12
 * @Version: V1.0
 */
public interface ISysUserOpinionService extends IService<SysUserOpinion> {

    SysUserOpinion findById(Integer iId);
    boolean updateBYIid(SysUserOpinion sysUserOpinion);
    boolean deleteByIidd(String id);
    IPage<SysUserOpinion> findPage(Integer pageNo, Integer pageSize, SysUserOpinion sysUserOpinion);

    /**
     * 查询用户个人意见
     * @param sysUserOpinion
     * @return
     */
    List<SysUserOpinion> getUserOpinion(SysUserOpinion sysUserOpinion);

}
