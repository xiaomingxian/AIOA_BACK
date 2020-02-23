package com.cfcc.modules.oaBus.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.oaBus.entity.BusPage;
import com.cfcc.modules.oaBus.entity.OaFile;
import com.cfcc.modules.oaBus.service.IBusPageService;
import com.cfcc.modules.oaBus.service.impl.OaFileServiceImpl;
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
import java.util.*;

/**
 * @Description: 业务页面表
 * @Author: jeecg-boot
 * @Date: 2019-10-14
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "业务页面表")
@RestController
@RequestMapping("/oaBus/busPage")
public class BusPageController {
    @Autowired
    private IBusPageService busPageService;
    @Value("${spring.freemarker.template-loader-path}")
    private String filePath;
    @Value("${jeecg.path.upload}")
    private String upFile;
    @Value("${actShow.default}")
    private String atcShow;

    @Autowired
    private OaFileServiceImpl oaFileService;


    /**
     * 分页列表查询
     *
     * @param busPage
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "业务页面表-分页列表查询")
    @ApiOperation(value = "业务页面表-分页列表查询", notes = "业务页面表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<BusPage>> queryPageList(BusPage busPage,
                                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                HttpServletRequest req) {
        Result<IPage<BusPage>> result = new Result<IPage<BusPage>>();
        /*QueryWrapper<BusPage> queryWrapper = QueryGenerator.initQueryWrapper(busPage, req.getParameterMap());
        Page<BusPage> page = new Page<BusPage>(pageNo, pageSize);
        queryWrapper.orderByDesc("i_id");*/
        IPage<BusPage> pageList = busPageService.getPage(pageNo,pageSize, busPage);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;


    }
    @ApiOperation("查询图片数据")
    @GetMapping(value = "/readPicture")
    public boolean readPicture(@RequestParam("pageId") int pageId,
                                 @RequestParam("resourceType") String resourceType, HttpServletResponse response) {

        try {
            busPageService.readPictureSer(pageId, resourceType, response);
        } catch (Exception e) {
            return false;
        }
        return true ;
    }
    @ApiOperation("查询图片数据")
    @GetMapping(value = "/getPicName")
    public Result<String> getPicName(@RequestParam("fileId") int fileId) {
        Result<String> result = new Result<>();
        try {
            String picName = busPageService.getPicNameSer(fileId);
            result.setResult(picName);
            result.success("查询成功");
        } catch (Exception e) {
            result.error500("查询失败！！！") ;
        }
        return result ;
    }
    @AutoLog(value = "业务页面配置表-初始化字典值")
    @ApiOperation(value = "业务页面配置表-初始化字典值", notes = "业务页面配置表-初始化字典值")
    @GetMapping(value = "/getInitDict")
    public Result<Map<String,Object>> getInitDict() {
        Map<String,Object> map = busPageService.getInitDictSer();
        Result<Map<String,Object>> result = new Result<>();
        result.setResult(map);
        return result;
    }

    @GetMapping("queryActShowByPageRef")
    @ApiOperation(value = "根据页面引用查询需要在流程中记录的字段")
    public Result queryActShowByPageRef(String pageRef) {
        try {
            String actShows = busPageService.queryActShowByPageRef(pageRef);
            Result<Object> ok = Result.ok(actShows);
            ok.setResult(actShows);
            return ok;
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error("查询失败");
        }
    }

    /**
     * 添加
     *
     * @param busPage
     * @return
     */
    @AutoLog(value = "业务页面表-添加")
    @ApiOperation(value = "业务页面表-添加", notes = "业务页面表-添加")
    @PostMapping(value = "/add")
    public Result<BusPage> add(@RequestBody BusPage busPage, HttpServletRequest request) {
        Result<BusPage> result = new Result<BusPage>();
        busPage.setActShow(atcShow);
        String token = request.getHeader("X-Access-Token");
        String username = JwtUtil.getUsername(token);
        busPage.setSCreateBy(username);
        try {
            busPageService.saveBusPage(busPage);
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
     * @param busPage
     * @return
     */
    @AutoLog(value = "业务页面表-编辑")
    @ApiOperation(value = "业务页面表-编辑", notes = "业务页面表-编辑")
    @PutMapping(value = "/edit")
    public Result<BusPage> edit(@RequestBody BusPage busPage, HttpServletRequest request) {
        String token = request.getHeader("X-Access-Token");
        String username = JwtUtil.getUsername(token);
        busPage.setSUpdateBy(username);
        Result<BusPage> result = new Result<BusPage>();
        BusPage busPageEntity = busPageService.getBusPageById(busPage.getIId());
        if (busPageEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = busPageService.updateBusPageById(busPage);
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
    @AutoLog(value = "业务页面表-通过id删除")
    @ApiOperation(value = "业务页面表-通过id删除", notes = "业务页面表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            busPageService.removeBusPageById(id);
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
    @AutoLog(value = "业务页面表-批量删除")
    @ApiOperation(value = "业务页面表-批量删除", notes = "业务页面表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<BusPage> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<BusPage> result = new Result<BusPage>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.busPageService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "业务页面表-通过id查询")
    @ApiOperation(value = "业务页面表-通过id查询", notes = "业务页面表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<BusPage> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<BusPage> result = new Result<BusPage>();
        BusPage busPage = busPageService.getById(id);
        if (busPage == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(busPage);
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
        QueryWrapper<BusPage> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                BusPage busPage = JSON.parseObject(deString, BusPage.class);
                queryWrapper = QueryGenerator.initQueryWrapper(busPage, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<BusPage> pageList = busPageService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "业务页面表列表");
        mv.addObject(NormalExcelConstants.CLASS, BusPage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("业务页面表列表数据", "导出人:Jeecg", "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }
    /**
     * 上传图片
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/upPic", method = RequestMethod.POST)
    public Result<?> upPic(HttpServletRequest request, HttpServletResponse response) {
        Result<OaFile> result = new Result<OaFile>();
        String token = request.getHeader("X-Access-Token");
        String username = JwtUtil.getUsername(token);
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            if (file != null) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    String path = upFile + calendar.get(Calendar.YEAR) +
                            "/" + (calendar.get(Calendar.MONTH) + 1) +
                            "/" + calendar.get(Calendar.DATE) + "/";        //

                    File newFile = new File(path);
                    if (!newFile.exists()) {
                        newFile.mkdirs();
                    }
                    File upFile = new File(path, file.getOriginalFilename());

                    file.transferTo(upFile);
                    OaFile oaFile = new OaFile();
                    oaFile.setSFileType("4");                               // 附件类型为 4 附件
                    //oaFile.setSFileName(upFile.getOriginalFilename());        //设置附件名字
                    oaFile.setSFileName(upFile.getName());        //设置附件名字
                    oaFile.setSFilePath(upFile.getAbsolutePath() );        //设置文件路径
                    oaFile.setSCreateBy(username);
                    oaFile.setDCreateTime(new Date());
                    oaFileService.save(oaFile);
                    log.info(oaFile.toString());
                    result.setResult(oaFile);
                    result.success("附件上传成功");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println(filePath);
        }
        return result;
    }

    /**
     * 上传附件
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/upAttachmentFile", method = RequestMethod.POST)
    public Result<?> upAttachmentFile(HttpServletRequest request, HttpServletResponse response) {
        Result<OaFile> result = new Result<OaFile>();
        String token = request.getHeader("X-Access-Token");
        String username = JwtUtil.getUsername(token);
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            if (file != null) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    String path = filePath + calendar.get(Calendar.YEAR) +
                            "/" + (calendar.get(Calendar.MONTH) + 1) +
                            "/" + calendar.get(Calendar.DATE) + "/";        //

                    File newFile = new File(path);
                    if (!newFile.exists()) {
                        newFile.mkdirs();
                    }
                    File upFile = new File(path, file.getOriginalFilename());

                    file.transferTo(upFile);
                    OaFile oaFile = new OaFile();
                    oaFile.setSFileType("4");                               // 附件类型为 4 附件
                    oaFile.setSFileName(file.getOriginalFilename());        //设置附件名字
                    oaFile.setSFilePath(newFile.getAbsolutePath());        //设置文件路径
                    oaFile.setSCreateBy(username);
                    oaFile.setDCreateTime(new Date());
                    oaFileService.save(oaFile);
                    QueryWrapper<OaFile> c = new QueryWrapper<>();
                    c.setEntity(oaFile);
                    OaFile ad = oaFileService.getOne(c);        //查询刚刚插入的那条数据的id
                    result.setResult(ad);
                    result.success("附件上传成功");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println(filePath);
        }
        return result;
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
                List<BusPage> listBusPages = ExcelImportUtil.importExcel(file.getInputStream(), BusPage.class, params);
                busPageService.saveBatch(listBusPages);
                return Result.ok("文件导入成功！数据行数:" + listBusPages.size());
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

}
