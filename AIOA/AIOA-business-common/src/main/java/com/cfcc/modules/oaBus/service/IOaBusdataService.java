package com.cfcc.modules.oaBus.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.common.api.vo.Result;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.OaBusdata;
import com.cfcc.modules.oaBus.entity.TableCol;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.entity.SysUser;

import java.util.List;
import java.util.Map;

/**
 * @Description: 业务数据表
 * @Author: jeecg-boot
 * @Date:   2019-10-21
 * @Version: V1.0
 */

public interface IOaBusdataService  extends  IService<OaBusdata>{



    IPage<OaBusdata> getBusdataPage(Integer pageNo, Integer pageSize, OaBusdata oaBusdata);

    OaBusdata getBusdataById(Integer iId);

    IPage<TableCol> getTableColList(int iPageId, int modelId, int functionId);

    void updateIsES(List<Map<String, Object>> oaFileList);

    List<Map> selectByTable(String table, String cols);

    /**
     * 通过model_id，进行简单的查询，返回map
     *
     * @return
     */
    Result<IPage<Map<String, Object>>> getByModelId(String json, String realName, String username) ;

    /**
     * 通过model_id，任务环节返回，返回map
     * @param json
     * @param userName
     * @return
     */
    Result<Map<String, Object>> getByModelIdAndTaskName(String json,String userName) throws Exception;
    /**
     * 运维工具--动态修改字段
     *
     * @return int
     */
    int updateAllOaData(Map<String, Object> map);
    //按表名，条件获取条数
    int listCountBytableName(Map<String, Object> map);

    List<String> getTaskNameList(String tableName);

    Map<String,Object> getBusDataById(String tableName,Integer tableId);

    Boolean updateApproveById(String sTable, Integer iTableId);

    List<Map<String, Object>> getQueryCondition(int funcId);

    List<Map<String, Object>> getSelFun(String modelId);

    /**
     * 根据functionId 和busdataID 查询出对应的列名和数据，以及对应的一个busdata数据
     * @param functionId
     * @param busdataId
     * @return
     */


    Map<String, Object> getBusDataAndDetailById(Map<String, Object> map, LoginInfo loginInfo);

    Map<String, Object> getBusDataUserDepartSer();

    boolean updateMiddleById(String table, OaBusdata oaBusdata);

    IPage<OaBusdata> selectDocList(OaBusdata oaBusdata, String sBusdataTable, Integer pageNo, Integer pageSize);

    Map<String,Object> getPageUrlSer(String functionId);

    IPage<Map<String, Object>>selBusDataListByModelId(String modelId,String userName, Integer pageNo, Integer pageSize);

    Result queryDataByModelAndFunctionId(String modelId, String functionId, LoginInfo loginInfo);

    String queryTableName(Integer modelId);

    /**
     * 添加发布范围，插入权限表
     * @param json
     * @param currentUser
     * @return
     */
    boolean commitPer(String json, SysUser currentUser);

    //无权限 全部查询业务数据表
    IPage<Map<String, Object>> getModifyFieldList(int pageNo,int pageSize,String column, String tableName, Map<String, Object> map);


    String getBusdataValueByIdAndFiled(String stable, Integer tableid, String sTableColumn);

    String getDictText(String sDictId, String itemValue);

    /**
     * 通过fun 的机构权限过滤对应的数据
     * @param busFunctionList
     * @param depart
     * @return
     */
    List<BusFunction> getFunListByFunUnit(List<BusFunction> busFunctionList, SysDepart depart);


    Result haveSavePermission(String table, String id, LoginInfo loginInfo);

    Map<String,Object> getFuncitonDataById(String functionId);

    Map<String, Object> queryStateById(String stable, Integer tableid);
}
