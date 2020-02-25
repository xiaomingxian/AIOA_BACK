package com.cfcc.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.modules.system.entity.SysRole;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.entity.SysUserAgent;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    public List<Map<String,Object>> getSysUserAllByAll(@Param("user")SysUser user,@Param("orderBydepart")String orderBydepart, @Param("start") Integer start,@Param("pageSize")Integer pageSize);

//    根据条件搜索用户总数
    public Long getSysUserTotalByAll(@Param("user")SysUser user);

    @Select("SELECT depart_name FROM sys_depart where id = #{parentId} ")
    public String getDepartNameById(@Param("parentId")String parentId);


    /**
     * 通过用户账号查询用户信息
     *
     * @param username
     * @return
     */
    public SysUser getUserByName(@Param("username") String username);

    /**
     * 根据部门Id查询用户信息
     *
     * @param page
     * @param departId
     * @return
     */
    IPage<SysUser> getUserByDepId(Page page, @Param("departId") String departId, @Param("username") String username);

    /**
     * 根据角色Id查询用户信息
     *
     * @param page
     * @param
     * @return
     */
    IPage<SysUser> getUserByRoleId(Page page, @Param("roleId") String roleId, @Param("username") String username);

    /**
     * 根据用户名设置部门ID
     *
     * @param username
     */
    void updateUserDepart(@Param("username") String username, @Param("orgCode") String orgCode);

    /**
     * 根据手机号查询用户信息
     *
     * @param phone
     * @return
     */
    public SysUser getUserByPhone(@Param("phone") String phone);


    /**
     * 根据邮箱查询用户信息
     *
     * @param email
     * @return
     */
    public SysUser getUserByEmail(@Param("email") String email);

    @Select("SELECT uu.id uid,uu.username uname,d.depart_name dname " +
            " FROM  sys_user uu " +
            " LEFT JOIN sys_user_depart ud ON uu.id = ud.user_id " +
            " LEFT JOIN sys_depart d ON ud.dep_id = d.id " +
            " LEFT JOIN  " +
            " (  SELECT u.id,u.username,d.depart_name,d.parent_id org_id,d.id depId " +
            "  FROM  sys_user u " +
            "  LEFT JOIN sys_user_depart ud ON u.id = ud.user_id " +
            "  LEFT JOIN sys_depart d ON ud.dep_id = d.id " +
            "  WHERE  u.id = #{userId} order by u.show_order  ) udp on 1=1 " +
            " LEFT JOIN sys_user_role ur on uu.id=ur.user_id " +
            " left JOIN sys_role r on ur.role_id=r.id " +
            " where d.id = udp.depId  " +
            " and r.role_name=#{roleName}")
        //当前用户所在部门 对应角色
    List<Map<String, Object>> getNextUsersByDept(@Param("userId") String userId, @Param("roleName") String roleName);

    @Select("select u.id uid,u.username uname,d.depart_name dname from sys_user u LEFT JOIN sys_user_depart ud on u.id=ud.user_id LEFT JOIN sys_depart d on d.id=ud.dep_id where u.id=#{drafterId}")
    List<Map<String, Object>> getDraftMsg(@Param("drafterId") String drafterId);


    @Select("SELECT " +
            "  uu.id uid,uu.username uname,d.depart_name dname " +
            " FROM sys_user uu " +
            " LEFT JOIN sys_user_depart ud ON uu.id = ud.user_id " +
            " LEFT JOIN sys_depart d ON ud.dep_id = d.id " +
            " LEFT JOIN  " +
            "  (  SELECT   u.username,d.depart_name,d.parent_id org_id,d.id depId,d.org_type " +
            "    FROM sys_user u " +
            "    LEFT JOIN sys_user_depart ud ON u.id = ud.user_id " +
            "    LEFT JOIN sys_depart d ON ud.dep_id = d.id " +
            "    WHERE " +
            "      u.id = #{id}  order by u.show_order  " +
            "  ) udp on 1=1  " +
            " LEFT JOIN sys_user_role ur on uu.id=ur.user_id " +
            " left JOIN sys_role r on ur.role_id=r.id " +
            " where d.parent_id = udp.org_id  " +
            " and r.role_name=#{candidates} ")
    List<Map<String, Object>> getNextUsersByOrg(@Param("id") String id, @Param("candidates") String candidates);

    @Select("select u.id uid,u.username uname,d.depart_name dname  from sys_user u LEFT JOIN " +
            " " +
            "sys_user_depart ud on ud.user_id=u.id LEFT JOIN sys_depart d on d.id=ud.dep_id " +
            " where u.id in ( " +
            "select user_id  from sys_user_role ur where ur.role_id = " +
            "(select id from sys_role r where r.role_name=#{candidates} ) " +
            ")")
    List<Map<String, Object>> getNextUsersAllDept(@Param("candidates") String candidates);


    @Select("SELECT r.*  FROM sys_user u LEFT JOIN sys_user_role ur on u.id=ur.user_id " +
            "LEFT JOIN sys_role r on r.id=ur.role_id " +
            "where  u.id=#{id};")
    List<SysRole> getRoleByUserId(@Param("id") String id);


    @Select("SELECT " +
            " u.id uId, " +
            " u.username uName, " +
            " r.id rId, " +
            " r.role_name rName, " +
            " d.id dId, " +
            " d.depart_name dName " +
            "FROM " +
            " `sys_user` u " +
            "LEFT JOIN sys_user_role ur ON ur.user_id = u.id " +
            "LEFT JOIN sys_role r ON r.id = ur.role_id " +
            "LEFT JOIN sys_user_depart ud ON ud.user_id = u.id " +
            "LEFT JOIN sys_depart d ON d.id = ud.dep_id " +
            "WHERE " +
            " u.username = #{username};")
    List<Map<String, Object>> getCurrentUserMsg(@Param("username") String username);

    @Select("select id,depart_name departName from sys_depart  where org_type=2  and parent_id=#{parentId} order by depart_order ")
    List<Map<String, Object>> selectAllDept(String parentId);

    @Select("<script> " +
            "SELECT " +
            " u.id id,u.username username ,d.depart_name departName,d.id departId " +
            "FROM " +
            " sys_depart d " +
            "LEFT JOIN sys_user_depart ud ON d.id = ud.dep_id " +
            "LEFT JOIN sys_user u ON ud.user_id = u.id " +
            "LEFT JOIN sys_user_role ur ON u.id = ur.user_id " +
            "LEFT JOIN sys_role r on ur.role_id=r.id " +
            "where  " +
            "d.id in " +
            "<foreach collection='deptids' item='id' index='index' separator=',' open='(' close=')'>" +
            " #{id}" +
            "</foreach> and r.role_name=#{role}  " +
            " GROUP BY u.id" +
            "</script>")
    List<SysUser> selectUsersByDeptAndRole(@Param("role") String role, @Param("deptids") ArrayList<String> deptids);

    @Select("<script>" +
            "SELECT " +
            " u.id uid, " +
            " d.id did, " +
            " d.parent_id dpid " +
            "FROM " +
            " sys_user u " +
            "LEFT JOIN sys_user_depart ud ON u.id = ud.user_id " +
            "LEFT JOIN sys_depart d ON d.id = ud.dep_id " +
            "WHERE " +
            " u.id IN " +
            "<foreach collection='uids' item='id' index='index' separator=',' open='(' close=')'>" +
            " #{id}" +
            "</foreach>" +
            "</script>")
    List<Map<String, Object>> selectUserAndDeptByUserId(@Param("uids") List<String> uids);

    @MapKey("id")
    @Select("<script> " +
            "select d.depart_name deptName,u.* from sys_user u LEFT JOIN sys_user_depart ud on ud.user_id=u.id  " +
            "LEFT JOIN sys_depart d on d.id=ud.dep_id where u.id in " +
            "<foreach collection='ids' item='id' open='(' close=')' separator=','>" +
            "#{id}" +
            "</foreach>" +
            " order BY u.show_order " +
            "</script>")
    Map<String, SysUser> queryUserMsgByIds(@Param("ids") ArrayList<String> userIds);

    List<SysUser> queryAllUser();


    @Select("SELECT d.org_code " +
            "FROM " +
            " sys_user u " +
            "LEFT JOIN sys_user_depart ud ON ud.user_id = u.id " +
            "LEFT JOIN sys_depart d ON d.id = ud.dep_id where u.id=#{id}")
    String selectOrgCodeByUserId(String id);

    List<String> queryUserIdsByName(@Param("list") List<SysUserAgent> userAgents);

    @Select("select username from sys_user  where id=#{id} ")
    String selectUserNameById(String userId);

//    @Select("select count(id) from sys_user ")
    Long getSysUserTotal(@Param("user")SysUser user);

    List<Map<String,Object>> getSysUserAndDepartName(@Param("user") SysUser user, @Param("start") Integer pageNo, @Param("pageSize") Integer pageSize);

//    @Select("select * from sys_user order by id limit #{pageNo}, #{pageSize}")
    List<SysUser> getSysUserAll(@Param("user") SysUser user, @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);

    @Select("SELECT " +
            "u.username " +
            "FROM " +
            " sys_user_depart ud " +
            "LEFT JOIN  " +
            " sys_user u " +
            "ON ud.user_id=u.id " +
            "WHERE ud.dep_id=#{id};")
    List<String> getUserNameByDeptId(@Param("id") String dept);

    @Select("SELECT " +
            " username " +
            "FROM " +
            " sys_user " +
            "WHERE " +
            "id=#{id};")
    SysUser getUserByUserId(@Param("id") String userId);

    List<SysUser> queryUserByDepts(@Param("ids")String[] ids);


    SysUser findById(String sysUserId);

    String getUserIdByUserName(String username);

    @Select("<script>" +
            "select DISTINCT d.id from sys_user u LEFT JOIN sys_user_depart ud on u.id=ud.user_id left join sys_depart d on d.id=ud.dep_id " +
            "where u.id in " +
            "        <foreach collection='ids' separator=',' item='id' open='(' close=')'>\n" +
            "           #{id} "+
            "        </foreach>" +
            "</script>")
    List<String> selectDeptsBysUsers(@Param("ids") List<String> deptUsers);

    Map<String,Object> getdeptIdByUser(@Param("username") String username);
}
