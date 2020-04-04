package com.cfcc.modules.app.service;

import com.cfcc.modules.system.entity.LoginInfo;

import java.util.List;
import java.util.Map;

public interface IAppDataListService {

    Map<String, Object> queryBusDataByFunctionId(Integer functionId, Integer modelId, LoginInfo loginInfo, Integer pageNo, Integer pageSize);
}
