package com.cfcc.modules.oaTeamwork.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.modules.oaTeamwork.entity.oaTeamwork;
import com.cfcc.modules.oaTeamwork.entity.oaTeamworkSet;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 个人协同办公业务配置明细
 * @Author: jeecg-boot
 * @Date:   2019-12-30
 * @Version: V1.0
 */
public interface oaTeamworkSetMapper extends BaseMapper<oaTeamworkSet> {

    boolean deleteByIid(String id);

    oaTeamworkSet findById(Integer iId);

    List<oaTeamworkSet> findPage(@Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize, @Param("oaTeamworkSet")oaTeamworkSet oaTeamworkSet);

    int count(oaTeamworkSet oaTeamworkSet);

    boolean updateByIid(oaTeamworkSet oaTeamworkSet);

    int Insert(oaTeamworkSet oaTeamworkSet);

    int findMax(Integer iTeamworkId);

    List<oaTeamworkSet> findTeamworkSet(@Param("iTeamworkId")Integer iTeamworkId);

    List<Integer> findorder();

    List<oaTeamworkSet> findListByteamworkId(@Param("TeamworkName")String TeamworkName);

    List<oaTeamworkSet>  queryList(@Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize, @Param("oaTeamworkSet")oaTeamworkSet oaTeamworkSet);


    List<String> findByTeamworkId(Integer iTeamworkId);

    List<String> findByFunctionId(Integer iTeamworkId);

    List<String> findByModelId(Integer iTeamworkId);

    oaTeamworkSet select(oaTeamworkSet oaTeamworkSet);

    Integer findMaxId();

}
