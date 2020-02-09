package com.cfcc.modules.utils;

import com.cfcc.modules.workflow.pojo.Activity;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 流程工具类
 * 节点属性 /el表达式...
 */
@Component
public class ProcssUtil implements ApplicationContextAware {

    ApplicationContext applicationContext;

    /**
     * 获取ioc容器
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    /**
     * 节点坐标信息
     *
     * @param activity
     * @return
     */
    public static Map getXY(ActivityImpl activity) {
        HashMap map = new HashMap();
        int x = activity.getX();
        int y = activity.getY();
        int width = activity.getWidth();
        int height = activity.getHeight();
        map.put("x", x);
        map.put("y", y);
        map.put("width", width);
        map.put("height", height);
        return map;
    }


    /**
     * 节点属性填充
     */
    public static void fillAllProperties(List<ActivityImpl> actsAll, ArrayList<Activity> allUserTask) {

        for (ActivityImpl currentAct : actsAll) {
            String type = (String) currentAct.getProperty("type");

            if ("subProcess".equalsIgnoreCase(type) || "usertask".equalsIgnoreCase(type)) {
                Activity activity = new Activity();
                //解析当前节点并记录
                ActivityBehavior activityBehavior = ((ActivityImpl) currentAct).getActivityBehavior();
                Object conditionText = null;
                if (conditionText != null) {
                    Map<String, String> parse = ElParse.parseCondition((String) conditionText);
                    activity.setConditionContext(parse);
                }


                if (activityBehavior instanceof UserTaskActivityBehavior) {

                    activity.setAllowMulti(false);
                    UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) activityBehavior;
                    Expression assigneeExpression = userTaskActivityBehavior.getTaskDefinition().getAssigneeExpression();
                    String assignee = assigneeExpression == null ? null : ElParse.parseNormal(assigneeExpression.getExpressionText());
                    activity.setAssignee(assignee);
                }
                if (activityBehavior instanceof ParallelMultiInstanceBehavior) {
                    activity.setAllowMulti(true);
                    ParallelMultiInstanceBehavior parallelMultiInstanceBehavior = (ParallelMultiInstanceBehavior) activityBehavior;
                    String mulAsssignee = parallelMultiInstanceBehavior.getCollectionVariable();
                    activity.setMultiAssignee(mulAsssignee);
                    String size = parallelMultiInstanceBehavior.getLoopCardinalityExpression() == null ?
                            null : ElParse.parseNormal(parallelMultiInstanceBehavior.getLoopCardinalityExpression().getExpressionText());
                    activity.setLoopSize(size);
                }

                activity.setName(currentAct.getProperty("name") + "");
                activity.setType(currentAct.getProperty("type") + "");
                activity.setProcDefKey(currentAct.getProcessDefinition().getKey());
                activity.setId(currentAct.getId());

                if (allUserTask.contains(activity)) {
                    return;
                }

                allUserTask.add(activity);
            }
        }


    }


    /**
     * 带流程设置的节点属性填充
     */
    public static void setActs(List<Activity> list, List<ActivityImpl> activities, ProcessDefinitionEntity proc, Map<String, Map<String, Object>> acts, boolean haveSub) {

        if (activities == null) {
            return;
        } else {
            activities.forEach(i -> {
                String type = (String) i.getProperty("type");

                if ((type.equalsIgnoreCase("userTask") || type.equalsIgnoreCase("subProcess"))) {
                    //输出连线
                    String taskId = i.getId();
                    Activity activity = new Activity();
                    activity.setId(taskId);
                    activity.setName((String) i.getProperty("name"));
                    activity.setType(type);
                    activity.setProcDefKey(i.getProcessDefinition().getKey());
                    activity.setUserOrRole(acts.get(taskId).get("userOrRole") + "");
                    activity.setDepts(acts.get(taskId).get("depts") + "");


                    //判断是多实例还是普通节点
                    if (i.getActivityBehavior() instanceof ParallelMultiInstanceBehavior) {
                        //有子流程---解析表达式
                        ParallelMultiInstanceBehavior activityBehavior = (ParallelMultiInstanceBehavior) i.getActivityBehavior();
                        //所有候选人
                        String collectionVariable = activityBehavior.getCollectionVariable();
                        //数量
                        String size = activityBehavior.getLoopCardinalityExpression().getExpressionText();
                        activity.setLoopSize(size);
                        activity.setMultiAssignee(collectionVariable);
                        activity.setMulti(true);
                        //查询是否允许多实例
                        Boolean isAllowMulti = acts.get(taskId).get("isMultAssignee") == null ?
                                false : Integer.parseInt(acts.get(taskId).get("isMultAssignee").toString()) == 1;
                        activity.setAllowMulti(isAllowMulti);
                    } else {
                        TaskDefinition td = proc.getTaskDefinitions().get(i.getId());
                        //解析办理人表达式
                        //先查抢签的
                        String expressionText=null;
                        Set<Expression> candidateUserIdExpressions = td.getCandidateUserIdExpressions();
                        if (candidateUserIdExpressions!=null && candidateUserIdExpressions.size()>0){
                            Expression next = candidateUserIdExpressions.iterator().next();
                             expressionText = next.getExpressionText();
                        }

                        if (expressionText==null){
                            Expression assigneeExpression = td.getAssigneeExpression();
                            expressionText = assigneeExpression == null ? null : td.getAssigneeExpression().getExpressionText();
                        }

                        activity.setAssignee(expressionText);//办理人
                        activity.setMulti(false);
                    }

                    list.add(activity);
                    //节点跳转/回退只查主流程--不查子流程
                    if (haveSub) {
                        List<ActivityImpl> activities1 = i.getActivities();
                        setActs(list, activities1, proc, acts, haveSub);
                    }
                }

            });
        }

    }

    /**
     * 查询所有节点信息(排除网关/连线等信息)
     */
    public static void findAllAct(List<ActivityImpl> activities, List<ActivityImpl> actsAll) {


        for (ActivityImpl activity : activities) {
            String type = activity.getProperty("type") + "";
            if ((type.equalsIgnoreCase("userTask") || type.equalsIgnoreCase("subProcess"))) {
                actsAll.add(activity);
            }
            List<ActivityImpl> acts = activity.getActivities();
            if (null != acts && acts.size() > 0) {
                findAllAct(acts, actsAll);
            }
        }
    }


}
