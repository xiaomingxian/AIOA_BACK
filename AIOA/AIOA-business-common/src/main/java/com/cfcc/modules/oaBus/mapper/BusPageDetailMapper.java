package com.cfcc.modules.oaBus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.oaBus.entity.BusPageDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @Description: 业务页面详情表（实际业务字段含义说明）
 * @Author: jeecg-boot
 * @Date:   2019-10-18
 * @Version: V1.0
 */
public interface BusPageDetailMapper extends BaseMapper<BusPageDetail> {

    List<String> getSDictIdByKey(@Param("functionId") Integer i_bus_function_id, @Param("sBusdataTable") String sBusdataTable, @Param("key") String key);

    int queryBusPageDetailCount(BusPageDetail busPageDetail);

    List<BusPageDetail> queryBusPageDetail(@Param("pageNo") int pageNO, @Param("pageSize")int pageSize, @Param("busPageDetail")BusPageDetail busPageDetail);

    int insertBusPageDetail(BusPageDetail busPageDetail);

    int insertBusPageDetailBatch(@Param("busPageDetailList") List<BusPageDetail> busPageDetailList, @Param("functionId")Integer functionId, @Param("iPageId")Integer iPageId, @Param("tableName")String tableName);

    @Select("SELECT CONCAT('  ',GROUP_CONCAT(s_table_column),'  ') FROM `oa_bus_page_detail` where s_busdata_table=#{table}")
    String getColums(@Param("table") String table);

    List<BusPageDetail> getColumsNameByTableAndColumn(@Param("sBusdataTable") String sBusdataTable, @Param("isEsColumnLists") List<String> isEsColumnLists);

    List<BusPageDetail> queryByIdDao(int iBusFunctionId);

    List<BusPageDetail> findList();

    List<String> getCloumnNameByTableAndEsquery(@Param("sBusdataTable") String sBusdataTable,@Param("iId") Integer iId);

    List<BusPageDetail> queryConditionByIdDao(int iBusFunctionId);

    //List<BusPageDetail> getConColumsDao(int functionId);
    List<Map<String,String>> getConColumsDao(@Param("functionId")int functionId, @Param("iPageId")Integer iPageId);

    List<Map<String,String>> getColumsByTableName(@Param("tableName") String tableName,@Param("functionId") String functionId);

    boolean updatePageDetail(BusPageDetail busPageDetail);

    @Select("select * from oa_bus_page_detail c where c.i_bus_function_id = #{functionId}")
    List<BusPageDetail> getListByFunIDDao(String functionId);

    @Select("select count(*) from oa_bus_page_detail  c where c.i_bus_function_id  = #{functionId} " +
            "and c.i_bus_page_id  = #{pageId} and c.s_busdata_table = #{tableName}")
    int getBusPageDetailCount(String functionId, String pageId, String tableName);


    /**
     * 通过IFunctionId和iFunVersion查询出对应的iPageId，再查出对应的PageDetail
     *  中的数据
     * @param functionId
     * @param iFunVersion
     * @return
     */
    List<BusPageDetail> getListByFunIDAndIPageIdDao(@Param("functionId")String functionId, @Param("iPageId")String iPageId);
}
