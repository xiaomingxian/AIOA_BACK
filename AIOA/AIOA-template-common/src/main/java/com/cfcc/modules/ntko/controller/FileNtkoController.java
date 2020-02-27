package com.cfcc.modules.ntko.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.util.FileUtils;
import com.cfcc.modules.message.websocket.WebSocket;
import com.cfcc.modules.oaBus.entity.OaBusdata;
import com.cfcc.modules.oaBus.entity.OaFile;
import com.cfcc.modules.oaBus.mapper.OaBusDynamicTableMapper;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.cfcc.modules.oaBus.service.IOaFileService;
import com.cfcc.modules.oaBus.service.OaBusDynamicTableService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author
 * @since
 */
@Slf4j
@RestController
@RequestMapping("/ntko/filentko")
public class FileNtkoController {

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Value(value = "${jeecg.path.uploadfile}")
    private String uploadfilepath;

    @Value(value = "${jeecg.path.templateFilePath}")
    private String tempFilePath;

    @Autowired
    private IOaFileService oaFileService;
    @Autowired
    private WebSocket webSocket;
    @Autowired
    private IOaBusdataService oaBusdataService;
    @Autowired
    private OaBusDynamicTableMapper dynamicTableMapper;

    /**
     * 起草底稿后保存上传到服务器
     *
     * @return
     * @Author
     */
    @PostMapping(value = "/upload")
    @Transactional
    public Result<String> upload(HttpServletRequest request,
                                 @RequestParam(value = "stable", required = true) String stable,
                                 @RequestParam(value = "tableid", required = true) String tableid,
                                 @RequestParam(value = "fileType", required = true) String fileType) {
        Result<String> result = new Result<>();
        try {
            String ctxPath = uploadpath;
//            String tempPath = uploadfilepath;
            Map<String, Object> map = FileUtils.Upload(ctxPath, request);
            String fileName = (String) map.get("fileName");
            String savePath = (String) map.get("savePath");
            OaFile oaFile = new OaFile();
            oaFile.setSTable(stable);
            oaFile.setITableId(Integer.parseInt(tableid));
            oaFile.setSFileType(fileType);
            oaFile.setSFileName(fileName);
            oaFile.setSFilePath(savePath);
            oaFile.setDCreateTime(new Date());
            Boolean flag = oaFileService.save(oaFile);
            String message = "kongjian";
            if (flag) {
                Map<String,Object> busdataMap=new HashMap<>();
                busdataMap.put("table",stable);
                busdataMap.put("i_id",Integer.parseInt(tableid));
                if ("1".equals(fileType)) {
                    busdataMap.put("i_is_draft",1);
                }
                if ("2".equals(fileType)) {
                    busdataMap.put("i_is_typeset",1);
                }
                if ("3".equals(fileType)) {
                    busdataMap.put("i_is_approve",1);
                }
                dynamicTableMapper.updateData(busdataMap);
//                oaBusdataService.updateFileStatus(busdataMap);
            }
            //System.out.println("---------------->>>>>>>>>>>>>>>创建保存成功");
            //----开启webSocket，起草底稿后向前端发一个请求，表明已经保存成功。
            sendWebSocketMessage(tableid,message) ;
            result.setSuccess(true);
        } catch (RuntimeException e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 向webSocket发送消息，表明已经保存完成
     * feng
     * @param
     * @param tableid
     */
    private void sendWebSocketMessage(String tableid, String message) {
        JSONObject obj = new JSONObject();
        obj.put("cmd", "user");
        obj.put("userId", tableid);
        obj.put("msgId", "M0001");
        obj.put("msgTxt", message);      //发送的消息为  true
        webSocket.sendOneMessage(tableid, obj.toJSONString());
    }

    /**
     * 查看查询当前的文件名
     *
     * @return
     * @Author
     */
    @GetMapping(value = "/file")
    public Result<String> showFile(@RequestParam(name = "stable", required = true) String stable,
                                   @RequestParam(value = "tableid", required = true) Integer tableid,
                                   @RequestParam(value = "fileType", required = true) String fileType) {
        Result<String> result = new Result();

        try {
            OaFile oaFile = new OaFile();
            oaFile.setSTable(stable);
            oaFile.setITableId(tableid);
            oaFile.setSFileType(fileType);
            QueryWrapper<OaFile> c = new QueryWrapper<>();
            c.setEntity(oaFile);

            OaFile ad = oaFileService.getOne(c);

//            String fileName = ad.getSFileName();
            String filePath=ad.getSFilePath();
            if (null == ad) {
                result.setMessage("文件控件系统出现问题，请联系管理");
            } else {
                result.setResult(filePath);
            }

            //System.out.println("----------------->>>>查看文件路径" + filePath);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("文件控件系统出现问题，请联系管理");
        }

        return result;
    }

    /**
     * 编辑底稿后保存上传新的文件并删除旧文件的信息
     *
     * @return
     * @Author
     */
    @PostMapping(value = "/edit")
    public Result<String> editFile(HttpServletRequest request,
                                   @RequestParam(name = "stable", required = true) String stable,
                                   @RequestParam(value = "tableid", required = true) Integer tableid,
                                   @RequestParam(value = "fileType", required = true) String fileType) throws IOException {

        Result<String> result = new Result<>();

        String ctxPath = uploadpath;
        Map<String, Object> map = FileUtils.Upload(ctxPath,request);

        String fileName = (String) map.get("fileName");
        String savePath = (String) map.get("savePath");


        OaFile oaFile = new OaFile();
        oaFile.setSTable(stable);
        oaFile.setITableId(tableid);
        oaFile.setSFileType(fileType);
        QueryWrapper<OaFile> c = new QueryWrapper<>();
        c.setEntity(oaFile);
        Boolean flag = oaFileService.remove(c);

        if (flag == true) {
            oaFile.setSTable(stable);
            oaFile.setITableId(tableid);
            oaFile.setSFileType(fileType);
            oaFile.setSFileName(fileName);
            oaFile.setSFilePath(savePath);
            oaFile.setDCreateTime(new Date());
            oaFileService.save(oaFile);
            result.setMessage("编辑成功");
        } else {
            result.setMessage("编辑功能出现问题，请联系管理员");
        }
        return result;
    }


    /**
     * 编辑附件后保存上传新的文件并删除旧文件的信息
     *
     * @return
     * @Author
     */
    @PostMapping(value = "/editFile")
    public Result<String> editEndsource(HttpServletRequest request,
                                        @RequestParam(value = "fileId", required = false) String fileId,
                                        @RequestParam(value = "fileType", required = false) String fileType) throws IOException {
        OaFile initFile = oaFileService.getById(fileId);
        Result<String> result = new Result<>();
        Calendar calendar = Calendar.getInstance();
        String path = uploadpath.replace("//", "/" +
                "") + "/" + calendar.get(Calendar.YEAR) +
                "/" + (calendar.get(Calendar.MONTH) + 1) +
                "/" + calendar.get(Calendar.DATE) + "/";
        File parent = new File(path);
        if (!parent.exists()){
            parent.mkdirs();
        }
        String fileName = initFile.getSFilePath().substring(initFile.getSFilePath().lastIndexOf(File.separator) + 1, initFile.getSFilePath().length());
        String savePath = parent.getPath() + File.separator + fileName;
        File savefile = new File(savePath);
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile mf = multipartRequest.getFile("file");// 获取上传文件对象
        FileCopyUtils.copy(mf.getBytes(), savefile);

        String saveFileName = fileName.replace(fileName.substring(fileName.lastIndexOf('_'), fileName.lastIndexOf('.')), "");

        Map<String,Object> oaFileMap=new HashMap<>();
        oaFileMap.put("table","oa_file");
        oaFileMap.put("i_id",initFile.getIId());
        oaFileMap.put("s_table",initFile.getSTable());
        oaFileMap.put("i_table_id",initFile.getITableId());
        oaFileMap.put("s_file_type",initFile.getSFileType());
        oaFileMap.put("s_file_name",saveFileName);
        oaFileMap.put("s_file_path",savePath);
        oaFileMap.put("d_create_time",new Date());
        int num=dynamicTableMapper.updateData(oaFileMap);
        if (fileType!=null){
            String projectPath = System.getProperty("user.dir");
            String path1 = projectPath.substring(0, projectPath.lastIndexOf(File.separator));
            String path2 = path1.substring(0, path1.lastIndexOf(File.separator));
            File template = new File(path2+File.separator+tempFilePath+File.separator+fileName);
            FileCopyUtils.copy(mf.getBytes(), template);
        }
        if (num!=0){
            log.info("----------------->编辑更新成功");
        }else {
            log.info("----------------->编辑更新失败");
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
    public Result singleCopyFile(@RequestParam(value = "filepath", required = false) String filepath) {
        Result<String> result = new Result< String >();
        Map<String,Object> map=new HashMap<>();
        map.put("sFilePath",filepath);
        OaFile oa = oaFileService.singleCopyFile(map);
        String fileName=oa.getSFileName();
        try {
            if (oa != null){
                result.success("复制成功！");
                result.setResult(fileName);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("复制失败");
        }
        return result;
    }

    @PostMapping(value = "/fileupload")
    public Result<String> fileupload(HttpServletRequest request,
                                     @RequestParam(name = "stable", required = true) String stable,
                                     @RequestParam(value = "tableid", required = true) Integer tableid) {
        Result<String> result = new Result<>();
        try {
            String ctxPath = uploadpath;
//            String tempPath = uploadfilepath;
            Map<String, Object> map = FileUtils.Upload(ctxPath, request);
            String fileName = (String) map.get("fileName");
            String savePath = (String) map.get("savePath");
            OaFile oaFile = new OaFile();
            oaFile.setSTable(stable);
            oaFile.setITableId(tableid);
            oaFile.setSFileType("1");
            oaFile.setSFileName(fileName);
            oaFile.setSFilePath(savePath);
            oaFile.setSCreateBy("admin");
            oaFile.setDCreateTime(new Date());
            oaFileService.save(oaFile);
            result.setSuccess(true);
        } catch (RuntimeException e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            log.error(e.getMessage(), e);
        }
        return result;
    }
}
