package com.cfcc.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.system.entity.QrtzBackUp;

/**
 * @Description: 备份情况
 * @Author: jeecg-boot
 * @Date:   2019-11-28
 * @Version: V1.0
 */
public interface IQrtzBackUpService extends IService<QrtzBackUp> {

    /**
     * 分页查询列表
     * @param qrtzBackUp
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<QrtzBackUp> queryBackUpList(QrtzBackUp qrtzBackUp, Integer pageNo, Integer pageSize);

    boolean deleteById(String id);

    QrtzBackUp queryById(String id);
}
