package com.cfcc.modules.utils;

import com.cfcc.modules.workflow.pojo.Activity;
import com.cfcc.modules.workflow.vo.TaskInfoVO;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.InclusiveGatewayActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class TaskUtil /*extends WorkFlowService implements ApplicationContextAware*/ {


    /**
     * 查询下n个节点
     * type :-1 普通
     * 0 包容
     * 1 并行
     */
    public static void searchNextActsInfo(List<Activity> nexts, PvmActivity currentAct, Map<String, Map<String, Object>> acts, int gateWayType) {

        //获取输出连线
        List<PvmTransition> outTransitions = currentAct.getOutgoingTransitions();

        if (outTransitions != null && outTransitions.size() > 0) {
            // 当前节点有后续连线的情况下---遍历输输出连线(跳过网关,事件)
            for (PvmTransition outLine : outTransitions) {
                PvmActivity destination = outLine.getDestination();
                //解析节点属性-需要转换类型
                ActivityBehavior activityBehavior = ((ActivityImpl) destination).getActivityBehavior();
                //节点属性存储实体
                Activity activity = new Activity();
                /**
                 * 所属网关类型
                 */

                if (gateWayType == 0) {
                    activity.setInclusiveGateway(true);
                }
                if (gateWayType == 1) {
                    activity.setParallelGateway(true);
                }


                //节点类型
                String type = (String) destination.getProperty("type");
                if (type.contains("Gateway")) {//当前节点为网关
                    //递归--查询
                    //判断网关类型
                    if (activityBehavior instanceof InclusiveGatewayActivityBehavior) {//包容网关
                        searchNextActsInfo(nexts, destination, acts, 0);
                    } else if (activityBehavior instanceof ParallelMultiInstanceBehavior) {//并行网关
                        searchNextActsInfo(nexts, destination, acts, 1);
                    } else {
                        searchNextActsInfo(nexts, destination, acts, -1);
                    }

                } else if (type.equalsIgnoreCase("subProcess")) {
                    //子流程 就直接获取该节点的信息
                    ParallelMultiInstanceOrUserTaskActivity(activity, activityBehavior, outLine, acts, destination);
                    nexts.add(activity);
                } else if (type.equalsIgnoreCase("endEvent")) {
                    //结束事件--判断是否有父节点(可能是子流程中的结束事件)
                    //TODO
                    //判断是否流程最终的结束时间
                    ParallelMultiInstanceOrUserTaskActivity(activity, activityBehavior, outLine, acts, destination);
                    activity.setType("endEvent");
                    activity.setName("办结");
                    nexts.add(activity);

                } else {
                    //普通节点
                    //判断节点是否是设计上的多实例
                    ParallelMultiInstanceOrUserTaskActivity(activity, activityBehavior, outLine, acts, destination);
                    nexts.add(activity);
                }

            }
        } else {
            //如果当前步骤定义信息为空，尝试获取父节点的Transition

            //TODO
        }

    }

    /**
     * 节点信息封装(有表设置信息)
     */
    public static void ParallelMultiInstanceOrUserTaskActivity(Activity activity, ActivityBehavior activityBehavior, PvmTransition outLine, Map<String, Map<String, Object>> acts, PvmActivity destination) {

        Object conditionText = outLine.getProperty("conditionText");
        if (conditionText != null) {
            Map<String, String> parse = ElParse.parseCondition((String) conditionText);
            activity.setConditionContext(parse);
        }


        if (activityBehavior instanceof ParallelMultiInstanceBehavior) {
            ParallelMultiInstanceBehavior parallelMultiInstanceBehavior = (ParallelMultiInstanceBehavior) activityBehavior;
            activity.setAllowMulti(true);
            String mulAsssignee = parallelMultiInstanceBehavior.getCollectionVariable();
            activity.setMultiAssignee(mulAsssignee);
            String size = parallelMultiInstanceBehavior.getLoopCardinalityExpression() == null ?
                    null : ElParse.parseNormal(parallelMultiInstanceBehavior.getLoopCardinalityExpression().getExpressionText());
            activity.setLoopSize(size);
        } else if (activityBehavior instanceof UserTaskActivityBehavior) {

            UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) activityBehavior;
            activity.setAllowMulti(false);
            Expression assigneeExpression = userTaskActivityBehavior.getTaskDefinition().getAssigneeExpression();
            String assignee = assigneeExpression == null ? null : ElParse.parseNormal(assigneeExpression.getExpressionText());
            activity.setAssignee(assignee);
        }

        activity.setProcDefKey(destination.getProcessDefinition().getKey());
        activity.setId(destination.getId());
        activity.setName(destination.getProperty("name") + "");
        activity.setType(destination.getProperty("type") + "");
        //查表
        Map<String, Object> act = acts.get(destination.getId());
        if (act == null) return;

        Boolean isAllowMulti = (Integer) acts.get(destination.getId()).get("isMultAssignee") == 1;
        String userOrRole = acts.get(destination.getId()).get("userOrRole") + "";

        activity.setMulti(isAllowMulti);
        activity.setUserOrRole(userOrRole);


    }


}
