package com.cfcc.modules.system.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface CommonDynamicTableMapper {
    @Select("select i_proc_button_id bth,i_proc_opinion_id opt,PROC_DEF_KEY_  pkey  from oa_bus_proc_set where i_id=#{iprocSetId}")
    Map<String, Object> getBusProcSet(@Param("iprocSetId") String iprocSetId);

    @Select("SELECT i_button_id FROM `oa_button_set`  where  PROC_DEF_KEY_=#{key} and TASK_DEF_KEY_=#{taskDefKey} and i_proc_button_id=#{btn}")
    List<String> queryButtonId(String key, String taskDefKey, String btn);

    int insertData(@Param("map") Map<String, Object> map);

    /**
     * 批量写入
     *
     * @param map
     * @return
     */
    int insertDataBatch(@Param("list") List<Map<String,Object>> list);


    List<Map<String, Object>> queryByEquals(@Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize, @Param("query") Map<String, Object> query, @Param("table") String table);

    Long queryCountByEquals(@Param("query") Map<String, Object> query, @Param("table") String table);


}
