package com.cfcc.modules.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.modules.data.mapper.DataAnalysisMapper;
import com.cfcc.modules.data.service.DataAnalysisService;
import com.cfcc.modules.oaBus.entity.OaBusdata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataAnalysisServiveImpl extends ServiceImpl<DataAnalysisMapper, OaBusdata> implements DataAnalysisService {
    @Autowired
    private DataAnalysisMapper dataAnalisisMapper;

    //查询部门id  ——LYJ
    @Override
    public String getDepartId(String sCreateBy) {
        String departId = dataAnalisisMapper.getDepartId(sCreateBy);
        return departId;
    }

    //统计我的数据的数据量  --LYJ
    @Override
    public List<Map<String, Object>> findByTableAndMy(String table, OaBusdata oaBusdata) {
        List<Map<String, Object>> byTableAndMy = dataAnalisisMapper.findByTableAndMy(table, oaBusdata);
        List<Map<String, Object>> sortList = null;
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Boolean b = true;
            for (int j = 0; j < byTableAndMy.size(); j++) {
                if (i == (Integer) byTableAndMy.get(j).get("i_create_month")) {
                    b = false;
                }
            }
            if (b) {
                list.add(i);
            }
        }
        list.forEach(i -> {
            Map<String, Object> map = new HashMap<>();
            map.put("i_create_month", i);
            map.put("num", 0);
            byTableAndMy.add(map);
        });
        sortList = byTableAndMy.stream()
                .sorted((m1, m2) -> {
                    if ((Integer) m1.get("i_create_month") >= (Integer) m2.get("i_create_month")) {
                        return 1;
                    } else {
                        return -1;
                    }
                })
                .collect(Collectors.toList());
        return sortList;
    }
    //统计办结率  --LYJ

    @Override
    public Map<String, Object> MyRate(String table, OaBusdata oaBusdata) {
        Map<String, Object> list = dataAnalisisMapper.MyRate(table, oaBusdata);
        return list;
    }
    @Override
    public Map<String, Object> PeerNum(String table, OaBusdata oaBusdata) {
        Map<String, Object> list = dataAnalisisMapper.PeerNum(table, oaBusdata);
        return list;
    }

    @Override
    public Map<String, Object> HandlingRate(String table, OaBusdata oaBusdata,String parentId) {
        oaBusdata.setSCreateUnitid(parentId);
        Map<String, Object> list = dataAnalisisMapper.HandlingRate(table, oaBusdata);
        return list;
    }

    @Override
    public Map<String, Object> Handling(String table, OaBusdata oaBusdata) {
        return dataAnalisisMapper.Handling(table, oaBusdata);
    }

    @Override
    public Map<String,Object> getAvg(String table,OaBusdata oaBusdata) {
        Map<String,Object> avg = dataAnalisisMapper.getAvg(table, oaBusdata);

        return avg;
    }

    //查询所有的字段名  ——LYJ
    @Override
    public List<Map<String, Object>> selectColums(String table,OaBusdata oaBusdata) {
        return dataAnalisisMapper.selectColums(table,oaBusdata);
    }

    //统计根据主办部门查询  ——LYJ
    @Override
    public List<Map<String, Object>> selectDeptNames(String table, OaBusdata oaBusdata) {
        return dataAnalisisMapper.selectDeptNames(table, oaBusdata);
    }

    //统计根据创建部门查询 ——LYJ
    @Override
    public List<Map<String, Object>> selectMyCreateDepart(String table, OaBusdata oaBusdata) {
        return dataAnalisisMapper.selectMyCreateDepart(table, oaBusdata);
    }
}
