package com.cfcc.modules.oaBus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.BusPageDetail;
import com.cfcc.modules.oabutton.entity.OaProcOpinion;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @Description: 业务配置表
 * @Author: jeecg-boot
 * @Date:   2019-10-15
 * @Version: V1.0
 */
public interface BusFunctionMapper extends BaseMapper<BusFunction> {

    List<BusFunction> getFunByIsEs(@Param("DBvalue")String DBvalue);

    int queryBusFunctionCount(BusFunction busFunction);

    List<BusFunction> queryBusFunction(@Param("pageNO") int pageNo, @Param("pageSize") int pageSize, @Param("busFunction") BusFunction busFunction, @Param("column")String column, @Param("order")String order);

    int delBusFunctionById(String id);


    @Select("select * from oa_bus_page_detail c where c.i_bus_function_id = #{id}" )
    List<BusPageDetail> queryConditionDao(@Param("id") int id);


    List<BusFunction> findList();

    BusFunction selectByIid(Integer iid);

    List<OaProcOpinion> queryProcOpinionDao(String key);

    void updateBusFunctionByIdDao(BusFunction queryBusFunction);

    List<BusFunction> getFunByModelIdDao(String id);

    @Select("SELECT i_is_proc FROM `oa_bus_function` where i_id=#{id};")
    Boolean haveProc(@Param("id")String functionId);

    @Select("SELECT i_id,s_busdata_table,i_busdata_id, s_text FROM `oa_busdata_text` where s_busdata_table=#{table}  and i_busdata_id=#{id} ;")
    Map<String,Object> getEdtorText(@Param("table")String s_busdata_table, @Param("id")String i_id);

    List<BusFunction> queryByModelIdDao(@Param("modelId")String modelId);

    List<BusFunction> getBusFunctionListByDepartId(@Param("departId") String departId,@Param("DBvalue") String DBvalue );

    BusFunction queryFunByModel(Integer functionId);
}



