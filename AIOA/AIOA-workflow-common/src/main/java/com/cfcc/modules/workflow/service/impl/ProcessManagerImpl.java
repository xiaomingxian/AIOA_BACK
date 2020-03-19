package com.cfcc.modules.workflow.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.exception.AIOAException;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.mapper.SysUserMapper;
import com.cfcc.modules.utils.ProcssUtil;
import com.cfcc.modules.workflow.mapper.TaskMapper;
import com.cfcc.modules.workflow.pojo.Activity;
import com.cfcc.modules.workflow.pojo.HisTaskJsonAble;
import com.cfcc.modules.workflow.pojo.ProcessDefinitionJsonAble;
import com.cfcc.modules.workflow.service.IoaProcActinstService;
import com.cfcc.modules.workflow.service.ProcessManagerService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.zip.ZipInputStream;

@Service
@Transactional
@Slf4j
public class ProcessManagerImpl implements ProcessManagerService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IoaProcActinstService ioaProcActinstService;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private TaskMapper taskMapper;

    static List<String> types;

    static {
        String[] ts = {"xml", "bpmn", "zip", "bar"};
        types = Arrays.asList(ts);
    }

    /**
     * 流程发布
     *
     * @return
     */
    @Override
    @CacheEvict(value = "defKv", allEntries = true)
    public void deploy(MultipartFile[] multipartFiles, String schemal) {

        Result result = new Result();
        try {

            for (MultipartFile multipartFile : multipartFiles) {
                DeploymentBuilder builder = repositoryService.createDeployment();

                //判断文件后缀
                String originalFilename = multipartFile.getOriginalFilename();
                if (originalFilename.contains(".")) {
                    String type = originalFilename.split("\\.")[1];
                    if (!types.contains(type)) throw new AIOAException("您上传的文件不合法,请上传bpmn/zip/xml格式的文件");
                } else {
                    throw new AIOAException("您上传的文件不合法,请上传bpmn/zip/xml格式的文件");
                }

                try {
                    if (originalFilename.endsWith(".zip") || originalFilename.endsWith(".bar")) {
                        //压缩格式
                        builder.addZipInputStream(new ZipInputStream(multipartFile.getInputStream()));

                    } else {
                        //文件
                        File file = new File(multipartFile.getOriginalFilename());
                        String name = file.getName();
                        if (name.endsWith(".xml")) {
                            name = name.replace(".xml", ".bpmn");
                        }
                        builder.addInputStream(name, multipartFile.getInputStream());
                    }
                } catch (IOException e) {
                    log.error("文件：" + multipartFile.getName() + " 添加失败：" + e.toString());
                }
                //发布
                builder.deploy();
            }


            result.setSuccess(true);
            result.setMessage("发布成功");

        }catch (AIOAException e){
            throw  new AIOAException(e.getMessage());
        }
        catch (Exception e) {
            throw new AIOAException("发布失败,请检查流程图是否正确(建议按照手册操作)");
        }
    }

    /**
     * 流程定义分页查询
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public Result<IPage> processQuery(ProcessDefinition vo, Integer pageNo, Integer pageSize) {

        Result<IPage> iPageResult = new Result<>();
        try {

            IPage iPage = new Page(pageNo, pageSize);

            ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();

            query.latestVersion();

            //name
            if (StringUtils.isNotEmpty(vo.getName())) query.processDefinitionNameLike(vo.getName());
            //key
            if (StringUtils.isNotEmpty(vo.getKey())) query.processDefinitionKeyLike(vo.getKey());
            //deploymentId
            if (StringUtils.isNotEmpty(vo.getDeploymentId())) query.deploymentId(vo.getDeploymentId());

            //总数统计
            int totalCount = (int) query.count();
            //分页后的结果查询
            List<ProcessDefinition> listPage = query.orderByDeploymentId().desc().listPage((pageNo - 1) * pageSize, pageSize);

            List<ProcessDefinitionJsonAble> dataJsonAble = new ArrayList<>();
            listPage.forEach(processDefinition -> {
                ProcessDefinitionJsonAble processDefinitionJsonAble = new ProcessDefinitionJsonAble();
                BeanUtils.copyProperties(processDefinition, processDefinitionJsonAble);
                dataJsonAble.add(processDefinitionJsonAble);

            });

            //分页参数
            iPageResult.setSuccess(true);
            iPage.setRecords(dataJsonAble);
            iPage.setTotal(totalCount);
            iPage.setCurrent(pageNo);
            iPage.setSize(pageSize);
            iPageResult.setResult(iPage);

        } catch (Exception e) {
            log.error(e.toString());
            iPageResult.setSuccess(false);
            iPageResult.setMessage("查询失败");
        }
        return iPageResult;
    }

    /**
     * @param ids
     * @param cascade 是否级联删除
     * @return
     */
    @Override
    @CacheEvict(value = "defKv", allEntries = true)
    public Result processDel(String[] ids, boolean cascade, String schemal) {

        Result result = new Result();
        try {
            //级联删除
            Arrays.stream(ids).forEach(id -> {
                        if (StringUtils.isNotEmpty(id)) {
                            repositoryService.deleteDeployment(id, cascade);
                        }
                    }
            );

            result.setSuccess(true);
            result.setMessage("流程删除成功");
        } catch (Exception e) {
            log.error(e.toString());
            result.setMessage("流程删除失败");
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 启用流程定义
     */
    @Override
    @CacheEvict(value = "defKv", allEntries = true)
    public Result activeProcess(ProcessDefinition vo, String schemal) {
        Result result = new Result();
        try {
            repositoryService.activateProcessDefinitionById(vo.getId());
            result.setSuccess(true);
        } catch (Exception e) {
            log.error("启用流程定义出错,ProcessDefinitionId=" + vo.getId(), e);
            result.setSuccess(false);
            result.setMessage("启用流程定义出错,ProcessDefinitionId=" + vo.getId());
        }
        return result;
    }


    /**
     * 挂起流程
     *
     * @param vo
     * @generated
     */
    @Override
    @CacheEvict(value = "defKv", allEntries = true)
    public Result disactiveProcess(ProcessDefinition vo, String schemal) {
        Result result = new Result();
        try {
            repositoryService.suspendProcessDefinitionById(vo.getId());
            result.setSuccess(true);
        } catch (Exception e) {
            log.error("停用流程定义出错,ProcessDefinitionId=" + vo.getId(), e);
            result.setSuccess(false);
            result.setMessage("启用流程定义出错,ProcessDefinitionId=" + vo.getId());
        }
        return result;
    }


    @Override
    public ProcessInstance start(String key, String bussinessKey, Map<String, Object> vars) {
        //存入任务描述
        try {
            Set<String> keySet = vars.keySet();
            ProcessInstance processInstance = null;
            if (keySet.size() == 0) {
                processInstance = runtimeService.startProcessInstanceByKey(key, bussinessKey);
            } else {
                processInstance = runtimeService.startProcessInstanceByKey(key, bussinessKey, vars);
            }

            if (processInstance == null) throw new AIOAException("流程开启失败");
            System.err.println("=========>>>>流程开启成功>>>>流程实例id" + processInstance.getId());
            return processInstance;
        } catch (Exception e) {
            log.error("流程开启失败" + e.toString());
            throw new AIOAException("流程开启失败");
        }
    }

    @Override
    public void loadByDeployment(String processDefinitionId, String resourceType, HttpServletResponse response) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();

        String resourceName = "";
        if (resourceType.equals("image")) {
            resourceName = processDefinition.getDiagramResourceName();
        }
        InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
                resourceName);

        byte[] b = new byte[1024];
        int len = -1;
        try {
            while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
        } catch (IOException e) {
            log.error("查询流程资源失败", e);
        }
    }

    @Override
//    , key = "'actPro'"
    @Cacheable(value = "defKv")
    public Map<String, String> defKv(String orgSchema) {
        HashMap<String, String> map = new HashMap<>();
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().active().latestVersion().list();
        list.stream().forEach(l -> {
            String name = l.getName();
            String key = l.getKey();
            map.put(name, key);
        });
        return map;
    }

    @Override
    public String loadByDeploymentXml(String processDefinitionId) throws IOException {

        StringBuilder str = new StringBuilder();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        String resourceName = processDefinition.getResourceName();

        InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
                resourceName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
        String line = null;

        while ((line = reader.readLine()) != null) {
            str.append(line).append("\n");
        }

        return str.toString();
    }

    @Override
    public void saveXml(String name, String xml) {
        repositoryService//获取流程定义和部署对象相关的Service
                .createDeployment()//创建部署对象
                .addString(name + ".bpmn", xml)
                .deploy();//完成部署
    }


    @Override
    public List<Activity> actsList(String processDefinitionId, boolean haveSub) {

        if (null == processDefinitionId) throw new AIOAException("流程定义id为空,未查询到流程");

        ProcessDefinitionEntity proc = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
        List<ActivityImpl> activities = proc.getActivities();

        List<ActivityImpl> actsAll = new ArrayList<>();
        ProcssUtil.findAllAct(activities, actsAll);

        List<Activity> list = new ArrayList<>();

        String key = proc.getKey();
        ArrayList<String> taskIds = new ArrayList<>();
        actsAll.stream().forEach(i -> taskIds.add(i.getId()));
        //查询节点信息
        Map<String, Map<String, Object>> acts = ioaProcActinstService.queryActs(key, taskIds);

        ProcssUtil.setActs(list, activities, proc, acts, haveSub);

        return list;
    }

    @Override
    public List<Activity> actsListPic(String processDefinitionId, String proKey, boolean b) {

        if (null == processDefinitionId) {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(proKey).latestVersion().singleResult();
            if (processDefinition == null) throw new AIOAException("未找到对应流程,请检查流程是否部署");//未找到对应流程
            processDefinitionId = processDefinition.getId();
        }


        ProcessDefinitionEntity proc = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
        List<ActivityImpl> activities = proc.getActivities();

        List<ActivityImpl> actsAll = new ArrayList<>();
        getAllAct(activities, actsAll);

        ArrayList<Activity> allUserTask = new ArrayList<>();


        ProcssUtil.fillAllProperties(actsAll, allUserTask);

        return allUserTask;
    }


    @Override
    public void getAllAct(List<ActivityImpl> activities, List<ActivityImpl> actsAll) {
        for (ActivityImpl activity : activities) {
            actsAll.add(activity);
            List<ActivityImpl> acts = activity.getActivities();
            if (null != acts && acts.size() > 0) {
                getAllAct(acts, actsAll);
            }
        }
    }


    @Override
    public Result lastVersionProc(String key) {
        Map<String, String> map = new HashMap<>();

        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).latestVersion().list();
        if (list != null && list.size() == 0) return Result.error("对应的流程不存在");
        ProcessDefinition processDefinition = list.get(0);
        String id = processDefinition.getId();
        map.put("name", processDefinition.getName());
        map.put("id", id);
        return Result.ok(map);
    }

    @Override
    public String queryActNameByKey(String key) {


        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key)
                .latestVersion().singleResult();

        String name = processDefinition.getName();
        return name;
    }

    /**
     * 展示可退回节点(节点属性)
     *
     * @param processInstanceId
     * @param taskDefinitionKey
     * @return
     */
    @Override
    public List<Activity> showBackAct(String processDefinitionId, String processInstanceId, String taskDefinitionKey) {
        if (processDefinitionId == null) {
            throw new AIOAException("流程定义id为空,未查询到流程");
        }
        ProcessDefinitionEntity proc = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
        //所有节点
        List<ActivityImpl> actsAll = proc.getActivities();
        //可退回节点
        List<ActivityImpl> actsBack = new ArrayList<ActivityImpl>();

        //返回结果
        ArrayList<Activity> activities = new ArrayList<>();
        //环节定义key
        ArrayList<String> taskDefKeys = new ArrayList<>();
        //每个节点用户信息存储(取最新)
        Map<String, Set<String>> userIds = new HashMap<>();

        //查询已办或已跳转
        List<HisTaskJsonAble> list = taskMapper.queryTaskHaveDoneNew(processInstanceId);

        if (list.size() == 0) throw new AIOAException("没有可回退的环节");

        //哪些节点是可退回节点(标记出来)
        Map<String, Boolean> hasTask = new HashMap<>();
        list.stream().forEach(i -> {
            String taskDefinitionKey1 = i.getTaskDefinitionKey();
            String assignee = i.getAssignee();

            hasTask.put(i.getTaskDefinitionKey(), true);
            //记录用户
            Set<String> ids = new HashSet<>();
            if (assignee != null) {
                if (assignee.contains(",")) {
                    List<String> is = Arrays.asList(assignee.split(","));
                    ids.addAll(is);
                } else {
                    ids.add(assignee);
                }
                userIds.put(taskDefinitionKey1, ids);
            }
        });
        if (hasTask.size() == 0) throw new AIOAException("没有可回退的环节");
        //查一遍用户
        ArrayList<String> idsAll = new ArrayList<>();
        Collection<Set<String>> values = userIds.values();
        values.stream().forEach(l -> {
            idsAll.addAll(l);
        });

        Map<String, SysUser> userMap = new HashMap<>();
        if (idsAll.size() > 0) {
            userMap = userMapper.queryUserMsgByIds(idsAll);
        }


        //可退回节点
        actsAll.stream().forEach(act -> {
            String id = act.getId();
            if (hasTask.get(id) != null && hasTask.get(id)) {
                actsBack.add(act);
                taskDefKeys.add(id);
            }
        });

        if (taskDefKeys.size() == 0) return activities;

        Map<String, Map<String, Object>> acts = ioaProcActinstService.queryActs(proc.getKey(), taskDefKeys);

        ProcssUtil.setActs(activities, actsBack, proc, acts, false);

        //填充用户属性
        for (Activity activity : activities) {

            String id = activity.getId();
            Set<String> ids = userIds.get(id);
            List<String> names = getUserNames(ids, userMap);
            activity.setUserIds(ids);
            activity.setUserNames(names);
        }
        return activities;
    }

    private List<String> getUserNames(Set<String> ids, Map<String, SysUser> userMap) {
        List<String> userNames = new ArrayList<>();
        for (String id : ids) {
            SysUser sysUser = userMap.get(id);
            if (sysUser != null) {
                userNames.add(sysUser.getUsername());
            }
        }
        return userNames;
    }


}
