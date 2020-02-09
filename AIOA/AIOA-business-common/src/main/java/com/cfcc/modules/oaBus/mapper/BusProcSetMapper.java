package com.cfcc.modules.oaBus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.oaBus.entity.BusProcSet;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @Description: 业务和流程关联配置表
 * @Author: jeecg-boot
 * @Date: 2019-11-05
 * @Version: V1.0
 */
public interface BusProcSetMapper extends BaseMapper<BusProcSet> {


    List<BusProcSet> findList();

    BusProcSet getProSetIdDao(@Param("modelId") int modelId, @Param("functionId") String functionId,
                              @Param("i_fun_version") Object i_fun_version);

    @Select("  select c.i_id,c.proc_def_key_ proKey,m.s_busdata_table busTable  from oa_bus_proc_set c " +
            "  LEFT JOIN oa_bus_model m on m.i_id=c.i_bus_model_id " +
            "  where c.i_bus_model_id = #{modelId} and c.i_bus_function_id = #{functionId} and c.i_version=#{version} limit 0,1")
    Map<String, Object> getProSetIdDaoNewTask(@Param("modelId") String modelId,
                                              @Param("functionId") String functionId,
                                              @Param("version") String version
    );


    List<BusProcSet>  getProcSetByprocbuttonId(Map<String,Object> map);

}
