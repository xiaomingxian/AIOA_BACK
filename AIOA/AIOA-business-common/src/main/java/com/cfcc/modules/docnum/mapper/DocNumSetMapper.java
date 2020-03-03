package com.cfcc.modules.docnum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.docnum.entity.DocNumExport;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.docnum.entity.DocNumSet;
import com.cfcc.modules.system.entity.SysDepart;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: 文号配置
 * @Author: jeecg-boot
 * @Date:   2019-10-16
 * @Version: V1.0
 */
public interface DocNumSetMapper extends BaseMapper<DocNumSet> {

    /**
     * 根据id删除
     * @param id
     * @return
     */
    boolean deleteDocNumByIID(@Param("id") String id);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    DocNumSet selectDocNumById(@Param("id") Integer id);

    /**
     * 根据id修改
     * @param docNumSet
     * @return
     */
    boolean updateDocNumById(DocNumSet docNumSet);

    /**
     *条件分页查询
     * @param docNumSet
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<DocNumSet> selectDocNumList(@Param("doc") DocNumSet docNumSet, @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);

    /**
     * 数据统计
     * @param docNumSet
     * @return
     */
    int selectDocNumTotal(DocNumSet docNumSet);

    /**
     * 查询模块下拉列表
     * @return
     */
    List<BusModel> busModelList();

    /**
     * 查询业务下拉列表
     * @return
     */
    List<BusFunction> busFunctionList(DocNumSet docNumSet);

    /**
     * 新增
     * @param docNumSet
     * @return
     */
    int insertDocNum(DocNumSet docNumSet);

    /**
     * 新增文号部门关联表
     * @param id
     * @param depart
     * @return
     */
    boolean insertDepart(@Param("docId") int id, @Param("departId") String depart);


    /**
     * 根据文号id查询部门列表
     * @param docId
     * @return
     */
    String selectDepartIds(@Param("docId") String docId);

    /**
     * 根据部门id查询明细
     * @param depIdList
     * @return
     */
    List<SysDepart> selectDepartByIds(@Param("depIdList") String depIdList);

    /**
     * 文号关联部门信息修改
     * @param docId
     * @return
     */
    boolean deleteDocNumDeptByIdocId(@Param("docId") Integer docId);

    DocNumSet queryById(Integer id);

    /**
     * 根据业务id查询对应文号列表
     * @param iBusFunctionId
     * @param sDeptId
     * @return
     */
    List<DocNumSet> getDocNumNameList(@Param("iBusFunctionId") Integer iBusFunctionId,@Param("sDeptId") String sDeptId,@Param("iBusUnitId") Integer iBusUnitId);

    List<Map<String,Object>> selectBusdataLIstsByDocId(@Param("docId") Integer docId);

    DocNumExport selectBusdataByIid(@Param("table") String table, @Param("iid") Integer iid);
}
