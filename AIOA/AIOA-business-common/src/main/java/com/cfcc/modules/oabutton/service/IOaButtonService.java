package com.cfcc.modules.oabutton.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.oabutton.entity.OaButton;

import java.util.List;

/**
 * @Description: 按钮管理
 * @Author: jeecg-boot
 * @Date:   2019-10-24
 * @Version: V1.0
 */
public interface IOaButtonService extends IService<OaButton> {


    /**
     * 重写查询方法，按条件查询
     * @param pageNo
     * @param pageSize
     * @param oaButton
     * @return
     */
    IPage<OaButton> getPage(Integer pageNo, Integer pageSize, OaButton oaButton);
    //通过id查询
    OaButton queryById(Integer id);
    //通过id删除
    void deleteOaButtonByID(String id);

    //编辑
    boolean updateOaButtonById(OaButton oaButton);

    void insertoaButton(OaButton oaButton);

    List<OaButton> buttonList();

    List<OaButton> findList();
}
