package com.cfcc.modules.oaTeamwork.service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.oaTeamwork.entity.oaTeamwork;
import com.cfcc.modules.oaTeamwork.entity.oaTeamworkInst;

import java.util.List;

/**
 * @Description: 个人协同办公业务实例
 * @Author: jeecg-boot
 * @Date:   2020-01-02
 * @Version: V1.0
 */
public interface IoaTeamworkInstService extends IService<oaTeamworkInst> {

    boolean deleteById(String id);

    oaTeamworkInst findById(Integer iId);

    IPage<oaTeamworkInst> findPage(Integer pageNo, Integer pageSize, oaTeamworkInst oaTeamworkInst);

    boolean updateByIid(oaTeamworkInst oaTeamworkInst);

    int Insert(oaTeamworkInst oaTeamworkInst);

    Integer findMax(Integer iteamworkId);

    List<oaTeamworkInst> findList(oaTeamworkInst oaTeamworkInst);

    List<Integer> findOrder(Integer iteamworkId);

    oaTeamworkInst setUp(Integer iOrder, Integer iVersion);
}
