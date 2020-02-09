package com.cfcc.modules.system.mapper;


import java.util.List;
import java.util.Map;

import com.cfcc.modules.system.entity.sysUserManagedepts;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.security.core.parameters.P;

/**
 * @Description: 分管领导部门管理
 * @Author: jeecg-boot
 * @Date:   2019-12-28
 * @Version: V1.0
 */
public interface SysUserManagedeptsMapper extends BaseMapper<sysUserManagedepts> {

    public void deleteListById(@Param("list")List<String> departMapId);

    @Select("select depart_name departName from sys_depart where id = #{parentId} ")
    public String finParentName(@Param("parentId")String parentId );

    @Select("select username from sys_user where id = #{userId} ")
    public String findByUserId(@Param("userId")String userId);

//    @Select("select * from sys_user_managedepts where 1 =1 and  s_user_id = #{userId} and s_dept_id = #{deptId}")
    public List<Map<String,Object>> findByUserIdAndDepartId(@Param("userId")String userId,@Param("list")List<String> deptIdList );

    @Select("select id from sys_depart where depart_name = #{deptName}")
    public List<String> getByUserIdAndDeptName(@Param("deptName")String deptName);

    public List<Map<String,Object>> findUserManagedepts(@Param("userId")String userId, @Param("start")Integer start,@Param("pageSize")Integer pageSize);

    public String findUserId(@Param("username")String username);

    public int removeByIdAndDeptId(@Param("userId")String userId,@Param("deptId")String deptId);

    public Long findTotal(@Param("username") String username);

    public Map<String,Object> findDepartNameByDeptId(@Param("sDeptId") String sDeptId);

    public List<Map<String,Object>> findUserIdByUserName(@Param("username")String username,@Param("start")Integer start,@Param("pageSize")Integer pageSize);

//    @Select("select count(i_id) from sys_user_managedepts where")
    public Long findDepartIdByUserId(@Param("userId")String userId,@Param("departId")String departId);

//    @Insert("insert into sys_user_managedepts(user_id)")
    public void saveDepartIdByUserId(@Param("userId")String userId,@Param("departId")String departId);
}
