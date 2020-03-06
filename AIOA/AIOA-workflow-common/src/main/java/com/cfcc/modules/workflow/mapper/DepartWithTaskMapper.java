package com.cfcc.modules.workflow.mapper;

import com.cfcc.common.util.StringUtil;
import com.cfcc.modules.workflow.pojo.TaskWithDepts;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DepartWithTaskMapper {


    @Insert("<script>" +
            " insert into  oa_task_dept(proc_inst_id,task_id,task_def_key,type,user_id) VALUES " +
            "<foreach collection='pojo.deptMsg' item='item' index='key' separator=','>" +
            "<foreach collection='item' item='value' index='key2' separator=','>" +
            "   (#{procInstId},#{pojo.tskId},#{pojo.taskDefKey},#{key},#{value} ) " +
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
}


