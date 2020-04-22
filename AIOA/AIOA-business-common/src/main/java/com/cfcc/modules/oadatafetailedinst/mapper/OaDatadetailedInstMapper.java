package com.cfcc.modules.oadatafetailedinst.mapper;

import java.util.List;
import java.util.Map;

import com.cfcc.modules.oadatafetailedinst.entity.OaDatadetailedInst;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: 明细存储
 * @Author: jeecg-boot
 * @Date:   2020-04-16
 * @Version: V1.0
 */
public interface OaDatadetailedInstMapper extends BaseMapper<OaDatadetailedInst> {

    @Select("SELECT i_id,s_create_dept,s_create_name,s_opinion,d_create_time,i_is_1,i_is_2,s_create_by,s_create_deptid,i_is_open,s_table,i_table_id " +
            "FROM oa_datadetailed_inst " +
            "WHERE s_create_by=#{sCreateBy} and s_create_deptid=#{sCreateDeptid} and s_table=#{sTable} and i_table_id=#{iTableId} and i_is_display !=0 ")
    List<OaDatadetailedInst> seletdetailedInstList(String sTable,Integer iTableId,String sCreateBy, String sCreateDeptid);

    @Select("SELECT i_id,s_create_dept,s_create_name,s_opinion,d_create_time,i_is_1,i_is_2,s_create_by,s_create_deptid " +
            "FROM oa_datadetailed_inst " +
            "WHERE s_table=#{sTable} and i_table_id=#{iTableId} and i_is_open=1 and i_is_display !=0")
    List<OaDatadetailedInst> seletSharedetailedInstList(String sTable, Integer iTableId);

    @Select("SELECT i_id,s_create_dept,s_create_name,s_opinion,d_create_time,i_is_1,i_is_2,s_create_by,s_create_deptid " +
            "FROM oa_datadetailed_inst " +
            "WHERE s_table=#{sTable} and i_table_id=#{iTableId} and i_is_open=1 and  s_create_by != #{sCreateBy} and s_create_deptid != #{sCreateDeptid} and i_is_display !=0")
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


    @Delete("DELETE FROM oa_datadetailed_inst WHERE i_id=#{iId}")
    int deteledetailedInst(Integer iId);

    void insertDataInst(OaDatadetailedInst oaDatadetailedInst);


    List<String> findFunctionIds(Integer modelId);
}
