package com.cfcc.modules.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.modules.data.mapper.DataAnalysisMapper;
import com.cfcc.modules.data.service.DataAnalysisService;
import com.cfcc.modules.oaBus.entity.OaBusdata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
        List<Map<String, Object>> BusDatalist = dataAnalisisMapper.findByTableAndMy(table, oaBusdata);
        return BusDatalist;
    }
    //统计办结率  --LYJ

    @Override
    public List<Map<String, Object>> MyRate(String table, OaBusdata oaBusdata) {
        List<Map<String, Object>> list = dataAnalisisMapper.MyRate(table, oaBusdata);
        return list;
    }
    @Override
    public List<Map<String, Object>> PeerNum(String table, OaBusdata oaBusdata) {
        List<Map<String, Object>> list = dataAnalisisMapper.PeerNum(table, oaBusdata);
        return list;
    }

    @Override
    public List<Map<String, Object>> HandlingRate(String table, OaBusdata oaBusdata) {
        List<Map<String, Object>> list = dataAnalisisMapper.HandlingRate(table, oaBusdata);
        return list;
    }

    @Override
    public Map<String, Object> Handling(String table, OaBusdata oaBusdata) {
        return dataAnalisisMapper.Handling(table, oaBusdata);
    }

    @Override
    public double getAvg(String table) {
        return dataAnalisisMapper.getAvg(table);
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
