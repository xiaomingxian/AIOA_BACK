package com.cfcc.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.cfcc.modules.system.entity.SysUserFun;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * SysUserFun Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
public interface SysUserFunMapper extends BaseMapper<SysUserFun> {

    @Insert("INSERT INTO sys_user_fun (s_user_id,i_bus_model_id,i_bus_function_id,status) VALUES (#{userId},#{modelId},#{functionId},#{statuss})")
    int addUserFun(String userId, Integer modelId, Integer functionId, Integer statuss);

    @Update("UPDATE sys_user_fun set status=#{statuss} WHERE s_user_id=#{userId} and i_bus_function_id=#{functionId};")
    void updateByUserId(String userId, Integer functionId, Integer statuss);

    @Select("SELECT b.s_name FROM  sys_user_fun u " +
            "LEFT join oa_bus_function b " +
            "on u.i_bus_function_id=b.i_id and u.i_bus_model_id=b.i_bus_model_id " +
            "WHERE u.s_user_id=#{userId} and u.status not in (0)")
    List<String> selecUserFuntList(String userId);

    @Select("SELECT i_bus_model_id,i_bus_function_id,status FROM sys_user_fun WHERE s_user_id=#{usrId}")
    List<SysUserFun> selectUserFunStatus(String userId);
    List<Map<String,Object>> queryAllList();

    List<SysUserFun> queryListByUserIdDao(String userId);

    List<Map<String, Object>> queryListMapByUserIdDao(String userId);
}
