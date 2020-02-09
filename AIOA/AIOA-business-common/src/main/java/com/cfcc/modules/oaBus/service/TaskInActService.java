package com.cfcc.modules.oaBus.service;

import com.cfcc.modules.workflow.vo.TaskInfoVO;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface TaskInActService {
    void doTask(TaskInfoVO taskInfoVO, HttpServletRequest request);

    void doTaskMore(List<TaskInfoVO> taskInfoVOs);

    void doAddUsers(ArrayList<TaskInfoVO> taskInfoVOS);
}
