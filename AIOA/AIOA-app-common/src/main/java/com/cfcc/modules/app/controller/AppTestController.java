package com.cfcc.modules.app.controller;

import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.modules.app.service.IAppDataListService;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysUserFun;
import com.cfcc.modules.system.service.ISysUserFunService;
import com.cfcc.modules.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author
 * @since
 */
@Slf4j
@Api(tags="App相关查询")
@RestController
@RequestMapping("/app/sys/SysUserFun")
public class AppTestController {
    @Autowired
    private ISysUserFunService sysUserFunService;
    @Autowired
    private IAppDataListService appDataListService ;
    @Autowired
    private ISysUserService isysUserService;


    @AutoLog(value = "用户收藏功能表-查询用户收藏对应的列表")
    @ApiOperation(value="用户收藏功能表-查询用户收藏对应的列表", notes="用户收藏功能表-查询用户收藏对应的列表")
    @GetMapping(value = "/queryListByUserId")
    public Result<Map<String,Object>> queryListByUserId(String userId,
                                                        @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                        @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                        HttpServletRequest request) {
        // 查询当前用户，作为assignee
        LoginInfo loginInfo = isysUserService.getLoginInfo(request);
        Result<Map<String,Object>> result = new Result<Map<String,Object>>();
        Map<String,Object> map = new HashMap<>() ;
        List<Map<String,Object>> funList = sysUserFunService.queryListMapByUserIdSer(userId) ;
        map.put("funList" ,funList) ;
        Map<String,Object> dataList = new HashMap<>() ;
        if(funList!= null && funList.size() > 0 ){
            Integer functionId = (Integer) funList.get(0).get("i_bus_function_id");
            Integer modelId = (Integer) funList.get(0).get("i_bus_model_id");
            dataList = appDataListService.queryBusDataByFunctionId(functionId,modelId,loginInfo,pageNo,pageSize) ;
        }
        map.put("dataMap",dataList) ;
        result.setSuccess(true);
        result.setResult(map);
        return result;
    }

}
