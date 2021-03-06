package com.cfcc.modules.workflow.mapper;

import com.cfcc.modules.workflow.pojo.*;
import com.cfcc.modules.workflow.vo.TaskInfoVO;
import org.activiti.engine.task.Task;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TaskMapper {

    long queryTaskToDoCount(@Param("pojo") TaskInfoVO taskInfoVO, @Param("ids") List<String> ids);


    List<TaskInfoJsonAble> queryTaskToDo(@Param("pojo") TaskInfoVO taskInfoVO, @Param("ids") List<String> ids,
                                         @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);


    List<HisTaskJsonAble> queryTaskDone(@Param("pojo") TaskInfoVO taskInfoVO, @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);

    Long queryTaskDoneCount(@Param("pojo") TaskInfoVO taskInfoVO);

    List<TaskInfoJsonAble> allUndoltLimitTimeHaveAssignee();

    int monitorCount(@Param("pojo") TaskInfoVO taskInfoVO,@Param("isAdmin")boolean isAdmin);

    List<TaskInfoJsonAble> monitorData(@Param("pojo") TaskInfoVO taskInfoVO,
                                       @Param("pageNo") Integer pageNo,
                                       @Param("pageSize") Integer pageSize,
                                       @Param("isAdmin")boolean isAdmin
    );

    Long deptTaskCount(@Param("pojo") TaskInfoVO taskInfoVO, @Param("type") String type);

    List<TaskInfoJsonAble> deptTaskQuery(@Param("pojo") TaskInfoVO taskInfoVO, @Param("type") String type,
                                         @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);

    Long deptTaskHaveDoneCount(@Param("pojo") TaskInfoVO taskInfoVO, @Param("type") String type);

    List<TaskInfoJsonAble> deptTaskHaveDone(@Param("pojo") TaskInfoVO taskInfoVO, @Param("type") String type,
                                            @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize
    );

    Long deptTaskMonitorCount(@Param("pojo") TaskInfoVO taskInfoVO,
                              @Param("type") String type,
                              @Param("isAdmin") boolean isAdmin
    );

    List<TaskInfoJsonAble> deptTaskMonitorQuery(@Param("pojo") TaskInfoVO taskInfoVO, @Param("type") String type,
                                                @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSizee,
                                 @Param("isAdmin") boolean isAdmin);

    String queryTaskDefId(@Param("proDefKey") String proDefKey, @Param("userId") String userId, @Param("bussinessKey") String bussinessKey);


    void recordUser(@Param("pId") String processInstanceId, @Param("list") List<String> list, @Param("user") Object o);


    void deleteOld(@Param("pId") String processInstanceId, @Param("list") List<String> asList);


    long queryTaskShiftCount(@Param("pojo") TaskInfoVO taskInfoVO, @Param("ids") List<String> ids);

    List<TaskInfoJsonAble> queryTaskShift(@Param("pojo") TaskInfoVO taskInfoVO, @Param("ids") List<String> ids,
                                          @Param("pageNo") Integer pageNo,
                                          @Param("pageSize") Integer pageSize);

    @Select("SELECT count(*) FROM `act_ru_execution` where proc_inst_id_=#{processId};")
    Integer exeHaveDone(@Param("processId") String processId);


    @Select("<script>" +
            "SELECT " +
            " TASK_ID_ taskId,USER_ID_ userId " +
            "FROM " +
            " `act_ru_identitylink` " +
            "WHERE  " +
            "  type_ = 'candidate' " +
            "AND TASK_ID_ IN " +
            "<foreach collection='map' item='item' index='key' separator=',' open='(' close=')'>" +
            "#{key}" +
            "</foreach>" +
            "</script>")
    List<Map<String, Object>> allUndoltLimitTimeNoAssignee(@Param("map") HashMap<String, TaskInfoJsonAble> taskInfoJsonAbleHashMap);


    @Select("select TASK_DEF_KEY_ taskKey,NAME_ taskName from act_hi_taskinst where ASSIGNEE_=#{assignee} " +
            " and PROC_INST_ID_=#{procInstId} and LENGTH(DELETE_REASON_)>0 group by TASK_DEF_KEY_")
    List<Map<String, String>> queryAllHaveDone(@Param("assignee") String assignee, @Param("procInstId") String procInstId);


    @MapKey("taskId")
    @Select("<script>" +
            " select TASK_ID_ taskId,GROUP_CONCAT(USER_ID_) userIds FROM act_hi_identitylink   where TASK_ID_ in  " +
            "<foreach collection='ids' item='id' open='(' close=')' separator=','>" +
            "#{id}" +
            "</foreach>" +
            " GROUP BY TASK_ID_ " +
            "</script>")
    Map<String, Map<String, String>> identitylinksUsers(@Param("ids") ArrayList<String> identitylinks);


    @MapKey("actId")
    @Select("SELECT * FROM `oa_proc_actinst` where PROC_DEF_KEY=#{key};")
    Map<String, OaProcActinst> queryTaskDefType(String key);


    @MapKey("userId")
    @Select("<script>" +
            "select task_id taskId,task_def_key taskDefKey,type,user_id userId from  oa_task_dept where task_id in " +
            "<foreach collection='ids' item='id' open='(' close=')' separator=','>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    Map<String, Map<String, Object>> taskDeptMsg(@Param("ids") List<String> deptTaskIds);


    @Select("select count(*)>0 from oa_task_dept where user_id=#{userId} and proc_inst_id=#{proInstanId} and task_id=#{taskId}")
    boolean isDeptTaskUser(@Param("proInstanId") String proInstanId, @Param("taskId") String taskId, @Param("userId") String id);

    @Select("select *  from oa_task_dept where task_id=#{taskId} and user_id=#{userId} limit 1")
    OaTaskDept isZhuBane(@Param("taskId") String taskId, @Param("userId") String id);





    @Select("SELECT record_val FROM `oa_task_user_record` where proc_inst_id=#{processInstanceId} and record_key=#{el};")
    String getValByEl(@Param("processInstanceId") String processInstanceId, @Param("el") String el);

    @Select("SELECT * FROM `oa_proc_actinst` where     #{processDefinitionId} like  CONCAT(PROC_DEF_KEY,':%')       " +
            " and act_id=#{taskDefinitionKey}   ")
    OaProcActinst isRecordCurrentUser(@Param("taskDefinitionKey") String taskDefinitionKey, @Param("processDefinitionId") String processDefinitionId);


    @Select("select TEXT_ from act_hi_varinst where NAME_='busMsg' and  TEXT_ like CONCAT('%,',#{id},',',#{table},',%')")
    List<String> queryTitleMsg(@Param("id") String i_id, @Param("table") String table);

    @Update("update act_hi_varinst set TEXT_=#{msg} where NAME_='busMsg'   and  TEXT_ like CONCAT('%,',#{id},',',#{table},',%')")
    void updateHisMsg(@Param("id") String i_id, @Param("table") String table, @Param("msg") String msgNew);

    @Update("update act_ru_variable set TEXT_=#{msg} where NAME_='busMsg'   and  TEXT_ like CONCAT('%,',#{id},',',#{table},',%')")
    void updateInTaskMsg(@Param("id") String i_id, @Param("table") String table, @Param("msg") String msgNew);

    @Select("select TEXT_ from act_ru_variable where NAME_='busMsg' and  TEXT_ like CONCAT('%,',#{id},',',#{table},',%')")
    List<String> queryTitleMsgInTask(@Param("id") String i_id, @Param("table") String table);

    @Select("select ID_ id, " +
            "TASK_DEF_KEY_ taskDefinitionKey, " +
            "PROC_DEF_ID_ processDefinitionId, " +
            "PROC_INST_ID_ processInstanceId, " +
            "EXECUTION_ID_ executionId, " +
            "NAME_ name, " +
            "ASSIGNEE_ assignee, " +
            "START_TIME_ startTime, " +
            "END_TIME_ endTime, " +
            "p.s_opinion  backReason " +
            " from act_hi_taskinst t " +
            " LEFT JOIN ${table} p on  t.ID_=p.s_task_id   " +
            " where (t.ASSIGNEE_ LIKE '撤回:%' " +
            "OR (t.ASSIGNEE_ LIKE '回退:%' and p.s_opinion_type=-1 )) and t.PROC_INST_ID_=#{procInstId}  " +
            "GROUP BY name_,TASK_DEF_KEY_ ,p.s_opinion " +
            "order  by cast( t.id_  as SIGNED INTEGER)  asc")
    List<BackRecord> backRecord(@Param("procInstId") String procInstId,@Param("table")String table);

    @Select(
            "select t.TASK_DEF_KEY_ taskDefinitionKey,t.NAME_ name,t.ASSIGNEE_ assignee " +
                    "from act_hi_taskinst t   LEFT JOIN act_ru_execution e  on e.ID_=t.EXECUTION_ID_  " +
                    " where    " +
                    "t.PROC_INST_ID_=#{val}     " +
                    "and   " +
                    " LENGTH(t.DELETE_REASON_)>0  and (locate('回退', ASSIGNEE_)=0 and  locate('撤回', ASSIGNEE_)=0 )  " +
                    "and e.PARENT_ID_ is null   " +
                    "UNION   " +
                    "select t.TASK_DEF_KEY_ taskDefinitionKey,t.NAME_ name,GROUP_CONCAT(t.ASSIGNEE_) assignee  " +
                    "from act_hi_taskinst t   LEFT JOIN act_ru_execution e  on e.ID_=t.EXECUTION_ID_  " +
                    " where    " +
                    "t.PROC_INST_ID_=#{val}     " +
                    "and   " +
                    " LENGTH(t.DELETE_REASON_)>0  and (locate('回退', ASSIGNEE_)=0 and  locate('撤回', ASSIGNEE_)=0 )  " +
                    "and e.PARENT_ID_ is not null   " +
                    "GROUP BY e.PARENT_ID_")
    List<HisTaskJsonAble> queryTaskHaveDoneNew(String processInstanceId);

    @Update("update   `act_hi_taskinst` set PARENT_TASK_ID_=null   where PARENT_TASK_ID_=#{taskId} and  PROC_INST_ID_=#{processInstanceId}")
    void deleteHiParent(@Param("processInstanceId") String processInstanceId, @Param("taskId") String taskId);

    @Update(" update  act_ru_task set PARENT_TASK_ID_=null   where PARENT_TASK_ID_=#{taskId} and PROC_INST_ID_=#{processInstanceId}")
    void deleteRuParent(@Param("processInstanceId")String processInstanceId, @Param("taskId") String taskId);

    @Select("<script>" +
            "select ID_ id , ASSIGNEE_ userId from act_hi_taskinst " +
            "where ID_ in " +
            "<foreach collection='ids' item='id' index='key' separator=',' open='(' close=')'>" +
            "#{id}" +
            "</foreach> " +
            "and ASSIGNEE_ is not null  " +
            "UNION " +
            "select TASK_ID_ id ,USER_ID_ userId from act_hi_identitylink where TASK_ID_ in " +
            "<foreach collection='ids' item='id' index='key' separator=',' open='(' close=')'>" +
            "#{id}" +
            "</foreach> " +
            "</script>")
    List<Map<String, String>> userHaveChoice(@Param("ids") ArrayList<String> taskIds);

    @Select("select count(*)  FROM oa_task_dept where task_def_key=#{taskDefKey} and proc_inst_id=#{procInstId} and type like '%主办%'")
    int haveMainDept(@Param("taskDefKey") String taskIdRecord,@Param("procInstId") String procInstId);

    @Select("select DISTINCT user_id  FROM oa_task_dept where task_def_key=#{taskDefKey} and proc_inst_id=#{procInstId}")
    List<String> deptUsers(@Param("taskDefKey") String taskIdRecord,@Param("procInstId") String procInstId);


    @Update("UPDATE act_hi_taskinst set PROC_DEF_ID_=#{processDefinitionId} ,PROC_INST_ID_=#{processInstanceId}," +
            " EXECUTION_ID_=#{executionId} where PARENT_TASK_ID_=#{parentTaskId}")
    void updateHisAct( Task task);

//    @Update("<foreach collection='list' item='t' index='index' open='' close=''  separator=';'> " +
//            "            UPDATE act_hi_taskinst " +
//            "            <set> " +
//            "               PROC_DEF_ID_=#{t.processDefinitionId} ,PROC_INST_ID_=#{t.processInstanceId}," +
//            " EXECUTION_ID_=#{t.executionId},PARENT_TASK_ID_=#{t.parentTaskId} " +
//            "            </set>" +
//            "            <where>" +
//            "                 PARENT_TASK_ID_=#{pid}" +
//            "            </where>" +
//            "        </foreach>" )
    void updateHisActDept(@Param("t") Task task,@Param("pid") String randomParent);

//    @Update("<foreach collection='list' item='t' index='index'  open='' close='' separator=';'>  " +
//            "            UPDATE act_ru_task " +
//            "            <set> " +
//            "             PROC_DEF_ID_=#{t.processDefinitionId} ,PROC_INST_ID_=#{t.processInstanceId}," +
//            " EXECUTION_ID_=#{t.executionId},PARENT_TASK_ID_=#{t.parentTaskId}" +
//            "            </set>" +
//            "            <where>" +
//            "               PARENT_TASK_ID_=#{pid} " +
//            "            </where> " +
//            "        </foreach> " )
    void updateRuActDept(@Param("t") Task task,@Param("pid") String randomParent);

    void deleteByprocessInstanceId(@Param("processInstanceId") String processInstanceId);

    void setParentId(@Param("processInstanceId") String processInstanceId,@Param("taskId") String taskId);

    void updateTaskDescript(@Param("procInstId") String procInstId,@Param("busMsg") String busMsg);

    void updateParentAndDesc(Task newTask);

    @Delete("delete from oa_task_dept where proc_inst_id=#{v}")
    void deleteDeptByProcessInstceId(String processInstanceId);

    @Select("SELECT COUNT(task_id)>0 FROM oa_task_dept  where task_id=#{taskId} and task_def_key=#{taskDefKey}")
    boolean isDeptTask(@Param("taskId") String taskId,@Param("taskDefKey") String taskDefKey);

    void updateTaskStatus(@Param("id") String taskId ,@Param("desc") String desc);
}
