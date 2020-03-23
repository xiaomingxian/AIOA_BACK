package com.cfcc.modules.oaBus.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.modules.oaBus.entity.*;
import com.cfcc.modules.oaBus.service.IBusFunctionPermitService;
import com.cfcc.modules.oaBus.service.IBusModelPermitService;
import com.cfcc.modules.oaBus.service.IBusProcSetService;
import com.cfcc.modules.oabutton.entity.OaButtonSet;
import com.cfcc.modules.oabutton.entity.OaProcButton;
import com.cfcc.modules.oabutton.service.IOaButtonSetService;
import com.cfcc.modules.oabutton.service.IOaProcButtonService;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.entity.SysRole;
import com.cfcc.modules.system.entity.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 业务类型
 * @Author: jeecg-boot
 * @Date: 2019-10-21
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "业务类型")
@RestController
@RequestMapping("/oaBus/busFunctionPermit")
public class BusFunctionPermitController {
    @Autowired
    private IBusFunctionPermitService iBusFunctionPermitService;
    /**
     * 分页列表查询
     *
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "业务类型-分页列表查询")
    @ApiOperation(value = "业务类型-分页列表查询", notes = "业务类型-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<BusFunctionPermit>> queryPageList(@RequestParam(name="ibusId",required=false) Integer ibusId,
                                                          @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                          @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                          HttpServletRequest req) {
        Result<IPage<BusFunctionPermit>> result = new Result<IPage<BusFunctionPermit>>();
        IPage<BusFunctionPermit> pageList = iBusFunctionPermitService.findPage(ibusId,pageNo, pageSize);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }
    @AutoLog(value = "业务类型-分页列表查询")
    @ApiOperation(value = "业务类型-分页列表查询", notes = "业务类型-分页列表查询")
    @GetMapping(value = "/findAllList")
    public Result<IPage<BusFunctionPermit>> findAllList(BusFunctionPermit busFunctionPermit,
                                                        @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                        @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                        HttpServletRequest req) {
        Result<IPage<BusFunctionPermit>> result = new Result<IPage<BusFunctionPermit>>();
        IPage<BusFunctionPermit> pageList = iBusFunctionPermitService.findAllList(busFunctionPermit,pageNo, pageSize);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param
     * @return
     */
    @AutoLog(value = "业务类型-添加")
    @ApiOperation(value = "业务类型-添加", notes = "业务类型-添加")
    @PostMapping(value = "/add")
    public Result<BusModelPermit> add(@RequestBody BusFunctionPermit busFunctionPermit, HttpServletRequest request) {
        Result<BusModelPermit> result = new Result<BusModelPermit>();
        String schema = MycatSchema.getSchema();
        try {
            /*busPermit.getIId()!=0*/
//            只选了model
            String token = request.getHeader("X-Access-Token");
            String username = JwtUtil.getUsername(token);
            String sPermitType = busFunctionPermit.getSPermitType();
            List<String> typeIdList= iBusFunctionPermitService.getTypeId(Integer.valueOf(busFunctionPermit.getIBusId()));
            switch(sPermitType)
            {
                case "0":
                    busFunctionPermit.setITypeId("");
                    iBusFunctionPermitService.save1(busFunctionPermit,schema);
                    break;
                case "1":
                    if(typeIdList.contains(busFunctionPermit.getITypeId())){
                        result.error500("该角色已选择");
                        return result;
                    }
                    iBusFunctionPermitService.save1(busFunctionPermit,schema);
                    break;
                case "2":
                    String[] typeidList = busFunctionPermit.getITypeId().split(",");

                    for (int j = 0; j <typeidList.length; j++) {
                        if( typeIdList.contains(typeidList[j])){
                            result.error500("该部门已选择");
                            return result;
                        }
                    }

                    for (int j = 0; j <typeidList.length; j++) {
                        if( !typeIdList.contains(typeidList[j])){
                            busFunctionPermit.setITypeId(typeidList[j]);
                            iBusFunctionPermitService.save1(busFunctionPermit,schema);
                        }
                    }
                    break;
                case "3":
                    String[] userList = busFunctionPermit.getITypeId().split(",");
                    for (int j = 0; j <userList.length; j++) {
                        if( typeIdList.contains(userList[j])){
                            result.error500("该用户已选择");
                            return result;
                        }
                    }

                    for (int j = 0; j <userList.length; j++) {
                        if( !typeIdList.contains(userList[j])){
                            busFunctionPermit.setITypeId(userList[j]);
                            iBusFunctionPermitService.save1(busFunctionPermit,schema);
                        }
                    }
                    break;
            }
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param
     * @return
     */
    @AutoLog(value = "业务类型-编辑")
    @ApiOperation(value = "业务类型-编辑", notes = "业务类型-编辑")
    @PutMapping(value = "/edit")
    public Result<BusFunctionPermit> edit(@RequestBody BusFunctionPermit busFunctionPermit) {
        Result<BusFunctionPermit> result = new Result<BusFunctionPermit>();
        BusFunctionPermit busModelPermitEntity = iBusFunctionPermitService.findById(busFunctionPermit.getIId());
        String schema = MycatSchema.getSchema();
        if (busModelPermitEntity == null) {
            result.error500("未找到对应实体");
        } else {
            String sPermitType = busFunctionPermit.getSPermitType();
            List<String> typeIdList = iBusFunctionPermitService.getTypeId(Integer.valueOf(busFunctionPermit.getIBusId()));
            switch (sPermitType) {
                case "0":
                    busFunctionPermit.setITypeId("");
                    iBusFunctionPermitService.updateBYIid(busFunctionPermit,schema);
                    break;
                case "1":
                    if (typeIdList.contains(busFunctionPermit.getITypeId()) && !busModelPermitEntity.getITypeId().equals(busFunctionPermit.getITypeId())) {
                        result.error500("该角色已选择");
                        return result;
                    }else{
                        iBusFunctionPermitService.updateBYIid(busFunctionPermit,schema);
                    }
                    break;
                case "2":
                    if (typeIdList.contains(busFunctionPermit.getITypeId()) && !busModelPermitEntity.getITypeId().equals(busFunctionPermit.getITypeId())) {
                        result.error500("该部门已选择");
                        return result;
                    }
                    else{
                        iBusFunctionPermitService.updateBYIid(busFunctionPermit,schema);
                    }

                    break;
                case "3":
                    if (typeIdList.contains(busFunctionPermit.getITypeId()) && !busModelPermitEntity.getITypeId().equals(busFunctionPermit.getITypeId())) {
                        result.error500("该用户已选择");
                        return result;
                    }
                    else{
                        iBusFunctionPermitService.updateBYIid(busFunctionPermit,schema);
                    }
                    break;

            }
            result.success("修改成功!");

        }

        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "业务类型-通过id删除")
    @ApiOperation(value = "业务类型-通过id删除", notes = "业务类型-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = false) String id) {
        String schema = MycatSchema.getSchema();
        try {
            iBusFunctionPermitService.deleteById(id,schema);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "业务类型-批量删除")
    @ApiOperation(value = "业务类型-批量删除", notes = "业务类型-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<BusFunctionPermit> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<BusFunctionPermit> result = new Result<BusFunctionPermit>();
        String schema = MycatSchema.getSchema();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            List<String> list = Arrays.asList(ids.split(","));
            for (int i=0;i<list.size();i++)
            {
                this.iBusFunctionPermitService.deleteById(list.get(i),schema);
            }
            //this.iBusFunctionPermitService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param
     * @return
     */
    @AutoLog(value = "业务类型-通过id查询")
    @ApiOperation(value = "业务类型-通过id查询", notes = "业务类型-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<BusFunctionPermit> queryById(@RequestParam(name = "iId", required = true) Integer iId) {
        Result<BusFunctionPermit> result = new Result<BusFunctionPermit>();
        BusFunctionPermit busFunctionPermit = iBusFunctionPermitService.findById(iId);
        if (busFunctionPermit == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(busFunctionPermit);
            result.setSuccess(true);
        }
        return result;
    }
    @AutoLog(value = "角色下拉选列表")
    @ApiOperation(value = "角色下拉选列表", notes = "角色下拉选列表")
    @GetMapping(value = "/roleList")
    public Result roleList() {
        Result<List<SysRole>> result = new Result<>();
        List<SysRole> roleList = iBusFunctionPermitService.roleList();
        if (roleList.size() == 0) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(roleList);
            result.setSuccess(true);
        }
        return result;
    }
    @AutoLog(value = "人员下拉选列表")
    @ApiOperation(value = "人员下拉选列表", notes = "人员下拉选列表")
    @GetMapping(value = "/userList")
    public Result userList() {
        Result<List<SysUser>> result = new Result<>();
        List<SysUser> userList = iBusFunctionPermitService.userList();
        if (userList.size() == 0) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(userList);
            result.setSuccess(true);
        }
        return result;

    }



    @GetMapping(value = "/findList")
    public Result findList() {
        String schema = MycatSchema.getSchema();
        Result<List<BusFunctionPermit>> result = new Result<>();
        List<BusFunctionPermit> functionPermitList = iBusFunctionPermitService.findList(schema);
        if (functionPermitList.size() == 0) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(functionPermitList);
            result.setSuccess(true);
        }
        return result;

    }
    @GetMapping(value = "/findRoleByCode")
    public Result findRoleByCode() {
        Result<List<SysRole>> result = new Result<>();
        List<SysRole> sysRoleList = iBusFunctionPermitService.findRoleByCode();
        if (sysRoleList.size() == 0) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(sysRoleList);
            result.setSuccess(true);
        }
        return result;

    }

}