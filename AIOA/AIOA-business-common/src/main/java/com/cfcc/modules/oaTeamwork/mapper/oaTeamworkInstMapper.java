package com.cfcc.modules.oaTeamwork.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.modules.oaTeamwork.entity.oaTeamworkInst;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 个人协同办公业务实例
 * @Author: jeecg-boot
 * @Date:   2020-01-02
 * @Version: V1.0
 */
public interface oaTeamworkInstMapper extends BaseMapper<oaTeamworkInst> {

    int count(oaTeamworkInst oaTeamworkInst);

    oaTeamworkInst findById(Integer iId);

    List<oaTeamworkInst> findPage(@Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize, @Param("oaTeamworkInst")oaTeamworkInst oaTeamworkInst);

    boolean updateByIid(oaTeamworkInst oaTeamworkInst);

    int Insert(oaTeamworkInst oaTeamworkInst);

    boolean deleteByid(String id);

    Integer findMax(Integer iteamworkId);

    List<oaTeamworkInst> findList( @Param("oaTeamworkInst")oaTeamworkInst oaTeamworkInst);

    List<Integer> findOrder(Integer iteamworkId);

    Integer getDataId(Integer iBusFunctionId,Integer iVersion);
}
