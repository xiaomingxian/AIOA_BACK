package com.cfcc.modules.workflow.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
public class Activity implements Serializable {

    private boolean isMulti;//表中是否允许多实例

    private String procDefKey;//流程定义key
    private String id;
    private String name;
    private String type;
    private String assignee;//办理人表达式
    private String MultiAssignee;//多实例办理人表达式
    private String loopSize;//数量
    private boolean isAllowMulti;//数据库中是否允许多实例

    private String userOrRole;
    private String depts;
    private boolean  canAdd;

    /**
     * 网关类型：包容网关与 并行网关 特殊处理
     *
     * @return
     */
    private boolean InclusiveGateway;
    private boolean InclusiveGatewayParent;//网关连着的情况，包含父网关
    private boolean ParallelGateway;
    private boolean ParallelGatewayParent;//网关连着的情况，并行父网关
    private boolean exclusiveGatewayParent;//网关连着的情况，排他父网关


    public List<String> getDeptsList() {
        ArrayList<String> deptsAll = new ArrayList<>();
        if (depts != null && !depts.contains(",")) {
            deptsAll.add(depts);
        }
        if (depts != null && depts.contains(",")) {
            deptsAll.addAll(Arrays.asList(depts.split(",")));
        }

        return deptsAll;
    }


    private Map<String, String> conditionContext=new HashMap<>();//完成条件 key-value

    //回退相关
    Set<String> userIds;
    List<String> userNames;

    //追加相关
    private String firstSonKey;//部门(子流程)下的第一个环节


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return isMulti == activity.isMulti &&
                isAllowMulti == activity.isAllowMulti &&
                Objects.equals(procDefKey, activity.procDefKey) &&
                Objects.equals(id, activity.id) &&
                Objects.equals(name, activity.name) &&
                Objects.equals(type, activity.type) &&
                Objects.equals(assignee, activity.assignee) &&
                Objects.equals(MultiAssignee, activity.MultiAssignee) &&
                Objects.equals(loopSize, activity.loopSize) &&
                Objects.equals(userOrRole, activity.userOrRole) &&
                Objects.equals(depts, activity.depts) &&
                Objects.equals(conditionContext, activity.conditionContext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isMulti, procDefKey, id, name, type, assignee, MultiAssignee, loopSize, isAllowMulti, userOrRole, depts, conditionContext);
    }
}
