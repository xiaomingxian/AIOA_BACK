package com.cfcc.modules.workflow.service.impl;

import com.cfcc.modules.workflow.mapper.DepartWithTaskMapper;
import com.cfcc.modules.workflow.pojo.TaskProcess;
import com.cfcc.modules.workflow.pojo.TaskWithDepts;
import com.cfcc.modules.workflow.service.DepartWithTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
@Transactional
public class DepartWithTaskServiceImpl implements DepartWithTaskService {

    @Autowired
    private DepartWithTaskMapper departWithTaskMapper;

    @Override
    public void save(String procInstId, TaskWithDepts taskWithDepts) {
        departWithTaskMapper.save(procInstId, taskWithDepts);

    }

    @Override
    public List<Map<String, Integer>> deptDone(List<String> fids) {
        List<String> procInsntIds=null;
        if (fids.size() > 0) {
             procInsntIds = departWithTaskMapper.queryProcInstIdsByFunction(fids);
        }

        return departWithTaskMapper.deptDone(procInsntIds);
    }

    @Override
    public List<TaskProcess> taskProcess(List<String> fids) {
        List<String> procInsntIds=null;
        if (fids.size() > 0) {
            procInsntIds = departWithTaskMapper.queryProcInstIdsByFunction(fids);
        }

        return departWithTaskMapper.taskProcess(procInsntIds);
    }
}
