package com.cfcc.modules.oaBus.service;

import com.cfcc.modules.oaBus.entity.BusFunctionView;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 业务功能数据查看权限表
 * @Author: jeecg-boot
 * @Date:   2019-10-21
 * @Version: V1.0
 */
public interface IBusFunctionViewService extends IService<BusFunctionView> {

    List<BusFunctionView> getFunViewListByFunId(int funid);
}
