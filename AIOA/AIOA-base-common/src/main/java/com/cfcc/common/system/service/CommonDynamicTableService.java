package com.cfcc.common.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

public interface CommonDynamicTableService {
    Map<String, Object> getBusProcSet(String iprocSetId);

    List<String> queryButtonId(String key, String taskDefKey, String btn);


    Map<String, Object> insertData(Map<String, Object> map);

    void insertDataBatch(List<Map<String, Object>> list);

    IPage<Map<String, Object>> queryByEquals(Integer pageNo, Integer pageSize, Map<String, Object> query);
}