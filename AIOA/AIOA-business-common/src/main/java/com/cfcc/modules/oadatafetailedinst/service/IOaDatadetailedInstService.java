package com.cfcc.modules.oadatafetailedinst.service;

import com.cfcc.modules.oadatafetailedinst.entity.OaDatadetailedInst;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: 明细存储
 * @Author: jeecg-boot
 * @Date:   2020-04-16
 * @Version: V1.0
 */
public interface IOaDatadetailedInstService extends IService<OaDatadetailedInst> {

    Map<String, Object> findByTableId(Integer iTableId,String sCreateName);
    int addorupdataDetailed( Map<String,Object> map);

    List<OaDatadetailedInst> seletdetailedInstList(String sCreateBy, String sCreateDeptid);

    List<OaDatadetailedInst> seletSharedetailedInstList(String sTable, Integer iTableId);

    List<OaDatadetailedInst> seletAlldetailedInstList(String sTable, Integer iTableId, String sCreateBy, String sCreateDeptid);
}
