package com.cfcc.modules.oadeferlog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.cfcc.modules.oadeferlog.entity.oaDeferLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: 任务类事务延期记录
 * @Author: jeecg-boot
 * @Date:   2020-04-23
 * @Version: V1.0
 */
public interface oaDeferLogMapper extends BaseMapper<oaDeferLog> {

    @Select("SELECT s_create_name,s_create_dept,d_datetime1,d_datetime2 " +
            "FROM oa_defer_log WHERE s_table=#{sTable} and i_table_id=#{iTableId}  Order By d_datetime2 Desc")
    List<oaDeferLog> selecturgeLog(String sTable, Integer iTableId);
}
