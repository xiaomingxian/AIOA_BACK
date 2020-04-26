package com.cfcc.modules.workflowset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cfcc.common.constant.CacheConstant;
import com.cfcc.common.exception.AIOAException;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.modules.oabutton.entity.OaButtonSet;
import com.cfcc.modules.oabutton.entity.OaOpinionSet;
import com.cfcc.modules.oabutton.entity.OaProcButton;
import com.cfcc.modules.oabutton.entity.OaProcOpinion;
import com.cfcc.modules.oabutton.mapper.OaButtonSetMapper;
import com.cfcc.modules.oabutton.mapper.OaOpinionSetMapper;
import com.cfcc.modules.oabutton.mapper.OaProcButtonMapper;
import com.cfcc.modules.oabutton.mapper.OaProcOpinionMapper;
import com.cfcc.modules.workflow.mapper.OaProcActinstMapper;
import com.cfcc.modules.workflow.pojo.OaProcActinst;
import com.cfcc.modules.workflow.service.ProcessManagerService;
import com.cfcc.modules.workflowset.entity.CopyMsg;
import com.cfcc.modules.workflowset.service.WorkFlowSetService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
@Slf4j
public class WorkFlowSetServiceImpl implements WorkFlowSetService {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ProcessManagerService processManagerService;

    @Autowired
    private OaButtonSetMapper oaButtonSetMapper;

    @Autowired
    private OaOpinionSetMapper oaOpinionSetMapper;

    @Autowired
    private OaProcOpinionMapper oaProcOpinionMapper;

    @Autowired
    private OaProcButtonMapper oaProcButtonMapper;

    @Autowired
    private OaProcActinstMapper oaProcActinstMapper;


    @Override
    @CacheEvict(value = "defKv" , allEntries = true)
    public void copy(CopyMsg copyMsg,String schemal) throws Exception {
        String copyKey = copyMsg.getCopyKey();
        String copyName = copyMsg.getCopyName();
        String sourceDefId = copyMsg.getSourceDefId();
        String descriptionNew = copyMsg.getDescription();

        List<ProcessDefinition> listKey = repositoryService.createProcessDefinitionQuery().processDefinitionKey(copyKey).list();
        if (listKey.size() > 0) throw new AIOAException("该流程key已经存在，请修改");
        List<ProcessDefinition> listName = repositoryService.createProcessDefinitionQuery().processDefinitionName(copyName).list();
        if (listName.size() > 0) throw new AIOAException("该流程名称已经存在，请修改");

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(sourceDefId).latestVersion().singleResult();

        String name = processDefinition.getName();
        String key = processDefinition.getKey();
        String description = processDefinition.getDescription();

        String xml = processManagerService.loadByDeploymentXml(sourceDefId);

        String copyXml = xml.replaceAll("id=\"" + key + "\"", "id=\"" + copyKey + "\"")
                .replaceAll("name=\"" + name + "\"", "name=\"" + copyName + "\"")
                .replaceAll("<documentation>"+description+"</documentation>","<documentation>"+descriptionNew+"</documentation>");
        if (!xml.contains("<documentation>")){
            StringBuffer sb = new StringBuffer();
            String[] split = copyXml.split("\n");
            for (String oneLine : split) {
                sb.append(oneLine+"\n");
                if (oneLine.contains("<process")){
                    sb.append("<documentation>"+descriptionNew+"</documentation>\n");
                }
            }
            copyXml=sb.toString();
        }
        //0 发布流程
        processManagerService.saveXml(copyKey, copyXml);
        //5张配置表(2按钮,2意见,1环节)
        //1/2 按钮
        ButtonCopy(copyKey, key);


        //3/4 意见
        OpinionCopy(copyKey, key);


        //5批量写入环节配置
        ProcActCopy(copyKey, key);

        //清除redis中的相关配置
        String schema = MycatSchema.getSchema();
        delButton(schema);
        delProcButton(schema);


    }



    private void OpinionCopy(String copyKey, String key) {

        List<OaProcOpinion> oaProcOpinions = oaProcOpinionMapper.queryByKey(key);
        //记录新旧id的对应关系
        HashMap<Integer, Integer> idsMap = new HashMap<>();
        ArrayList<Integer> procOpinionIds = new ArrayList<>();
        //写入 oaProcOpinion 并记录新旧id对应关系
        for (OaProcOpinion oaProcOpinion : oaProcOpinions) {
            Integer oldOpionId = oaProcOpinion.getIId();
            procOpinionIds.add(oldOpionId);
            oaProcOpinion.setProcDefKey(copyKey);
            oaProcOpinionMapper.insert(oaProcOpinion);
            Integer newOpId = oaProcOpinion.getIId();
            idsMap.put(oldOpionId, newOpId);
        }
        //写入 opinionSet表
        if (oaProcOpinions.size() > 0) {
            LambdaQueryWrapper<OaOpinionSet> procOpinionWrapper = Wrappers.lambdaQuery();
            procOpinionWrapper.eq(OaOpinionSet::getProcDefKey, key).in(OaOpinionSet::getIProcOpinionId, procOpinionIds);
            List<OaOpinionSet> oaOpinionSets = oaOpinionSetMapper.selectList(procOpinionWrapper);
            if (oaOpinionSets.size() > 0) {

                oaOpinionSets.stream().forEach(oaOpinionSet -> {
                    Integer iProcOpinionId = oaOpinionSet.getIProcOpinionId();
                    Integer newProBtnId = idsMap.get(iProcOpinionId);
                    oaOpinionSet.setIProcOpinionId(newProBtnId);
                    oaOpinionSet.setProcDefKey(copyKey);
                });
            }
            oaOpinionSetMapper.insertBatch(oaOpinionSets);

        }
    }

    private void ButtonCopy(String copyKey, String key) {



        //redisUtil.del(CacheConstant.BUTTON_SET_CACHE, CacheConstant.PROC_BUTTON_CACHE);

        List<OaProcButton> oaProcButtons = oaProcButtonMapper.queryByKey(key);
        //记录新旧id的对应关系
        HashMap<Integer, Integer> idsMap = new HashMap<>();
        ArrayList<Integer> procButtonIds = new ArrayList<>();

        //写入 oaProcButton 并记录新旧id对应关系
        for (OaProcButton oaProcButton : oaProcButtons) {
            Integer oldId = oaProcButton.getIId();
            procButtonIds.add(oldId);
            //写入数据得到最新id
            oaProcButton.setProcDefKey(copyKey);
            oaProcButtonMapper.insert(oaProcButton);
            Integer newId = oaProcButton.getIId();
            idsMap.put(oldId, newId);
        }
        //写入 buttonSet表
        if (oaProcButtons.size() > 0) {
            LambdaQueryWrapper<OaButtonSet> buttonSetWrapper = Wrappers.<OaButtonSet>lambdaQuery();
            buttonSetWrapper.eq(OaButtonSet::getProcDefKey, key).in(OaButtonSet::getIProcButtonId, procButtonIds);
            List<OaButtonSet> oaButtonSets = oaButtonSetMapper.selectList(buttonSetWrapper);
            //构造写入的数据
            oaButtonSets.stream().forEach(oaButtonSet -> {
                Integer iProcButtonId = oaButtonSet.getIProcButtonId();
                Integer newProBtnId = idsMap.get(iProcButtonId);
                oaButtonSet.setIProcButtonId(newProBtnId);
                oaButtonSet.setProcDefKey(copyKey);
            });
            //批量写入
            if (oaButtonSets.size() > 0) oaButtonSetMapper.insertBatch(oaButtonSets);
        }
    }



    private void ProcActCopy(String copyKey, String key) {
        LambdaQueryWrapper<OaProcActinst> procActinstWrapper = Wrappers.<OaProcActinst>lambdaQuery();
        procActinstWrapper.eq(OaProcActinst::getProcDefKey, key);
        List<OaProcActinst> oaProcActinsts = oaProcActinstMapper.selectList(procActinstWrapper);
        if (oaProcActinsts.size() > 0) oaProcActinstMapper.insertBatch(oaProcActinsts, copyKey);
    }

    @CacheEvict(value = CacheConstant.PROC_BUTTON_CACHE, allEntries=true)
    public void delProcButton(String schema) {
        log.info("------------>>流程拷贝 按钮配置 PROC_BUTTON_CACHE 更新");
    }

    @CacheEvict(value = CacheConstant.BUTTON_SET_CACHE, allEntries=true)
    public void delButton(String schemal) {
        log.info("------------>>流程拷贝 按钮配置 BUTTON_SET_CACHE 更新");
    }
}
