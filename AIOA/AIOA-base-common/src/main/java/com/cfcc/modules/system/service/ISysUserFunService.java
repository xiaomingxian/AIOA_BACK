package com.cfcc.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.system.entity.SysUserFun;

import java.util.List;
import java.util.Map;

/**
 * ClassName:ISysUserFunService
 * Package:com.cfcc.modules.system.controller
 * Description:<br/>
 *
 * @date:2020/3/31
 * @author:
 */
public interface ISysUserFunService extends IService<SysUserFun> {

    int addUserFun(String userId, Integer modelId, Integer functionId, Integer status);

    List<Map<String,Object>> showUserFun(String userId);

    List<SysUserFun> showUserFunStatus(String userId);

    List<SysUserFun> queryListByUserIdSer(String userId);

    List<Map<String, Object>> queryListMapByUserIdSer(String userId);

    List<SysUserFun> showUserFunMF(String userId);
}
