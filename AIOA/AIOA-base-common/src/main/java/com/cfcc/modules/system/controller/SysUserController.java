package com.cfcc.modules.system.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.PermissionData;
import com.cfcc.common.constant.CommonConstant;
import com.cfcc.common.system.api.ISysBaseAPI;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.common.system.vo.LoginUser;
import com.cfcc.common.util.PasswordUtil;
import com.cfcc.common.util.RedisUtil;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.shiro.vo.DefContants;
import com.cfcc.modules.system.entity.*;
import com.cfcc.modules.system.model.DepartIdModel;
import com.cfcc.modules.system.service.*;
import com.cfcc.modules.system.vo.SysDepartUsersVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Api(tags = "用户相关接口")
@Slf4j
@RestController
@RequestMapping("/sys/user")
public class SysUserController {
    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysDepartService sysDepartService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private ISysUserDepartService sysUserDepartService;

    @Autowired
    private ISysUserRoleService userRoleService;

    @Autowired
    private ISysUserManagedeptsService sysUserManagedeptsService;

    @Autowired
    private ISysUserFunService iSysUserFunService;


    @Autowired
    private RedisUtil redisUtil;

    @GetMapping(value = "/queryAllUser")
    public Result<List<SysUser>> queryAllUser() {
        Result<List<SysUser>> result = new Result<>();
        List<SysUser> sysUserList = sysUserService.queryAllUser();
        result.setSuccess(true);
        result.setResult(sysUserList);
        return result;
    }

    @PermissionData(pageComponent = "system/UserList")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<Map<String,Object>>> queryPageList(SysUser user, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<Map<String,Object>>> result = new Result<IPage<Map<String,Object>>>();
        IPage<Map<String,Object>> pageList = sysUserService.getAllUserByAll(user,pageNo,pageSize);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }


    @PermissionData(pageComponent = "system/UserList")
    @RequestMapping(value = "/lists", method = RequestMethod.GET)
    public Result<IPage<Map<String,Object>>> queryPageLists(SysUser user, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<Map<String,Object>>> result = new Result<IPage<Map<String,Object>>>();
        if (user != null){
            if (user.getUsername() != null){
                String username = user.getUsername().trim();
                user.setUsername(username);
            }
            if (user.getDepartName() != null){
                try {
                    String departName = user.getDepartName().trim();
                    user.setDepartName(departName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        IPage<Map<String,Object>> pageList = sysUserService.pageUserAndDepart(user, pageNo, pageSize);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @RequiresPermissions("user:add")
    public Result<SysUser> add(@RequestBody JSONObject jsonObject) {
        Result<SysUser> result = new Result<SysUser>();
        String selectedRoles = jsonObject.getString("selectedroles");
        String selectedDeparts = jsonObject.getString("selecteddeparts");
        try {
            SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
            user.setCreateTime(new Date());//设置创建时间
            String salt = oConvertUtils.randomGen(8);
            user.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), salt);
            user.setPassword(passwordEncode);
            user.setStatus(1);
            user.setDelFlag("0");
            sysUserService.addUserWithRole(user, selectedRoles);
            sysUserService.addUserWithDepart(user, selectedDeparts);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    //@RequiresPermissions("user:edit")
    public Result<SysUser> edit(@RequestBody JSONObject jsonObject) {
        Result<SysUser> result = new Result<SysUser>();
        try {
            SysUser sysUser = sysUserService.getById(jsonObject.getString("id"));
            sysBaseAPI.addLog("编辑用户，id： " + jsonObject.getString("id"), CommonConstant.LOG_TYPE_2, 2);
            if (sysUser == null) {
                result.error500("未找到对应实体");
            } else {
                SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
                user.setUpdateTime(new Date());
                //String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), sysUser.getSalt());
                user.setPassword(sysUser.getPassword());
                String roles = jsonObject.getString("selectedroles");
                String departs = jsonObject.getString("selecteddeparts");
                sysUserService.editUserWithRole(user, roles);
                sysUserService.editUserWithDepart(user, departs);
                result.success("修改成功!");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 删除用户
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<SysUser> delete(@RequestParam(name = "id", required = true) String id) {
        Result<SysUser> result = new Result<SysUser>();
        // 定义SysUserDepart实体类的数据库查询LambdaQueryWrapper
        LambdaQueryWrapper<SysUserDepart> query = new LambdaQueryWrapper<SysUserDepart>();
        SysUser sysUser = sysUserService.getUserById(id);
//        SysUser sysUser = sysUserService.getById(id);
        sysBaseAPI.addLog("删除用户，id： " + id, CommonConstant.LOG_TYPE_2, 3);
        if (sysUser == null) {
            result.error500("未找到对应实体");
        } else {
            // 当某个用户被删除时,删除其ID下对应的部门数据
            query.eq(SysUserDepart::getUserId, id);
            sysUserService.deleteUserById(id);
//            boolean ok = sysUserService.removeById(id);
            sysUserDepartService.remove(query);
//            if (ok) {
                result.success("删除成功!");
//            }
        }

        return result;
    }

    /**
     * 批量删除用户
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<SysUser> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        // 定义SysUserDepart实体类的数据库查询对象LambdaQueryWrapper
        LambdaQueryWrapper<SysUserDepart> query = new LambdaQueryWrapper<SysUserDepart>();
        String[] idArry = ids.split(",");
        sysBaseAPI.addLog("批量删除用户， id： " + idArry.toString(), CommonConstant.LOG_TYPE_2, 3);
        Result<SysUser> result = new Result<SysUser>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
//            this.sysUserService.removeByIds(Arrays.asList(ids.split(",")));
//            // 当批量删除时,删除在SysUserDepart中对应的所有部门数据
//            for (String id : idArry) {
//                query.eq(SysUserDepart::getUserId, id);
//                this.sysUserDepartService.remove(query);
//            }
            try {
                for (String id : idArry) {
                    sysUserService.deleteUserById(id);
                    sysUserDepartService.deleteUserDepartByUserId(id);
                    sysUserManagedeptsService.deleteUserManageDepartsListByUserId(id);
                }
            } catch (Exception e) {
//                e.printStackTrace();
                result.success("删除异常");
            }
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 冻结&解冻用户
     *
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/frozenBatch", method = RequestMethod.PUT)
    public Result<SysUser> frozenBatch(@RequestBody JSONObject jsonObject) {
        Result<SysUser> result = new Result<SysUser>();
        try {
            String ids = jsonObject.getString("ids");
            String status = jsonObject.getString("status");
            String[] arr = ids.split(",");
            for (String id : arr) {
                if (oConvertUtils.isNotEmpty(id)) {
                    this.sysUserService.update(new SysUser().setStatus(Integer.parseInt(status)),
                            new UpdateWrapper<SysUser>().lambda().eq(SysUser::getId, id));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败" + e.getMessage());
        }
        result.success("操作成功!");
        return result;

    }

    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<SysUser> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysUser> result = new Result<SysUser>();
        SysUser sysUser = sysUserService.getById(id);
        if (sysUser == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(sysUser);
            result.setSuccess(true);
        }
        return result;
    }

    @ApiOperation(value = "查询用户相关角色id")
    @RequestMapping(value = "/queryUserRole", method = RequestMethod.GET)
    public Result<List<String>> queryUserRole(@RequestParam(name = "userid", required = true) String userid) {
        Result<List<String>> result = new Result<>();
        List<String> list = new ArrayList<String>();
        List<SysUserRole> userRole = sysUserRoleService.list(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, userid));
        if (userRole == null || userRole.size() <= 0) {
            result.error500("未找到用户相关角色信息");
        } else {
            for (SysUserRole sysUserRole : userRole) {
                list.add(sysUserRole.getRoleId());
            }
            result.setSuccess(true);
            result.setResult(list);
        }
        return result;
    }

    @ApiOperation(value = "查询用户相关角色信息")
    @RequestMapping(value = "/queryUserRoleAll", method = RequestMethod.GET)
    public Result<List<SysUserRole>> queryUserRoleAll(@RequestParam(name = "userid", required = true) String userid) {
        Result<List<SysUserRole>> result = new Result<>();
        List<SysUserRole> list = new ArrayList<SysUserRole>();
        List<SysUserRole> userRole = sysUserRoleService.list(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, userid));
        if (userRole == null || userRole.size() <= 0) {
            result.error500("未找到用户相关角色信息");
        } else {
            for (SysUserRole sysUserRole : userRole) {
                list.add(sysUserRole);
            }
            result.setSuccess(true);
            result.setResult(list);
        }
        return result;
    }


    /**
     * 校验用户账号是否唯一<br>
     * 可以校验其他 需要检验什么就传什么。。。
     *
     * @param sysUser
     * @return
     */
    @RequestMapping(value = "/checkOnlyUser", method = RequestMethod.GET)
    public Result<Boolean> checkOnlyUser(SysUser sysUser) {
        Result<Boolean> result = new Result<>();
        //如果此参数为false则程序发生异常
        result.setResult(true);
        try {
            //通过传入信息查询新的用户信息
            SysUser user = sysUserService.getOne(new QueryWrapper<SysUser>(sysUser));
            if (user != null) {
                result.setSuccess(false);
                result.setMessage("用户账号已存在");
                return result;
            }

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    /**
     * 修改密码
     */
    @RequestMapping(value = "/changPassword", method = RequestMethod.PUT)
    public Result<SysUser> changPassword(@RequestBody SysUser sysUser) {
        Result<SysUser> result = new Result<SysUser>();
        String password = sysUser.getPassword();
        sysUser = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, sysUser.getUsername()));
        if (sysUser == null) {
            result.error500("未找到对应实体");
        } else {
            String salt = oConvertUtils.randomGen(8);
            sysUser.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
            sysUser.setPassword(passwordEncode);
            this.sysUserService.updateById(sysUser);
            result.setResult(sysUser);
            result.success("密码修改完成！");
        }
        return result;
    }
    /**
     * 修改IP
     */
    @RequestMapping(value = "/changeIP", method = RequestMethod.POST)
    public Result<SysUser> changIp(@RequestBody  Map<String,Object> map,HttpServletRequest request) {
        Result<SysUser> result = new Result<SysUser>();
        //查询当前用户，作为assignee
        String token = request.getHeader(DefContants.X_ACCESS_TOKEN);
        String username = JwtUtil.getUsername(token);
        SysUser sysUser = sysUserService.getUserByName(username);
        if (sysUser == null) {
            result.error500("未找到对应实体");
        } else {
            String avatar = map.get("ip")+"";
            sysUser.setAvatar(avatar);
            this.sysUserService.updateById(sysUser);
            result.setResult(sysUser);
        }
        return result;
    }

    /**
     * 查询指定用户和部门关联的数据
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/userDepartList", method = RequestMethod.GET)
    public Result<List<DepartIdModel>> getUserDepartsList(@RequestParam(name = "userId", required = true) String userId) {
        Result<List<DepartIdModel>> result = new Result<>();
        try {
            List<DepartIdModel> depIdModelList = this.sysUserDepartService.queryDepartIdsOfUser(userId);
            if (depIdModelList != null && depIdModelList.size() > 0) {
                result.setSuccess(true);
                result.setMessage("查找成功");
                result.setResult(depIdModelList);
            } else {
                result.setSuccess(false);
                result.setMessage("查找失败");
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("查找过程中出现了异常: " + e.getMessage());
            return result;
        }

    }

    /**
     * 生成在添加用户情况下没有主键的问题,返回给前端,根据该id绑定部门数据
     *
     * @return
     */
    @RequestMapping(value = "/generateUserId", method = RequestMethod.GET)
    public Result<String> generateUserId() {
        Result<String> result = new Result<>();
        //System.out.println("我执行了,生成用户ID==============================");
        String userId = UUID.randomUUID().toString().replace("-", "");
        result.setSuccess(true);
        result.setResult(userId);
        return result;
    }

    /**
     * 根据部门id查询用户信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/queryUserByDepId", method = RequestMethod.GET)
    public Result<List<SysUser>> queryUserByDepId(@RequestParam(name = "id", required = true) String id) {
        Result<List<SysUser>> result = new Result<>();
        List<SysUser> userList = sysUserDepartService.queryUserByDepId(id);
        try {
            result.setSuccess(true);
            result.setResult(userList);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            return result;
        }
    }

    /**
     * 查询所有用户所对应的角色信息
     *
     * @return
     */
    @RequestMapping(value = "/queryUserRoleMap", method = RequestMethod.GET)
    public Result<Map<String, String>> queryUserRole() {
        Result<Map<String, String>> result = new Result<>();
        Map<String, String> map = userRoleService.queryUserRole();
        result.setResult(map);
        result.setSuccess(true);
        return result;
    }

    /**
     * 导出excel
     *
     * @param sysUser
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysUser sysUser, HttpServletRequest request) {
        // Step.1 组装查询条件
        QueryWrapper<SysUser> queryWrapper = QueryGenerator.initQueryWrapper(sysUser, request.getParameterMap());
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SysUser> pageList = sysUserService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "用户列表");
        mv.addObject(NormalExcelConstants.CLASS, SysUser.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("用户列表数据", "导出人:" + user.getRealname(), "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    //@RequiresPermissions("user:import")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<SysUser> listSysUsers = ExcelImportUtil.importExcel(file.getInputStream(), SysUser.class, params);
                for (SysUser sysUserExcel : listSysUsers) {
                    if (sysUserExcel.getPassword() == null) {
                        // 密码默认为“123456”
                        sysUserExcel.setPassword("123456");
                    }
                    String salt = oConvertUtils.randomGen(8);
                    String passwordEncode = PasswordUtil.encrypt(sysUserExcel.getUsername(), sysUserExcel.getPassword(), salt);
                    sysUserExcel.setSalt(salt);
                    sysUserExcel.setPassword(passwordEncode);
                    sysUserService.save(sysUserExcel);
                }
                return Result.ok("文件导入成功！数据行数：" + listSysUsers.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("抱歉! 您导入的数据中用户名已经存在.");
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    /**
     * @param userIds
     * @return
     * @功能：根据id 批量查询
     */
    @RequestMapping(value = "/queryByIds", method = RequestMethod.GET)
    public Result<Collection<SysUser>> queryByIds(@RequestParam String userIds) {
        Result<Collection<SysUser>> result = new Result<>();
        String[] userId = userIds.split(",");
        Collection<String> idList = Arrays.asList(userId);
        Collection<SysUser> userRole = sysUserService.listByIds(idList);
        result.setSuccess(true);
        result.setResult(userRole);
        return result;
    }

    /**
     * 首页密码修改
     */
    @RequestMapping(value = "/updatePassword", method = RequestMethod.PUT)
    public Result<SysUser> changPassword(@RequestBody JSONObject json) {
        Result<SysUser> result = new Result<SysUser>();
        String username = json.getString("username");
        String oldpassword = json.getString("oldpassword");
        SysUser user = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) {
            result.error500("未找到用户!");
            return result;
        }
        String passwordEncode = PasswordUtil.encrypt(username, oldpassword, user.getSalt());
        if (!user.getPassword().equals(passwordEncode)) {
            result.error500("旧密码输入错误!");
            return result;
        }

        String password = json.getString("password");
        String confirmpassword = json.getString("confirmpassword");
        if (oConvertUtils.isEmpty(password)) {
            result.error500("新密码不存在!");
            return result;
        }

        if (!password.equals(confirmpassword)) {
            result.error500("两次输入密码不一致!");
            return result;
        }
        String newpassword = PasswordUtil.encrypt(username, password, user.getSalt());
        this.sysUserService.update(new SysUser().setPassword(newpassword), new LambdaQueryWrapper<SysUser>().eq(SysUser::getId, user.getId()));
        result.success("密码修改完成！");
        return result;
    }

    @RequestMapping(value = "/userRoleList", method = RequestMethod.GET)
    public Result<IPage<SysUser>> userRoleList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysUser>> result = new Result<IPage<SysUser>>();
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        String roleId = req.getParameter("roleId");
        String username = req.getParameter("username");
        IPage<SysUser> pageList = sysUserService.getUserByRoleId(page, roleId, username);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }


  /*  String sysRoleId = sysUserRoleVO.getRoleId();
            for (String sysUserId : sysUserRoleVO.getUserIdList()) {
        SysUserRole sysUserRole = new SysUserRole(sysUserId, sysRoleId);
        QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
        queryWrapper.eq("role_id", sysRoleId).eq("user_id", sysUserId);
        SysUserRole one = sysUserRoleService.getOne(queryWrapper);
        if (one == null) {
            sysUserRoleService.save(sysUserRole);
        }
    }*/
    /**
     * 给指定角色添加用户
     *
     * @paramSysUserRoleVO sysUserRoleVO
     * @return Map<String,Object> map
     */
    @RequestMapping(value = "/addSysUserRole", method = RequestMethod.POST)
    public Result<String> addSysUserRole(@RequestBody Map<String,Object> map ) {
        Result<String> result = new Result<String>();
        if (map.get("roleId") == null || map.get("roleId") == ""){
            result.setSuccess(false);
            result.setMessage("不存在该角色");
            return result;
        }
        try {
            String sysRoleId = map.get("roleId")+"";
            List<String> userIdList = (List<String>) map.get("userIdList");
            for (String sysUserId : userIdList) {
                SysUserRole sysUserRole = new SysUserRole(sysUserId, sysRoleId);
                QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
                queryWrapper.eq("role_id", sysRoleId).eq("user_id", sysUserId);
                SysUserRole one = sysUserRoleService.getOne(queryWrapper);
                if (one == null) {
                    sysUserRoleService.save(sysUserRole);
                }
            }
            result.setMessage("添加成功!");
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("出错了: " + e.getMessage());
            return result;
        }
    }

    /**
     * 删除指定角色的用户关系
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/deleteUserRole", method = RequestMethod.DELETE)
    public Result<SysUserRole> deleteUserRole(@RequestParam(name = "roleId") String roleId,
                                              @RequestParam(name = "userId", required = true) String userId
    ) {
        Result<SysUserRole> result = new Result<SysUserRole>();
        try {
            QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
            queryWrapper.eq("role_id", roleId).eq("user_id", userId);
            sysUserRoleService.remove(queryWrapper);
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 批量删除指定角色的用户关系
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/deleteUserRoleBatch", method = RequestMethod.DELETE)
    public Result<SysUserRole> deleteUserRoleBatch(
            @RequestParam(name = "roleId") String roleId,
            @RequestParam(name = "userIds", required = true) String userIds) {
        Result<SysUserRole> result = new Result<SysUserRole>();
        try {
            QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
            queryWrapper.eq("role_id", roleId).in("user_id", Arrays.asList(userIds.split(",")));
            sysUserRoleService.remove(queryWrapper);
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 部门用户列表
     */
    @RequestMapping(value = "/departUserList", method = RequestMethod.GET)
    public Result<IPage<SysUser>> departUserList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysUser>> result = new Result<IPage<SysUser>>();
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        String depId = req.getParameter("depId");
        String username = req.getParameter("username");
        IPage<SysUser> pageList = sysUserService.getUserByDepId(page, depId, username);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 给指定部门添加对应的用户
     */
    @RequestMapping(value = "/editSysDepartWithUser", method = RequestMethod.POST)
    public Result<String> editSysDepartWithUser(@RequestBody SysDepartUsersVO sysDepartUsersVO) {
        Result<String> result = new Result<String>();
        try {
            String sysDepId = sysDepartUsersVO.getDepId();
            for (String sysUserId : sysDepartUsersVO.getUserIdList()) {
                SysUserDepart sysUserDepart = new SysUserDepart(null, sysUserId, sysDepId);
                QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
                queryWrapper.eq("dep_id", sysDepId).eq("user_id", sysUserId);
                SysUserDepart one = sysUserDepartService.getOne(queryWrapper);
                if (one == null) {
                    sysUserDepartService.save(sysUserDepart);
                }
            }
            result.setMessage("添加成功!");
            result.setSuccess(true);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("出错了: " + e.getMessage());
            return result;
        }
    }

    /**
     * 删除指定机构的用户关系
     */
    @RequestMapping(value = "/deleteUserInDepart", method = RequestMethod.DELETE)
    public Result<SysUserDepart> deleteUserInDepart(@RequestParam(name = "depId") String depId,
                                                    @RequestParam(name = "userId", required = true) String userId
    ) {
        Result<SysUserDepart> result = new Result<SysUserDepart>();
        try {
            QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
            queryWrapper.eq("dep_id", depId).eq("user_id", userId);
            sysUserDepartService.remove(queryWrapper);
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 批量删除指定机构的用户关系
     */
    @RequestMapping(value = "/deleteUserInDepartBatch", method = RequestMethod.DELETE)
    public Result<SysUserDepart> deleteUserInDepartBatch(
            @RequestParam(name = "depId") String depId,
            @RequestParam(name = "userIds", required = true) String userIds) {
        Result<SysUserDepart> result = new Result<SysUserDepart>();
        try {
            QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
            queryWrapper.eq("dep_id", depId).in("user_id", Arrays.asList(userIds.split(",")));
            sysUserDepartService.remove(queryWrapper);
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 查询当前用户的所有部门/当前部门编码
     *
     * @return
     */
    @RequestMapping(value = "/getCurrentUserDeparts", method = RequestMethod.GET)
    public Result<Map<String, Object>> getCurrentUserDeparts() {
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            List<SysDepart> list = this.sysDepartService.queryUserDeparts(sysUser.getId());
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("list", list);
            map.put("orgCode", sysUser.getOrgCode());
            result.setSuccess(true);
            result.setResult(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("查询失败！");
        }
        return result;
    }


    /**
     * 用户注册接口
     *
     * @param jsonObject
     * @param user
     * @return
     */
    @PostMapping("/register")
    public Result<JSONObject> userRegister(@RequestBody JSONObject jsonObject, SysUser user) {
        Result<JSONObject> result = new Result<JSONObject>();
        String phone = jsonObject.getString("phone");
        String smscode = jsonObject.getString("smscode");
        Object code = redisUtil.get(phone);
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        String email = jsonObject.getString("email");
        SysUser sysUser1 = sysUserService.getUserByName(username);
        if (sysUser1 != null) {
            result.setMessage("用户名已注册");
            result.setSuccess(false);
            return result;
        }
        SysUser sysUser2 = sysUserService.getUserByPhone(phone);

        if (sysUser2 != null) {
            result.setMessage("该手机号已注册");
            result.setSuccess(false);
            return result;
        }
        SysUser sysUser3 = sysUserService.getUserByEmail(email);
        if (sysUser3 != null) {
            result.setMessage("邮箱已被注册");
            result.setSuccess(false);
            return result;
        }

        if (!smscode.equals(code)) {
            result.setMessage("手机验证码错误");
            result.setSuccess(false);
            return result;
        }

        try {
            user.setCreateTime(new Date());// 设置创建时间
            String salt = oConvertUtils.randomGen(8);
            String passwordEncode = PasswordUtil.encrypt(username, password, salt);
            user.setSalt(salt);
            user.setUsername(username);
            user.setRealname(username);
            user.setPassword(passwordEncode);
            user.setEmail(email);
            user.setPhone(phone);
            user.setStatus(1);
            user.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
            user.setActivitiSync(CommonConstant.ACT_SYNC_1);
            sysUserService.addUserWithRole(user, "ee8626f80f7c2619917b6236f3a7f02b");//默认临时角色 test
            result.success("注册成功");
        } catch (Exception e) {
            result.error500("注册失败");
        }
        return result;
    }

    /**
     * @return
     */
    @GetMapping("/querySysUser")
    public Result<Map<String, Object>> querySysUser(SysUser sysUser) {
        String phone = sysUser.getPhone();
        String username = sysUser.getUsername();
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        if (oConvertUtils.isNotEmpty(phone)) {
            SysUser user = sysUserService.getUserByPhone(phone);
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("phone", user.getPhone());
                result.setSuccess(true);
                result.setResult(map);
                return result;
            }
        }
        if (oConvertUtils.isNotEmpty(username)) {
            SysUser user = sysUserService.getUserByName(username);
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("phone", user.getPhone());
                result.setSuccess(true);
                result.setResult(map);
                return result;
            }
        }
        result.setSuccess(false);
        result.setMessage("验证失败");
        return result;
    }

    /**
     * 用户手机号验证
     */
    @PostMapping("/phoneVerification")
    public Result<String> phoneVerification(@RequestBody JSONObject jsonObject) {
        Result<String> result = new Result<String>();
        String phone = jsonObject.getString("phone");
        String smscode = jsonObject.getString("smscode");
        Object code = redisUtil.get(phone);
        if (!smscode.equals(code)) {
            result.setMessage("手机验证码错误");
            result.setSuccess(false);
            return result;
        }
        redisUtil.set(phone, smscode);
        result.setResult(smscode);
        result.setSuccess(true);
        return result;
    }

    /**
     * 用户更改密码
     */
    @GetMapping("/passwordChange")
    public Result<SysUser> passwordChange(@RequestParam(name = "username") String username,
                                          @RequestParam(name = "password") String password,
                                          @RequestParam(name = "smscode") String smscode,
                                          @RequestParam(name = "phone") String phone) {
        Result<SysUser> result = new Result<SysUser>();
        SysUser sysUser = new SysUser();
        Object object = redisUtil.get(phone);
        if (null == object) {
            result.setMessage("更改密码失败");
            result.setSuccess(false);
        }
        if (!smscode.equals(object)) {
            result.setMessage("更改密码失败");
            result.setSuccess(false);
        }
        sysUser = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (sysUser == null) {
            result.setMessage("未找到对应实体");
            result.setSuccess(false);
            return result;
        } else {
            String salt = oConvertUtils.randomGen(8);
            sysUser.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
            sysUser.setPassword(passwordEncode);
            this.sysUserService.updateById(sysUser);
            result.setSuccess(true);
            result.setMessage("密码修改完成！");
            return result;
        }
    }


    /**
     * 根据TOKEN获取用户的部分信息（返回的数据是可供表单设计器使用的数据）
     *
     * @return
     */
    @GetMapping("/getUserSectionInfoByToken")
    public Result<?> getUserSectionInfoByToken(HttpServletRequest request, @RequestParam(name = "token", required = false) String token) {
        try {
            String username = null;
            // 如果没有传递token，就从header中获取token并获取用户信息
            if (oConvertUtils.isEmpty(token)) {
                username = JwtUtil.getUserNameByToken(request);
            } else {
                username = JwtUtil.getUsername(token);
            }

            log.info(" ------ 通过令牌获取部分用户信息，当前用户： " + username);

            // 根据用户名查询用户信息
            Map map = sysUserService.getAllUserMsg(username);


            log.info(" ------ 通过令牌获取部分用户信息，已获取的用户信息： " + map);

            return Result.ok(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(500, "查询失败:" + e.getMessage());
        }
    }


    @ApiOperation("根据角色名查询相关信息")
    @GetMapping("getMsgByRoleName")
    public Result getCurrentUserPart(String roleName) {
        try {
            List<Map<String, String>> res = sysUserService.getCurrentUserPart(roleName);
            return Result.ok(res);
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error("查询失败");
        }
    }

    /**
     * 获取当前用户信息[已选择部门]
     */
    @GetMapping("getLoginInfo")
    public LoginInfo getLoginInfo(HttpServletRequest request) {
        return sysUserService.getLoginInfo(request);
    }

    /**
     * 根据部门查询用户
     */
    @PostMapping("queryUserByDepts")
    public Result queryUserByDepts(@RequestBody Map<String, String[]> data) {
        List<SysUser> users = sysUserService.queryUserByDepts(data.get("ids"));
        return Result.ok(users);
    }
    /**
     * 根据id查询用户
     */
    @GetMapping(value = "/queryUserNameById")
    public Result<String> queryUserNameById(@RequestParam(name = "userId",required=true) String userId,
                                            @RequestParam(value = "orgSchema", required = false) String orgSchema,
                                            HttpServletRequest request){
        Result<String> result = new Result<>();
        String userName=sysUserService.selectUserNameById(userId);
        result.setResult(userName);
        return result;
    }

    @PostMapping(value = "/addUserFun")
    public Result<String> addUserFun(@RequestParam(name = "userId",required=true) String userId,
                                     @RequestParam(name = "modelId",required=true) Integer modelId,
                                     @RequestParam(name = "functionId",required=true) Integer functionId,
                                     @RequestParam(name = "status",required=false) Integer status){
        Result<String> result = new Result<>();
        int num=iSysUserFunService.addUserFun(userId,modelId,functionId,status);
        if (num!=1){
            result.setSuccess(false);
        }else if (num == 1){
            result.setSuccess(true);
        }
        return result;
    }

    @GetMapping(value = "/showUserFun")
    public Result<List<String>> showUserFun(@RequestParam(name = "userId",required=true) String userId){
        Result<List<String>> result = new Result<>();
        List<String> list=iSysUserFunService.showUserFun(userId);
        if (list.size()==0){
            result.setSuccess(false);
        }else {
            result.setSuccess(true);
        }
        result.setResult(list);
        return result;
    }

}
