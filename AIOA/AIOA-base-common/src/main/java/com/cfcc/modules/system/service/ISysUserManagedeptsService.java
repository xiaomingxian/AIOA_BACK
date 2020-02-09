package com.cfcc.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.system.entity.sysUserManagedepts;

import java.util.List;
import java.util.Map;

/**
 * @Description: 分管领导部门管理
 * @Author: jeecg-boot
 * @Date:   2019-12-28
 * @Version: V1.0
 */
public interface ISysUserManagedeptsService extends IService<sysUserManagedepts> {

    public void deleteListById(List<String> departMapId);

    public IPage<Map<String,Object>> getByUserIdAndDeptName(String userId,String deptName);

    public Boolean removeByIdAndDeptId(String userId,String deptId);

    public Boolean saveDepartIdByUserId(Map<String,Object> map);

    public IPage<Map<String,Object>> findUserManageDeptsByUserName(String username,Integer start,Integer pageSize);
}
