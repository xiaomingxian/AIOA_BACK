package com.cfcc.modules.system.service.impl;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.modules.system.entity.Tempmanage;
import com.cfcc.modules.system.mapper.TempmanageMapper;
import com.cfcc.modules.system.service.ITempmanageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 测试类
 * @Author: jeecg-boot
 * @Date:   2019-09-25
 * @Version: V1.0
 */
@Service
public class TempmanageServiceImpl extends ServiceImpl<TempmanageMapper, Tempmanage> implements ITempmanageService {

    @Autowired
    private TempmanageMapper tempmanageMapper;

    @Override
    public boolean updateBYIid(Tempmanage tempmanage) {

        try {
            tempmanageMapper.updateByIid(tempmanage);
            return true;

        }catch (Exception e){
            return false;
        }


    }
}
