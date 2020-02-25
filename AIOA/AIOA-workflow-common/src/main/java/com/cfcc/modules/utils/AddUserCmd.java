package com.cfcc.modules.utils;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cfg.IdGenerator;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AddUserCmd implements Command<Void> {

    protected String executionId;//执行实例id

    protected List<String> assignees;
    protected List<List<String>> candidateUsers;
    protected boolean isDept;
    protected TaskEntity taskAfter;
    private List<Task> tasks;

    private RuntimeService runtimeService;
    private ProcessEngineConfigurationImpl processEngineConfiguration;

    private TaskService taskService;


    private String descript;
    private String parentTaskId;
    private IdGenerator idGenerator;

    public AddUserCmd() {
    }

    public AddUserCmd(String executionId,
                      List<String> assignees,
                      List<List<String>> candidateUsers,
                      String descript,
                      String parentTaskId,
                      boolean isDept, TaskEntity taskAfter, List<Task> tasks) {
        this.executionId = executionId;
        this.assignees = assignees;
        this.candidateUsers = candidateUsers;
        this.descript = descript;
        this.parentTaskId = parentTaskId;
        this.isDept = isDept;
        this.taskAfter = taskAfter;
        this.tasks = tasks;
    }


    @Override
    public Void execute(CommandContext commandContext) {

        if (processEngineConfiguration == null)
            processEngineConfiguration = commandContext.getProcessEngineConfiguration();
        if (runtimeService == null) runtimeService = processEngineConfiguration.getRuntimeService();
        if (taskService == null) taskService = processEngineConfiguration.getTaskService();
        if (idGenerator == null) idGenerator = processEngineConfiguration.getIdGenerator();

        Execution execution = this.runtimeService.createExecutionQuery().executionId(executionId).singleResult();

        ExecutionEntity executionEntity = (ExecutionEntity) execution;

        ExecutionEntity parent = executionEntity.getParent();//获取父级 ExecutionEntity实例对象


        if (isDept) {
            ExecutionEntity parent2 = parent.getParent();//向上2级
            //ExecutionEntity execution2 = execution1.createExecution();
            //execution2
            subType(parent2, commandContext, execution);
        } else {
            //非子流程类型

            notSub(parent);
        }


        return null;
    }

    private void subType(ExecutionEntity parent2, CommandContext commandContext, Execution sourceExecution) {


        ProcessEngineConfigurationImpl pec = commandContext
                .getProcessEngineConfiguration();
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) pec
                .getRepositoryService().getProcessDefinition(parent2.getProcessDefinitionId());
        ActivityImpl branchActivity = (ActivityImpl) processDefinitionEntity
                .findActivity(sourceExecution.getActivityId());

        Task newTask = taskService.createTaskQuery().executionId(executionId).singleResult();
        for (List<String> candidateUser : candidateUsers) {

            ExecutionEntity execution1 = parent2.createExecution();

            //子流程下 第一级
            execution1.setActive(true);//激活状态
            execution1.setConcurrent(true);//设置创建的新实例对象是分支
            execution1.setScope(false);


            //创建新taskEntity 填充属性

            //子流程下 第二级
            ExecutionEntity execution = execution1.createExecution();
            execution.setActive(true);//激活状态
            execution.setConcurrent(true);//设置创建的新实例对象是分支
            execution.setScope(false);
            execution.setActivity(branchActivity);


            TaskEntity t = (TaskEntity) newTask;
            TaskEntity taskEntity = new TaskEntity();
            String id = idGenerator.getNextId();
//            String id = UUID.randomUUID().toString().replaceAll("-", "");
            taskEntity.setId(id);
            taskEntity.setCreateTime(new Date());
            taskEntity.setTaskDefinitionKey(t.getTaskDefinitionKey());
            taskEntity.setProcessDefinitionId(t.getProcessDefinitionId());
            taskEntity.setTaskDefinition(t.getTaskDefinition());
            taskEntity.setProcessInstanceId(t.getProcessInstanceId());
            taskEntity.setExecutionId(execution.getId());
            taskEntity.setName(t.getName());
            taskEntity.setExecution(execution);
            taskEntity.addCandidateUsers(candidateUser);
            taskEntity.setParentTaskId(parentTaskId);

            //保存
            taskEntity.setDescription(descript);
            execution.addTask(taskEntity);
            taskService.saveTask(taskEntity);

            TaskEntity taskEntity1 = new TaskEntity();
            taskEntity1.setId(id);
            taskEntity1.setParentTaskId(taskAfter.getParentTaskId());
            taskEntity1.setExecutionId(execution.getId());
            taskEntity1.setProcessInstanceId(taskAfter.getProcessInstanceId());
            taskEntity1.setProcessDefinitionId(taskAfter.getProcessDefinitionId());
            tasks.add(taskEntity1);
        }


        Integer nrOfInstances = LoopVariableUtils.getLoopVariableDept(parent2, "nrOfInstances");
        Integer nrOfActiveInstances = LoopVariableUtils.getLoopVariableDept(parent2, "nrOfActiveInstances");

        LoopVariableUtils.setLoopVariableDept(parent2, "nrOfInstances", nrOfInstances + candidateUsers.size());
        LoopVariableUtils.setLoopVariableDept(parent2, "nrOfActiveInstances", nrOfActiveInstances + candidateUsers.size());
    }

    private void notSub(ExecutionEntity parent) {

        for (String assignee : assignees) {

            ExecutionEntity newExecution = parent.createExecution();//创建一个新的ExecutionEntity实例对象

            newExecution.setActive(true);//激活状态
            newExecution.setConcurrent(true);//设置创建的新实例对象是分支
            newExecution.setScope(false);

            //创建新taskEntity 填充属性
            Task newTask = taskService.createTaskQuery().executionId(executionId).singleResult();
            TaskEntity t = (TaskEntity) newTask;


            TaskEntity taskEntity = new TaskEntity();
            String id = idGenerator.getNextId();
//UUID.randomUUID().toString().replaceAll("-", "")
            taskEntity.setId(id);
            taskEntity.setCreateTime(new Date());
            taskEntity.setTaskDefinitionKey(t.getTaskDefinitionKey());
            taskEntity.setProcessDefinitionId(t.getProcessDefinitionId());
            taskEntity.setTaskDefinition(t.getTaskDefinition());
            taskEntity.setProcessInstanceId(t.getProcessInstanceId());
            taskEntity.setExecutionId(newExecution.getId());
            taskEntity.setName(t.getName());
            taskEntity.setExecution(newExecution);
            taskEntity.setAssignee(assignee);
            taskEntity.setParentTaskId(parentTaskId);
            //保存
            taskEntity.setDescription(descript);
            taskService.saveTask(taskEntity);


            Integer nrOfInstances = LoopVariableUtils.getLoopVariable(newExecution, "nrOfInstances");
            Integer nrOfActiveInstances = LoopVariableUtils.getLoopVariable(newExecution, "nrOfActiveInstances");

            LoopVariableUtils.setLoopVariable(newExecution, "nrOfInstances", nrOfInstances + 1);
            LoopVariableUtils.setLoopVariable(newExecution, "nrOfActiveInstances", nrOfActiveInstances + 1);


            TaskEntity taskEntity1 = new TaskEntity();
            taskEntity1.setId(id);
            taskEntity1.setParentTaskId(taskAfter.getParentTaskId());
            taskEntity1.setExecutionId(newExecution.getId());
            taskEntity1.setProcessInstanceId(taskAfter.getProcessInstanceId());
            taskEntity1.setProcessDefinitionId(taskAfter.getProcessDefinitionId());
            tasks.add(taskEntity1);

        }
    }
}
