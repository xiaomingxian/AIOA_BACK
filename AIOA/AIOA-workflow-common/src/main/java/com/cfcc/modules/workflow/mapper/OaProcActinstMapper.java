package com.cfcc.modules.workflow.mapper;

import com.cfcc.modules.workflow.pojo.OaProcActinst;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 流程节点配置
 */
public interface OaProcActinstMapper extends BaseMapper<OaProcActinst> {

    @MapKey("actId")
    Map<String, Map<String, Object>> queryActs(@Param("key") String key, @Param("taskIds") List<String> taskIds);

    @Select("SELECT is_dept_finish FROM `oa_proc_actinst` where PROC_DEF_KEY=#{key} and act_id=#{taskDefKey}; ")
    Boolean isDeptFinish(@Param("key") String key, @Param("taskDefKey") String taskDefKey);

    @Select("select count(*)>0 from oa_proc_actinst where PROC_DEF_KEY=#{pojo.procDefKey} and act_id=#{pojo.actId}")
    Boolean isExist(@Param("pojo") OaProcActinst oaProcActinst);


    long selectCountMy(@Param("pojo") OaProcActinst oaProcActinst);


    List<OaProcActinst> selectData(@Param("pojo") OaProcActinst oaProcActinst,
                                   @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize
    );


    void insertBatch(@Param("list") List<OaProcActinst> oaProcActinsts, @Param("copyKey") String copyKey);
}
