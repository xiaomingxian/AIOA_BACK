package com.cfcc.modules.system.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.shiro.vo.DefContants;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.entity.SysUserOpinion;
import com.cfcc.modules.system.service.ISysUserOpinionService;
import com.cfcc.modules.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 快捷意见
 * @Author: jeecg-boot
 * @Date: 2019-10-12
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "快捷意见")
@RestController
@RequestMapping("/sys_user_opinion/sysUserOpinion")
public class SysUserOpinionController {
    @Autowired
    private ISysUserOpinionService sysUserOpinionService;
    @Autowired
    private ISysUserService userService;

    /**
     * 分页列表查询
     *
     * @param sysUserOpinion
     * @param pageNo
     * @param pageSize
     * @param request
     * @return
     */
    @AutoLog(value = "快捷意见-分页列表查询")
    @ApiOperation(value = "快捷意见-分页列表查询", notes = "快捷意见-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SysUserOpinion>> queryPageList(SysUserOpinion sysUserOpinion,
                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                       HttpServletRequest request) {
		/*Result<IPage<SysUserOpinion>> result = new Result<IPage<SysUserOpinion>>();
		QueryWrapper<SysUserOpinion> queryWrapper = QueryGenerator.initQueryWrapper(sysUserOpinion, req.getParameterMap());
		Page<SysUserOpinion> page = new Page<SysUserOpinion>(pageNo, pageSize);*/
        Result<IPage<SysUserOpinion>> result = new Result<IPage<SysUserOpinion>>();
        SysUser currentUser = userService.getCurrentUser(request);
        String id = currentUser.getId();
        sysUserOpinion.setSUserId(id);
        IPage<SysUserOpinion> pageList = sysUserOpinionService.findPage(pageNo, pageSize, sysUserOpinion);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param sysUserOpinion
     * @return
     */
    @AutoLog(value = "快捷意见-添加")
    @ApiOperation(value = "快捷意见-添加", notes = "快捷意见-添加")
    @PostMapping(value = "/add")
    public Result<SysUserOpinion> add(@RequestBody SysUserOpinion sysUserOpinion, HttpServletRequest request) {
        Result<SysUserOpinion> result = new Result<SysUserOpinion>();
        try {
            SysUser currentUser = userService.getCurrentUser(request);
            String id = currentUser.getId();
            sysUserOpinion.setSUserId(id);
            sysUserOpinionService.save(sysUserOpinion);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param sysUserOpinion
     * @return
     */
    @AutoLog(value = "快捷意见-编辑")
    @ApiOperation(value = "快捷意见-编辑", notes = "快捷意见-编辑")
    @PutMapping(value = "/edit")
    public Result<SysUserOpinion> edit(@RequestBody SysUserOpinion sysUserOpinion) {
        Result<SysUserOpinion> result = new Result<SysUserOpinion>();
        SysUserOpinion sysUserOpinionEntity = sysUserOpinionService.findById(sysUserOpinion.getIId());
        if (sysUserOpinionEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysUserOpinionService.updateBYIid(sysUserOpinion);

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
    @AutoLog(value = "快捷意见-通过id删除")
    @ApiOperation(value = "快捷意见-通过id删除", notes = "快捷意见-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            sysUserOpinionService.deleteByIidd(id);
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
    @AutoLog(value = "快捷意见-批量删除")
    @ApiOperation(value = "快捷意见-批量删除", notes = "快捷意见-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<SysUserOpinion> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<SysUserOpinion> result = new Result<SysUserOpinion>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            List<String> list = Arrays.asList(ids.split(","));
           for (int i=0;i<list.size();i++)
           {
               this.sysUserOpinionService.deleteByIidd(list.get(i));
           }

            //this.sysUserOpinionService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "快捷意见-通过id查询")
    @ApiOperation(value = "快捷意见-通过id查询", notes = "快捷意见-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<SysUserOpinion> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysUserOpinion> result = new Result<SysUserOpinion>();
        SysUserOpinion sysUserOpinion = sysUserOpinionService.getById(id);
        if (sysUserOpinion == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(sysUserOpinion);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        // Step.1 组装查询条件
        QueryWrapper<SysUserOpinion> queryWrapper = new QueryWrapper<SysUserOpinion>();
        try {
            String paramsStr = request.getParameter("paramsStr");
            SysUserOpinion sysUserOpinion = new SysUserOpinion() ;
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                sysUserOpinion = JSON.parseObject(deString, SysUserOpinion.class);

            }
            SysUser currentUser = userService.getCurrentUser(request);
            String id = currentUser.getId();
            sysUserOpinion.setSUserId(id) ;
            //queryWrapper = QueryGenerator.initQueryWrapper(sysUserOpinion, request.getParameterMap());
            queryWrapper.setEntity(sysUserOpinion) ;
            //System.out.println(queryWrapper+"]]]]]");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SysUserOpinion> pageList = sysUserOpinionService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "快捷意见列表");
        mv.addObject(NormalExcelConstants.CLASS, SysUserOpinion.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("快捷意见列表数据", "导出人:Jeecg", "导出信息"));
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
                List<SysUserOpinion> listSysUserOpinions = ExcelImportUtil.importExcel(file.getInputStream(), SysUserOpinion.class, params);
                sysUserOpinionService.saveBatch(listSysUserOpinions);
                return Result.ok("文件导入成功！数据行数:" + listSysUserOpinions.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入失败！");
    }


    /**
     * 查询用户个人意见
     * @param sysUserOpinion
     * @return
     */
    @AutoLog(value = "业务按钮-查询用户个人意见")
    @ApiOperation(value="业务按钮-查询用户个人意见", notes="业务按钮-查询用户个人意见")
    @GetMapping(value = "/getUserOpinion")
    public Result getUserOpinion(SysUserOpinion sysUserOpinion) {
        Result<List<SysUserOpinion>> result = new Result<>();
        List<SysUserOpinion> list  = sysUserOpinionService.getUserOpinion(sysUserOpinion);
        result.setSuccess(true);
        result.setResult(list);
        return result;
    }


}
