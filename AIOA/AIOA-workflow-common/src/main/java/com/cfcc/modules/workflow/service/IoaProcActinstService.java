package com.cfcc.modules.workflow.service;

import com.cfcc.modules.workflow.pojo.OaProcActinst;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.workflow.vo.TaskInfoVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 流程节点配置
 * @Author: jeecg-boot
 * @Date: 2019-11-01
 * @Version: V1.0
 */
public interface IoaProcActinstService extends IService<OaProcActinst> {

    Map<String, Map<String,Object>> queryActs(String key, List<String> taskIds);

    Boolean isDeptFinish(String key, String taskDefKey);

    Boolean isExist(OaProcActinst oaProcActinst);

    long selectCount( OaProcActinst oaProcActinst);

    List<OaProcActinst> selectData( OaProcActinst oaProcActinst,Integer pageNo,Integer pageSize);
}
