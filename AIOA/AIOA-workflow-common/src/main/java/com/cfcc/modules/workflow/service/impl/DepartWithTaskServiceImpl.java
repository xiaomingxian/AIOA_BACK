package com.cfcc.modules.workflow.service.impl;

import com.cfcc.modules.workflow.mapper.DepartWithTaskMapper;
import com.cfcc.modules.workflow.pojo.TaskWithDepts;
import com.cfcc.modules.workflow.service.DepartWithTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class DepartWithTaskServiceImpl implements DepartWithTaskService {

    @Autowired
    private DepartWithTaskMapper departWithTaskMapper;

    @Override
    public void save(String procInstId,TaskWithDepts taskWithDepts) {
        departWithTaskMapper.save(procInstId,taskWithDepts);

    }
}
