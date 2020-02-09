package com.cfcc.modules.oaBus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.oaBus.entity.OaBusdataOpinion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 业务数据意见表
 * @Author: jeecg-boot
 * @Date:   2019-11-18
 * @Version: V1.0
 */
public interface OaBusdataOpinionMapper extends BaseMapper<OaBusdataOpinion> {

    /**
     * 填写意见
     * @param opinion
     * @return
     */
    boolean putBusOpinion(@Param("op") OaBusdataOpinion opinion, @Param("busTable") String busTable);

    List<OaBusdataOpinion> selectOpinionList(@Param("table") String table, @Param("oabus") OaBusdataOpinion oaBusdataOpinion);

}
