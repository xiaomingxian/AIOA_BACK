package com.cfcc.modules.quartz.job;


import com.cfcc.common.util.ZipUtils;
import com.cfcc.modules.system.entity.QrtzBackUp;
import com.cfcc.modules.system.service.IQrtzBackUpService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class DatabaseBackUpJob implements Job {


    @Value("${spring.datasource.dynamic.datasource.master.url}")
    private String url;

    @Value("${spring.datasource.dynamic.datasource.master.username}")
    private String username;

    @Value("${spring.datasource.dynamic.datasource.master.password}")
    private String password;

    @Value("${jeecg.path.databaseCopyPath}")
    private String dataPath;

    @Autowired
    private IQrtzBackUpService iQrtzBackUpService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        File savePath = new File(dataPath);
        if (!savePath.exists()) {
            savePath.mkdirs();
        }
        String ip = url.substring(url.indexOf("//") + 2, url.lastIndexOf(":"));
        String port = url.substring(url.lastIndexOf(":") + 1, url.lastIndexOf(":") + 5);
        String databaseName = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"));
        Date startDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String startTime = sdf.format(startDate);
        log.info(String.format("数据库备份开始-----" + startTime));
//        String stmt ="cmd /C D:\\mysqldump -h127.0.0.1 -p3306 -uroot -proot --databases aioa>F:\\aioa_2019-11-26.sql";
        String stmt = "cmd /C " + "mysqldump  -h" + "127.0.0.1" + " "
                + "-p" + port + " "
                + "-u" + username + " "
                + "-p" + password + " "
                + "--hex-blob "
                + databaseName
                + ">" + dataPath + File.separator + databaseName + "_" + startTime + ".sql";
        log.info(String.format("执行备份命令-----" + stmt));
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(stmt);
            InputStream in = process.getInputStream();
            if (process.waitFor() == 0) {
                Date endDate = new Date();
                String endTime = sdf.format(endDate);
                log.info(String.format("数据库备份结束-----" + endTime));
                String filePath = dataPath + File.separator + databaseName + "_" + startTime + ".sql";
                File file = new File(filePath);
                QrtzBackUp backUp = new QrtzBackUp();
                backUp.setIBackType(1);
                backUp.setSName(file.getName());
                backUp.setSBackPath(filePath);
                backUp.setSFileSize(ZipUtils.getFileSize(file));
                backUp.setDStartTime(startDate);
                backUp.setDEndTime(endDate);
                boolean save = iQrtzBackUpService.save(backUp);
                if (save) {
                    log.info(String.format("数据库备份信息已生成"));
                } else {
                    log.info(String.format("数据库备份信息未完成"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
