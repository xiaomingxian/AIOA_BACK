package com.cfcc.modules.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.modules.system.entity.QrtzBackUp;
import com.cfcc.modules.system.mapper.QrtzBackUpMapper;
import com.cfcc.modules.system.service.IQrtzBackUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 备份情况
 * @Author: jeecg-boot
 * @Date:   2019-11-28
 * @Version: V1.0
 */
@Service
public class QrtzBackUpServiceImpl extends ServiceImpl<QrtzBackUpMapper, QrtzBackUp> implements IQrtzBackUpService {
    @Autowired
    private QrtzBackUpMapper qrtzBackUpMapper;

    @Override
    public IPage<QrtzBackUp> queryBackUpList(QrtzBackUp qrtzBackUp, Integer pageNo, Integer pageSize) {
        int total = qrtzBackUpMapper.selectBackUpTotal(qrtzBackUp);
        List<QrtzBackUp> backUpList = qrtzBackUpMapper.selectBackUpList(qrtzBackUp,(pageNo-1)*pageSize,pageSize );
        IPage<QrtzBackUp> pageList = new Page<QrtzBackUp>();
        pageList.setTotal(total);
        pageList.setCurrent(pageNo);
        pageList.setCurrent(pageSize);
        pageList.setRecords(backUpList);
        return pageList;
    }

    @Override
    public boolean deleteById(String id) {
        return qrtzBackUpMapper.deleteBackUpByIID(id);
    }

    @Override
    public QrtzBackUp queryById(String id) {
        return qrtzBackUpMapper.getBackUpById(id);
    }
}
