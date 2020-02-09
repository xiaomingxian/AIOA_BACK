package com.cfcc.modules.workflow.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.workflow.pojo.OaBusdataPermit;
import com.cfcc.modules.workflow.vo.OaBusdataPermitRead;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 业务数据权限表
 * @Author: jeecg-boot
 * @Date: 2019-11-26
 * @Version: V1.0
 */
public interface OaBusdataPermitMapper extends BaseMapper<OaBusdataPermit> {


    List<OaBusdataPermitRead> queryOaBusdataPermitRead(@Param("sBusdataTable") String sBusdataTable, Integer iBusFunctionId, Integer iBusdataId, int i, Integer pageSize);


    int queryBusdataPermitUserTotal(@Param("sBusdataTable") String sBusdataTable, Integer iBusFunctionId, Integer iBusdataId);


    //批量插入数据
    @Insert("<script>" +
            "INSERT INTO ${table}  " +
            "(i_bus_function_id,i_busdata_id, s_user_id,s_userdept_id,s_userunit_id,i_is_read,i_is_cancel)  " +
            "VALUES  " +
            "<foreach collection='list' item='item' index='index'  separator=','>" +
            "(#{item.iBusFunctionId},#{item.iBusdataId},#{item.sUserId},#{item.sUserdeptId},#{item.sUserunitId},#{item.iIsRead},#{item.iIsCancel})" +
            "</foreach>" +
            "</script>")
    void insertBatch(@Param("table") String table,@Param("list") ArrayList<OaBusdataPermit> oaBusdataPermits);

    @Delete("DELETE FROM ${table} WHERE i_busdata_id=#{busdataId} AND i_bus_function_id=#{funcationId}")
    boolean deleteBusDataPermitByBusDataIdAndFuncationId(@Param("table")String table,@Param("busdataId") Integer busdataId,@Param("funcationId") Integer funcationId);
}
