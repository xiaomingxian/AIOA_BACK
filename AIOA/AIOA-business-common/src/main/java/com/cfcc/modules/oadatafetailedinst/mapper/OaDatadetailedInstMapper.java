package com.cfcc.modules.oadatafetailedinst.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import com.cfcc.modules.oadatafetailedinst.entity.OaDatadetailedInst;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: 明细存储
 * @Author: jeecg-boot
 * @Date:   2020-04-16
 * @Version: V1.0
 */
public interface OaDatadetailedInstMapper extends BaseMapper<OaDatadetailedInst> {

    @Select("SELECT i_id,s_create_dept,s_create_name,s_opinion,d_datetime1,i_is_1,i_is_2,s_create_by,s_create_deptid " +
            "FROM oa_datadetailed_inst " +
            "WHERE s_create_by=#{sCreateBy} and s_create_deptid=#{sCreateDeptid}")
    List<OaDatadetailedInst> seletdetailedInstList(String sCreateBy, String sCreateDeptid);

    @Select("SELECT i_id,s_create_dept,s_create_name,s_opinion,d_datetime1,i_is_1,i_is_2,s_create_by,s_create_deptid " +
            "FROM oa_datadetailed_inst " +
            "WHERE s_table=#{sTable} and i_table_id=#{iTableId} and i_is_open=1")
    List<OaDatadetailedInst> seletSharedetailedInstList(String sTable, Integer iTableId);

    @Select("SELECT i_id,s_create_dept,s_create_name,s_opinion,d_datetime1,i_is_1,i_is_2,s_create_by,s_create_deptid " +
            "FROM oa_datadetailed_inst " +
            "WHERE s_table=#{sTable} and i_table_id=#{iTableId} and i_is_open=1 and  s_create_by != #{sCreateBy} and s_create_deptid != #{sCreateDeptid} ")
    List<OaDatadetailedInst> seletAlldetailedInstList(String sTable, Integer iTableId, String sCreateBy, String sCreateDeptid);

    Map<String, Object> findByTableId(Integer iTableId, String sCreateName);

    Integer findOpions(Integer tableid, String userName);

    Integer findIsFile(String tableName,Integer tableid, String userName,String deptName);

    Map<String, Object> findDate(Integer tableid);

    Integer getBanjieBydept(String itableId);

    Integer getDateCount(String createTime, String dateTime,String sCreateName);

    List<Map<String, Object>> findTypeNum(String table,String userId,int year,String parentId);

    Map<String, Object> getDept(String parentId);

    List<Map<String, Object>> findorganizeNum(String table, String userId, int year, String parentId);

}
