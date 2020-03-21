package com.cfcc.modules.workflow.service.impl;

import com.cfcc.config.activiti.AioaProcessDiagramGenerator;
import com.cfcc.config.activiti.WorkflowConstants;
import com.cfcc.modules.utils.ProcssUtil;
import com.cfcc.modules.workflow.service.ActPicService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ActPicServiceImpl implements ActPicService {

    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ProcessEngineConfiguration processEngineConfiguration;

    @Autowired
    private RuntimeService runtimeService;

    @Value("${jeecg.path.upload}")
    private String savePath;


    @Override
    public void queryProPlan(String processInstanceId, HttpServletResponse response) throws IOException {
        rwPic(processInstanceId, response, null);
    }

    @Override
    public void savePic(String processInstanceId, HttpServletResponse response, String dest) throws IOException {
        rwPic(processInstanceId, response, dest);
    }

    @Override
    public void queryProPlanFromLocal(String processInstanceId, String endTime, HttpServletResponse response) {
        String timePath = endTime.replaceAll("-", "/");
        String fullPath = savePath + "/activiti/" + timePath+"/" + processInstanceId + ".png";


        File file = new File(fullPath);

        BufferedInputStream buf = null;
        ServletOutputStream out = null;
        //使用输入读取缓冲流读取一个文件输入流
        try {
            buf = new BufferedInputStream(new FileInputStream(file));
            //利用response获取一个字节流输出对象
            out = response.getOutputStream();
            //定义个数组，由于读取缓冲流中的内容
            byte[] buffer = new byte[1024];
            //while循环一直读取缓冲流中的内容到输出的对象中
            while (buf.read(buffer) != -1) {
                out.write(buffer);
            }
            //写出到请求的地方
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (buf != null) buf.close();
                if (out != null) out.close();
            }catch (Exception e){

            }

        }
    }

    private void rwPic(String processInstanceId, HttpServletResponse response, String dest) throws IOException {
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity) repositoryService
                .getProcessDefinition(processInstance.getProcessDefinitionId());
        List<HistoricActivityInstance> highLightedActivitList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
        // 高亮环节id集合
        List<String> highLightedActivitis = new ArrayList<String>();
        // 高亮线路id集合
        List<String> highLightedFlows = ProcssUtil.getHighLightedFlows(definitionEntity, highLightedActivitList);
        for (HistoricActivityInstance tempActivity : highLightedActivitList) {
            String activityId = tempActivity.getActivityId();
            highLightedActivitis.add(activityId);
        }

        Set<String> currIds = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).list()
                .stream().map(e -> e.getActivityId()).collect(Collectors.toSet());

        AioaProcessDiagramGenerator diagramGenerator = (AioaProcessDiagramGenerator) processEngineConfiguration
                .getProcessDiagramGenerator();
        InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis,
                highLightedFlows, "宋体", "宋体", "宋体",
                null, 1.0, new Color[]{WorkflowConstants.COLOR_NORMAL, WorkflowConstants.COLOR_CURRENT}, currIds);
        // 输出资源内容到相应对象
        byte[] b = new byte[1024];
        int len;
        if (StringUtils.isBlank(dest)) {
            while ((len = imageStream.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
        } else {
            FileOutputStream saveos = new FileOutputStream(dest);
            while ((len = imageStream.read(b, 0, 1024)) != -1) {
                saveos.write(b, 0, len);
                saveos.flush();
            }
        }

    }


}
