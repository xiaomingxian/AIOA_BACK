package com.cfcc.modules.utils;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.HistoryServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class AddUserCmd implements Command<Void> {

    protected String executionId;//执行实例id

    protected String assignee;//办理人

    //@Autowired
    private RuntimeService runtimeService;

    //@Autowired
    private TaskService taskService;

    private String descript;
    private String parentTaskId;

    //@Autowired
    //private IdGenerator idGenerator;
    public AddUserCmd() {
    }

    public AddUserCmd(String executionId, String assignee, String descript, String parentTaskId,
                      RuntimeService runtimeService, TaskService taskService) {
        this.executionId = executionId;
        this.assignee = assignee;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.descript = descript;
        this.parentTaskId = parentTaskId;
    }


    @Override
    public Void execute(CommandContext commandContext) {

        Execution execution = runtimeService.createExecutionQuery().executionId(executionId).singleResult();

        ExecutionEntity executionEntity = (ExecutionEntity) execution;

        ExecutionEntity parent = executionEntity.getParent();//获取父级 ExecutionEntity实例对象

        ExecutionEntity newExecution = parent.createExecution();//创建一个新的ExecutionEntity实例对象

        newExecution.setActive(true);//激活状态
        newExecution.setConcurrent(true);//设置创建的新实例对象是分支
        newExecution.setScope(false);

        //创建新taskEntity 填充属性
        Task newTask = taskService.createTaskQuery().executionId(executionId).singleResult();
        TaskEntity t = (TaskEntity) newTask;
        TaskEntity taskEntity = new TaskEntity();
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

        //一般只会有一个
        List<Task> list = taskService.createTaskQuery().processInstanceId(t.getProcessInstanceId())
                .processDefinitionKey(t.getProcessDefinitionId())
                .taskDefinitionKey(t.getTaskDefinitionKey())
                .taskCandidateOrAssigned(t.getAssignee())
                .list();

        for (Task task : list) {
            String parentTaskId = task.getParentTaskId();
            if (parentTaskId.equalsIgnoreCase(t.getParentTaskId())){
                taskService.saveTask(task);
            }
        }

        Integer nrOfInstances = LoopVariableUtils.getLoopVariable(newExecution, "nrOfInstances");
        Integer nrOfActiveInstances = LoopVariableUtils.getLoopVariable(newExecution, "nrOfActiveInstances");

        LoopVariableUtils.setLoopVariable(newExecution, "nrOfInstances", nrOfInstances + 1);
        LoopVariableUtils.setLoopVariable(newExecution, "nrOfActiveInstances", nrOfActiveInstances + 1);


        return null;
    }
}
