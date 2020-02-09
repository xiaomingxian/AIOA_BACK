package com.cfcc.modules.system.mapper;

import com.cfcc.modules.system.entity.SysRole;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    Long pageOneTotal( @Param("role")String role);

    List<Map<String,Object>> pageOneSysRole(@Param("role")String role, @Param("start") Integer start, @Param("pageSize") Integer pageSize);

}
