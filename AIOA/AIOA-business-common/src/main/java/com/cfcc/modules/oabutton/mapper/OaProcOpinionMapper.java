package com.cfcc.modules.oabutton.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.oabutton.entity.OaProcOpinion;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 意见配置按钮
 * @Author: jeecg-boot
 * @Date: 2019-10-27
 * @Version: V1.0
 */
public interface OaProcOpinionMapper extends BaseMapper<OaProcOpinion> {

    int queryButtonCount(OaProcOpinion oaProcOpinion);

    List<OaProcOpinion> queryButton(@Param("procopinion") OaProcOpinion oaProcOpinion, @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);

    OaProcOpinion queryById(@Param("id") Integer id);

    boolean updateOaProcOpinionById(OaProcOpinion oaProcOpinion);

    void deleteOaProcOpinionByID(@Param("id") String id);

    int queryBysProcOpinionName(@Param("sProcOpinionName") String sProcOpinionName);

    int insertOaProcOpinion(OaProcOpinion oaProcOpinion);

    @Select("SELECT * FROM `oa_proc_opinion` where PROC_DEF_KEY_=#{key};")
    List<OaProcOpinion> queryByKey(String key);
}
