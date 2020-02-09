package com.cfcc.modules.papertitle.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.papertitle.entity.PaperTitleSetting;

import java.util.List;

/**
 * @Description: 稿纸头配置
 * @Author: jeecg-boot
 * @Date:   2019-10-14
 * @Version: V1.0
 */
public interface IPaperTitleSettingService extends IService<PaperTitleSetting> {
    /**
     * 根据id查询
     * @param id
     * @return
     */
    PaperTitleSetting queryById(Integer id);

    /**
     * 根据id修改
     * @param paperTitleSetting
     * @return
     */
    boolean updatePaperById(PaperTitleSetting paperTitleSetting);
    /**
     * 根据iid删除
     * @param id
     * @return
     */
    boolean deletePaperTitleByIID(String id);

    /**
     * 分页查询列表
     * @param paperTitleSetting
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<PaperTitleSetting> queryPaperList(PaperTitleSetting paperTitleSetting,Integer pageNo,Integer pageSize);

    /**
     * 稿纸头下拉选列表
     * @return
     */
    List<PaperTitleSetting> paperTitleList();

    /**
     * 修改文头
     * @param paperTitleSetting
     * @return
     */
    boolean updateTitleById(PaperTitleSetting paperTitleSetting);
}
