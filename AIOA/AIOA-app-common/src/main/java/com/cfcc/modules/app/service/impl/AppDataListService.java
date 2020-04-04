package com.cfcc.modules.app.service.impl;

import com.cfcc.modules.app.service.IAppDataListService;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.oaBus.mapper.OaBusdataMapper;
import com.cfcc.modules.oaBus.service.IBusModelService;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.cfcc.modules.system.entity.LoginInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AppDataListService implements IAppDataListService {

    @Autowired
    private OaBusdataMapper oaBusdataMapper;
    @Autowired
    private IBusModelService iBusModelService;
    @Autowired
    private IOaBusdataService iOaBusdataService;


    @Override
    public Map<String, Object> queryBusDataByFunctionId(Integer functionId, Integer modelId, LoginInfo loginInfo, Integer pageNo, Integer pageSize) {

        Map<String, Object> result = new HashMap<>() ;
        String col = "i_id,s_title,d_create_time" ;
        String orderFlag = "" ;
        Map<String, Object> condition = new HashMap<>() ;
        condition.put("function_id",functionId) ;
        BusModel busModel =  iBusModelService.getById(modelId);
        String tableName = busModel.getSBusdataTable() ;
        Map<String, String> permitData = iOaBusdataService.permit(functionId, tableName, loginInfo.getUsername());       //根据权限，查询出对应的查询条件
        String userId = permitData.get("userId");
        String userUnit = permitData.get("userUnit");
        String userDepart = permitData.get("userDepart");
        permitData.remove("userId");
        permitData.remove("userUnit");
        permitData.remove("userDepart");
        log.info(permitData.toString());
        List<Map<String, Object>> dataList = oaBusdataMapper.getBusdataByMap((pageNo - 1) * pageSize,
                pageSize, col, tableName, condition, permitData, userId, userUnit, userDepart, orderFlag);
        int total = oaBusdataMapper.getBusdataByMapTotal(tableName, condition, permitData, userId, userUnit, userDepart);

        result.put("list",dataList) ;
        result.put("tatal" ,total) ;
        //System.out.println(dataList);
        return result;
    }
}
