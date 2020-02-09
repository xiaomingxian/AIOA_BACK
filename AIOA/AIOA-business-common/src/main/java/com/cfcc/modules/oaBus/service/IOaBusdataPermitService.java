package com.cfcc.modules.oaBus.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.workflow.pojo.OaBusdataPermit;
import com.cfcc.modules.workflow.vo.OaBusdataPermitRead;

/**
 * @Description: 业务数据权限表
 * @Author: jeecg-boot
 * @Date:   2019-11-26
 * @Version: V1.0
 */
public interface IOaBusdataPermitService extends IService<OaBusdataPermit> {


    IPage<OaBusdataPermitRead> queryUserList(String sBusdataTable, Integer iBusFunctionId, Integer iBusdataId, Integer pageNo, Integer pageSize);
}
