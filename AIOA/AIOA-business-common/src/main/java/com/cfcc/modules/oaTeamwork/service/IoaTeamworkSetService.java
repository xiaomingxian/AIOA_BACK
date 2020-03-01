package com.cfcc.modules.oaTeamwork.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.oaTeamwork.entity.oaTeamwork;
import com.cfcc.modules.oaTeamwork.entity.oaTeamworkSet;

import java.util.List;

/**
 * @Description: 个人协同办公业务配置明细
 * @Author: jeecg-boot
 * @Date:   2019-12-30
 * @Version: V1.0
 */
public interface IoaTeamworkSetService extends IService<oaTeamworkSet> {

    boolean deleteById(String id);

    IPage<oaTeamworkSet> findPage(Integer pageNo, Integer pageSize, oaTeamworkSet oaTeamworkSet);

    boolean updateByIid(oaTeamworkSet oaTeamworkSet);

    void Insert(oaTeamworkSet oaTeamworkSet);

    oaTeamworkSet findByid(Integer iId);

    int insert(oaTeamworkSet oaTeamworkSet);

    int findMax(Integer iTeamworkId);

    List<oaTeamworkSet> findTeamworkSet(Integer iTeamworkId);

    List<Integer> findorder();

    List<oaTeamworkSet> findListByteamworkId(String TeamworkName);

    IPage<oaTeamworkSet> queryList(Integer pageNo, Integer pageSize, oaTeamworkSet oaTeamworkSet);

    oaTeamworkSet select(oaTeamworkSet oaTeamworkSet);

    Integer findMaxId();

}
