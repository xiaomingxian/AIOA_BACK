package com.cfcc.modules.workflow.mapper;

import com.cfcc.modules.workflow.pojo.TaskWithDepts;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface DepartWithTaskMapper {


    @Insert("<script>" +
            " insert into  oa_task_dept(proc_inst_id,task_id,task_def_key,type,user_id,dept_id) VALUES " +
            "<foreach collection='pojo.deptMsg' item='item' index='key' separator=','>" +
            "<foreach collection='item' item='value' index='key2' separator=','>" +
            "   (#{procInstId},#{pojo.tskId},#{pojo.taskDefKey},#{key},#{value},#{pojo.userDeptMap[${value}]} ) " +
            "</foreach>" +
            "</foreach>" +
            "</script>")
    void save(@Param("procInstId") String procInstId, @Param("pojo") TaskWithDepts taskWithDepts);

    @Select("select task_id from oa_task_dept where type='传阅' and user_id=#{value} ")
    List<String> selectTaskIdsByUserId(String id);


    @Select("select DISTINCT type from oa_task_dept   " +
            " where proc_inst_id=#{processInstanceId} and task_id=#{taskId} " +
            " and task_def_key=#{taskDef}   and user_id=#{userId} limit 1")
    List<String> selectMyType(@Param("processInstanceId") String processInstanceId, @Param("taskId") String taskId
            , @Param("taskDef") String taskDef, @Param("userId") String userId);
    @Select("select DISTINCT type from oa_task_dept   " +
            " where proc_inst_id=#{processInstanceId} and task_id=#{taskId} " +
            "   and user_id=#{userId} limit 1")
    List<String> selectMyParentType(@Param("processInstanceId")  String processInstanceId,
                                    @Param("taskId")  String parentTaskId, @Param("userId")String assignee);

    @Delete("   DELETE from oa_task_dept where proc_inst_id=#{processInstanceId} and task_def_key=#{task_def_key};")
    void deleteSameTask(@Param("processInstanceId") String processInstanceId, @Param("task_def_key") String taskDefinitionKey);

    @Select("SELECT td.dept_id did,COUNT(ht.ID_) countDone FROM oa_task_dept  td LEFT JOIN act_hi_taskinst ht on td.task_id=ht.ID_ and td.user_id=ht.ASSIGNEE_ and ht.DELETE_REASON_ is not null \n" +
            "where ht.ID_ is not null \n" +
            "GROUP BY td.dept_id ")
    Map<String, Integer> deptDone();
}


