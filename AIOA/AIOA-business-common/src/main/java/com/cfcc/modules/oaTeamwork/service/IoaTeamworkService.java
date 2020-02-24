package com.cfcc.modules.oaTeamwork.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.oaTeamwork.entity.oaTeamwork;

import java.util.List;

/**
 * @Description: 个人协同办公业务配置分类
 * @Author: jeecg-boot
 * @Date:   2019-12-30
 * @Version: V1.0
 */
public interface IoaTeamworkService extends IService<oaTeamwork> {

    boolean deleteById(String id);

    oaTeamwork findById(Integer iId);

    IPage<oaTeamwork> findPage(Integer pageNo, Integer pageSize, oaTeamwork oaTeamwork);

    boolean updateByIid(oaTeamwork oaTeamwork);

    int Insert(oaTeamwork oaTeamwork);

    List<oaTeamwork> findTeamworkName(String id);

}
