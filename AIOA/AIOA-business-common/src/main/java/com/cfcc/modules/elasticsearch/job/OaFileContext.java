package com.cfcc.modules.elasticsearch.job;

/*
 *
 *
 */

import com.alibaba.fastjson.JSON;
import com.cfcc.modules.elasticsearch.service.SearchService;
import com.cfcc.modules.oaBus.entity.OaFile;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.cfcc.modules.oaBus.service.IOaFileService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
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

    @Resource
    private RestHighLevelClient restHighLevelClient;


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        /*
        * 一、首先去主库的数据库字典表根据dict_code查询item_text
        * sysDictService.queryDictItemsByCode(dictCode);
        * 二、根据查询的出来的item_text字段值（数据库名）遍历查询各个数据库的数据，分别根据各中支获取数据，
        * 根据查到的   item_text字段值（数据库名）+ 去
        *   1、在sys_depart表中根据parent_id = 0 和 1 和org_type = 1 条件查询id
        *   全文检索：
        *   1、根据oa_bus_function表中确定的添加到es库的数据查询
        *   2、找到到oa_busdate数据表时也要根据s_create_unitid(创建者机构id)查找
        *   3、当查找数据中特殊意义的数据
        *           参考位置private Map<String, Object> selOptionByDtailList
        *   对附件检索：
        *       第一种方法：
        *       1、直接检索oa_file表，查询所有数据List，再取其中的s_file_name字段值，进行字段截取，判断是否是能检索的文件4
        *
        *       第二种方法：
        *       1、直接根据全文检索得到的oa_busdata的数据根据里面的i_is_file判断是否有附件
        *           如果有则去oa_file表中查询数据（判断文件是否为可检索文件）
        *       2、对得到的数据中s_file_path字段获取其内容，存放到该字段s_file_path中
        *   。。。。。
        * 三、
        * */
        String DBvalue = null;

        List<Map<String,Object>> oaFileList = iOaFileService.getOaFile(DBvalue);
        for (Map<String, Object> map : oaFileList) {
            //System.out.println(map);
        }
        System.out.println("***************222"+oaFileList.size());
        if (oaFileList.size() != 0){
            /*BulkRequest bulkRequest = new BulkRequest();
            for (Map<String, Object> map : oaFileList) {
                IndexRequest request = new IndexRequest("elasticsearch1","oa_busdata", map.get("i_id").toString()+map.get("i_bus_model_id").toString());
                request.source(JSON.toJSONString(map), XContentType.JSON);//指定添加的数据
                bulkRequest.add(request);
            }
            BulkResponse bulk = null;
            try {
                bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                System.out.println("***************" + bulk.status() + "***************");
            } catch (IOException e) {
                e.printStackTrace();
            }*/

             try {
              if (!searchService.existsIndex(INDEX_NAME1)){
                    logger.info(INDEX_NAME1+"索引开始创建！！！！");
                    searchService.createIndex(INDEX_NAME1,INDEX_TYPE1);
                }
                for (Map<String, Object> map : oaFileList) {
                    String json = JSON.toJSONString(map);
                    RestStatus restStatus = searchService.saveOrUpdate(json, map.get("table_name").toString() + map.get("i_id").toString() , INDEX_NAME1, INDEX_TYPE1);
                    //System.out.println("----是否添加成功----"+restStatus);
                    if (restStatus.equals("OK")){
                        log.info("-----------------oa_busdata添加数据成功-------------------");
                    }else {
                        log.info("-----------------oa_busdata添加数据失败-------------------");
                        while (restStatus.equals("OK")){
                            restStatus = searchService.saveOrUpdate(json, map.get("table_name").toString() + map.get("i_id").toString() , INDEX_NAME1, INDEX_TYPE1);
                        }
                        log.info("-----------------oa_busdata添加数据成功-------------------");
                    }
                    //将数据库中修改为已添加ES库成功
                }
                 iOaBusdataService.updateIsES(oaFileList,DBvalue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //=============================

        List<OaFile> oaFileContextList = iOaFileService.getOaFileContext(DBvalue);
        for (OaFile oaFile : oaFileContextList) {
            //System.out.println(oaFile);
        }
        System.out.println("***************333"+oaFileContextList.size());
        if (oaFileContextList.size() != 0){
            try {
                //判断索引是否存在
                if (!searchService.existsIndex(INDEX_NAME2)){
                    logger.info(INDEX_NAME2+"索引开始创建！！！！");
                    searchService.createIndex(INDEX_NAME2,INDEX_TYPE2);
                }
                for (OaFile oaFile : oaFileContextList) {
                    String jsonString = JSON.toJSONString(oaFile);
                    String id = "oaFile" + oaFile.getIId().toString();
                    RestStatus restStatus = searchService.saveOrUpdate(jsonString,id, INDEX_NAME2, INDEX_TYPE2);
                    System.out.println("----是否添加成功----"+restStatus);
                    if (restStatus.equals("OK")){
                        log.info("-----------------oa_busdata添加数据成功-------------------");
                    }else {
                        log.info("-----------------oa_busdata添加数据失败-------------------");
                        while (restStatus.equals("OK")){
                            restStatus = searchService.saveOrUpdate(jsonString, id, INDEX_NAME2, INDEX_TYPE2);
                        }
                        log.info("-----------------oa_busdata添加数据成功-------------------");
                    }
                }
                System.out.println("-----------------oafile文件添加成功-----------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
