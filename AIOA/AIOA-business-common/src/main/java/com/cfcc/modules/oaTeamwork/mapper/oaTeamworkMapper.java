package com.cfcc.modules.oaTeamwork.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.oaTeamwork.entity.oaTeamwork;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 个人协同办公业务配置分类
 * @Author: jeecg-boot
 * @Date:   2019-12-30
 * @Version: V1.0
 */
public interface oaTeamworkMapper extends BaseMapper<oaTeamwork> {

    boolean deleteByIid(String id);

    oaTeamwork findById(Integer iId);

    List<oaTeamwork> findPage(@Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize, @Param("oaTeamwork")oaTeamwork oaTeamwork);

    int count(oaTeamwork oaTeamwork);

    void updateByIid(oaTeamwork oaTeamwork);

    int Insert(oaTeamwork oaTeamwork);

    List<oaTeamwork> findTeamworkName(String id);

    oaTeamwork selectByName(String teamworkName);

    String getfirstModel(Integer iTeamworkId);

    String getTitle(String busdata, Integer iorder,Integer busdataId);

    String getTitle1(String busdata, Integer iorder,Integer iId,Integer itemworkId);

    String getBusData(Integer iTeamworkId, int iorder,Integer id);

    String getBusData1(Integer iTeamworkId,Integer id);

    String getBusData2(Integer iTeamworkId, Integer iOrder);
}
