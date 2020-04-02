package com.cfcc.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.modules.system.entity.SysDictItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
public interface ISysDictItemService extends IService<SysDictItem> {

    public List<SysDictItem> selectItemsByMainId(String mainId);

    void saveDictAndDeparts(SysDictItem sysDictItem, List<String> departs);

    void queryDeparts(IPage<SysDictItem> pageList);

    void deleteDepartAbout(String id);

    boolean updateByIdAndDeparts(SysDictItem sysDictItem);

    String getDictItemText(String dictKey, String itemValue);

    Boolean deleteDictItemByDictID(String id);
}
