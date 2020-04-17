package com.cfcc.modules.utils;

import com.cfcc.modules.workflow.pojo.Activity;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.InclusiveGatewayActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.apache.commons.lang.StringUtils;
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
    public static void searchNextActsInfo(List<Activity> nexts, PvmActivity currentAct,
                                          Map<String, Map<String, Object>> acts,
                                          int gateWayType, PvmTransition outLineInGateWay
    ) {

        //获取输出连线
        List<PvmTransition> outTransitions = currentAct.getOutgoingTransitions();

        if (outTransitions != null && outTransitions.size() > 0) {
            // 当前节点有后续连线的情况下---遍历输输出连线(跳过网关,事件)
            for (PvmTransition outLine : outTransitions) {
                PvmActivity destination = outLine.getDestination();
                PvmActivity source = outLine.getSource();
                //解析节点属性-需要转换类型
                ActivityBehavior activityBehavior = ((ActivityImpl) destination).getActivityBehavior();
                //节点属性存储实体 网关连着而且可能存储完成条件(条件要接起来)
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
                //String sourceType = (String) source.getProperty("type");
                Object conditionText = outLine.getProperty("conditionText");
                //排他网关存在一个没有条件的情况
                exclusiveGateWayNoCondition(activity, source, conditionText);


                if (type.contains("Gateway")) {//当前节点为网关
                    // 网关连着的情况
                    //判断网关类型
                    if (activityBehavior instanceof InclusiveGatewayActivityBehavior) {//包容网关
                        searchNextActsInfo(nexts, destination, acts, 0, outLine);
                    } else if (activityBehavior instanceof ParallelMultiInstanceBehavior) {//并行网关
                        searchNextActsInfo(nexts, destination, acts, 1, outLine);
                    } else {
                        searchNextActsInfo(nexts, destination, acts, -1, outLine);
                    }

                } else if (type.equalsIgnoreCase("subProcess")) {
                    //子流程 就直接获取该节点的信息
                    //上上条线(可能是多个,选择两头都是网关的情况)
                    gateWayMore(outLineInGateWay, activity);

                    ParallelMultiInstanceOrUserTaskActivity(activity, activityBehavior, outLine, acts, destination);
                    List<? extends PvmActivity> activities = destination.getActivities();
                    if (activities.size() > 0) {
                        PvmActivity pvmActivity = activities.get(0);
                        String id = pvmActivity.getId();
                        activity.setFirstSonKey(id);//子流程下的第一个环节
                    }
                    nexts.add(activity);
                } else if (type.equalsIgnoreCase("endEvent")) {
                    //结束事件--判断是否有父节点(可能是子流程中的结束事件)
                    //判断是否流程最终的结束时间
                    //上上条线(可能是多个,选择两头都是网关的情况)
                    gateWayMore(outLineInGateWay, activity);

                    ParallelMultiInstanceOrUserTaskActivity(activity, activityBehavior, outLine, acts, destination);
                    activity.setType("endEvent");
                    activity.setName("办结");
                    nexts.add(activity);

                } else {
                    //普通节点
                    //判断节点是否是设计上的多实例
                    //如果网关是排他 没有完成条件时 得设置一个 用来办理任务
                    //上上条线(可能是多个,选择两头都是网关的情况)
                    gateWayMore(outLineInGateWay, activity);
                    ParallelMultiInstanceOrUserTaskActivity(activity, activityBehavior, outLine, acts, destination);
                    nexts.add(activity);
                }

            }
        } else {
            //如果当前步骤定义信息为空，尝试获取父节点的Transition
            //TODO
        }

    }

    private static void gateWayMore(PvmTransition outLineInGateWay, Activity activity) {
        //网关1指向网关2 的情况只允许有一种(不允许多个网关指向同一个网关)[规范]
        if (outLineInGateWay != null) {
            PvmActivity source = outLineInGateWay.getSource();
            PvmActivity destination = outLineInGateWay.getDestination();
            if (source != null && ((String) source.getProperty("type")).endsWith("Gateway")
                    && destination != null && ((String) destination.getProperty("type")).endsWith("Gateway")) {

                Object conditionText = outLineInGateWay.getProperty("conditionText");
                Map<String, String> parse = null;
                if (conditionText != null) {
                    parse = ElParse.parseCondition((String) conditionText);
                }
                if (null != parse) activity.getConditionContext().putAll(parse);


                String sourceType = (String) source.getProperty("type");

                if (sourceType.equalsIgnoreCase("exclusiveGateway"))
                    activity.setExclusiveGatewayParent(true);

                if (sourceType.equalsIgnoreCase("InclusiveGateway"))
                    activity.setInclusiveGatewayParent(true);
                if (sourceType.equalsIgnoreCase("ParallelGateway"))
                    activity.setParallelGatewayParent(true);

                activity.setParentActId(source.getId());
            }

        }
    }

    /**
     * 排他网关存在
     *
     * @param activity
     * @param conditionText
     */
    private static void exclusiveGateWayNoCondition(Activity activity, PvmActivity source, Object conditionText) {
        String sourceType = (String) source.getProperty("type");

        if (sourceType.equalsIgnoreCase("ExclusiveGateway") && conditionText == null) {
            HashMap<String, String> condition = new HashMap<>();
            List<PvmTransition> outgoingTransitions = source.getOutgoingTransitions();
            for (PvmTransition outgoingTransition : outgoingTransitions) {
                Object conditionTextHaveContext = outgoingTransition.getProperty("conditionText");
                if (conditionTextHaveContext != null) {
                    Map<String, String> parse = ElParse.parseCondition((String) conditionTextHaveContext);
                    for (String k : parse.keySet()) {
                        //为排他网关下没有 条件的节点设置随机值 使其他节点条件不成立
                        String randomCondition = UUID.randomUUID().toString().replaceAll("-", "");
                        condition.put(k, randomCondition);
                    }
                    activity.setConditionContext(condition);
                    break;
                }
            }
        }
    }

    /**
     * 节点信息封装(有表设置信息)
     */
    public static void ParallelMultiInstanceOrUserTaskActivity(Activity activity, ActivityBehavior activityBehavior, PvmTransition outLine, Map<String, Map<String, Object>> acts, PvmActivity destination) {

        Object conditionText = outLine.getProperty("conditionText");
        if (conditionText != null) {
            Map<String, String> parse = ElParse.parseCondition((String) conditionText);
            activity.getConditionContext().putAll(parse);
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
            if (StringUtils.isBlank(assignee)) {
                int size = userTaskActivityBehavior.getTaskDefinition().getCandidateUserIdExpressions().size();

                if (size > 0) {
                    for (Expression candidateGroupIdExpression : userTaskActivityBehavior.getTaskDefinition().getCandidateUserIdExpressions()) {
                        String expressionText = candidateGroupIdExpression.getExpressionText();
                        String mulAssignees = expressionText == null ? null : ElParse.parseNormal(expressionText);
                        activity.setMultiAssignee(mulAssignees);
                    }
//                    activity.setQiangQian(true);
                    activity.setAllowMulti(true);
                }

            }
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
