package com.cfcc.modules.oaBus.service.impl;

import com.cfcc.common.constant.CacheConstant;
import com.cfcc.modules.oaBus.entity.BusProcSet;
import com.cfcc.modules.oaBus.mapper.BusProcSetMapper;
import com.cfcc.modules.oaBus.service.IBusProcSetService;
import com.sun.corba.se.spi.ior.ObjectKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description: 业务和流程关联配置表
 * @Author: jeecg-boot
 * @Date:   2019-11-05
 * @Version: V1.0
 */
@Service
@Transactional
public class BusProcSetServiceImpl extends ServiceImpl<BusProcSetMapper, BusProcSet> implements IBusProcSetService {
  @Autowired
  private BusProcSetMapper busProcSetMapper;

    @Cacheable(value = CacheConstant.FUNCTION_CACHE)
    public List<BusProcSet> findList(String schema) {
        return busProcSetMapper.findList();
    }

  @Override
  public BusProcSet getProcSet(int modelId, String functionId, Object i_fun_version) {
    return busProcSetMapper.getProSetIdDao(modelId,functionId,i_fun_version);
  }

  @Override
  public Map<String, Object> getProcSetNewTask(String modelId, String functionId,String versioin) {
    return busProcSetMapper.getProSetIdDaoNewTask(modelId,functionId,versioin);
  }

  //根据发布类按钮id查询
  @Override
  public  List<BusProcSet>  getProcSetByprocbuttonId(Map<String,Object> map) {
    return busProcSetMapper.getProcSetByprocbuttonId(map);
  }
}
