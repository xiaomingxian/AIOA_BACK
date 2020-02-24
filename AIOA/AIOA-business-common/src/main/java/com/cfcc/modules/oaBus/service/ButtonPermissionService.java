package com.cfcc.modules.oaBus.service;

import com.cfcc.modules.oaBus.entity.ButtonPermit;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ButtonPermissionService {

    Map<String, Object> getBtnAndOpt(Map<String, Object> result,Map<String, Boolean> currentUserPermission);

    boolean havePermission(ButtonPermit buttonPermit, Map<String, Boolean> currentUserPermission);

    Map<String, Boolean> currentUserPermission(String key, Map<String, Object> query, LoginInfo currentUser,
                                               String taskDefKey, String proInstanId, String taskId,String isDaiBan);

    List<Map<String, Object>> reloadOpinionList(Map<String,Object> map);
}
