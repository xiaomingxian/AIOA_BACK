package com.cfcc.modules.system.mapper;

import java.util.List;

import com.cfcc.modules.system.entity.SysUserDepart;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Select;

public interface SysUserDepartMapper extends BaseMapper<SysUserDepart>{

	@Select("select dep_id from sys_user_depart where user_id = #{userId}")
	List<String> getDepIdByUserId(@Param("userId")String userId);

	List<SysUserDepart> getUserDepartByUid(@Param("userId") String userId);
}
