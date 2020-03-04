package com.cfcc.modules.system.controller;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.system.query.QueryGenerator;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.common.util.oConvertUtils;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.entity.SysUserSet;
import com.cfcc.modules.system.service.ISysUserService;
import com.cfcc.modules.system.service.ISysUserSetService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
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
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 权限设置
 * @Author: jeecg-boot
 * @Date: 2019-10-17
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "权限设置")
@RestController
@RequestMapping("/testt/sysUserSet")
public class SysUserSetController {
    @Autowired
    private ISysUserSetService sysUserSetService;

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 分页列表查询
     *
     * @param sysUserSet
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "权限设置-分页列表查询")
    @ApiOperation(value = "权限设置-分页列表查询", notes = "权限设置-分页列表查询")
    @GetMapping(value = "/Home")
    public Result<SysUserSet> HomeAndDay(SysUserSet sysUserSet,
                                         HttpServletRequest req) {
        Result<SysUserSet> result = new Result<SysUserSet>();
        SysUserSet pageList = sysUserSetService.HomeAndDay(sysUserSet);
        result.setResult(pageList);
        return result;
    }

    /**
     * 分页列表查询
     *
     * @param sysUserSet
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "权限设置-分页列表查询")
    @ApiOperation(value = "权限设置-分页列表查询", notes = "权限设置-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SysUserSet>> queryPageList(SysUserSet sysUserSet,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   HttpServletRequest req) {
        Result<IPage<SysUserSet>> result = new Result<IPage<SysUserSet>>();
        IPage<SysUserSet> pageList = sysUserSetService.findPage(pageNo, pageSize, sysUserSet);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @AutoLog(value = "权限设置-分页列表查询")
    @ApiOperation(value = "权限设置-分页列表查询", notes = "权限设置-分页列表查询")
    @GetMapping(value = "/findByUserId")
    public Result<SysUserSet> findByUserId(@RequestParam(name = "userid", required = true) String suserId, HttpServletRequest req) {
        Result<SysUserSet> result = new Result<SysUserSet>();
        SysUserSet sysUserSet = sysUserSetService.findByUserId(suserId);
        result.setSuccess(true);
        result.setResult(sysUserSet);
        return result;
    }

    /**
     * 添加
     *
     * @param sysUserSet
     * @return
     */
    @AutoLog(value = "权限设置-添加")
    @ApiOperation(value = "权限设置-添加", notes = "权限设置-添加")
    @PostMapping(value = "/add")
    public Result<SysUserSet> add(@RequestBody SysUserSet sysUserSet, HttpServletRequest request) {
        Result<SysUserSet> result = new Result<SysUserSet>();
        try {
            List<SysUserSet> sysUserSetList = sysUserSetService.findByIId(sysUserSet.getIId());
            if (sysUserSetList.contains(sysUserSet.getIId())) {
                result.error500("该用户已添加");
                return result;
            }
            String token = request.getHeader("X-Access-Token");
            String username = JwtUtil.getUsername(token);
            SysUser sysUser = sysUserService.getUserByName(username);
            if (sysUser != null) {
                String userId = sysUser.getId();
                sysUserSet.setSUserId(userId);
            }
            sysUserSetService.insert(sysUserSet);
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
     * @param sysUserSet
     * @return
     */
    @AutoLog(value = "权限设置-编辑")
    @ApiOperation(value = "权限设置-编辑", notes = "权限设置-编辑")
    @PostMapping(value = "/edit")
    public Result<SysUserSet> edit(@RequestBody SysUserSet sysUserSet, HttpServletRequest request) {
        Result<SysUserSet> result = new Result<SysUserSet>();
        SysUserSet sysUserSetEntity = sysUserSetService.findById(sysUserSet.getIId());

        if (sysUserSetEntity == null) {
            result.error500("未找到对应实体");
        } else {
            String token = request.getHeader("X-Access-Token");
            String username = JwtUtil.getUsername(token);
            SysUser sysUser = sysUserService.getUserByName(username);
            if (sysUser != null) {
                String userId = sysUser.getId();
                sysUserSet.setSUserId(userId);
            }
            boolean ok = sysUserSetService.updateBYIid(sysUserSet);
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 查询下一任务时对应的人是否设置了代办消息提醒
     *
     * @return
     */
    @AutoLog(value = "查询是否设置了代办消息提醒")
    @ApiOperation(value = "查询是否设置了代办消息提醒", notes = "查询是否设置了代办消息提醒")
    @PostMapping(value = "/queryUserSet")
    public Result<List<String>> queryUserSet(@RequestBody String json, HttpServletRequest request) {
        Result<List<String>> result = new Result<List<String>>();
        Map<String, Object> map = (Map) JSONObject.parse(json);
        List<String> list = (List<String>) map.get("ids");
        System.out.println(list);
        String nameStr = String.join(",", list);
        sendMegToRtx("张杰,chen") ;
        System.out.println(nameStr);
        List<String> userName = sysUserSetService.queryUserSetByIds(nameStr);
        result.setResult(userName);
        return result;
    }

    public void sendMegToRtx(String userName) {
        try {
            userName = new String(userName.getBytes("GBK"), "GBK") ;
            String sendImg = "/SendNotify.cgi?";  //  RTX发送消息接口
            String host = "169.254.189.218";  //  RTX服务器地址
            String getSessionkey = "/getsessionkey.cgi?";  //  RTX获取会话接口
            int port = 8012;  //  RTX服务器监听端口
            //String[] receiverss  =  {  " woailuo "  };  //  接收人，RTX帐号
            //String sender = "";  //  发送人
            String content = "有代办消息";  //  内容
            StringBuffer sendMsgParams = new StringBuffer(sendImg);
            StringBuffer receiveUrlStr = new StringBuffer(userName);

        /*for  ( int  i  =   0 ; i  <  receiverss.length;  ++ i) {
            if  (receiveUrlStr.length()  ==   0 ) {
                receiveUrlStr.append(receiverss[i]);
            }  else  {
                receiveUrlStr.append( " , "   +  receiverss[i]);
            }
        }*/
            sendMsgParams.append("&receiver=" + receiveUrlStr);
            if (content != null) {
                sendMsgParams.append("&msg=" + new String(content.getBytes("utf-8"), "utf-8"));
            }

            /*if (sender != null ) {
                sendMsgParams.append(" &sender= " + sender);
            }*/
            URL url = null;

            url = new URL("HTTP", host, port, sendMsgParams.toString());
            System.out.println(url);
            HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
            String ret = httpconn.getHeaderField(3);
            System.out.println(ret);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "权限设置-通过id删除")
    @ApiOperation(value = "权限设置-通过id删除", notes = "权限设置-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            sysUserSetService.deleteByIidd(id);
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
    @AutoLog(value = "权限设置-批量删除")
    @ApiOperation(value = "权限设置-批量删除", notes = "权限设置-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<SysUserSet> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<SysUserSet> result = new Result<SysUserSet>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            List<String> list = Arrays.asList(ids.split(","));
            for (int i = 0; i < list.size(); i++) {
                this.sysUserSetService.deleteByIidd(list.get(i));
            }
            //this.sysUserSetService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "权限设置-通过id查询")
    @ApiOperation(value = "权限设置-通过id查询", notes = "权限设置-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<SysUserSet> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysUserSet> result = new Result<SysUserSet>();
        SysUserSet sysUserSet = sysUserSetService.getById(id);
        if (sysUserSet == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(sysUserSet);
            result.setSuccess(true);
        }
        return result;
    }


    /**
     * 通过userid查询
     *
     * @param
     * @return
     */
    @AutoLog(value = "权限设置-通过userid查询")
    @ApiOperation(value = "权限设置-通过userid查询", notes = "权限设置-通过userid查询")
    @GetMapping(value = "/queryByUserId")
    public Result<SysUserSet> queryByUserId(@RequestParam(name = "userId", required = false) String userId) {
        Result<SysUserSet> result = new Result<SysUserSet>();
        SysUserSet sysUserSet = sysUserSetService.findByUserId(userId);
        if (sysUserSet == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(sysUserSet);
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
        QueryWrapper<SysUserSet> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                SysUserSet sysUserSet = JSON.parseObject(deString, SysUserSet.class);
                queryWrapper = QueryGenerator.initQueryWrapper(sysUserSet, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SysUserSet> pageList = sysUserSetService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "权限设置列表");
        mv.addObject(NormalExcelConstants.CLASS, SysUserSet.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("权限设置列表数据", "导出人:Jeecg", "导出信息"));
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
                List<SysUserSet> listSysUserSets = ExcelImportUtil.importExcel(file.getInputStream(), SysUserSet.class, params);
                sysUserSetService.saveBatch(listSysUserSets);
                return Result.ok("文件导入成功！数据行数:" + listSysUserSets.size());
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
