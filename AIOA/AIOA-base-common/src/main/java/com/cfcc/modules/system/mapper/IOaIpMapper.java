package com.cfcc.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.system.entity.OaIp;
import org.apache.ibatis.annotations.Param;

public interface IOaIpMapper extends BaseMapper<OaIp> {

    String getIpByUserId(@Param("userId") String userId);

    void addOaIp(@Param("userId") String userId,@Param("ip") String ip);

    void updateOaIp(@Param("userId") String userId,@Param("ip") String ip);
}
