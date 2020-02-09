package com.cfcc.modules.elasticsearch.job;/*
 *
 *
 */

import com.alibaba.fastjson.JSON;
import com.cfcc.modules.elasticsearch.service.SearchService;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.cfcc.modules.oaBus.service.IOaFileService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.rest.RestStatus;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class OaFile implements Job {

    private Logger logger = LoggerFactory.getLogger(OaFile.class);

    @Autowired
    private IOaFileService iOaFileService;

    @Autowired
    private IOaBusdataService iOaBusdataService;

    @Resource
    private RestHighLevelClient client;//操作es

    @Resource
    private SearchService searchService;

    @Value("${spring.data.elasticsearch.indexName1}")
    private String INDEX_NAME;

    @Value("${spring.data.elasticsearch.typeName1}")
    private String INDEX_TYPE;

    //    //库名
//    static String INDEX_NAME = "elasticsearch1";
    //分片
//    static String INDEX_TYPE = "oa_busdata";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<Map<String,Object>> oaFileList = iOaFileService.getOaFile();
        if (oaFileList == null){
            log.info("【全局检索信息】定时任务添加数据失败oaFileList = {}", oaFileList);
        }
        //判断索引是否存在
        try {
            if (!searchService.existsIndex(INDEX_NAME)){
                logger.info(INDEX_NAME+"索引开始创建！！！！");
                searchService.createIndex(INDEX_NAME,INDEX_TYPE);
            }
            //批量增加/更新
            BulkRequest bulkAddRequest = new BulkRequest();
            for (Map<String, Object> map : oaFileList) {
                String json = JSON.toJSONString(map);
                RestStatus restStatus = searchService.saveOrUpdate(json, map.get("i_id").toString() + map.get("table").toString(), INDEX_NAME, INDEX_TYPE);
                System.out.println("----------"+!restStatus.equals("OK")+"----------");
                //restStatus.equals("OK")成功时返回false
                if (!restStatus.equals("OK")){
                    log.info("-----------------oa_busdata添加数据成功-------------------");
                }else {
                    log.info("-----------------oa_busdata添加数据失败-------------------");
                    while (restStatus.equals("OK")){
                        restStatus = searchService.saveOrUpdate(json, map.get("i_id").toString() + map.get("table").toString(), INDEX_NAME, INDEX_TYPE);
                    }
                    log.info("-----------------oa_busdata添加数据成功-------------------");

                }
                //将数据库中修改为已添加ES库成功
                iOaBusdataService.updateIsES(oaFileList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //------------------------------------------




    }

}
