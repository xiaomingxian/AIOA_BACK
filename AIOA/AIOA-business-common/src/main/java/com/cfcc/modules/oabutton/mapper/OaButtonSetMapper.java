package com.cfcc.modules.oabutton.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.oabutton.entity.OaButton;
import com.cfcc.modules.oabutton.entity.OaButtonSet;
import com.cfcc.modules.workflow.pojo.OaProcActinst;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: 发布类按钮描述
 * @Author: jeecg-boot
 * @Date: 2019-10-26
 * @Version: V1.0
 */
public interface OaButtonSetMapper extends BaseMapper<OaButtonSet> {

    int queryButtonCount(@Param("id") Integer id,@Param("buttonId") List<OaButton> buttonId,@Param("taskDefKey") List<OaProcActinst> taskDefKey);

    List<OaButtonSet> queryButton(@Param("id") Integer id,@Param("buttonId") List<OaButton> buttonId,@Param("taskDefKey") List<OaProcActinst> taskDefKey, @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);

    OaButtonSet queryById(@Param("id") Integer id);

    OaButtonSet queryByTaskDefKeyAndBtnId(Map<String, Object> map);

    boolean updateOaButtonSetById(OaButtonSet oaButtonSet);

    void deleteOaButtonSetByID(@Param("id") String id);

    void deleteOaButtonSetByProcId(@Param("iProcButtonId") String iProcButtonId);

    List<OaButtonSet> findById(@Param("id") Integer id);

    void insertoaButtonSet(OaButtonSet oaButtonSet);

    List<OaButtonSet> buttonSetList();

    List<OaButtonSet> queryByProcButtonId(@Param("id") Integer id);

    List<OaButtonSet> queryByButtonId(@Param("id") Integer id);

    List<OaButtonSet> findList();

    List<OaButtonSet> queryByProcButtonIdAndProcDefKey(@Param("id") Integer id, @Param("procDefKey") String procDefKey);

    void insertBatch(@Param("list") List<OaButtonSet> oaButtonSets);
}
