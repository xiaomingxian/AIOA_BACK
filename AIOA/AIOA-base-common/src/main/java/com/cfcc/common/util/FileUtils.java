package com.cfcc.common.util;


import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class FileUtils {
    /**
     * 数字转换中文大写
     */
    public static String int2bing(int src){
        final String num[]={"〇","一","二","三","四","五","六","七","八","九"};
        String dst="";
        while (src>0){
            dst=(num [src % 10]) + dst;
            src=src / 10;
        }
        return dst;
    }
    /**
     * 文件上传
     */
    public static Map<String,Object> Upload(String Path1,String Path2, HttpServletRequest request){
        Map<String,Object> map = new HashMap<>();
        try {
//            String Path = Path1;//服务器路径
//            String fileName = null;
//			String bizPath = "files";
//            File filePath=new File("FileUtils.java");
//            String apath=filePath.getAbsolutePath();
            String apath=System.getProperty("user.dir");
            String bpath=apath.substring(0,apath.lastIndexOf("\\"));
//            String cpath=bpath.substring(0,bpath.lastIndexOf("\\"));
            String newpath=bpath.replace("\\","/");
            String path2=newpath+"/"+Path2;

            Calendar calendar = Calendar.getInstance();
            String path = Path1.replace("//", "/" +
                    "") + "/" + calendar.get(Calendar.YEAR) +
                    "/" + (calendar.get(Calendar.MONTH) + 1) +
                    "/" + calendar.get(Calendar.DATE) + "/";
            File file = new File(path);
            if (!file.exists()) {
//                System.out.println("--------------------------->>>>>>文件目录没有！！！！");
                file.mkdirs();// 创建文件根目录
            }
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile mf = multipartRequest.getFile("file");// 获取上传文件对象
            String orgName = mf.getOriginalFilename();// 获取文件名
            String fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.indexOf("."));
            String savePath = file.getPath() + File.separator + fileName;
            File savefile = new File(savePath);
            FileCopyUtils.copy(mf.getBytes(), savefile);
            String tempPath=path2;//临时文件路径
            File tempfile = new File(tempPath);
            String tempFile=tempfile.getPath()+File.separator+ fileName;
            File file1 = new File(tempFile);
//            if (!file1.exists()) {
//                file1.mkdirs();// 创建临时文件目录
//            }
            FileCopyUtils.copy(mf.getBytes(),file1 );
            map.put("fileName",fileName);
            map.put("savePath",savePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

}
