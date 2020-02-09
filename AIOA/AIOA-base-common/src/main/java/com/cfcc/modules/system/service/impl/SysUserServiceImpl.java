package com.cfcc.modules.system.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.constant.CacheConstant;
import com.cfcc.common.constant.CommonConstant;
import com.cfcc.common.system.api.ISysBaseAPI;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.common.system.vo.LoginUser;
import com.cfcc.common.system.vo.SysUserCacheInfo;
import com.cfcc.common.util.RedisUtil;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.shiro.vo.DefContants;
import com.cfcc.modules.system.entity.*;
import com.cfcc.modules.system.mapper.*;
import com.cfcc.modules.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @Author: scott
 * @Date: 2018-12-20
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysPermissionMapper sysPermissionMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysUserDepartMapper sysUserDepartMapper;
    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private SysDepartMapper sysDepartMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public IPage<Map<String,Object>> getAllUserByAll(SysUser user,Integer pageNo,Integer pageSize){
        Long total = sysUserMapper.getSysUserTotalByAll(user);
        String orderBydepart = null;
        if (user != null){
            if (user.getOrederBydepart() != null ){
                if ( user.getOrederBydepart() == -1){
                    orderBydepart = "DESC";
                }
            }
        }
        List<Map<String,Object>> sysUserList = sysUserMapper.getSysUserAllByAll(user,orderBydepart, (pageNo - 1) * pageSize,pageSize);
        for (Map<String, Object> sysUser : sysUserList) {
            String parentId = sysUser.get("parentId")+"";
            System.out.println(parentId);
            if (parentId == null || parentId == "" || parentId.equals("1")){
                sysUser.put("parentName","" );
                continue;
            }
            String parentName = sysUserMapper.getDepartNameById(parentId);
            sysUser.put("parentName",parentName );
        }
        IPage<Map<String,Object>> pageList = new Page<Map<String,Object>>();
        pageList.setRecords(sysUserList);
        pageList.setTotal(total);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;
    }

    @Override
    public IPage<Map<String,Object>> pageUserAndDepart(SysUser user, Integer pageNo, Integer pageSize){
        Long total = sysUserMapper.getSysUserTotal(user);
        if (user.getDepartName() != null){
            if ( user.getDepartName().contains("-")) {
                String departName = user.getDepartName().substring(user.getDepartName().indexOf("-") + 1);
                user.setDepartName(departName);
            }
        }
        List<Map<String,Object>> sysUserList = sysUserMapper.getSysUserAndDepartName(user, (pageNo - 1) * pageSize,pageSize);
        for (Map<String, Object> sysUser : sysUserList) {
            String parentId = sysUser.get("parentId")+"";
            if (parentId == null || parentId == "" || parentId.equals("1")){
                sysUser.put("parentName","" );
                continue;
            }
            String parentName = sysUserMapper.getDepartNameById(parentId);
            sysUser.put("parentName",parentName );
        }
        IPage<Map<String,Object>> pageList = new Page<Map<String,Object>>();
        pageList.setRecords(sysUserList);
        pageList.setTotal(total);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;
    }

    @Override
    public SysUser getUserByName(String username) {
        return userMapper.getUserByName(username);
    }


    @Override
    @Transactional
    public void addUserWithRole(SysUser user, String roles) {
        this.save(user);
        if (oConvertUtils.isNotEmpty(roles)) {
            String[] arr = roles.split(",");
            for (String roleId : arr) {
                SysUserRole userRole = new SysUserRole(user.getId(), roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
    }

    @Override
    @CacheEvict(value = CacheConstant.LOGIN_USER_RULES_CACHE, allEntries = true)
    @Transactional
    public void editUserWithRole(SysUser user, String roles) {
        this.updateById(user);
        //先删后加
        sysUserRoleMapper.delete(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, user.getId()));
        if (oConvertUtils.isNotEmpty(roles)) {
            String[] arr = roles.split(",");
            for (String roleId : arr) {
                SysUserRole userRole = new SysUserRole(user.getId(), roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
    }


    @Override
    public List<String> getRole(String username) {
        return sysUserRoleMapper.getRoleByUserName(username);
    }

    /**
     * 通过用户名获取用户角色集合
     *
     * @param username 用户名
     * @return 角色集合
     */
    @Override
    @Cacheable(value = CacheConstant.LOGIN_USER_RULES_CACHE, key = "'Roles_'+#username")
    public Set<String> getUserRolesSet(String username) {
        // 查询用户拥有的角色集合
        List<String> roles = sysUserRoleMapper.getRoleByUserName(username);
        log.info("-------通过数据库读取用户拥有的角色Rules------username： " + username + ",Roles size: " + (roles == null ? 0 : roles.size()));
        return new HashSet<>(roles);
    }

    /**
     * 通过用户名获取用户权限集合
     *
     * @param username 用户名
     * @return 权限集合
     */
    @Override
    @Cacheable(value = CacheConstant.LOGIN_USER_RULES_CACHE, key = "'Permissions_'+#username")
    public Set<String> getUserPermissionsSet(String username) {
        Set<String> permissionSet = new HashSet<>();
        List<SysPermission> permissionList = sysPermissionMapper.queryByUser(username);
        for (SysPermission po : permissionList) {
//			// TODO URL规则有问题？
//			if (oConvertUtils.isNotEmpty(po.getUrl())) {
//				permissionSet.add(po.getUrl());
//			}
            if (oConvertUtils.isNotEmpty(po.getPerms())) {
                permissionSet.add(po.getPerms());
            }
        }
        log.info("-------通过数据库读取用户拥有的权限Perms------username： " + username + ",Perms size: " + (permissionSet == null ? 0 : permissionSet.size()));
        return permissionSet;
    }

    @Override
    public SysUserCacheInfo getCacheUser(String username) {
        SysUserCacheInfo info = new SysUserCacheInfo();
        info.setOneDepart(true);
//		SysUser user = userMapper.getUserByName(username);
//		info.setSysUserCode(user.getUsername());
//		info.setSysUserName(user.getRealname());


        LoginUser user = sysBaseAPI.getUserByName(username);
        if (user != null) {
            info.setSysUserCode(user.getUsername());
            info.setSysUserName(user.getRealname());
            info.setSysOrgCode(user.getOrgCode());
        }

        //多部门支持in查询
        List<SysDepart> list = sysDepartMapper.queryUserDeparts(user.getId());
        List<String> sysMultiOrgCode = new ArrayList<String>();
        if (list == null || list.size() == 0) {
            //当前用户无部门
            //sysMultiOrgCode.add("0");
        } else if (list.size() == 1) {
            sysMultiOrgCode.add(list.get(0).getOrgCode());
        } else {
            info.setOneDepart(false);
            for (SysDepart dpt : list) {
                sysMultiOrgCode.add(dpt.getOrgCode());
            }
        }
        info.setSysMultiOrgCode(sysMultiOrgCode);

        return info;
    }

    // 根据部门Id查询
    @Override
    public IPage<SysUser> getUserByDepId(Page<SysUser> page, String departId, String username) {
        return userMapper.getUserByDepId(page, departId, username);
    }


    // 根据角色Id查询
    @Override
    public IPage<SysUser> getUserByRoleId(Page<SysUser> page, String roleId, String username) {
        return userMapper.getUserByRoleId(page, roleId, username);
    }


    @Override
    public void updateUserDepart(String username, String orgCode) {
        baseMapper.updateUserDepart(username, orgCode);
    }


    @Override
    public SysUser getUserByPhone(String phone) {
        return userMapper.getUserByPhone(phone);
    }


    @Override
    public SysUser getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }

    @Override
    @Transactional
    public void addUserWithDepart(SysUser user, String selectedParts) {
//		this.save(user);  //保存角色的时候已经添加过一次了
        if (oConvertUtils.isNotEmpty(selectedParts)) {
            String[] arr = selectedParts.split(",");
            for (String deaprtId : arr) {
                SysUserDepart userDeaprt = new SysUserDepart(user.getId(), deaprtId);
                sysUserDepartMapper.insert(userDeaprt);
            }
        }
    }


    @Override
    @Transactional
    @CacheEvict(value = "loginUser_cacheRules", allEntries = true)
    public void editUserWithDepart(SysUser user, String departs) {
        this.updateById(user);  //更新角色的时候已经更新了一次了，可以再跟新一次
        //先删后加
        sysUserDepartMapper.delete(new QueryWrapper<SysUserDepart>().lambda().eq(SysUserDepart::getUserId, user.getId()));
        if (oConvertUtils.isNotEmpty(departs)) {
            String[] arr = departs.split(",");
            for (String departId : arr) {
                SysUserDepart userDepart = new SysUserDepart(user.getId(), departId);
                sysUserDepartMapper.insert(userDepart);
            }
        }
    }


    /**
     * 校验用户是否有效
     *
     * @param sysUser
     * @return
     */
    @Override
    public Result<?> checkUserIsEffective(SysUser sysUser) {
        Result<?> result = new Result<Object>();
        //情况1：根据用户信息查询，该用户不存在
        if (sysUser == null) {
            result.error500("该用户不存在，请注册");
            sysBaseAPI.addLog("用户登录失败，用户不存在！", CommonConstant.LOG_TYPE_1, null);
            return result;
        }
        //情况2：根据用户信息查询，该用户已注销
        if (CommonConstant.DEL_FLAG_1.toString().equals(sysUser.getDelFlag())) {
            sysBaseAPI.addLog("用户登录失败，用户名:" + sysUser.getUsername() + "已注销！", CommonConstant.LOG_TYPE_1, null);
            result.error500("该用户已注销");
            return result;
        }
        //情况3：根据用户信息查询，该用户已冻结
        if (CommonConstant.USER_FREEZE.equals(sysUser.getStatus())) {
            sysBaseAPI.addLog("用户登录失败，用户名:" + sysUser.getUsername() + "已冻结！", CommonConstant.LOG_TYPE_1, null);
            result.error500("该用户已冻结");
            return result;
        }
        return result;
    }

    @Override
    public SysUser getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader(DefContants.X_ACCESS_TOKEN);
        String username = JwtUtil.getUsername(token);
        SysUser user = getUserByName(username);
        return user;
    }

    @Override
    public List<Map<String, String>> getCurrentUserPart(String roleName) {
        return sysUserRoleMapper.getCurrentUserPart(roleName);
    }

    @Override
    public SysDepart getDeptById(String orgCode) {
        return sysUserRoleMapper.getDeptById(orgCode);
    }

    @Override
    public List<Map<String, Object>> getNextUsersByDept(String drafterId, String roleScope) {

        return userMapper.getNextUsersByDept(drafterId, roleScope);
    }

    @Override
    public List<Map<String, Object>> getDraftMsg(String drafterId) {
        return userMapper.getDraftMsg(drafterId);
    }

    @Override
    public List<Map<String, Object>> getNextUsersByOrg(String drafterId, String candidates) {
        return userMapper.getNextUsersByOrg(drafterId, candidates);
    }

    @Override
    public List<SysRole> getRoleByUserId(String id) {
        return userMapper.getRoleByUserId(id);
    }

    @Override
    public List<Map<String, Object>> getCurrentUserMsg(String username) {
        return userMapper.getCurrentUserMsg(username);
    }

    @Override
    public List<Map<String, Object>> selectAllDept(String parentId) {
        return userMapper.selectAllDept( parentId);
    }

    @Override
    public List<SysUser> selectUsersByDeptAndRole(String role, ArrayList<String> deptids) {
        return userMapper.selectUsersByDeptAndRole(role, deptids);
    }

    @Override
    public Map<String, Object> getAllUserMsg(String username) {


        SysUser sysUser = getUserByName(username);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sysUserId", sysUser.getId());
        map.put("sysUserCode", sysUser.getUsername()); // 当前登录用户登录账号
        map.put("sysUserName", sysUser.getRealname()); // 当前登录用户真实名称
        String orgCode = sysUser.getOrgCode();
        map.put("sysOrgCode", orgCode); // 当前登录用户部门编号
        if (null == orgCode) orgCode = userMapper.selectOrgCodeByUserId(sysUser.getId());
        SysDepart dept = getDeptById(orgCode);
        map.put("sysDeptName", dept.getDepartName()); // 部门名称
        map.put("deptId", dept.getId()); // 部门名称
        map.put("parentId", dept.getParentId()); // 机构id

        List<SysRole> role = getRoleByUserId(sysUser.getId());
        map.put("role", role);

        return map;
    }

    @Override
    public List<SysUser> queryAllUser() {
        List<SysUser> sList = userMapper.queryAllUser();
        return sList;
    }

    @Override
    public List<SysRole> getCurrentUserRoles(String id) {
        return sysUserRoleMapper.currentUserRoles(id);
    }


    @Override
    public LoginInfo getLoginInfo(HttpServletRequest request) {

        String token = request.getHeader(DefContants.X_ACCESS_TOKEN);

        return (LoginInfo) redisUtil.get(CommonConstant.LOGIN_INFO + token);
    }

    @Override
    public String selectUserNameById(String userId) {
        return userMapper.selectUserNameById( userId) ;
    }

    @Override
    public List<SysUser> queryUserByDepts(String[] ids) {

        return userMapper.queryUserByDepts(ids);
    }

    @Override
    public List<Map<String, Object>> getNextUsersAllDept(String candidates) {
        return userMapper.getNextUsersAllDept(candidates);
    }

    @Override
    public SysUser selectById(String sysUserId) {
        return userMapper.findById(sysUserId);
    }
}
