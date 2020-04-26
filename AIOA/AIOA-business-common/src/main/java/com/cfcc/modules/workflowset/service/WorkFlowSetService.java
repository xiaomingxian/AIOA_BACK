package com.cfcc.modules.workflowset.service;

import com.cfcc.modules.workflowset.entity.CopyMsg;

public interface WorkFlowSetService {
    void copy(CopyMsg copyMsg, String schemal)  throws Exception ;
}
