package com.cfcc.modules.oabutton.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.oabutton.entity.OaButtonSet;
import com.cfcc.modules.oabutton.entity.OaOpinionSet;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: 意见配置按钮
 * @Author: jeecg-boot
 * @Date:   2019-10-27
 * @Version: V1.0
 */
public interface OaOpinionSetMapper extends BaseMapper<OaOpinionSet> {

    int queryButtonCount(OaOpinionSet oaOpinionSet);

    List<OaOpinionSet> queryButton(@Param("opinionset")OaOpinionSet oaOpinionSet,  @Param("pageNo")Integer pageNo, @Param("pageSize")Integer pageSize);

    OaOpinionSet queryById(@Param("id") Integer id);

    List<OaOpinionSet> queryByType(@Param("type") String type,@Param("iProcOpinionId")  Integer  iProcOpinionId);

    OaOpinionSet queryByTaskDefKey(Map<String,Object> map);

    boolean updateOaButtonById(OaOpinionSet oaOpinionSet);

    void deleteOaOpinionSetByID(@Param("id") String id);

    List<OaOpinionSet> queryListByOpinionId(@Param("iProcOpinionId") Integer iProcOpinionId);

    List<OaOpinionSet> queryByOrderAndKey(Map<String,Object> map);

    void insertOaButtonSet(OaOpinionSet oaOpinionSet);

    void insertBatch(@Param("list") List<OaOpinionSet> oaOpinionSets);
}
