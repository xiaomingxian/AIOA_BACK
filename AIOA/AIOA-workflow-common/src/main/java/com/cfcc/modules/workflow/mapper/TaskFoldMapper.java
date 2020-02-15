package com.cfcc.modules.workflow.mapper;

import com.cfcc.modules.system.entity.SysDictItem;
import com.cfcc.modules.workflow.pojo.HisTaskJsonAble;
import com.cfcc.modules.workflow.pojo.TaskInfoJsonAble;
import com.cfcc.modules.workflow.vo.TaskInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskFoldMapper {

//待办
    List<TaskInfoJsonAble> queryTaskToDoFold(@Param("urgencyDegree") String urgencyDegree,
            @Param("pojo") TaskInfoVO taskInfoVO, @Param("ids") List<String> ids,
                                         @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);

    long queryTaskToDoCountFold(@Param("urgencyDegree") String urgencyDegree,
            @Param("pojo") TaskInfoVO taskInfoVO, @Param("ids") List<String> ids);

//已办
    List<HisTaskJsonAble> queryTaskDoneFold(@Param("urgencyDegree") String urgencyDegree,
            @Param("pojo") TaskInfoVO taskInfoVO, @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);

    Long queryTaskDoneCountFold(@Param("urgencyDegree") String urgencyDegree,
            @Param("pojo") TaskInfoVO taskInfoVO);
//流程監控折疊數據---------缓急一级折叠------------------
    List<SysDictItem> monitorFoldUrgency(@Param("urgencyDegree") String urgencyDegree,
                                              @Param("pojo") TaskInfoVO taskInfoVO,@Param("isAdmin")boolean isAdmin);

//流程监控数据：直接参与的数据, 身为候选人的信息
    int monitorCountFold(@Param("urgencyDegree") String urgencyDegree,
            @Param("pojo") TaskInfoVO taskInfoVO,@Param("isAdmin")boolean isAdmin);

    List<TaskInfoJsonAble> monitorDataFold(@Param("urgencyDegree") String urgencyDegree,
            @Param("pojo") TaskInfoVO taskInfoVO,
            @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize,
                                           @Param("isAdmin")boolean isAdmin);
//部门待办数量
    Long deptTaskCountFold(@Param("urgencyDegree") String urgencyDegree,
            @Param("pojo") TaskInfoVO taskInfoVO, @Param("type") String type);

    List<TaskInfoJsonAble> deptTaskQueryFold(@Param("urgencyDegree") String urgencyDegree,
            @Param("pojo") TaskInfoVO taskInfoVO, @Param("type") String type,
                                         @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);
//部门已办数量
    Long deptTaskHaveDoneCountFold(@Param("urgencyDegree") String urgencyDegree,
            @Param("pojo") TaskInfoVO taskInfoVO, @Param("type") String type);

    List<TaskInfoJsonAble> deptTaskHaveDoneFold(@Param("urgencyDegree") String urgencyDegree,
            @Param("pojo") TaskInfoVO taskInfoVO, @Param("type") String type,
                                            @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);
//部门监控数量
    Long deptTaskMonitorCountFold(@Param("urgencyDegree") String urgencyDegree,
            @Param("pojo") TaskInfoVO taskInfoVO, @Param("type") String type,@Param("isAdmin")boolean isAdmin);

    List<TaskInfoJsonAble> deptTaskMonitorQueryFold(@Param("urgencyDegree") String urgencyDegree,
            @Param("pojo") TaskInfoVO taskInfoVO, @Param("type") String type,
                                                @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize,
                                                    @Param("isAdmin")boolean isAdmin);

//时间限制范围内的数据
    List<TaskInfoJsonAble> allUndoltLimitTimeHaveAssigneeFold();
}
