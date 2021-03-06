package com.cfcc.modules.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.workflow.pojo.OaProcActinst;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @Description: 流程节点配置
 */
public interface OaProcActinstMapper extends BaseMapper<OaProcActinst> {
//    根据流程key和任务名称查询-lvjian
List<OaProcActinst> queryByKeyAndName(@Param("pojo") OaProcActinst oaProcActinst);

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

    @Select("SELECT COUNT(*)>0 FROM oa_proc_actinst where #{key} like  CONCAT(PROC_DEF_KEY,':','%') and ACT_ID =#{actDefKey}" +
            " and user_or_role='dept' and depts like '%主办%'")
    boolean taskHaveMain(@Param("actDefKey") String destActDefKey,@Param("key") String processDefinitionId);
}
