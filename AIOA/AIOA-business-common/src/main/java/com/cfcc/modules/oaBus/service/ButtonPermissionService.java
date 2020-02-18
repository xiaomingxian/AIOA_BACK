package com.cfcc.modules.oaBus.service;

import com.cfcc.modules.oaBus.entity.ButtonPermit;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysUser;

import java.util.HashMap;
import java.util.Map;

public interface ButtonPermissionService {

    Map<String, Object> getBtnAndOpt(Map<String, Object> result);
    Map<String, Object> getBtn(Map<String, Object> result);
    Map<String, Object> getOpt(Map<String, Object> result);

    boolean havePermission(ButtonPermit buttonPermit, Map<String, Boolean> currentUserPermission);

    Map<String, Boolean> currentUserPermission(String key, Map<String, Object> query, LoginInfo currentUser,
                                               String taskDefKey, String proInstanId, String taskId,Boolean isDaiBan);

    boolean haveSavePermission(Object proKey1, HashMap<String, Object> map, LoginInfo loginInfo,
                               String taskDef,String taskId,boolean isDaiBan);
}
