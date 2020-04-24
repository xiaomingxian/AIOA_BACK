package com.cfcc.modules.oaBus.mapper;

import com.cfcc.modules.oaBus.entity.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * @Description: 业务数据表
 * @Author: jeecg-boot
 * @Date: 2019-10-21
 * @Version: V1.0
 */
public interface OaBusdataMapper extends BaseMapper<OaBusdata> {

    String getBusdataByOaFile(OaFile oaFile);

    List<Integer> getBusdataIidByTable(@Param("sBusdataTable")String sBusdataTable, @Param("iId") Integer iId);

    Integer updateIsESByid(@Param("tableName") String tableName, @Param("id") String id,@Param("DBvalue")String DBvalue);

    int queryBusdataCount(OaBusdata oaBusdata);

    List<Map<String,Object>> getBusdataByTable(@Param("columnLists") String columnLists,@Param("busFunction") BusFunction busFunction, @Param("DBvalue") String DBvalue);

    List<OaBusdata> queryBusdata(@Param("pageNo") int pageNo, @Param("pageSize") Integer pageSize, @Param("oaBusdata")OaBusdata oaBusdata);

    OaBusdata getBusdataByIdDao(int iId);


    /*void transUpdate(BusPageDetail busPageDetail);

    String queryList(BusPageDetail busPageDetail);*/


    List<TableCol> getTableColListDao(String tableName);

    /**
     * 查询出是否已经定义了页面原型
     *
     * @param iPageId
     * @return
     */
    BusPageDetail queryIsDetailDefault(int iPageId);

    String queryTabeleNameByModelId(int modelId);

    List<TableCol> getTableColListByPageID(int iPageId);

    @Select("select ${cols} from ${table}")
    List<Map> selectByTable(@Param("table") String table, @Param("cols") String cols);

    @Update("update ${busdataTab} set i_is_send=1 where i_id=#{iId}")
    void updateSendState(@Param("busdataTab") String busdataTab, @Param("iId") Integer iId);

    List<Map<String, Object>> getBusdataByCondition(@Param("column")String column, @Param("tableName")String tableName, @Param("map")Map<String, String> map);

    Map<String, Object> getBusdataById(@Param("tableName")String tableName, @Param("tableId")Integer tableId);


    @Update("update ${sTable} set i_is_approve=1 where i_id=#{iTableId}")
    Boolean updateApproveById(@Param("sTable") String sTable,@Param("iTableId") Integer iTableId);



    List<String> queryUserManagedepts(String userId);

    String getDataIdfromPermit(@Param("funid")int funid,
                                     @Param("tableName")String tableName,
                                     @Param("userId")String userId,
                                     @Param("unit")String unit);

    List<Map<String, Object>> getBusdataByMap(@Param("pageNo") int pageNo,
                                              @Param("pageSize") int pageSize,
                                              @Param("column") String column,
                                              @Param("tableName") String tableName,
                                              @Param("map") Map<String, Object> map,
                                              @Param("permitData") Map<String, String> permitData,
                                              @Param("userId") String userId,
                                              @Param("userUnit") String userUnit,
                                              @Param("userDepart") String userDepart,
                                              @Param("orderFlag")String orderFlag);
    int getBusdataByMapTotal(@Param("tableName") String tableName,
                             @Param("map") Map<String, Object> map,
                             @Param("permitData") Map<String, String> permitData,
                             @Param("userId")String userId,
                             @Param("userUnit")String userUnit,
                             @Param("userDepart")String userDepart);

    @Select("select s_table_column,s_column_name,i_column_type from oa_bus_page_detail c where c.i_bus_function_id = #{funcId} and c.i_is_listquery = 1")
    List<Map<String, Object>> getQueryCOnditionDao(int funcId);

    List<Map<String, Object>> getSelFunDao(@Param("modelId")String modelId);
    @Select("select * from ${tableName} where i_id = #{busdataId}")
    Map<String, Object> getBusdataMapByIdDao(@Param("tableName") String tableName,@Param("busdataId")String busdataId);

    Map<String, Object> getBusDataUserDepartDao(String userName);

    boolean updateMiddleById(@Param("table") String table,@Param("oabus") OaBusdata oaBusdata);

    int getDocListTotal(@Param("bdata")OaBusdata oaBusdata, @Param("table")String sBusdataTable);

    List<OaBusdata> selectDocList(@Param("bdata") OaBusdata oaBusdata, @Param("table")String sBusdataTable,         @Param("pageNo")int pageNo, @Param("pageSize")Integer pageSize);
//一级折叠列表-任务环节
    List<String> getTaskNameList(@Param("tableName") String tableName);
//通过任务环节获取二级列表
    List<Map<String, Object>> getBusdataByMapNoPages(
            @Param("column") String column,
            @Param("tableName") String tableName,
            @Param("taskName") String taskName,
            @Param("map") Map<String, Object> map,
            @Param("permitData") Map<String, String> permitData,
            @Param("userId")String userId,
            @Param("userUnit")String userUnit,
            @Param("userDepart")String userDepart,
            @Param("orderFlag")String orderFlag);

//    二级折叠列表=无权限
    List<Map<String, Object>> getBusdataByCreateNameNoPages(@Param("column") String column,
                                                     @Param("tableName") String tableName,
                                                     @Param("taskName") String taskName,
                                                     @Param("map") Map<String, Object> map,
                                                     @Param("userName")String userName,
                                                            @Param("orderFlag")String orderFlag);

    List<Map<String, Object>> getBusdataByCreateName(@Param("pageNo") int pageNo,
                                                     @Param("pageSize") int pageSize,
                                                     @Param("column") String column,
                                                     @Param("tableName") String tableName,
                                                     @Param("map") Map<String, Object> map,
                                                     @Param("userName")String userName,
                                                     @Param("orderFlag") String orderFlag);

    int getBusdataByCreateNameCount(@Param("tableName")String tableName,
                                    @Param("map")Map<String, Object> map,
                                    @Param("userName")String userName);
    @Select("Select c.s_page_path pageRef,c.act_show actShow from oa_bus_page c where c.i_id = (select d.i_page_id from oa_bus_function d where d.i_id = ${functionId})")
    Map<String,Object> getPageUrlDao(@Param("functionId")String functionId);


    int updateAllOaData(Map<String, Object> map);

    //查询某一条具体业务数据
    List<Map<String, Object>> getModifyFieldDataOne(@Param("column") String column,
                                                    @Param("tableName") String tableName,
                                                    @Param("iid") Integer iid);

//无权限 全部查询业务数据表
    List<Map<String, Object>> getModifyFieldList(@Param("pageNo") int pageNo,
                                                     @Param("pageSize") int pageSize,
                                                     @Param("column") String column,
                                                     @Param("tableName") String tableName,
                                                     @Param("map") Map<String, Object> map);
//无权限 全部查询业务数据表--总条数
    int getModifyFieldListTotals(@Param("tableName") String tableName,
                                 @Param("map") Map<String, Object> map);


    String queryValue(@Param("tableName") String stable, @Param("tableid") Integer tableid, @Param("sTableColumn") String sTableColumn);

    int listCountBytableName(Map<String, Object> map);

    Map<String,Object> queryFuncitonDataById(@Param("functionId")String functionId);

    String getBusdataByIdAndTableName(@Param("id") String id,@Param("sBusdataTable") String sBusdataTable,@Param("DBvalue") String DBvalue);


    List<Map<String, Object>> getCheckData(@Param("tableName") String tableName,
                              @Param("functionId")String functionId,
                              @Param("permitData")Map<String, String> permitData,
                              @Param("userId")String userId,
                              @Param("userUnit")String userUnit,
                              @Param("userDepart")String userDepart);

    Integer getOaBusFunctionIsEsByOaBusdata(@Param("tableId") String tableId,@Param("table") String table);

    Boolean updateBusDataByIdAndTable(@Param("id") Integer bBusdataId,@Param("tableName") String bTableName,@Param("s_title") String s_title,@Param("s_file_num") String s_file_num,@Param("s_create_dept") String s_create_dept,@Param("i_is_file") Integer i_is_file);

    Boolean saveOaDataData(@Param("s_file_type") String s_file_type,@Param("i_a_bus_model_id") Integer i_a_bus_model_id,@Param("i_a_bus_function_id") Integer i_a_bus_function_id,
                           @Param("s_a_busdata_table") String s_a_busdata_table,@Param("i_a_fun_version") Integer i_a_fun_version,@Param("i_a_busdata_id") Integer i_a_busdata_id,@Param("i_b_bus_model_id")Integer i_b_bus_model_id,@Param("i_b_bus_function_id") Integer i_b_bus_function_id,
                           @Param("s_b_busdata_table") String s_b_busdata_table,@Param("i_b_fun_version") Integer i_b_fun_version,@Param("i_b_busdata_id") Integer i_b_busdata_id,@Param("s_create_name") String s_create_name,@Param("s_create_by") String s_create_by);

    List<Map<String, Object>> getOaDataAllByBusdataId(@Param("busdataId") Integer busdataId,@Param("tableName") String tableName);

//    Boolean saveOaDataData(Integer sFileType, String iABusModelId, String iABusFunctionId, String sABusdataTable, String iAFunVersion, String iBBusFunctionId, Object sBBusdataTable, String iBFunVersion, String iBBusdataId, String sCreateName, String sCreateBy);

//    Boolean saveOaDataData(Map<String, Object> oaDataMap);




















}