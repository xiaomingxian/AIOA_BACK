package com.cfcc.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.system.vo.SysUserCacheInfo;
import com.cfcc.modules.system.entity.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
public interface ISysUserService extends IService<SysUser> {


	public IPage<Map<String,Object>> getAllUserByAll(SysUser user,Integer pageNo,Integer pageSize);

	public SysUser getUserByName(String username);

	IPage<Map<String,Object>> pageUserAndDepart(SysUser user, Integer pageNo, Integer pageSize);

	/**
	 * 添加用户和用户角色关系
	 * @param user
	 * @param roles
	 */
	public void addUserWithRole(SysUser user,String roles);
	
	
	/**
	 * 修改用户和用户角色关系
	 * @param user
	 * @param roles
	 */
	public void editUserWithRole(SysUser user,String roles);

	/**
	 * 获取用户的授权角色
	 * @param username
	 * @return
	 */
	public List<String> getRole(String username);
	
	/**
	  * 查询用户信息包括 部门信息
	 * @param username
	 * @return
	 */
	public SysUserCacheInfo getCacheUser(String username);

	/**
	 * 根据部门Id查询
	 * @param
	 * @return
	 */
	public IPage<SysUser> getUserByDepId(Page<SysUser> page, String departId, String username);

	/**
	 * 根据角色Id查询
	 * @param
	 * @return
	 */
	public IPage<SysUser> getUserByRoleId(Page<SysUser> page,String roleId, String username);

	/**
	 * 通过用户名获取用户角色集合
	 *
	 * @param username 用户名
	 * @return 角色集合
	 */
	Set<String> getUserRolesSet(String username);

	/**
	 * 通过用户名获取用户权限集合
	 *
	 * @param username 用户名
	 * @return 权限集合
	 */
	Set<String> getUserPermissionsSet(String username);
	
	/**
	 * 根据用户名设置部门ID
	 * @param username
	 * @param orgCode
	 */
	void updateUserDepart(String username,String orgCode);
	
	/**
	 * 根据手机号获取用户名和密码
	 */
	public SysUser getUserByPhone(String phone);


	/**
	 * 根据邮箱获取用户
	 */
	public SysUser getUserByEmail(String email);


	/**
	 * 添加用户和用户部门关系
	 * @param user
	 * @param selectedParts
	 */
	void addUserWithDepart(SysUser user, String selectedParts);

	/**
	 * 编辑用户和用户部门关系
	 * @param user
	 * @param departs
	 */
	void editUserWithDepart(SysUser user, String departs);
	
	/**
	   * 校验用户是否有效
	 * @param sysUser
	 * @return
	 */
	Result checkUserIsEffective(SysUser sysUser);


	/**
	 * 获取当前用户信息
	 * @return
	 */
	SysUser getCurrentUser(HttpServletRequest request);


    List<Map<String, String>> getCurrentUserPart(String roleName);

	SysDepart getDeptById(String orgCode);

    List<Map<String, Object>> getNextUsersByDept(String drafterId, String roleScope);

    List<Map<String, Object>> getDraftMsg(String drafterId);

	List<Map<String, Object>> getNextUsersByOrg(String drafterId, String candidates);

	List<SysRole> getRoleByUserId(String id);

    List<Map<String, Object>> getCurrentUserMsg(String username);

    List<Map<String, Object>> selectAllDept(String parentId);

	List<SysUser> selectUsersByDeptAndRole(String role, ArrayList<String> deptids);

	Map<String, Object>  getAllUserMsg(String username);

    List<SysUser> queryAllUser();

	List<SysRole> getCurrentUserRoles(String id);

	LoginInfo getLoginInfo(HttpServletRequest request);

    String selectUserNameById(String userId);

    List<SysUser> queryUserByDepts(String[] ids);

    List<Map<String, Object>> getNextUsersAllDept(String candidates);

	SysUser selectById(String sysUserId);

    List<String> selectDeptsBysUsers(List<String> deptUsers);

    String getdeptIdByUser(String username);

    List<Map<String, Object>> getNextUsersMainDept(String candidates, String s_main_unit_names);

    void deleteUserById(String id);

	List<Map<String, Object>> deptUserChoice(String id);

    Map<String, SysUser> selectUsersByUids(List<String> allUserIds);

	SysUser getUserById(String id);



	//----------------以下为app-----------------------------


	Map<String, Object> getPersonage(String userId);

	Boolean updateSysUser(SysUser sysUser);

	Boolean updatePasswordById(String id, String password);

	Boolean saveavatar(String userId, String savePath);

	Boolean getAvatarByUsername(String userId, String resourceType, HttpServletResponse response);
}
