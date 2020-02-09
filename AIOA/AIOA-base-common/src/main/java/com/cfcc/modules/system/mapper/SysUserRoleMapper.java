package com.cfcc.modules.system.mapper;

import java.util.List;
import java.util.Map;

import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.entity.SysRole;
import com.cfcc.modules.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 用户角色表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    @Select("select role_code from sys_role where id in (select role_id from sys_user_role where user_id = (select id from sys_user where username=#{username}))")
    List<String> getRoleByUserName(@Param("username") String username);

    @Select("SELECT  " +
            "u.id,u.username,GROUP_CONCAT(DISTINCT d.depart_name) departs  " +
            "FROM  " +
            "sys_role r " +
            "LEFT JOIN sys_user_role ur ON r.id = ur.role_id  " +
            "LEFT join sys_user u ON ur.user_id = u.id  " +
            "LEFT JOIN sys_user_depart ud ON ud.user_id = u.id  " +
            "LEFT JOIN sys_depart d ON d.id = ud.dep_id " +
            "WHERE " +
            "r.role_name = #{roleName} " +
            "GROUP BY r.id")
    List<Map<String, String>> getCurrentUserPart(@Param("roleName") String roleName);

    @Select("select * from sys_depart where org_code=#{orgCode} ")
    SysDepart getDeptById(@Param("orgCode") String orgCode);


    @Select("select r.id id,r.role_code roleCode,r.role_name roleName FROM sys_user_role ur  " +
            "LEFT JOIN sys_role r on r.id=ur.role_id  " +
            "where ur.user_id=#{v}")
    List<SysRole> currentUserRoles(String userId);


}
