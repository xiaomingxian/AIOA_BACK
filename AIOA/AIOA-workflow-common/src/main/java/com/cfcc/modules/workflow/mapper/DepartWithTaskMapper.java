package com.cfcc.modules.workflow.mapper;

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
}
