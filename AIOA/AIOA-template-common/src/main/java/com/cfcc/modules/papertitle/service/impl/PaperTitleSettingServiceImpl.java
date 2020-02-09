package com.cfcc.modules.papertitle.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.modules.papertitle.entity.PaperTitleSetting;
import com.cfcc.modules.papertitle.mapper.PaperTitleSettingMapper;
import com.cfcc.modules.papertitle.service.IPaperTitleSettingService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 稿纸头配置
 * @Author: jeecg-boot
 * @Date:   2019-10-14
 * @Version: V1.0
 */
@Service
public class PaperTitleSettingServiceImpl extends ServiceImpl<PaperTitleSettingMapper, PaperTitleSetting> implements IPaperTitleSettingService {

    @Resource
    private  PaperTitleSettingMapper paperTitleSettingMapper;
    @Override
    public boolean deletePaperTitleByIID(String id) {
        return paperTitleSettingMapper.deletePaperTitleByIID(id);
    }

    @Override
    public IPage<PaperTitleSetting> queryPaperList(PaperTitleSetting paperTitleSetting,Integer pageNo,Integer pageSize) {
        int total = paperTitleSettingMapper.selectPaperTotal(paperTitleSetting);
        List<PaperTitleSetting> paperList = paperTitleSettingMapper.selectPaperList(paperTitleSetting,(pageNo-1)*pageSize,pageSize );
        IPage<PaperTitleSetting> pageList = new Page<PaperTitleSetting>();
        pageList.setTotal(total);
        pageList.setCurrent(pageNo);
        pageList.setCurrent(pageSize);
        pageList.setRecords(paperList);
        return pageList;
    }

    @Override
    public List<PaperTitleSetting> paperTitleList() {
        return paperTitleSettingMapper.paperTitleList();
    }

    @Override
    public boolean updateTitleById(PaperTitleSetting paperTitleSetting) {
        return paperTitleSettingMapper.updateTitleById(paperTitleSetting);
    }

    @Override
    public PaperTitleSetting queryById(Integer id) {
        return paperTitleSettingMapper.selectPaperById(id);
    }

    @Override
    public boolean updatePaperById(PaperTitleSetting paperTitleSetting) {
        return paperTitleSettingMapper.updatePaperById(paperTitleSetting);
    }
}
