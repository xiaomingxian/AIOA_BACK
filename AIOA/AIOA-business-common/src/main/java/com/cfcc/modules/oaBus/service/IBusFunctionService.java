package com.cfcc.modules.oaBus.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.modules.oaBus.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.oabutton.entity.OaProcOpinion;

import java.util.List;
import java.util.Map;

/**
 * @Description: 业务配置表
 * @Author: jeecg-boot
 * @Date:   2019-10-15
 * @Version: V1.0
 */
public interface IBusFunctionService extends IService<BusFunction> {

    IPage<BusFunction> getPage(Integer pageNo, Integer pageSize, BusFunction busFunction, String column, String order);

    int removeBusFunctionById(String id,String schema);

    void saveMain(BusFunction busFunction, List<BusPageDetail> busPageDetailList);


    /**
     * 根据id查询出业务对应的可以显示的查询条件
     * @param id
     * @return
     */
    List<BusPageDetail> queryConditionSer(int id);

    List<BusFunction> findList(String schema);

    /**
     * 查询出流程对应的意见表中的数据，oa_proc_opinion
     * @param key*/
    List<OaProcOpinion> queryoaProcOpinionSer(String key);


    void saveMain(BusFunction busFunction, List<BusFunctionUnit> busFunctionUnit, BusProcSet busProcSet, List<BusFunctionView> busFunctionView,String schema);

    IPage<BusFunction> getFunByModelId(String id);


    BusFunction getOneByFunId(String functionId);

    void updateFunction(BusFunction busFunction, List<BusFunctionUnit> busFunctionUnit, BusProcSet busProcSet, List<BusFunctionView> busFunctionView,String schema);

    Map<String, Object> getEditInit(int functionId);

    Map<String, Object> queryRoleAndDepartSer();

    List<BusFunction> queryByModelId(String modelId);

    List<BusFunction> getBusFunctionListByDepartId(String departId,String DBvalue);
}
