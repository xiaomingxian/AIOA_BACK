package com.cfcc.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.system.entity.SysUserDepart;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SysUserDepartMapper extends BaseMapper<SysUserDepart>{

    @Select("select dep_id from sys_user_depart where user_id = #{userId}")
	List<String> getDepIdByUserId(@Param("userId")String userId);

	List<SysUserDepart> getUserDepartByUid(@Param("userId") String userId);

    Long findDepartIdByUserId(@Param("userId") String userId,@Param("departId") String departId);

	void saveDepartIdByUserId(String userId, String departId);

    void deleteUserDepartByUserId(@Param("id") String id);
}
