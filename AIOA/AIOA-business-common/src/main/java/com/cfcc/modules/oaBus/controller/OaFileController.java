package com.cfcc.modules.oaBus.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.util.ZipUtils;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.oaBus.entity.OaFile;
import com.cfcc.modules.oaBus.service.IOaFileService;
import com.cfcc.modules.oabutton.entity.OaButton;
import com.cfcc.modules.system.entity.LoginInfo;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @Description: 附件表
 * @Author: jeecg-boot
 * @Date: 2019-10-21
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "附件表")
@RestController
@RequestMapping("/oaBus/oaFile")
public class OaFileController {
    @Autowired
    private IOaFileService oaFileService;

    //上传文件地址
    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Autowired
    @Lazy
    private ISysUserService sysUserService;
    /**
     * 下载所有附件
     *
     * @param response
     * @param list
     */
    @RequestMapping("/downloadZipFile")
    public void downloadZipFile(HttpServletRequest request, HttpServletResponse response, @RequestBody List<Map<String,Object>> list) { //获取的对象
        if (list.size()== 0){
            return;
        }
        LoginInfo loginInfo = sysUserService.getLoginInfo(request);
        String orgSchema ="";
        if (loginInfo.getOrgSchema() != null && !loginInfo.getOrgSchema().equals("")) {
            orgSchema = loginInfo.getOrgSchema();
        }
        for (Map map :list) {
            map.put("sfilePath",uploadpath + File.separator + orgSchema + File.separator + map.get("sfilePath")+"");
        }
        ZipUtils.downFile(list, response);
    }

    /**
     * 分页列表查询
     *
     * @param oaFile
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "附件表-分页列表查询")
    @ApiOperation(value = "附件表-分页列表查询", notes = "附件表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<OaFile>> queryPageList(OaFile oaFile,
                                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                               HttpServletRequest req) {
        Result<IPage<OaFile>> result = new Result<IPage<OaFile>>();
		/*QueryWrapper<OaFile> queryWrapper = QueryGenerator.initQueryWrapper(oaFile, req.getParameterMap());
		Page<OaFile> page = new Page<OaFile>(pageNo, pageSize);*/
        IPage<OaFile> pageList = oaFileService.getPage(pageNo, pageSize, oaFile);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 分页列表查询
     *
     * @return
     */
    @AutoLog(value = "附件表-查询busData对应的附件列表")
    @ApiOperation(value = "附件表-查询busData对应的附件列表", notes = "附件表-查询busData对应的附件列表")
    @GetMapping(value = "/queryOaFileList")
    public Result<List<OaFile>> queryOaFileList(String tableName, String busDataId,
                                                HttpServletRequest req) {
        Result<List<OaFile>> result = new Result<List<OaFile>>();
		/*QueryWrapper<OaFile> queryWrapper = QueryGenerator.initQueryWrapper(oaFile, req.getParameterMap());
		Page<OaFile> page = new Page<OaFile>(pageNo, pageSize);*/
        List<OaFile> pageList = oaFileService.getOaFileListSer(tableName, busDataId);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 分页列表查询
     *
     * @return
     */
    @AutoLog(value = "附件表-查询busData对应的附件列表")
    @ApiOperation(value = "附件表-查询busData对应的附件列表", notes = "附件表-查询busData对应的附件列表")
    @GetMapping(value = "/queryBanWenList")
    public Result<List<OaFile>> queryBanWenList(String tableName, String busDataId, String sFileType) {
        Result<List<OaFile>> result = new Result<List<OaFile>>();
		/*QueryWrapper<OaFile> queryWrapper = QueryGenerator.initQueryWrapper(oaFile, req.getParameterMap());
		Page<OaFile> page = new Page<OaFile>(pageNo, pageSize);*/
        List<OaFile> pageList = oaFileService.getBanWenList(tableName, busDataId, sFileType);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param oaFile
     * @return
     */
    @AutoLog(value = "附件表-添加")
    @ApiOperation(value = "附件表-添加", notes = "附件表-添加")
    @PostMapping(value = "/add")
    public Result<OaFile> add(@RequestBody OaFile oaFile) {
        Result<OaFile> result = new Result<OaFile>();
        try {
            oaFileService.save(oaFile);
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
     * @param oaFile
     * @return
     */
    @AutoLog(value = "附件表-编辑")
    @ApiOperation(value = "附件表-编辑", notes = "附件表-编辑")
    @PutMapping(value = "/edit")
    public Result<OaFile> edit(@RequestBody OaFile oaFile) {
        Result<OaFile> result = new Result<OaFile>();
        OaFile oaFileEntity = oaFileService.getById(oaFile.getIId());
        if (oaFileEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = oaFileService.updateById(oaFile);
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
    @AutoLog(value = "附件表-通过id删除")
    @ApiOperation(value = "附件表-通过id删除", notes = "附件表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            oaFileService.removeById(id);
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
    @AutoLog(value = "附件表-批量删除")
    @ApiOperation(value = "附件表-批量删除", notes = "附件表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<OaFile> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<OaFile> result = new Result<OaFile>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.oaFileService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "附件表-通过id查询")
    @ApiOperation(value = "附件表-通过id查询", notes = "附件表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<OaFile> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<OaFile> result = new Result<OaFile>();
        OaFile oaFile = oaFileService.getById(id);
        if (oaFile == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(oaFile);
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
        QueryWrapper<OaFile> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                OaFile oaFile = JSON.parseObject(deString, OaFile.class);
                queryWrapper = QueryGenerator.initQueryWrapper(oaFile, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<OaFile> pageList = oaFileService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "附件表列表");
        mv.addObject(NormalExcelConstants.CLASS, OaFile.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("附件表列表数据", "导出人:Jeecg", "导出信息"));
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
                List<OaFile> listOaFiles = ExcelImportUtil.importExcel(file.getInputStream(), OaFile.class, params);
                oaFileService.saveBatch(listOaFiles);
                return Result.ok("文件导入成功！数据行数:" + listOaFiles.size());
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
     * 修改附件名称
     *
     * @param
     * @return
     */
    @AutoLog(value = "业务按钮-修改附件名称")
    @ApiOperation(value = "业务按钮-修改附件名称", notes = "业务按钮-修改附件名称")
    @PostMapping(value = "/updateFileName")
    public Result updateFileName(@RequestBody Map<String, Object> param,HttpServletRequest request) {
        Result result = new Result<>();
        try {
            boolean ok = oaFileService.updateDocNameById(param,request);
            if (ok) {
                result.success("修改成功");
            } else {
                result.error("文件不存在");
            }
        } catch (Exception e) {
            log.error("修改失败", e.getMessage());
            return Result.error("修改失败!");
        }
        return result;
    }

    /**
     * 批量业务附件
     *
     * @param param
     * @return
     */
    @AutoLog(value = "复制附件")
    @ApiOperation(value = "复制附件", notes = "复制附件")
    @PostMapping(value = "/copyFile")
    public Result copyFileList(@RequestBody String param,HttpServletRequest request) {
        Result<List<OaFile>> result = new Result<List<OaFile>>();
        List<OaFile> oaFileList = oaFileService.copyFiles(param,request);
        try {
            result.success("复制附件成功！");
            result.setResult(oaFileList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("复制附件失败");
        }
        return result;
    }

    /**
     * 单文件复制
     *
     * @param
     * @return
     */
    @AutoLog(value = "单文件复制")
    @ApiOperation(value = "单文件复制", notes = "单文件复制")
    @PostMapping(value = "/singleCopyFile")
    public Result singleCopyFile(@RequestBody Map<String, Object> map,HttpServletRequest request) {
        Result<OaFile> result = new Result<OaFile>();
        OaFile oa = oaFileService.singleCopyFile(map,request);
        try {
            if (oa != null) {
                result.success("复制成功！");
                result.setResult(oa);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("复制失败");
        }
        return result;
    }


    /**
     * 排序
     *
     * @param
     * @return
     */
    @AutoLog(value = "附件排序按钮")
    @PostMapping(value = "/sortFile")
    public Result sortFile(@RequestBody Map<String, Object> param) {
        Result result = new Result<>();
        try {
            boolean ok = oaFileService.sortFile(param);
            if (ok) {
                result.success("交换成功");
            } else {
                result.error("交换失败");
            }
        } catch (Exception e) {
            log.error("交换失败", e.getMessage());
            return Result.error("交换失败!");
        }
        return result;
    }

    @AutoLog(value = "附件按钮权限")
    @PostMapping(value = "/isFileBtn")
    public Result isFileBtn(@RequestBody Map<String, List<OaButton>> map) {
        Result result = new Result<>();
        boolean ok = oaFileService.isShowFileBtn(map);
        result.setResult(ok);
        return result;
    }

    /**
     * 添加
     *
     * @param oaFile
     * @return
     */
    @AutoLog(value = "附件信息写入")
    @ApiOperation(value = "附件信息写入", notes = "附件信息写入")
    @PostMapping(value = "/addFiles")
    public Result<OaFile> addFiles(@RequestBody List<Map<String,Object>> maps,HttpServletRequest request) {
        Result<OaFile> result = new Result<OaFile>();
        LoginInfo loginInfo = sysUserService.getLoginInfo(request);
        String username = loginInfo.getUsername();
        try {
            for (Map map:maps){
                OaFile oaFile = new OaFile();
                oaFile.setSFilePath(map.get("sFilePath")+"");
                oaFile.setSFileName(map.get("sFileName")+"");
                oaFile.setSTable(map.get("sTable")+"");
                oaFile.setITableId(Integer.valueOf(map.get("iTableId")+""));
                oaFile.setSFileType(map.get("sFileType")+"");
                oaFile.setSCreateBy(username);
                oaFile.setDCreateTime(new Date());
                oaFileService.save(oaFile);
                oaFileService.updateIorderById(oaFile.getIId());
                result.success("添加成功！");
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

}
