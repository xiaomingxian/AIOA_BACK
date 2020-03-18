package com.cfcc.common.util;


import lombok.extern.slf4j.Slf4j;
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
import java.util.Random;

@Slf4j
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
    public static Map<String,Object> Upload(String orgSchema,String Path1, HttpServletRequest request){
        Map<String,Object> map = new HashMap<>();
        try {
            String path="";
            Calendar calendar = Calendar.getInstance();
            String savePath1=calendar.get(Calendar.YEAR) +
                    File.separator + (calendar.get(Calendar.MONTH) + 1) +
                    File.separator + calendar.get(Calendar.DATE) + File.separator;;
            if (!orgSchema.equals("")){
                path = Path1.replace("//", "/" +
                        "")+File.separator+ orgSchema + File.separator + calendar.get(Calendar.YEAR) +
                        File.separator + (calendar.get(Calendar.MONTH) + 1) +
                        File.separator + calendar.get(Calendar.DATE) + File.separator;
            }else {
                path = Path1.replace("//", "/" +
                        "") + File.separator + calendar.get(Calendar.YEAR) +
                        File.separator + (calendar.get(Calendar.MONTH) + 1) +
                        File.separator + calendar.get(Calendar.DATE) + File.separator;
            }

            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();// 创建文件根目录
            }
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile mf = multipartRequest.getFile("file");// 获取上传文件对象
            String orgName = mf.getOriginalFilename();// 获取文件名
            String fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis()+FileUtils.generatePassword(10) + orgName.substring(orgName.indexOf("."));
            String numName=fileName.substring(fileName.indexOf("_")+1);
            String savePath = file.getPath() + File.separator + numName;
            String newSavePath = savePath1 + numName;
            File savefile = new File(savePath);
            FileCopyUtils.copy(mf.getBytes(), savefile);
            map.put("fileName",numName);
            map.put("savePath",savePath);
            map.put("newSavePath",newSavePath);
            log.info("文件保存成功！！") ;
            System.out.println("AAAAA文件保存成功！！");
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.toString());
        }
        return map;
    }

    public static String generatePassword (int length) {
        // 最终生成的密码
        String password = "";
        Random random = new Random();
        for (int i = 0; i < length; i ++) {
            // 随机生成0或1，用来确定是当前使用数字还是字母 (0则输出数字，1则输出字母)
            int charOrNum = random.nextInt(2);
            if (charOrNum == 1) {
                // 随机生成0或1，用来判断是大写字母还是小写字母 (0则输出小写字母，1则输出大写字母)
                int temp = random.nextInt(2) == 1 ? 65 : 97;
                password += (char) (random.nextInt(26) + temp);
            } else {
                // 生成随机数字
                password += random.nextInt(10);
            }
        }
        return password;
    }

    public static Boolean deleteFile(String sPath){
        Boolean flag=false;
        File file = new File(sPath);
        if (file.isFile() && file.exists()){
            file.delete();
            flag=true;
        }
        return flag;
    }
}
