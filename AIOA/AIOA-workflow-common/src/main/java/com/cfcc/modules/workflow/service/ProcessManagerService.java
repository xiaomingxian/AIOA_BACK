package com.cfcc.modules.workflow.service;

import com.cfcc.common.api.vo.Result;
import com.cfcc.modules.workflow.pojo.Activity;
import com.cfcc.modules.workflow.pojo.ProcessDefinitionJsonAble;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProcessManagerService {


    void deploy(MultipartFile[] files,String schema);

    Result processQuery(ProcessDefinition vo, Integer pageNo, Integer pageSize);

    Result processDel(String[] ids, boolean cascade,String schemal);

    Result activeProcess(ProcessDefinition vo,String schemal);

    Result disactiveProcess(ProcessDefinition vo,String schemal);

    ProcessInstance start(String key, String bussinessKey, Map<String, Object> vars);

    void loadByDeployment(String processDefinitionId, String resourceType, HttpServletResponse response);

    Map<String, ProcessDefinitionJsonAble> defKv(String schema);

    String loadByDeploymentXml(String processDefinitionId) throws IOException;

    void saveXml(String name, String xml);

    List<Activity> showBackAct(String processDefinitionId, String processInstanceId, String taskDefinitionKey);

    List<Activity> actsList(String processDefinitionId, boolean haveSub);

    List<Activity> actsListPic(String processDefinitionId, String key, boolean b);

    String queryActNameByKey(String key);

    public void getAllAct(List<ActivityImpl> activities, List<ActivityImpl> actsAll);


    Result lastVersionProc(String key);

    List<String> queryTaskDefkeys(String key);
}
