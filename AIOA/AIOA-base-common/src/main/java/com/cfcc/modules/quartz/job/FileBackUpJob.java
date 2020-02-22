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
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class FileBackUpJob implements Job {

    @Value("${jeecg.path.fileSourcePath}")
    private String sourcePath;

    @Value("${jeecg.path.fileDataPath}")
    private String dataPath;

    @Autowired
    private IQrtzBackUpService iQrtzBackUpService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Date startDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String startTime = sdf.format(startDate);
        log.info("开始执行备份文件------" + startTime);
        File sourceFile = new File(sourcePath);
        File[] sourceFileList = sourceFile.listFiles();
        String dayDataPath = "filebackUp_" + sdf.format(new Date()); //每日数据备份文件名称
        File homeFile = new File(dataPath + File.separator + dayDataPath);
        if (!homeFile.exists()) {
            homeFile.mkdirs();
        }
        for (int i = 0; i < sourceFileList.length; i++) {
            String sourceName = sourceFileList[i].getName();
            //System.out.println(sourceName);
            String savePath = "";
            if (sourceName.indexOf(".") != -1) {
                savePath = dataPath + File.separator + dayDataPath + File.separator + sourceName.substring(0, sourceName.lastIndexOf(".")) + ".zip";
            } else {
                savePath = dataPath + File.separator + dayDataPath + File.separator + sourceName + ".zip";
            }
            ZipUtils.createZip(sourceFileList[i].getAbsolutePath(), savePath);
        }
        Date endDate = new Date();
        String endTime = sdf.format(endDate);
        log.info("结束执行备份文件------" + endTime);
        File backFile = new File(dataPath + File.separator + dayDataPath);
        long filesLength = ZipUtils.countFilesLength(backFile);
        QrtzBackUp backUp = new QrtzBackUp();
        backUp.setIBackType(2);
        backUp.setSName(backFile.getName());
        backUp.setSBackPath(backFile.getPath());
        backUp.setSFileSize(ZipUtils.countFileLength(filesLength));
        backUp.setDStartTime(startDate);
        backUp.setDEndTime(endDate);
        boolean save = iQrtzBackUpService.save(backUp);
        if (save) {
            log.info(String.format("附件备份信息已完成"));
        } else {
            log.info(String.format("附件备份信息未完成"));

        }
    }
}
