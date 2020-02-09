package com.cfcc.modules.oabutton.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.common.constant.CacheConstant;
import com.cfcc.modules.oabutton.entity.OaProcButton;
import com.cfcc.modules.oabutton.mapper.OaProcButtonMapper;
import com.cfcc.modules.oabutton.service.IOaProcButtonService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @Description: 发布类按钮管理
 * @Author: jeecg-boot
 * @Date:   2019-10-26
 * @Version: V1.0
 */
@Service
public class OaProcButtonServiceImpl extends ServiceImpl<OaProcButtonMapper, OaProcButton> implements IOaProcButtonService {

    @Autowired
    private OaProcButtonMapper oaProcButtonMapper;
    @Override
    public IPage<OaProcButton> getPage(Integer pageNo, Integer pageSize, OaProcButton oaProcButton) {

        int total = oaProcButtonMapper.queryProcButtonCount(oaProcButton);
        List<OaProcButton> procButtonList = oaProcButtonMapper.queryProcButton(oaProcButton,(pageNo-1)*pageSize,pageSize);
        IPage<OaProcButton> pageList = new Page<>();
        pageList.setRecords(procButtonList);
        pageList.setTotal(total);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;
    }

    @Override
    public OaProcButton queryById(Integer id) {
        return oaProcButtonMapper.queryById(id);
    }

    @Override
    public boolean updateProcOaButtonById(OaProcButton oaProcButton) {
        return oaProcButtonMapper.updateProcOaButtonById(oaProcButton);
    }

    @Override
    public void deleteProcOaButtonByID(String id) {
        oaProcButtonMapper.deleteProcOaButtonByID(id);
    }

    @Override
    public List<OaProcButton> procButtonList() {
        return oaProcButtonMapper.procButtonList();
    }

    @Override
    public List<OaProcButton> queryByKey(String key) {
        return oaProcButtonMapper.queryByKey(key);
    }

    @Override
    @Cacheable(value = CacheConstant.PROC_BUTTON_CACHE)
    public List<OaProcButton> findList() {

        return oaProcButtonMapper.findList();
    }

    @Override
    public List<OaProcButton> queryProcButtonByProc(String procKey) {


        return oaProcButtonMapper.queryProcButtonByProcDao(procKey);
    }

    /**
     *
     * @return
     */
    @Override
    public List<OaProcButton> queryProcButtonByProc() {
        return oaProcButtonMapper.queryProcButtonByProcDaoNull();
    }

    @Override
    public int insertProcOaButton(OaProcButton oaProcButton) {
        return oaProcButtonMapper.insertProcOaButton(oaProcButton);
    }

//根据配置名称查询条数
    @Override
    public int queryProcButtonBySButtonSetName(String sButtonSetName) {
        return oaProcButtonMapper.queryProcButtonBySButtonSetName(sButtonSetName);
    }

    @Override
    public List<OaProcButton> queryNoProcButtonByProc() {
        return oaProcButtonMapper.queryNoProcButtonByProcDao();
    }
}
