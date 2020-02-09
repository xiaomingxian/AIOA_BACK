package com.cfcc.modules.oabutton.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.modules.oabutton.entity.OaButtonSet;
import com.cfcc.modules.oabutton.entity.OaOpinionSet;
import com.cfcc.modules.oabutton.mapper.OaOpinionSetMapper;
import com.cfcc.modules.oabutton.service.IOaOpinionSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Description: 意见配置按钮
 * @Author: jeecg-boot
 * @Date:   2019-10-27
 * @Version: V1.0
 */
@Service
public class OaOpinionSetServiceImpl extends ServiceImpl<OaOpinionSetMapper, OaOpinionSet> implements IOaOpinionSetService {

    @Autowired
    private OaOpinionSetMapper oaOpinionSetMapper;
    @Override
    public IPage<OaOpinionSet> getPage(Integer pageNo, Integer pageSize, OaOpinionSet oaOpinionSet) {
        int total = oaOpinionSetMapper.queryButtonCount(oaOpinionSet);
        List<OaOpinionSet> buttonList = oaOpinionSetMapper.queryButton(oaOpinionSet,(pageNo-1)*pageSize,pageSize);
        IPage<OaOpinionSet> pageList = new Page<>();
        pageList.setRecords(buttonList);
        pageList.setTotal(total);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;
    }

    @Override
    public OaOpinionSet queryById(Integer id) {
        return oaOpinionSetMapper.queryById(id);
    }

    @Override
    public OaOpinionSet queryByTaskKey(Map<String,Object> map) {
        return oaOpinionSetMapper.queryByTaskDefKey(map);
    }

    @Override
    public boolean updateOaButtonById(OaOpinionSet oaOpinionSet) {
        return oaOpinionSetMapper.updateOaButtonById(oaOpinionSet);
    }

    @Override
    public void deleteOaOpinionSetByID(String id) {
        oaOpinionSetMapper.deleteOaOpinionSetByID(id);
    }

    @Override
    public List<OaOpinionSet> queryListByOpinionId(Integer iProcOpinionId) {
        return oaOpinionSetMapper.queryListByOpinionId(iProcOpinionId);
    }


    @Override
    public List<OaOpinionSet> queryByOrderAndKey(Map<String, Object> map) {
        return oaOpinionSetMapper.queryByOrderAndKey(map);
    }

    @Override
    public void insertOaButtonSet(OaOpinionSet oaOpinionSet) {
        oaOpinionSetMapper.insertOaButtonSet(oaOpinionSet);
    }

    @Override
    public List<OaOpinionSet> queryByType(String type,  Integer  iProcOpinionId) {
        return oaOpinionSetMapper.queryByType(type,iProcOpinionId);
    }
}
