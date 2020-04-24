package com.cfcc.modules.app.controller;


import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.service.ISysUserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

/**
 * @Author
 * @since
 */
@Slf4j
@RestController
@RequestMapping("/app/appPersonal")
public class AppPersonalController {

    //上传文件地址
    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Autowired
    private ISysUserService sysUserService;


    @AutoLog(value = "我的")
    @ApiOperation(value="我的", notes="获取用户的头像、姓名、职位")
    @RequestMapping(value = "/getPersonage")
    public Result<Map<String,Object>> getPersonage(String userId){
        Result<Map<String,Object>> result = new Result<Map<String,Object>>();
        try {
            Map<String,Object> userInfor = sysUserService.getPersonage(userId);
            result.setResult(userInfor);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
            log.error(e.getMessage(), e);
        }
        return result;
    }

    @AutoLog(value = "个人设置")
    @ApiOperation(value="我的-个人设置", notes="个人设置")
    @RequestMapping(value = "/updateSysUser")
    public Result<String> updateSysUser(SysUser sysUser){
        Result<String> result = new Result<String>();
        try {
            Boolean flag = sysUserService.updateSysUser(sysUser);
            if (flag){
                result.setSuccess(true);
                result.setMessage("保存成功");
            }else {
                result.setSuccess(false);
                result.setMessage("保存失败");
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("保存失败");
            log.error(e.getMessage(), e);
        }
        return result;
    }


    @AutoLog(value = "个人设置")
    @ApiOperation(value="我的-密码", notes="个人设置")
    @RequestMapping(value = "/updatePasswordById")
    public Result<String> updatePasswordById(SysUser sysUser){
        Result<String> result = new Result<String>();
        if (sysUser.getPassword() != null){
            try {
                String id = sysUser.getId();
                String password = sysUser.getPassword();
                Boolean flag = sysUserService.updatePasswordById(id,password);
                if (flag){
                    result.setSuccess(true);
                    result.setMessage("保存成功");
                }else {
                    result.setSuccess(false);
                    result.setMessage("保存失败");
                }
            } catch (Exception e) {
                result.setSuccess(false);
                result.setMessage("保存失败");
                log.error(e.getMessage(), e);
            }
        }else {
            result.setSuccess(false);
            result.setMessage("保存失败");
        }
        return result;
    }

    /**
     * 单文件上传
     * 上传前把数据库中所有order 为0的数据删除
     * 只保留一条order 为1的数据
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/upload")
    public Result<?> upload(HttpServletRequest request, HttpServletResponse response) {
        Result<String> result = new Result<>();
        try {
            //获取用户id
            LoginInfo loginInfo = sysUserService.getLoginInfo(request);
            String userId = loginInfo.getId();
            //获取用户名称
//            String token = request.getHeader("X-Access-Token");
//            String username = JwtUtil.getUsername(token);
            String ctxPath = uploadpath;
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
            MultipartHttpServletRequest multipartRequest = null;
            if (request instanceof MultipartHttpServletRequest){
                multipartRequest = (MultipartHttpServletRequest)request;
            }
            MultipartFile mf = multipartRequest.getFile("file");// 获取上传文件对象
            String orgName = mf.getOriginalFilename();// 获取文件名
            fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.indexOf("."));
            String savePath = file.getPath() + File.separator + fileName;
            File savefile = new File(savePath);
            FileCopyUtils.copy(mf.getBytes(), savefile);
            Boolean flag = sysUserService.saveavatar(userId,savePath);
            if (!flag){
                result.setSuccess(true);
                result.setMessage("上传头像失败");
                return result;
            }
            result.setResult(savePath);
            result.setSuccess(false);
        } catch (IOException e) {
            result.setSuccess(false);
            result.setMessage("上传头像失败");
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 下载图片
     * @return
     */
    @ApiOperation("查询图片数据")
    @GetMapping(value = "/readPicture")
    public boolean readPicture(@RequestParam("resourceType") String resourceType,
                               HttpServletResponse response, HttpServletRequest request) {
        //获取用户名称
        //获取用户id
        LoginInfo loginInfo = sysUserService.getLoginInfo(request);
        String userId = loginInfo.getId();
        Boolean b = sysUserService.getAvatarByUsername(userId,resourceType, response);
        return b ;
    }



}
