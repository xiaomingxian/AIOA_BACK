package com.cfcc.modules.oaBus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.oaBus.entity.BusFunctionView;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 业务功能数据查看权限表
 * @Author: jeecg-boot
 * @Date:   2019-10-21
 * @Version: V1.0
 */
public interface BusFunctionViewMapper extends BaseMapper<BusFunctionView> {

    @Select("select * from oa_bus_function_view where i_bus_function_id = #{funid}")
    List<BusFunctionView> getFunViewListByFunIdDao(int funid);
}
