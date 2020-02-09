package com.cfcc.modules.oabutton.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.oabutton.entity.OaProcButton;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 发布类按钮管理
 * @Author: jeecg-boot
 * @Date:   2019-10-26
 * @Version: V1.0
 */
public interface OaProcButtonMapper extends BaseMapper<OaProcButton> {

    int queryProcButtonCount(OaProcButton oaProcButton);

    List<OaProcButton> queryProcButton(@Param("procbutton")OaProcButton oaProcButton, @Param("pageNo")Integer pageNo, @Param("pageSize")Integer pageSize);

    boolean updateProcOaButtonById(OaProcButton oaProcButton);

    OaProcButton queryById(@Param("id") Integer id);

    void deleteProcOaButtonByID(@Param("id")String id);

    List<OaProcButton> procButtonList();

    List<OaProcButton> queryByKey(@Param("key") String key);

    List<OaProcButton> findList();

    List<OaProcButton> queryProcButtonByProcDao(@Param("procKey")String procKey);

    List<OaProcButton> queryProcButtonByProcDaoNull();

    int insertProcOaButton(OaProcButton oaProcButton);

    int queryProcButtonBySButtonSetName(@Param("sButtonSetName") String sButtonSetName);
    @Select("select * from oa_proc_button c where c.proc_def_key is null or c.proc_def_key = ''")
    List<OaProcButton> queryNoProcButtonByProcDao();
}
