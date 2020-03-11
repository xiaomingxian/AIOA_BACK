package com.cfcc.modules.workflow.vo;

import com.cfcc.modules.workflow.pojo.TaskWithDepts;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
public class TaskInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;   //用户名
    private String userName;   //用户名
    private String agentUserName;//代理人

    private String busKey;

    private String processId;//流程实例id  processInstanceId

    private String processDefinitionId;

    private String taskId;//任务id

    private String executionId;

    private String taskDefKey;//任务环节id

    private String taskName;//任务名称

    private String endtime; //任务完成时间-提醒用户

    private String operstatus; //任务状态(代办/已办)
    private String iImport; //行领导关注


    //流程需要的变量
    private Map<String, Object> vars;
    //业务数据
    private Map<String, Object> busData;

    /**
     * 是否是部门办理
     * 即是办理任务的参数，又是查询条件
     */
    private Boolean isDept=false;

    //如果下一节点是选部门-存入部门信息
    private TaskWithDepts taskWithDepts;

    //用于存储：业务数据权限表（流转过程中逐步维护该表数据、收回删除人员权限
    //private String[] assignee;
    private Object assignee;
    private Integer functionId;
    private Integer busDataId;//业务数据id


    //***************************************** 查询条件 *****************************************
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startQueryTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endQueryTime;
    //标题
    private String dataTitle;
    /**
     * 主办/辅办/传阅...
     */
    private String deptType;

    /**
     * 主办部门
     */
    private String mainDept;
    /**
     * 辅办部门
     */
    private String fuDept;
    /**
     * 传阅部门
     */
    private String cyDept;
    /**
     * 文号
     */
    private String fileNum;
    /**
     * 拟稿人
     */
    private String createName;


    //****************************************** table标题排序
    private boolean tableOrder;
    /**
     * 按照标题排序 1正序 -1倒序
     */
    private Integer orederByTile;
    private Integer orederByWenHao;
    private Integer orederByHuanJie;
    private Integer orederByDrafter;
    private Integer orederByTime;

    /**
     * 移交人s
     */
    private String shiftUsers;


}
