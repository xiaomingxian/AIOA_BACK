package com.cfcc.modules.oadatafetailedinst.service.impl;

import com.cfcc.modules.oadatafetailedinst.entity.OaDatadetailedInst;
import com.cfcc.modules.oadatafetailedinst.mapper.OaDatadetailedInstMapper;
import com.cfcc.modules.oadatafetailedinst.service.IOaDatadetailedInstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;

/**
 * @Description: 明细存储
 * @Author: jeecg-boot
 * @Date:   2020-04-16
 * @Version: V1.0
 */
@Service
public class OaDatadetailedInstServiceImpl extends ServiceImpl<OaDatadetailedInstMapper, OaDatadetailedInst> implements IOaDatadetailedInstService {

    @Autowired
    private OaDatadetailedInstMapper  oaDatadetailedInstMapper;
    @Override
    public Map<String, Object> findByTableId(Integer iTableId,String sCreateName) {
        return oaDatadetailedInstMapper.findByTableId(iTableId,sCreateName);
    }
}
