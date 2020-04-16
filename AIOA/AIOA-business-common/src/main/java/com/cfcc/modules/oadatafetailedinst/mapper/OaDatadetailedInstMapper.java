package com.cfcc.modules.oadatafetailedinst.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import com.cfcc.modules.oadatafetailedinst.entity.OaDatadetailedInst;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 明细存储
 * @Author: jeecg-boot
 * @Date:   2020-04-16
 * @Version: V1.0
 */
public interface OaDatadetailedInstMapper extends BaseMapper<OaDatadetailedInst> {

    Map<String, Object> findByTableId(Integer iTableId,String sCreateName);
}
