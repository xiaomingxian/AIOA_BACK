package com.cfcc.modules.oabutton.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.modules.oabutton.entity.OaButton;
import com.cfcc.modules.oabutton.mapper.OaButtonMapper;
import com.cfcc.modules.oabutton.service.IOaButtonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 按钮管理
 * @Author: jeecg-boot
 * @Date:   2019-10-24
 * @Version: V1.0
 */
@Service
@Transactional
public class OaButtonServiceImpl extends ServiceImpl<OaButtonMapper, OaButton> implements IOaButtonService {

    @Autowired
    private OaButtonMapper oaButtonMapper;

    @Override
    public IPage<OaButton> getPage(Integer pageNo, Integer pageSize, OaButton oaButton) {

        int total = oaButtonMapper.queryButtonCount(oaButton);
        List<OaButton> buttonList = oaButtonMapper.queryButton(oaButton,(pageNo-1)*pageSize,pageSize);
        for (int i = 0; i < buttonList.size(); i++) {
            if (buttonList.get(i).getSMethod()!=null){
                buttonList.get(i).setSBtnValue(buttonList.get(i).getSMethod());
            }
        }
        IPage<OaButton> pageList = new Page<>();
        pageList.setRecords(buttonList);
        pageList.setTotal(total);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;
    }

    @Override
    public OaButton queryById(Integer id,String sBtnName) {
        return oaButtonMapper.queryById(id,sBtnName);
    }

    @Override
    public void deleteOaButtonByID(String id) {
        oaButtonMapper.deleteOaButtonByID(id);
    }

    @Override
    public boolean updateOaButtonById(OaButton oaButton) {
        return oaButtonMapper.updateOaButtonById(oaButton);
    }

    @Override
    public void insertoaButton(OaButton oaButton) {
        oaButtonMapper.insertoaButton(oaButton);
    }

    @Override
    public List<OaButton> buttonList() {
        return oaButtonMapper.buttonList();
    }

    @Override
    public List<OaButton> findList() {
        return oaButtonMapper.findList();
    }
}
