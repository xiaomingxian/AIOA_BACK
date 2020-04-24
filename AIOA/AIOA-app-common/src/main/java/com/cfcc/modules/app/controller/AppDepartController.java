package com.cfcc.modules.app.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.constant.CacheConstant;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.model.SysDepartTreeModel;
import com.cfcc.modules.system.service.ISysDepartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author
 * @since
 */
@Slf4j
@RestController
@RequestMapping("/app/sysDepart")
public class AppDepartController {

    @Autowired
    private ISysDepartService sysDepartService;

    @GetMapping("query")
    public Result<List<SysDepart>> query(String orgType) {
        QueryWrapper<SysDepart> sysDepartQueryWrapper = new QueryWrapper<>();
        sysDepartQueryWrapper.eq("org_type", orgType);
        List<SysDepart> list = sysDepartService.list(sysDepartQueryWrapper);

        Result<List<SysDepart>> listResult = new Result<>();
        listResult.setSuccess(true);
        listResult.setResult(list);

        return listResult;

    }


    @RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
    public Result<List<SysDepartTreeModel>> queryTreeList() {
        Result<List<SysDepartTreeModel>> result = new Result<>();
        try {
            // 从内存中读取
//			List<SysDepartTreeModel> list =FindsDepartsChildrenUtil.getSysDepartTreeList();
//			if (CollectionUtils.isEmpty(list)) {
//				list = sysDepartService.queryTreeList();
//			}
            List<SysDepartTreeModel> list = sysDepartService.queryTreeList();
            result.setResult(list);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return result;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @CacheEvict(value= {CacheConstant.DEPART_INFO_CACHE,CacheConstant.DEPART_IDMODEL_CACHE}, allEntries=true)
    public Result<SysDepart> add(@RequestBody SysDepart sysDepart, HttpServletRequest request) {
        Result<SysDepart> result = new Result<SysDepart>();
        String username = JwtUtil.getUserNameByToken(request);
        try {
            sysDepart.setCreateBy(username);
            sysDepartService.saveDepartData(sysDepart, username);
            //清除部门树内存
            // FindsDepartsChildrenUtil.clearSysDepartTreeList();
            // FindsDepartsChildrenUtil.clearDepartIdModel();
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            result.error500("操作失败");
        }
        return result;
    }


    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    @CacheEvict(value= {CacheConstant.DEPART_INFO_CACHE,CacheConstant.DEPART_IDMODEL_CACHE}, allEntries=true)
    public Result<SysDepart> edit(@RequestBody SysDepart sysDepart, HttpServletRequest request) {
        String username = JwtUtil.getUserNameByToken(request);
        sysDepart.setUpdateBy(username);
        Result<SysDepart> result = new Result<SysDepart>();
        SysDepart sysDepartEntity = sysDepartService.getById(sysDepart.getId());
        if (sysDepartEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysDepartService.updateDepartDataById(sysDepart, username);
            // TODO 返回false说明什么？
            if (ok) {
                //清除部门树内存
                //FindsDepartsChildrenUtil.clearSysDepartTreeList();
                //FindsDepartsChildrenUtil.clearDepartIdModel();
                result.success("修改成功!");
            }
        }
        return result;
    }
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @CacheEvict(value= {CacheConstant.DEPART_INFO_CACHE,CacheConstant.DEPART_IDMODEL_CACHE}, allEntries=true)
    public Result<SysDepart> delete(@RequestParam(name="id",required=true) String id) {

        Result<SysDepart> result = new Result<SysDepart>();
        SysDepart sysDepart = sysDepartService.getById(id);
        if(sysDepart==null) {
            result.error500("未找到对应实体");
        }else {
            boolean ok = sysDepartService.delete(id);
            if(ok) {
                //清除部门树内存
                //FindsDepartsChildrenUtil.clearSysDepartTreeList();
                // FindsDepartsChildrenUtil.clearDepartIdModel();
                result.success("删除成功!");
            }
        }
        return result;
    }

    @RequestMapping(value = "/searchBy", method = RequestMethod.GET)
    public Result<List<SysDepartTreeModel>> searchBy(@RequestParam(name = "keyWord", required = true) String keyWord) {
        Result<List<SysDepartTreeModel>> result = new Result<List<SysDepartTreeModel>>();
        try {
            List<SysDepartTreeModel> treeList = this.sysDepartService.searhBy(keyWord);
            if (treeList.size() == 0 || treeList == null) {
                throw new Exception();
            }
            result.setSuccess(true);
            result.setResult(treeList);
            return result;
        } catch (Exception e) {
            e.fillInStackTrace();
            result.setSuccess(false);
            result.setMessage("查询失败或没有您想要的任何数据!");
            return result;
        }
    }


}
