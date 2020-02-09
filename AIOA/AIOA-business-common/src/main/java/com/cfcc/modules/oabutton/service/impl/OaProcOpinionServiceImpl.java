package com.cfcc.modules.oabutton.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.modules.oabutton.entity.OaProcOpinion;
import com.cfcc.modules.oabutton.mapper.OaProcOpinionMapper;
import com.cfcc.modules.oabutton.service.IOaProcOpinionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 意见配置按钮
 * @Author: jeecg-boot
 * @Date:   2019-10-27
 * @Version: V1.0
 */
@Service
public class OaProcOpinionServiceImpl extends ServiceImpl<OaProcOpinionMapper, OaProcOpinion> implements IOaProcOpinionService {

    @Autowired
    private OaProcOpinionMapper oaProcOpinionMapper;
    @Override
    public IPage<OaProcOpinion> getPage(Integer pageNo, Integer pageSize, OaProcOpinion oaProcOpinion) {
        int total = oaProcOpinionMapper.queryButtonCount(oaProcOpinion);
        List<OaProcOpinion> buttonList = oaProcOpinionMapper.queryButton(oaProcOpinion,(pageNo-1)*pageSize,pageSize);
        IPage<OaProcOpinion> pageList = new Page<>();
        pageList.setRecords(buttonList);
        pageList.setTotal(total);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;
    }

    @Override
    public OaProcOpinion queryById(Integer id) {
        return oaProcOpinionMapper.queryById(id);
    }

    @Override
    public boolean updateOaProcOpinionById(OaProcOpinion oaProcOpinion) {
        return oaProcOpinionMapper.updateOaProcOpinionById(oaProcOpinion);
    }

    @Override
    public void deleteOaProcOpinionByID(String id) {
        oaProcOpinionMapper.deleteOaProcOpinionByID(id);
    }

    @Override
    public int queryBysProcOpinionName(String sProcOpinionName) {
        return oaProcOpinionMapper.queryBysProcOpinionName(sProcOpinionName);
    }

    @Override
    public List<OaProcOpinion> queryByKey(String key) {
        return oaProcOpinionMapper.queryByKey(key);
    }

    @Override
    public int insertOaProcOpinion(OaProcOpinion oaProcOpinion) {
        return oaProcOpinionMapper.insertOaProcOpinion(oaProcOpinion);
    }
}

