package com.cfcc.modules.oaBus.mapper;

import com.cfcc.modules.oaBus.entity.ButtonPermit;
import com.cfcc.modules.oaBus.entity.OaBusdataOpinion;
import com.cfcc.modules.oaBus.entity.OaOutLog;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface OaBusDynamicTableMapper {
    @Select("select m.s_busdata_table tableName,s.i_proc_button_id btn,s.i_proc_opinion_id opt,s.PROC_DEF_KEY_  pkey   " +
            "from oa_bus_proc_set s  LEFT JOIN oa_bus_model m  on m.i_id=s.i_bus_model_id where s.i_id=#{iprocSetId} ")
    Map<String, Object> getBusProcSet(@Param("iprocSetId") Integer iprocSetId);


    List<ButtonPermit> queryButtonId(@Param("key") String key, @Param("taskDefKey") String taskDefKey, @Param("btn") String btn);
    List<ButtonPermit> querySaveButton(@Param("key") String key, @Param("taskDefKey") String taskDefKey, @Param("btn") String btn);

    int insertData(@Param("map") Map<String, Object> map);


    List<Map<String, Object>> queryByEquals(@Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize, @Param("query") Map<String, Object> query, @Param("table") String table);

    Long queryCountByEquals(@Param("query") Map<String, Object> query, @Param("table") String table);


    @Select("SELECT  " +
            "  t.id optId," +
            "  i_task_opinion_name optionName,  " +
            "  i_id optionSetId,  " +
            "  TASK_DEF_KEY_ taskDefKey,  " +
            "  i_task_opinion_order orderId,  " +
            "  optionContext,  " +  //GROUP_CONCAT(optionContext)
            "  s_name signName,  " +
            "  d_sign signTime,  " +
            "  type  " +
            "FROM  " +
            "  (  " +
            "    SELECT " +
            "      ifnull(b.i_id, '') id, " +
            "      os.i_task_opinion_name,  " +
            "      os.i_id,  " +
            "      os.TASK_DEF_KEY_,  " +
            "      os.i_task_opinion_order,  " +
            "      ifnull(b.s_opinion, '') optionContext,  " +
            "      ifnull(b.s_name, '')  s_name,  " +
            "      ifnull(b.d_sign, '') d_sign,  " +
            "      os.type_ type  " +
            "    FROM  " +
            "      oa_opinion_set os  " +
            "    LEFT JOIN ${optionTable}  b ON os.i_id = b.i_opinion_set_id  and os.TASK_DEF_KEY_=b.s_taskdef_key " +
            "    AND b.i_busdata_id =  #{busDataId}  " +
            "    WHERE  " +
            "      os.i_proc_opinion_id =#{opt}  " +
            //"    AND os.i_proc_set_id = #{iprocSetId}   " +
            "    AND os.PROC_DEF_KEY_ = #{key}  " +
            "    ORDER BY  " +
            "      os.i_task_opinion_order ASC,  " +
            "      b.d_sign   desc " +
            "  ) AS t  "
//            +
//            "GROUP BY  " +
//            "  optionSetId "
    )
    List<Map<String, Object>> queryOptions(@Param("opt") String opt, @Param("iprocSetId") String iprocSetId,
                                           @Param("key") String key, @Param("taskDefKey") String taskDefKey,
                                           @Param("optionTable") String optionTable, @Param("busDataId") String busDataId);

    int updateData(@Param("map") Map<String, Object> map);

    boolean deleteData(@Param("map") Map<String, Object> map);

    Map<String, Object> queryDataById(@Param("query") Map<String, Object> map);

    List<String> isReader(@Param("id") String busDataId, @Param("table") String table);

    @Select("SELECT p.act_show FROM `oa_bus_function` f LEFT JOIN oa_bus_page p on p.i_id=f.i_page_id where f.i_id=#{v};")
    String queryActShow(Object i_bus_function_id);

    @Select("SELECT s_name, d_sign,s_opinion,s_opinion_type FROM ${table} WHERE i_bus_function_id=#{functionId} AND i_busdata_id=#{busdataId};")
    List<OaBusdataOpinion> queryOptionsByBusDataIdAndFuncationId(@Param("table") String tableName,@Param("busdataId") Integer tableid, @Param("functionId") Integer funcationid);


    List<Map<String, Object>> queryUnitsByUser(String orgCode);

    @Select("SELECT * FROM oa_bus_model WHERE i_id in ( SELECT  i_bus_model_id FROM oa_bus_function WHERE i_id = #{functionId})")
    Map<String, Object> queryModelByFunctionId(String functionId);


    List<Map<String, Object>> queryUsersByUnit(String unitId);

    Map<String,Object> getTableByUnitAndFunction(String unitId, String functionId);

    List<OaOutLog> queryOaOutLogById(OaOutLog oaOutLog);

    @Delete("DELETE FROM oa_file where  s_table=#{table} and i_table_id=#{id} and s_file_type=#{type}")
    void deleteOaFile(@Param("table") String table,@Param("id") String id,@Param("type") int i);
}
