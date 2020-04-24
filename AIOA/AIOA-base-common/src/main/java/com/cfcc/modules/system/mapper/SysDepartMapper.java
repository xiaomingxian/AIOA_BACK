package com.cfcc.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.system.entity.SysDepart;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 部门 Mapper 接口
 * <p>
 * 
 * @Author: Steve
 * @Since：   2019-01-22
 */
public interface SysDepartMapper extends BaseMapper<SysDepart> {

	String findUserDepartByDepart(@Param("departId") String departId);

    @Select("select depart_name from sys_depart where id = #{depId}")
	public String getDepartNameByDepId(@Param("depId") String depId);


	@Select("select depart_order from sys_depart where depart_name = #{roleName}")
	public Integer getDepartOrderByRoleName(@Param("roleName") String roleName);

	/**
	 * 根据用户ID查询部门集合
	 */
	public List<SysDepart> queryUserDeparts(@Param("userId") String userId);
	SysDepart getById(String id);

	@Select("select * from sys_depart c where c.org_code = #{orgCode}")
	SysDepart queryUserDepartByOrgCodeDao(@Param("orgCode")String orgCode);

	List<String> findSysDepartByParentIdAndOrgType0(@Param("DBvalue")String DBvalue);

	Map<String, Object> getDepartIdById(@Param("parentId") String parentId);

    Map<String, Object> getUserDepartByUserId(@Param("userId") String userId);
}
