package com.cfcc.modules.oabutton.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.oabutton.entity.OaButton;
import com.cfcc.modules.oabutton.entity.OaButtonSet;
import com.cfcc.modules.workflow.pojo.OaProcActinst;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: 发布类按钮描述
 * @Author: jeecg-boot
 * @Date:   2019-10-26
 * @Version: V1.0
 */
public interface IOaButtonSetService extends IService<OaButtonSet> {

    /**
     * 重写查询方法，按条件查询
     * @param pageNo
     * @param pageSize
     * @param id
     * @return
     */
    IPage<OaButtonSet> getPage(Integer pageNo, Integer pageSize, Integer id, List<OaButton> buttonId, List<OaProcActinst> taskDefKey);
    //通过id查询
    OaButtonSet queryById(Integer iId);
    //通过任务KEY和按钮ID查询
    OaButtonSet queryByTaskDefKeyAndBtnId( Map<String,Object> map);

    //编辑
    boolean updateOaButtonSetById(OaButtonSet oaButtonSet, String schemal);
    //通过id删除
    void deleteOaButtonSetByID(String id, String schemal);
    //通过发布类id删除
    void deleteOaButtonSetByProcId(String iProcButtonId, String schemal);

    List<OaButtonSet> findById(Integer id);

    void insertoaButtonSet(OaButtonSet oaButtonSet, String schemal);

    List<OaButtonSet> buttonSetList();

    List<OaButtonSet> queryByProcButtonId(Integer id);

    List<OaButtonSet> queryByButtonId(Integer id);

    List<OaButtonSet> findList(String schemal);

    List<OaButtonSet> queryByProcButtonIdAndProcDefKey(Integer id, String procDefKey);


}
