package com.cfcc.modules.oabutton.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.common.constant.CacheConstant;
import com.cfcc.modules.oabutton.entity.OaButton;
import com.cfcc.modules.oabutton.entity.OaButtonSet;
import com.cfcc.modules.oabutton.mapper.OaButtonSetMapper;
import com.cfcc.modules.oabutton.service.IOaButtonSetService;
import com.cfcc.modules.workflow.pojo.OaProcActinst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


/**
 * @Description: 发布类按钮描述
 * @Author: jeecg-boot
 * @Date:   2019-10-26
 * @Version: V1.0
 */
@Service
@Transactional
public class OaButtonSetServiceImpl extends ServiceImpl<OaButtonSetMapper, OaButtonSet> implements IOaButtonSetService {

    @Autowired
    private OaButtonSetMapper oaButtonSetMapper;

    @Override
    public IPage<OaButtonSet> getPage(Integer pageNo, Integer pageSize, Integer id, List<OaButton> buttonId, List<OaProcActinst> taskDefKey) {
        int total = oaButtonSetMapper.queryButtonCount(id,buttonId,taskDefKey);
        List<OaButtonSet> buttonSetList = oaButtonSetMapper.queryButton(id,buttonId,taskDefKey,(pageNo-1)*pageSize,pageSize);
        IPage<OaButtonSet> pageList = new Page<>();
        pageList.setRecords(buttonSetList);
        pageList.setTotal(total);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;
    }

    @Override
    public OaButtonSet queryById(Integer id) {
        return oaButtonSetMapper.queryById(id);
    }

    @Override
    public OaButtonSet queryByTaskDefKeyAndBtnId( Map<String,Object> map) {
        return oaButtonSetMapper.queryByTaskDefKeyAndBtnId(map);
    }

    @Override
    @CacheEvict(value = CacheConstant.BUTTON_SET_CACHE, allEntries=true)
    public boolean updateOaButtonSetById(OaButtonSet oaButtonSet, String schemal) {
        return oaButtonSetMapper.updateOaButtonSetById(oaButtonSet);
    }

    @Override
    @CacheEvict(value = CacheConstant.BUTTON_SET_CACHE, allEntries=true)
    public void deleteOaButtonSetByID(String id, String schemal) {
        oaButtonSetMapper.deleteOaButtonSetByID(id);
    }

    @Override
    @CacheEvict(value = CacheConstant.BUTTON_SET_CACHE, allEntries=true)
    public void deleteOaButtonSetByProcId(String iProcButtonId, String schemal) {
        oaButtonSetMapper.deleteOaButtonSetByProcId(iProcButtonId);
    }

    @Override
    public List<OaButtonSet> findById(Integer id) {
        return oaButtonSetMapper.findById(id);
    }

    @Override
    @CacheEvict(value = CacheConstant.BUTTON_SET_CACHE, allEntries=true)
    public void insertoaButtonSet(OaButtonSet oaButtonSet, String schemal) {
        oaButtonSetMapper.insertoaButtonSet(oaButtonSet);
    }

    @Override
    public List<OaButtonSet> buttonSetList() {
        return oaButtonSetMapper.buttonSetList();
    }

    @Override
    public List<OaButtonSet> queryByProcButtonId(Integer id) {
        return oaButtonSetMapper.queryByProcButtonId(id);
    }

    @Override
    @Cacheable(value = CacheConstant.BUTTON_SET_CACHE)
    public List<OaButtonSet> findList(String schemal) {
        return oaButtonSetMapper.findList();
    }

    @Override
    public List<OaButtonSet> queryByProcButtonIdAndProcDefKey(Integer id, String procDefKey) {
        return oaButtonSetMapper.queryByProcButtonIdAndProcDefKey(id,procDefKey);
    }
}
