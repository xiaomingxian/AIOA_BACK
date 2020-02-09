package com.cfcc.modules.workflow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.modules.workflow.mapper.OaProcActinstMapper;
import com.cfcc.modules.workflow.pojo.OaProcActinst;
import com.cfcc.modules.workflow.service.IoaProcActinstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description: 流程节点配置
 * @Author: jeecg-boot
 * @Date: 2019-11-01
 * @Version: V1.0
 */
@Service
@Transactional
public class OaProcActinstServiceImpl extends ServiceImpl<OaProcActinstMapper, OaProcActinst> implements IoaProcActinstService {

    @Autowired
    private OaProcActinstMapper oaProcActinstMapper;
    //移交 委托 重置


    @Override
    public Map<String, Map<String, Object>> queryActs(String key, List<String> taskIds) {
        return oaProcActinstMapper.queryActs(key, taskIds);
    }

    @Override
    public Boolean isDeptFinish(String key, String taskDefKey) {

        return oaProcActinstMapper.isDeptFinish(key, taskDefKey);
    }

    @Override
    public Boolean isExist(OaProcActinst oaProcActinst) {
        return oaProcActinstMapper.isExist(oaProcActinst);
    }

    @Override
    public long selectCount(OaProcActinst oaProcActinst) {
        return oaProcActinstMapper.selectCountMy(oaProcActinst);
    }

    @Override
    public List<OaProcActinst> selectData(OaProcActinst oaProcActinst, Integer pageNo, Integer pageSize) {
        return oaProcActinstMapper.selectData(oaProcActinst, (pageNo - 1) * pageSize, pageSize);
    }

}
