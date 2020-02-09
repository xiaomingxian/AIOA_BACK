package com.cfcc.modules.workflow.mapper;

import com.cfcc.modules.workflow.pojo.TaskTransfer;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface TaskTransferMapper {


    @Select("SELECT i_id id,task_id taskId," +
            "transfer_log transferLog,sourceuser_id sourceUserId FROM `oa_bus_task_transfer` where task_id=#{taskId};")
    TaskTransfer selectTaskLog(String taskId);

    @Update("update oa_bus_task_transfer set transfer_log=#{pojo.transferLog} where i_id=#{pojo.id}")
    void update(@Param("pojo") TaskTransfer taskTransfer);

    @Insert("insert into oa_bus_task_transfer (proc_inst_id_,task_id,transfer_log,sourceuser_id)" +
            " values(#{pojo.procInstId},#{pojo.taskId},#{pojo.transferLog},#{pojo.sourceUserId})")
    void insert(@Param("pojo") TaskTransfer newT);

}