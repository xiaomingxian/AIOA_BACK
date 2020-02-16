package com.cfcc.modules.papertitle.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.oaBus.entity.OaFile;
import com.cfcc.modules.oaBus.service.impl.OaFileServiceImpl;
import com.cfcc.modules.papertitle.entity.OaTemplate;
import com.cfcc.modules.papertitle.service.IOaTemplateService;
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
import org.springframework.util.AntPathMatcher;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;

/**
 * @Description: 模板管理
 * @Author: jeecg-boot
 * @Date: 2019-10-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "模板管理")
@RestController
@RequestMapping("/papertitle/oaTemplate")
public class OaTemplateController {
    @Autowired
    private IOaTemplateService oaTemplateService;

    @Autowired
    private OaFileServiceImpl oaFileService;


    //上传文件地址
    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    //附件临时路径
    @Value(value = "${jeecg.path.uploadfile}")
    private String uploadfile;

    //上传模板文件地址
    @Value(value = "${jeecg.path.tempFilePath}")
    private String templatePath;
    /**
     * 分页列表查询
     *
     * @param oaTemplate
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "模板管理-分页列表查询")
    @ApiOperation(value = "模板管理-分页列表查询", notes = "模板管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<OaTemplate>> queryPageList(OaTemplate oaTemplate,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Result<IPage<OaTemplate>> result = new Result<IPage<OaTemplate>>();
        IPage<OaTemplate> pageList = oaTemplateService.queryTemplateList(oaTemplate, pageNo, pageSize);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param oaTemplate
     * @return
     */
    @AutoLog(value = "模板管理-添加")
    @ApiOperation(value = "模板管理-添加", notes = "模板管理-添加")
    @PostMapping(value = "/add")
    public Result<OaTemplate> add(@RequestBody OaTemplate oaTemplate, HttpServletRequest request) {
        Result<OaTemplate> result = new Result<OaTemplate>();
        try {
            //获取当前用户名称
            String token = request.getHeader("X-Access-Token");
            String username = JwtUtil.getUsername(token);
            oaTemplate.setSCreateBy(username);
            oaTemplate.setDCreateTime(new Date());
            oaTemplateService.save(oaTemplate);
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
     * @param oaTemplate
     * @return
     */
    @AutoLog(value = "模板管理-编辑")
    @ApiOperation(value = "模板管理-编辑", notes = "模板管理-编辑")
    @PutMapping(value = "/edit")
    public Result<OaTemplate> edit(@RequestBody OaTemplate oaTemplate, HttpServletRequest request) {
        Result<OaTemplate> result = new Result<OaTemplate>();
        OaTemplate oaTemplateEntity = oaTemplateService.queryById(oaTemplate.getIId());
        if (oaTemplateEntity == null) {
            result.error500("未找到对应实体");
        } else {
            //获取当前用户名称
            String token = request.getHeader("X-Access-Token");
            String username = JwtUtil.getUsername(token);
            oaTemplate.setSUpdateBy(username);

            boolean ok = oaTemplateService.updateTemplateById(oaTemplate);
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
    @AutoLog(value = "模板管理-通过id删除")
    @ApiOperation(value = "模板管理-通过id删除", notes = "模板管理-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            oaTemplateService.deleteTemplateTitleByIID(id);
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
    @AutoLog(value = "模板管理-批量删除")
    @ApiOperation(value = "模板管理-批量删除", notes = "模板管理-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<OaTemplate> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<OaTemplate> result = new Result<OaTemplate>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            List<String> idList = Arrays.asList(ids.split(","));
            for (int i = 0; i < idList.size(); i++) {
                this.oaTemplateService.deleteTemplateTitleByIID(idList.get(i));
            }
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
    @AutoLog(value = "模板管理-通过id查询")
    @ApiOperation(value = "模板管理-通过id查询", notes = "模板管理-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<OaTemplate> queryById(@RequestParam(name = "id", required = true) Integer id) {
        Result<OaTemplate> result = new Result<OaTemplate>();
        OaTemplate oaTemplate = oaTemplateService.queryById(id);
        if (oaTemplate == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(oaTemplate);
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
        QueryWrapper<OaTemplate> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                OaTemplate oaTemplate = JSON.parseObject(deString, OaTemplate.class);
                queryWrapper = QueryGenerator.initQueryWrapper(oaTemplate, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<OaTemplate> pageList = oaTemplateService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "模板管理列表");
        mv.addObject(NormalExcelConstants.CLASS, OaTemplate.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("模板管理列表数据", "导出人:Jeecg", "导出信息"));
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
                List<OaTemplate> listOaTemplates = ExcelImportUtil.importExcel(file.getInputStream(), OaTemplate.class, params);
                oaTemplateService.saveBatch(listOaTemplates);
                return Result.ok("文件导入成功！数据行数:" + listOaTemplates.size());
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
     * 根据类型查询下拉列表
     *
     * @param type
     * @return
     */
    @AutoLog(value = "模板管理-下拉选列表")
    @ApiOperation(value = "模板管理-下拉选列表", notes = "模板管理-下拉选列表")
    @GetMapping(value = "/templateList")
    public Result templateList(@RequestParam("type") Integer type) {
        Result<List<OaTemplate>> result = new Result<>();
        List<OaTemplate> templateList = oaTemplateService.templateList(type);
        if (templateList.size() == 0) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(templateList);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 单文件上传
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/upload")
    public Result<?> upload(HttpServletRequest request, HttpServletResponse response) {
        Result<OaFile> result = new Result<>();
        try {
            //获取用户名称
            String token = request.getHeader("X-Access-Token");
            String username = JwtUtil.getUsername(token);

            String ctxPath = templatePath;
            String fileName = null;
            Calendar calendar = Calendar.getInstance();
            String path = ctxPath.replace("//", "/" +
                    "") + "/" + calendar.get(Calendar.YEAR) +
                    "/" + (calendar.get(Calendar.MONTH) + 1) +
                    "/" + calendar.get(Calendar.DATE) + "/";

            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();// 创建文件根目录
            }
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile mf = multipartRequest.getFile("file");// 获取上传文件对象
            String orgName = mf.getOriginalFilename();// 获取文件名
            fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.indexOf("."));
            String savePath = file.getPath() + File.separator + fileName;
            File savefile = new File(savePath);
            FileCopyUtils.copy(mf.getBytes(), savefile);

            //获取后台项目路径
            String projectPath = System.getProperty("user.dir");
            String path1 = projectPath.substring(0, projectPath.lastIndexOf(File.separator));
            String path2 = path1.substring(0, path1.lastIndexOf(File.separator));
            File template = new File(path2+File.separator+templatePath+File.separator+orgName);
            FileCopyUtils.copy(mf.getBytes(), template);

            OaFile oaFile = new OaFile();
            oaFile.setSFileType("7");        // 附件类型为 4 附件
            oaFile.setSFileName(orgName);        //设置附件名字
            oaFile.setSFilePath(savePath);        //设置文件路径
            oaFile.setSCreateBy(username);
            oaFile.setDCreateTime(new Date());
            oaFileService.save(oaFile);
            QueryWrapper<OaFile> c = new QueryWrapper<>();
            c.setEntity(oaFile);
            OaFile ad = oaFileService.getOne(c);        //查询刚刚插入的那条数据的id
            result.setResult(ad);
            result.setMessage(savePath);
            result.setSuccess(true);
        } catch (IOException e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 批量上传
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/uploads")
    public Result<?> uploads(@RequestParam("file") MultipartFile[] files,
                             @RequestParam("sTable") String sTable,
                             @RequestParam("iTableId") Integer iTableId,
                             @RequestParam("sFileType") String sFileType,
                             HttpServletRequest request, HttpServletResponse response) {
        Result<List<OaFile>> result = new Result<List<OaFile>>();
        List<OaFile> oaFiles = oaTemplateService.batchUploads(files, sTable, iTableId, sFileType, request, response);
        result.setResult(oaFiles);
        return result;
    }


    /**
     * 通过id查询
     *
     * @param ifileId
     * @return
     */
    @AutoLog(value = "模板管理-通过id查询")
    @ApiOperation(value = "模板管理-通过id查询", notes = "模板管理-通过id查询")
    @GetMapping(value = "/getFileNameById")
    public Result<OaFile> getFileNameById(@RequestParam(name = "ifileId", required = true) String ifileId) {
        Result<OaFile> result = new Result<OaFile>();
        OaFile oa = oaTemplateService.getFileNameById(ifileId);
        result.setResult(oa);
        result.setSuccess(true);

        return result;
    }

    /**
     * 下载文件
     * 请求地址：http://localhost:8080/common/download/{user/20190119/e1fe9925bc315c60addea1b98eb1cb1349547719_1547866868179.jpg}
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @GetMapping(value = "/download/**")
    public void download(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // ISO-8859-1 ==> UTF-8 进行编码转换
        String filePath = extractPathFromPattern(request);
        // 其余处理略
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            filePath = filePath.replace("..", "");
            if (filePath.endsWith(",")) {
                filePath = filePath.substring(0, filePath.length() - 1);
            }
            File file = new File(filePath);
            if (file.exists()) {
                String fileName = file.getName();
                String downFileName = fileName.substring(0, fileName.lastIndexOf("_"))
                        + fileName.substring(fileName.lastIndexOf("."), fileName.length());
                response.setContentType("application/msword");// 设置强制下载不打开            
                response.addHeader("Content-Disposition", "attachment;fileName=" + new String(downFileName.getBytes("UTF-8"), "iso-8859-1"));
                inputStream = new BufferedInputStream(new FileInputStream(file));
                outputStream = response.getOutputStream();
                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }
                response.flushBuffer();
            }

        } catch (Exception e) {
            log.info("文件下载失败" + e.getMessage());
            // e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /**
     * 把指定URL后的字符串全部截断当成参数
     * 这么做是为了防止URL中包含中文或者特殊字符（/等）时，匹配不了的问题
     *
     * @param request
     * @return
     */
    private static String extractPathFromPattern(final HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
    }

}
