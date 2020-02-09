package com.cfcc.common.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.system.service.CommonDynamicTableService;
import com.cfcc.modules.system.mapper.CommonDynamicTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CommonDynamicTableServiceImpl implements CommonDynamicTableService {

    @Autowired
    private CommonDynamicTableMapper dynamicTableMapper;


    @Override
    public Map<String, Object> getBusProcSet(String iprocSetId) {
        return dynamicTableMapper.getBusProcSet(iprocSetId);
    }

    @Override
    public List<String> queryButtonId(String key, String taskDefKey, String btn) {
        return dynamicTableMapper.queryButtonId(key, taskDefKey, btn);
    }

    @Override
    public Map<String, Object> insertData(Map<String, Object> map) {
        dynamicTableMapper.insertData(map);
        return map;
    }

    @Override
    public void insertDataBatch(List<Map<String, Object>> list) {

        dynamicTableMapper.insertDataBatch(list);
    }


    @Override
    public IPage<Map<String, Object>> queryByEquals(Integer pageNo, Integer pageSize, Map<String, Object> map) {

        String table = (String) map.get("table");
        map.remove("table");

        //封装分页信息
        Page<Map<String, Object>> page = new Page<>(pageNo, pageSize);
        Long total = dynamicTableMapper.queryCountByEquals(map, table);
        page.setTotal(total);
        List<Map<String, Object>> recoders = dynamicTableMapper.queryByEquals((pageNo - 1) * pageSize, pageSize, map, table);
        page.setRecords(recoders);

        return page;
    }


}
