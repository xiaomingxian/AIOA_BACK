package com.cfcc.modules.oaBus.service;

import com.cfcc.modules.oaBus.entity.BusProcSet;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: 业务和流程关联配置表
 * @Author: jeecg-boot
 * @Date:   2019-11-05
 * @Version: V1.0
 */
public interface IBusProcSetService extends IService<BusProcSet> {

    List<BusProcSet> findList(String schema);

    BusProcSet getProcSet(int modelId, String functionId,Object i_fun_version);

    Map<String, Object> getProcSetNewTask(String parseInt, String functionId,String versioin);
//根据发布类按钮id查询
List<BusProcSet>  getProcSetByprocbuttonId(Map<String,Object> map);
}
