package com.cfcc.modules.elasticsearch.job;/*
 *
 *
 */

import com.alibaba.fastjson.JSON;
import com.cfcc.modules.elasticsearch.service.SearchService;
import com.cfcc.modules.oaBus.entity.OaFile;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.cfcc.modules.oaBus.service.IOaFileService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.rest.RestStatus;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class OaFileContext implements Job {

    private Logger logger = LoggerFactory.getLogger(OaFileContext.class);

    @Autowired
    private IOaBusdataService iOaBusdataService;

    @Autowired
    private IOaFileService iOaFileService;

    @Autowired
    private SearchService searchService;

    @Value("${spring.data.elasticsearch.indexName1}")
    private String INDEX_NAME1;

    @Value("${spring.data.elasticsearch.typeName1}")
    private String INDEX_TYPE1;

    @Value("${spring.data.elasticsearch.indexName2}")
    private String INDEX_NAME2;

    @Value("${spring.data.elasticsearch.typeName2}")
    private String INDEX_TYPE2;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<Map<String,Object>> oaFileList = iOaFileService.getOaFile();
        System.out.println("***************111"+oaFileList.toString());
        System.out.println("***************222"+oaFileList.size());

        if (oaFileList.size() != 0){
            //判断索引是否存在
            try {
                if (!searchService.existsIndex(INDEX_NAME1)){
                    logger.info(INDEX_NAME1+"索引开始创建！！！！");
                    searchService.createIndex(INDEX_NAME1,INDEX_TYPE1);
                }
                RestStatus status = searchService.saveOrUpdate(oaFileList, INDEX_NAME1, INDEX_TYPE1);
                System.out.println(status);


//                for (Map<String, Object> map : oaFileList) {
//
//                    String json = JSON.toJSONString(map);
//                    RestStatus restStatus = searchService.saveOrUpdate(json, map.get("i_id").toString() + map.get("table_name").toString(), INDEX_NAME1, INDEX_TYPE1);
////                    System.out.println("----------"+!restStatus.equals("OK")+"----------");
//                    //restStatus.equals("OK")成功时返回false
//                    if (!restStatus.equals("OK")){
//                        log.info("-----------------oa_busdata添加数据成功-------------------");
//                    }else {
//                        log.info("-----------------oa_busdata添加数据失败-------------------");
//                        while (restStatus.equals("OK")){
//                            restStatus = searchService.saveOrUpdate(json, map.get("i_id").toString() + map.get("table_name").toString(), INDEX_NAME1, INDEX_TYPE1);
//                        }
//                        log.info("-----------------oa_busdata添加数据成功-------------------");
//                    }
//                    //将数据库中修改为已添加ES库成功
//                    iOaBusdataService.updateIsES(oaFileList);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //=============================

        List<OaFile> oaFileContextList = iOaFileService.getOaFileContext();
        for (OaFile oaFile : oaFileContextList) {
            System.out.println(oaFile);
        }
        System.out.println("***************333"+oaFileContextList.size());
        if (oaFileContextList.size() != 0){
            try {
                //判断索引是否存在
                if (!searchService.existsIndex(INDEX_NAME2)){
                    logger.info(INDEX_NAME2+"索引开始创建！！！！");
                    searchService.createIndex(INDEX_NAME2,INDEX_TYPE2);
                }
                int i = 0;
                for (OaFile oaFile : oaFileContextList) {
                    i += 1;
                    System.out.println("hahah"+ i);
                    String jsonString = JSON.toJSONString(oaFile);
                    String id = "oaFile" + oaFile.getIId().toString();
                    RestStatus restStatus = searchService.saveOrUpdate(jsonString,id, INDEX_NAME2, INDEX_TYPE2);
                }
                System.out.println("-----------------oafile文件添加成功-----------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
