package com.cfcc.modules.oaouttransdata.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.oaBus.entity.BusPageDetail;
import com.cfcc.modules.oaouttransdata.entity.OaOutTransData;

import java.util.List;
import java.util.Map;

/**
 * @Description: 归档送公文传输
 * @Author: jeecg-boot
 * @Date: 2019-10-18
 * @Version: V1.0
 */
public interface IOaOutTransDataService extends IService<OaOutTransData> {
    void transInsert(List<BusPageDetail> pageDetails, Integer iId);

    Boolean InsertTransData(Map<String, Object> map, String stable, Integer tableid);
}