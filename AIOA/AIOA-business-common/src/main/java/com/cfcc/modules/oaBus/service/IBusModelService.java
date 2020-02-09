package com.cfcc.modules.oaBus.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.modules.oaBus.entity.BusModel;

import java.util.List;

/**
 * @Description: 业务模块表（业务分类表）
 * @Author: jeecg-boot
 * @Date:   2019-10-12
 * @Version: V1.0
 */
public interface IBusModelService extends IService<BusModel> {

    BusModel getBusModelById(Integer iId);

    boolean updateBusModelById(BusModel busModel, String schema);

    boolean removeBusModelById(String id, String schema);

    /**
     * 重写查询方法，按条件查询
     * @param pageNo
     * @param pageSize
     * @param busModel
     * @return
     */
    IPage<BusModel> getPage(Integer pageNo, Integer pageSize, BusModel busModel);

    List<BusModel> getModelList();

    List<BusModel> findList(String schema);


    List<BusModel> allPeopleSee();

    List<BusModel> currentRoleSee(String type, Object rId);


    List<BusModel> currentDeptSee(String type, Object dId);

    List<BusModel> currentUserSee(String type, Object uId);

    List<BusModel> selectDocType();

    /**
     * 根据model表中的s_en_name查询出对应的数据
     * @param str
     * @return
     */
    String getModelIdByUrlSer(String str);
}
