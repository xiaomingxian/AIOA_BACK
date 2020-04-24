package com.cfcc.modules.app.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.entity.SysUserDepart;
import com.cfcc.modules.system.service.ISysUserDepartService;
import com.cfcc.modules.system.service.ISysUserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * @Author
 * @since
 */
@Slf4j
@RestController
@RequestMapping("/app/sysUser")
public class AppUserController {

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysUserDepartService sysUserDepartService;

    @AutoLog(value = "用户管理")
    @ApiOperation(value="我的-用户管理查询", notes="个人")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<Map<String,Object>>> queryPageList(SysUser user, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<Map<String,Object>>> result = new Result<IPage<Map<String,Object>>>();
        IPage<Map<String,Object>> pageList = sysUserService.getAllUserByAll(user,pageNo,pageSize);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @AutoLog(value = "用户管理")
    @ApiOperation(value="用户-用户管理编辑", notes="个人")
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    //@RequiresPermissions("user:edit")
    public Result<SysUser> edit(@RequestBody JSONObject jsonObject) {
        Result<SysUser> result = new Result<SysUser>();
        try {
            SysUser sysUser = sysUserService.getById(jsonObject.getString("id"));
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

    @AutoLog(value = "用户管理")
    @ApiOperation(value="用户-用户管理删除", notes="个人")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<SysUser> delete(@RequestParam(name = "id", required = true) String id) {
        Result<SysUser> result = new Result<SysUser>();
        // 定义SysUserDepart实体类的数据库查询LambdaQueryWrapper
        LambdaQueryWrapper<SysUserDepart> query = new LambdaQueryWrapper<SysUserDepart>();
        SysUser sysUser = sysUserService.getUserById(id);
        if (sysUser == null) {
            result.error500("未找到对应实体");
        } else {
            // 当某个用户被删除时,删除其ID下对应的部门数据
            query.eq(SysUserDepart::getUserId, id);
            sysUserService.deleteUserById(id);
            sysUserDepartService.remove(query);
            result.success("删除成功!");
        }
        return result;
    }

}
