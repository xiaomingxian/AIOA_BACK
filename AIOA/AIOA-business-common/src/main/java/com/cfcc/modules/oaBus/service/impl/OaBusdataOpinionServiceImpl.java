package com.cfcc.modules.oaBus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.modules.oaBus.entity.OaBusdataOpinion;
import com.cfcc.modules.oaBus.mapper.OaBusdataOpinionMapper;
import com.cfcc.modules.oaBus.service.IOaBusdataOpinionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 业务数据意见表
 * @Author: jeecg-boot
 * @Date:   2019-11-18
 * @Version: V1.0
 */
@Service
public class OaBusdataOpinionServiceImpl extends ServiceImpl<OaBusdataOpinionMapper, OaBusdataOpinion> implements IOaBusdataOpinionService {

    @Autowired
    private  OaBusdataOpinionMapper oaBusdataOpinionMapper;
    @Override
    public boolean putBusOpinion(OaBusdataOpinion opinion,String busTable) {
        return oaBusdataOpinionMapper.putBusOpinion(opinion,busTable);
    }

    @Override
    public List<OaBusdataOpinion> selectOpinionList(String table, OaBusdataOpinion oaBusdataOpinion) {
        return oaBusdataOpinionMapper.selectOpinionList(table, oaBusdataOpinion);
    }
}
