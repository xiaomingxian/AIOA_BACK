package com.cfcc.modules.docnum.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.docnum.entity.DocNumManage;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.docnum.entity.DocNumSet;
import com.cfcc.modules.system.model.DepartIdModel;

import java.util.List;

/**
 * @Description: 文号配置
 * @Author: jeecg-boot
 * @Date:   2019-10-16
 * @Version: V1.0
 */
public interface IDocNumSetService extends IService<DocNumSet> {

    /**
     * 根据年、文号id查询最大docnum
     * @param id
     * @param sYear 选填
     * @return
     */
    DocNumSet queryById(Integer id,String sYear);

    /**
     *根据id查询
     * @param id
     * @return
     */
    DocNumSet queryById(Integer id);

    /**
     * 根据id修改
     * @param docNumSet
     * @return
     */
    boolean updateDocNumById(DocNumSet docNumSet);
    /**
     * 根据iid删除
     * @param id
     * @return
     */
    boolean deleteDocNumTitleByIID(String id);

    /**
     * 分页查询列表
     * @param docNumSet
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<DocNumSet> queryDocNumList(DocNumSet docNumSet, Integer pageNo, Integer pageSize);

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

    boolean insertDepart(int id, String departId);

    List<DepartIdModel> queryDepartIdsOfDocNum(String docId);

    Integer queryByIdAndSendObj(DocNumManage docNumManage);

    /**
     * 根据业务id查询对应文号列表
     * @param iBusFunctionId
     * @param sDeptId
     * @return
     */
    List<DocNumSet> getDocNumNameListByBf(Integer iBusFunctionId, String sDeptId,Integer iBusUnitId);

    Integer queryByTemplateId(DocNumManage docNumManage);
}
