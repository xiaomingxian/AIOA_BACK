package com.cfcc.modules.oaBus.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.oaBus.entity.BusModelPermit;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * @Description: 业务模板
 * @Author: jeecg-boot
 * @Date:   2019-10-21
 * @Version: V1.0
 */
public interface IBusModelPermitService extends IService<BusModelPermit> {



    boolean updateBYIid(BusModelPermit busModelPermit,String schema);

    boolean deleteById(String id,String schema);

    BusModelPermit findById(Integer iId);

    IPage<BusModelPermit> findPage(Integer ibusmodelId, Integer pageNo, Integer pageSize);

    List<BusModelPermit> findList(String schema);

    List<String> getTypeId(Integer modelId);

    IPage<BusModelPermit> findAllList(BusModelPermit busModelPermit, Integer pageNo, Integer pageSize);



}
