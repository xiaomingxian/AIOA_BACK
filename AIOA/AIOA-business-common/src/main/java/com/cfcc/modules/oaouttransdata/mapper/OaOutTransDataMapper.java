package com.cfcc.modules.oaouttransdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.cfcc.modules.oaouttransdata.entity.OaOutTransData;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 归档送公文传输
 * @Author: jeecg-boot
 * @Date: 2019-10-18
 * @Version: V1.0
 */
public interface OaOutTransDataMapper extends BaseMapper<OaOutTransData> {
    @Insert("insert into oa_out_trans_data (${transCol}) select ${busdataCol} from ${busdataTab} where i_id = #{iId}")
    void transInsert(@Param("busdataTab") String busdataTab, @Param("busdataCol") String busdataCol, @Param("transCol") String transCol, @Param("iId") Integer iId);
}