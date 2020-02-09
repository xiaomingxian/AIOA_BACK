package com.cfcc.modules.oaBus.service.impl;

import com.cfcc.modules.oaBus.entity.BusFunctionView;
import com.cfcc.modules.oaBus.mapper.BusFunctionViewMapper;
import com.cfcc.modules.oaBus.service.IBusFunctionViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 业务功能数据查看权限表
 * @Author: jeecg-boot
 * @Date:   2019-10-21
 * @Version: V1.0
 */
@Service
public class BusFunctionViewServiceImpl extends ServiceImpl<BusFunctionViewMapper, BusFunctionView> implements IBusFunctionViewService {

    @Autowired
    BusFunctionViewMapper busFunctionViewMapper ;
    /**
     * 通过functiId查询出对应的functionViewId
     * @param funid
     * @return
     */
    @Override
    public List<BusFunctionView> getFunViewListByFunId(int funid) {
        return busFunctionViewMapper.getFunViewListByFunIdDao(funid);
    }
}
