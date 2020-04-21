package com.cfcc.modules.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.modules.app.service.IAppDataListService;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.service.IBusFunctionService;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.entity.SysUserFun;
import com.cfcc.modules.system.service.ISysUserFunService;
import com.cfcc.modules.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private IBusFunctionService ibusFunctionService;
    @Autowired
    private IOaBusdataService oaBusdataService;


    @ApiOperation(value="用户收藏功能表-查询用户收藏对应的列表", notes="用户收藏功能表-查询用户收藏对应的列表")
    @PostMapping(value = "/queryListByUserId")
    public Result<Map<String,Object>> queryListByUserId(@RequestBody String json,
                                                        HttpServletRequest request) {
        log.info(json.toString());
        Map<String,Object> jsonObj = (Map<String,Object> ) JSONObject.parse(json);
        Integer pageNo = (Integer) jsonObj.get("pageNo");
        Integer pageSize = (Integer) jsonObj.get("pageSize");
        // 查询当前用户，作为assignee
        LoginInfo loginInfo = isysUserService.getLoginInfo(request);
        String userId = loginInfo.getId() ;
        Result<Map<String,Object>> result = new Result<Map<String,Object>>();
        Map<String,Object> map = new HashMap<>() ;
        List<Map<String,Object>> funList = sysUserFunService.queryListMapByUserIdSer(userId) ;
        map.put("funList" ,funList) ;
        Map<String,Object> dataList = new HashMap<>() ;
        if(funList!= null && funList.size() > 0 ){
            Integer functionId = (Integer) funList.get(0).get("i_bus_function_id");
            Integer modelId = (Integer) funList.get(0).get("i_bus_model_id");
            Map<String,Object> condition = new HashMap<>() ;
            condition.put("function_id",functionId) ;
            dataList = appDataListService.queryBusDataByFunctionId(functionId,modelId,loginInfo,pageNo,pageSize,condition) ;
        }
        map.put("dataMap",dataList) ;
        result.setSuccess(true);
        result.setResult(map);
        return result;
    }
    @ApiOperation(value="用户收藏功能表-查询用户收藏对应的列表", notes="用户收藏功能表-查询用户收藏对应的列表")
    @GetMapping(value = "/queryFunByModelId")
    public Result<Map<String,Object>> queryFunByModelId(String modelId,
                                                        HttpServletRequest request) {
        // 查询当前用户，作为assignee
        LoginInfo loginInfo = isysUserService.getLoginInfo(request);
        SysDepart depart = loginInfo.getDepart();
        String userId = loginInfo.getId() ;
        Result<Map<String,Object>> result = new Result<Map<String,Object>>();
        List<BusFunction> busFunctionList = ibusFunctionService.queryByModelId(modelId);
        //权限过滤，有些fun只能特定的部门能看到
        busFunctionList = oaBusdataService.getFunListByFunUnit(busFunctionList, depart);
        Map<String,Object> map = new HashMap<>() ;

        map.put("funList", busFunctionList);
        result.setSuccess(true);
        result.setResult(map);
        return result;
    }

    /**
     * 根据条件进行查询
     * @param request
     * @return
     */
    @PostMapping(value = "/queryDataListApp")
    public Result<Map<String,Object>> queryDataListApp(@RequestBody String json,
                                                        HttpServletRequest request) {
        Result<Map<String,Object>> result = new Result<Map<String,Object>>();
        // 查询当前用户，作为assignee
        LoginInfo loginInfo = isysUserService.getLoginInfo(request);
        String userId = loginInfo.getId() ;

        Map<String,Object> map = new HashMap<>() ;
        Map<String,Object> dataList = new HashMap<>() ;
        Map<String,Object> jsonObj = (Map<String,Object> ) JSONObject.parse(json);
        Map<String,Object> condition = (Map<String,Object> ) jsonObj.get("json");
        Integer pageNo = (Integer) jsonObj.get("pageNo");
        Integer pageSize = (Integer) jsonObj.get("pageSize");
        /*Integer modelId = (Integer) condition.get("model_id");
        Integer functionId = (Integer) condition.get("function_id");*/
        System.out.println(condition);
        //String modeiId
        String modelIdStr = condition.get("model_id")+"" ;
        String functionIdStr = condition.get("function_id")+"" ;
        if("".equals(modelIdStr)|| modelIdStr == null){
            log.error("moderId为空！！！");
            result.setSuccess(false);
            result.setMessage("moderId为空！！！");
            return result;
        }
        if("".equals(functionIdStr)|| functionIdStr == null){
            log.error("functionId为空！！！");
            result.setSuccess(false);
            result.setMessage("moderId为空！！！");
            return result;
        }
        Integer modelId = Integer.parseInt(modelIdStr);
        Integer functionId = Integer.parseInt(functionIdStr);
        if(condition.containsKey("model_id")){
            condition.remove("model_id") ;
        }
        dataList = appDataListService.queryBusDataByFunctionId(functionId,modelId,loginInfo,pageNo,pageSize,condition) ;
        map.put("dataMap",dataList) ;
        result.setSuccess(true);
        result.setResult(map);
        return result;
    }

    /**
     * 根据条件进行查询
     * @param request
     * @return
     */
    @PostMapping(value = "/getDetail")
    public Result<Map<String,Object>> getDetail(@RequestBody String json,
                                                       HttpServletRequest request) {
        Result<Map<String,Object>> result = new Result<Map<String,Object>>();
        LoginInfo loginInfo = isysUserService.getLoginInfo(request);
        Map<String,Object> res = appDataListService.getDetailSer(loginInfo,json) ;
        result.setResult(res);
        return result ;

    }


}
