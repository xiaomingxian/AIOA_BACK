package com.cfcc.modules.oaBus.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.modules.workflow.pojo.OaBusdataPermit;
import com.cfcc.modules.workflow.mapper.OaBusdataPermitMapper;
import com.cfcc.modules.oaBus.service.IOaBusdataPermitService;
import com.cfcc.modules.workflow.vo.OaBusdataPermitRead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 业务数据权限表
 * @Author: jeecg-boot
 * @Date:   2019-11-26
 * @Version: V1.0
 */
@Service
public class OaBusdataPermitServiceImpl extends ServiceImpl<OaBusdataPermitMapper, OaBusdataPermit> implements IOaBusdataPermitService {

    @Autowired
    private OaBusdataPermitMapper oaBusdataPermitMapper;


    @Override
    public IPage<OaBusdataPermitRead> queryUserList(String sBusdataTable, Integer iBusFunctionId, Integer iBusdataId, Integer pageNo, Integer pageSize) {
        int total=oaBusdataPermitMapper.queryBusdataPermitUserTotal(sBusdataTable,iBusFunctionId,iBusdataId);
        List<OaBusdataPermitRead> oaBusdataPermitList=oaBusdataPermitMapper.queryOaBusdataPermitRead(sBusdataTable,iBusFunctionId,iBusdataId,(pageNo-1)*pageSize,pageSize);
        IPage<OaBusdataPermitRead> oaBusdataPermitRead=new Page<>();
        oaBusdataPermitRead.setTotal(total);
        oaBusdataPermitRead.setRecords(oaBusdataPermitList);
        oaBusdataPermitRead.setSize(pageSize);
        oaBusdataPermitRead.setPages(pageNo);
        return oaBusdataPermitRead;
    }
}
