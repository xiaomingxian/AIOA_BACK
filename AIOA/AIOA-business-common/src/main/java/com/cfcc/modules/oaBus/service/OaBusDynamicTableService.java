package com.cfcc.modules.oaBus.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.modules.oaBus.entity.ButtonPermit;
import com.cfcc.modules.oaBus.entity.OaOutLog;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysDepart;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface OaBusDynamicTableService {
    Map<String, Object> getBusProcSet(Integer iprocSetId);

    //查询一条数据-推荐用id作为条件
    Map<String, Object> queryDataById(Map<String, Object> map);

    //查询按钮信息-及相关权限(位置未改)
    List<ButtonPermit> queryButtonId(String key, String taskDefKey, String btn);

    //废弃-未删
    void insertDataAndStartPro(Map<String, Object> map, HashMap<String, Object> vars, String keyPro);

    //查询-业务数据权限表-的参与者
    List<String> isReader(String busDataId, String table);


    Map<String, Object> insertData(Map<String, Object> map);

    IPage<Map<String, Object>> queryByEquals(Integer pageNo, Integer pageSize, Map<String, Object> query);

    List<Map<String, Object>> queryOptions(String opt, String iprocSetId, String key, String taskDefKey, String optionTable, String busDataId);

    int updateData(Map<String, Object> map);

    boolean deleteData(Map<String, Object> map);

    void checkProc(Map<String, Object> map);

    Map<String, Object> updateOpinionByTaskId(Map<String, Object> map);

    Map<String, Object> queryOptionsByBusDataIdAndFuncationId(String stable, Integer tableid, Integer funcationid);

    List<Map<String, Object>> queryUnitsByUser(String orgCode);

    Map<String, Object> queryModelByFunctionId(String functionId);


    Map<String, List<Map<String, Object>>> downSendData(List<String> unit, HttpServletRequest request);

    Map<String, List<Map<String, Object>>> upSendFile(SysDepart depart, HttpServletRequest request);

    boolean insertUserPermit(String param);

    Result provinceToCityReceiveFile(Map<String,Object> obj,HttpServletRequest request);

    Result provinceToCityInsideFile(Map<String,Object> obj,HttpServletRequest request);

    boolean shareFile(Map<String, Object> map, LoginInfo loginInfo,HttpServletRequest request);

    Result provinceToCityClient(HttpServletRequest request);

    boolean deleteBusdata(Map<String, Object> map);

    Result provinceToCityInsideClient(HttpServletRequest request);

    void insertOaOutLog(Map<String, Object> map,HttpServletRequest request);

    List<String> queryOaOutLogById(OaOutLog oaOutLog);
}
