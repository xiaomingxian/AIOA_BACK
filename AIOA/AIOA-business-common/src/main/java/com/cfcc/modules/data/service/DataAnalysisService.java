package com.cfcc.modules.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.oaBus.entity.OaBusdata;

import java.util.List;
import java.util.Map;

public interface DataAnalysisService extends IService<OaBusdata> {
    //统计我的数据的数据量  --LYJ
    List<Map<String, Object>> findByTableAndMy(String table, OaBusdata oaBusdata);
    //我的办结率   --LYJ
    List<Map<String, Object>>  MyRate(String table, OaBusdata oaBusdata);


    String getDepartId(String sCreateBy);

    //查询表里面的表字段
    List<Map<String, Object>> selectColums(String table);

    List<Map<String, Object>> selectDeptNames(String table,OaBusdata oaBusdata);

    List<Map<String, Object>> selectMyCreateDepart( String table,OaBusdata oaBusdata);

    List<Map<String, Object>> PeerNum(String table, OaBusdata oaBusdata);

    List<Map<String, Object>> HandlingRate(String table, OaBusdata oaBusdata);

    Map<String, Object> Handling(String table, OaBusdata oaBusdata);

    double getAvg(String table);
}
