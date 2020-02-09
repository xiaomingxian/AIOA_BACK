package com.cfcc.modules.oaBus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.oaBus.entity.OaBusdataOpinion;

import java.util.List;

/**
 * @Description: 业务数据意见表
 * @Author: jeecg-boot
 * @Date:   2019-11-18
 * @Version: V1.0
 */
public interface IOaBusdataOpinionService extends IService<OaBusdataOpinion> {

    /**
     * 填写意见
     * @param opinion
     * @return
     */
    boolean putBusOpinion(OaBusdataOpinion opinion,String busTable);

    /**
     * 查询填写意见列表
     * @param table
     * @param oaBusdataOpinion
     * @return
     */
    List<OaBusdataOpinion> selectOpinionList(String table, OaBusdataOpinion oaBusdataOpinion);
}
