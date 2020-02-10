package com.cfcc.modules.papertitle.controller;

import com.cfcc.common.api.vo.Result;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.modules.oaBus.entity.OaFile;
import com.cfcc.modules.oaBus.service.impl.OaFileServiceImpl;
import io.swagger.annotations.Api;
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
import java.util.Date;
import java.util.List;

@Slf4j
@Api(tags = "上传登录图片")
@RestController
@RequestMapping("/oafile/LoginPicture")
public class LoginPictureController {

    @Autowired
    private OaFileServiceImpl oaFileService;

    //上传文件地址
    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    //文件在项目目录中的临时文件路径
    @Value(value = "${jeecg.path.uploadfile}")
    private String uploadfile;

    @GetMapping(value = "/checkDefault")
    public Result<?> checkDefault(@RequestParam("id")Integer id,@RequestParam("checked")Integer checked){
        Result<OaFile> result = new Result<>();
        Integer fileType = 9;
        Boolean b = oaFileService.checkDefault(fileType,id,checked);
        result.setSuccess(b);
        return result;
    }


    /**
     * 登陆时获取登录图片
     * @return
     */
    @PostMapping(value = "/loginPictrue")
    public Result<?> loginPictrue() {
        Result<OaFile> result = new Result<>();
        Integer fileType = 9;
        List<OaFile> oaFileList = oaFileService.getOaFileByType(fileType);
        if (oaFileList.size() == 1){
            result.setResult(oaFileList.get(0));
        }
        result.setSuccess(true);
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
        Result<OaFile> result = new Result<>();
        try {
            Integer fileType = 9;
            List<OaFile> oaFileList = oaFileService.getOaFileByType0(fileType);
            if (oaFileList.size() != 0){
//                oaFileService.deleteFileByTypeAndOrder(oaFileList);
                for (OaFile oaFile : oaFileList) {
                    File file = new File(oaFile.getSFilePath());
                    file.delete();
                    //删除之前的登录图片
                    oaFileService.deleteOneFileByType(oaFile.getIId());
                }
            }
            //获取用户名称
            String token = request.getHeader("X-Access-Token");
            String username = JwtUtil.getUsername(token);
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
            OaFile oaFile = new OaFile();
            oaFile.setSFileType("9");        // 附件类型为 9 登录页面图片
            oaFile.setSFileName(orgName);        //设置附件名字
            oaFile.setSFilePath(savePath);        //设置文件路径
            oaFile.setSCreateBy(username);
            oaFile.setIOrder(0);
            oaFile.setITableId(1);
            oaFile.setDCreateTime(new Date());
            oaFileService.save(oaFile);
            List<OaFile> oaFileLists = oaFileService.getOaFileByType0(fileType);
            if (oaFileLists.size() == 1){
                result.setResult(oaFileLists.get(0));
                result.setSuccess(true);
                return result;
            }
            result.setSuccess(false);
        } catch (IOException e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     *
     * @param id 图片的id，将其修改为登录背景
     * @return
     */
    @ApiOperation("更新图片数据")
    @GetMapping(value = "/updatePicture")
    public Result<?> updatePicture(Integer id ) {
        Result<String> result = new Result<>();
        Integer fileType = 9;
        Boolean b = oaFileService.updatePicture(fileType,id);
        if (b){
            result.setSuccess(true);
            result.setMessage("修改登录页面成功");
        }else {
            result.setSuccess(false);
            result.setMessage("修改登录页面失败");
        }
        return result;
    }


    @ApiOperation("查询图片数据")
    @GetMapping(value = "/readPicture")
    public boolean readPicture(@RequestParam("resourceType") String resourceType,
                               HttpServletResponse response) {
        Integer fileType = 9;
        Boolean b = oaFileService.readPictureSer(fileType, resourceType, response);
        return b ;
    }


    @ApiOperation("查询图片数据")
    @GetMapping(value = "/readNotPicture")
    public boolean readPicture1(@RequestParam("id")Integer id ,@RequestParam("resourceType") String resourceType,
                               HttpServletResponse response) {
        try {
            Boolean b = oaFileService.readNotPicture(id,resourceType, response);
            if (!b){
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true ;
    }

    @ApiOperation("更新图片数据")
    @GetMapping(value = "/updatePicText")
    public Result<?> updatePicText(String picText) {
        Result<String> result = new Result<>();
        Integer fileType = 12;
        String oldPicText = oaFileService.findPicText(fileType);
        Boolean flag = false;
        if (oldPicText == null){
            flag = oaFileService.savePicText(fileType,picText);
        }else {
            flag = oaFileService.updatePicText(fileType,picText);
        }
        if (flag){
            result.setSuccess(true);
            result.setMessage("修改登录页面成功");
        }else {
            result.setSuccess(false);
            result.setMessage("修改登录页面失败");
        }
        return result;
    }

    @PostMapping(value = "/getPictrueText")
    public Result<?> getPictrueText() {
        Result<String> result = new Result<>();
        Integer fileType = 12;
        String picText = oaFileService.findPicText(fileType);
        result.setResult(picText);
        return result;
    }
}
