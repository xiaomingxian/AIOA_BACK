package com.cfcc.modules.oabutton.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.oabutton.entity.OaProcButton;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 发布类按钮管理
 * @Author: jeecg-boot
 * @Date:   2019-10-26
 * @Version: V1.0
 */
public interface IOaProcButtonService extends IService<OaProcButton> {

    /**
     * 重写查询方法，按条件查询
     * @param pageNo
     * @param pageSize
     * @param oaProcButton
     * @return
     */
    IPage<OaProcButton> getPage(Integer pageNo, Integer pageSize, OaProcButton oaProcButton);
    //通过id查询
    OaProcButton queryById(Integer id);
    //编辑
    boolean updateProcOaButtonById(OaProcButton oaProcButton);
    //通过id删除
    void deleteProcOaButtonByID(String id);

    List<OaProcButton> procButtonList();

    List<OaProcButton> queryByKey(String key);

    List<OaProcButton> findList();


    List<OaProcButton> queryProcButtonByProc(String procKey);

    List<OaProcButton> queryProcButtonByProc();

    int insertProcOaButton(OaProcButton oaProcButton);

    int queryProcButtonBySButtonSetName(String sButtonSetName);

    List<OaProcButton> queryNoProcButtonByProc();

}
