/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cfcc.modules.drawing.controller.editor.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.bpmn.behavior.ExclusiveGatewayActivityBehavior;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jni.Proc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tijs Rademakers
 */
@RestController
@RequestMapping("service")
public class ModelSaveRestResource1 implements ModelDataJsonConstants {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ModelSaveRestResource1.class);

    @Autowired
    private RepositoryService repositoryService;


    @RequestMapping(value = "/model/{modelId}/save", method = RequestMethod.PUT)
    public void saveModel(@PathVariable String modelId, @RequestParam String json_xml,
                          @RequestParam String svg_xml, @RequestParam String name, @RequestParam String description) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Model model = repositoryService.getModel(modelId);

            ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());

            modelJson.put(MODEL_NAME, name);
            modelJson.put(MODEL_DESCRIPTION, description);
            model.setMetaInfo(modelJson.toString());
            model.setName(name);

            repositoryService.saveModel(model);

            repositoryService.addModelEditorSource(model.getId(), json_xml.getBytes("utf-8"));

            InputStream svgStream = new ByteArrayInputStream(svg_xml.getBytes("utf-8"));
            TranscoderInput input = new TranscoderInput(svgStream);

            PNGTranscoder transcoder = new PNGTranscoder();
            // Setup output
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outStream);

            // Do the transformation
            transcoder.transcode(input, output);
            final byte[] result = outStream.toByteArray();
            repositoryService.addModelEditorSourceExtra(model.getId(), result);
            outStream.close();

            //获取流程图的字节数组对象--通过字节流进行输出图片
            //byte[] modelEditorSourceExtra = repositoryService.getModelEditorSourceExtra(modelId);

            //部署流程
            Model modelData = repositoryService.getModel(modelId);
            ObjectNode modelNode = (ObjectNode) objectMapper.readTree(repositoryService.getModelEditorSource(modelData.getId()));
            byte[] bpmnBytes = null;
            BpmnModel model2 = new BpmnJsonConverter().convertToBpmnModel(modelNode);
            List<Process> processes = model2.getProcesses();
            Process process = processes.get(0);
            Collection<FlowElement> flowElements = process.getFlowElements();
           //解决排他网关没有默认出口现象
            Map<String, ExclusiveGateway> exclusiveGatewayMap = new HashMap<>();
            Map<String, SequenceFlow> sequenceFlowHashMap = new HashMap<>();
            for (FlowElement flowElement : flowElements) {
                if (flowElement instanceof ExclusiveGateway){
                    ExclusiveGateway exclusiveGateway = (ExclusiveGateway) flowElement;
                    exclusiveGatewayMap.put(exclusiveGateway.getId(),exclusiveGateway);

                }
                if (flowElement instanceof  SequenceFlow){
                    SequenceFlow sequenceFlow= (SequenceFlow) flowElement;
                    String sourceRef = sequenceFlow.getSourceRef();
                    sequenceFlowHashMap.put(sourceRef,sequenceFlow);
                }
            }
            for (String key : exclusiveGatewayMap.keySet()) {
                ExclusiveGateway exclusiveGateway = exclusiveGatewayMap.get(key);
                SequenceFlow sequenceFlow = sequenceFlowHashMap.get(key);
                exclusiveGateway.setDefaultFlow(sequenceFlow.getId());
            }

            if (StringUtils.isBlank(process.getName())) process.setName(model.getName());
            if (StringUtils.isBlank(process.getId())) process.setName(model.getKey());
            if (process.getId()!=null && process.getId().equals("process")) process.setId(model.getKey());


            bpmnBytes = new BpmnXMLConverter().convertToXML(model2);


            String processName = modelData.getName() + ".bpmn";

            //部署流程
            Deployment deployment = repositoryService.createDeployment()
                    .name(modelData.getName())
                    .addString(processName, StringUtils.toEncodedString(bpmnBytes, Charset.forName("UTF-8")))
                    .deploy();

            //    更新model信息
            Model model1 = repositoryService.createModelQuery().modelId(modelId).singleResult();
            model1.setDeploymentId(deployment.getId());
            repositoryService.saveModel(model1);
        } catch (Exception e) {
            LOGGER.error("Error saving model", e);
            throw new ActivitiException("Error saving model", e);
        }

    }
}