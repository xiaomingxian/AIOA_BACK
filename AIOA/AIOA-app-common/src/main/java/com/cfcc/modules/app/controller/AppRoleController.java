package com.cfcc.modules.app.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.constant.CacheConstant;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.system.entity.SysPermissionDataRule;
import com.cfcc.modules.system.entity.SysRole;
import com.cfcc.modules.system.entity.SysRolePermission;
import com.cfcc.modules.system.service.ISysPermissionDataRuleService;
import com.cfcc.modules.system.service.ISysPermissionService;
import com.cfcc.modules.system.service.ISysRolePermissionService;
import com.cfcc.modules.system.service.ISysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Author
 * @since
 */
@Slf4j
@RestController
@RequestMapping("/app/sysRole")
public class AppRoleController {

    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private ISysPermissionDataRuleService sysPermissionDataRuleService;

    @Autowired
    private ISysRolePermissionService sysRolePermissionService;

    @Autowired
    private ISysPermissionService sysPermissionService;


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<Map<String,Object>>> queryPageList(String role,
                                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                           HttpServletRequest req) {
        Result<IPage<Map<String,Object>>> result = new Result<IPage<Map<String,Object>>>();
        IPage<Map<String,Object>> pageList = sysRoleService.pageOneSysRole(role, pageNo, pageSize);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<SysRole> add(@RequestBody SysRole role) {
        Result<SysRole> result = new Result<SysRole>();
        try {
            role.setCreateTime(new Date());
            sysRoleService.save(role);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }


    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public Result<SysRole> edit(@RequestBody SysRole role) {
        Result<SysRole> result = new Result<SysRole>();
        SysRole sysrole = sysRoleService.getById(role.getId());
        if (sysrole == null) {
            result.error500("未找到对应实体");
        } else {
            role.setUpdateTime(new Date());
            boolean ok = sysRoleService.updateById(role);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }


    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @CacheEvict(value = CacheConstant.LOGIN_USER_RULES_CACHE, allEntries = true)
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<SysRole> delete(@RequestParam(name = "id", required = true) String id) {
        Result<SysRole> result = new Result<SysRole>();
        SysRole sysrole = sysRoleService.getById(id);
        if (sysrole == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysRoleService.removeById(id);
            if (ok) {
                result.success("删除成功!");
            }
        }

        return result;
    }


    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @CacheEvict(value = CacheConstant.LOGIN_USER_RULES_CACHE, allEntries = true)
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<SysRole> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<SysRole> result = new Result<SysRole>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.sysRoleService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<SysRole> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysRole> result = new Result<SysRole>();
        SysRole sysrole = sysRoleService.getById(id);
        if (sysrole == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(sysrole);
            result.setSuccess(true);
        }
        return result;
    }


    /**
     * 查询数据规则数据
     */
    @GetMapping(value = "/datarule/{permissionId}/{roleId}")
    public Result<?> loadDatarule(@PathVariable("permissionId") String permissionId, @PathVariable("roleId") String roleId) {
        List<SysPermissionDataRule> list = sysPermissionDataRuleService.getPermRuleListByPermId(permissionId);
        if (list == null || list.size() == 0) {
            return Result.error("未找到权限配置信息");
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("datarule", list);
            LambdaQueryWrapper<SysRolePermission> query = new LambdaQueryWrapper<SysRolePermission>()
                    .eq(SysRolePermission::getPermissionId, permissionId)
                    .eq(SysRolePermission::getRoleId, roleId);
            SysRolePermission sysRolePermission = sysRolePermissionService.getOne(query);
            if (sysRolePermission == null) {
                //return Result.error("未找到角色菜单配置信息");
            } else {
                String drChecked = sysRolePermission.getDataRuleIds();
                if (oConvertUtils.isNotEmpty(drChecked)) {
                    map.put("drChecked", drChecked.endsWith(",") ? drChecked.substring(0, drChecked.length() - 1) : drChecked);
                }
            }
            return Result.ok(map);
            //TODO 以后按钮权限的查询也走这个请求 无非在map中多加两个key
        }
    }


}
