package com.cfcc.modules.elasticsearch.job;/*
 *
 *
 */

import com.alibaba.fastjson.JSON;
import com.cfcc.common.system.vo.DictModel;
import com.cfcc.modules.elasticsearch.service.SearchService;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.service.IBusFunctionService;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.cfcc.modules.oaBus.service.IOaFileService;
import com.cfcc.modules.system.service.ISysDepartService;
import com.cfcc.modules.system.service.ISysDictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
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
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class OaFile implements Job {

    private Logger logger = LoggerFactory.getLogger(OaFile.class);



    @Resource
    private RestHighLevelClient client;//操作es

    @Resource
    private SearchService searchService;

    @Value("${spring.data.elasticsearch.indexName1}")
    private String INDEX_NAME;

    @Value("${spring.data.elasticsearch.typeName1}")
    private String INDEX_TYPE;

    @Autowired
    private IOaFileService oaFileService;

    @Autowired
    private IOaBusdataService oaBusdataService;

    @Autowired
    private ISysDictService sysDictService;

    @Autowired
    private ISysDepartService sysDepartService;

//    @Autowired
//    private IOaBusdataService oaBusdataService;

    @Autowired
    private IBusFunctionService busFunctionService;

    //上传文件地址
    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Autowired
    private IOaBusdataService iOaBusdataService;

    public static String ANNOTATION = "/*!mycat:schema=";
    public static String ANNOTATION_END = "*/ ";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        /*
        * 一、首先去主库的数据库字典表根据dict_code查询item_text
        * text:库名
        * */
        String dictCode = "org_schema";
        List<DictModel> dictModels = sysDictService.queryDictItemsByCode(dictCode);
        if (dictModels.size() > 0 ){
            isNotNull(dictModels);
        }else {
            isNull();
        }
    }

    public void isNull(){
        String DBtext = "";
        String DBvalue = "";
        List<String> departIdList = sysDepartService.findSysDepartByParentIdAndOrgType(DBvalue);
        for (String departId : departIdList) {
            List<BusFunction> busFunctionList = busFunctionService.getBusFunctionListByDepartId(departId,DBvalue);
            List<Map<String, Object>> oaBusdataList = new ArrayList<>();
            List<Map<String, Object>> oaBusdata1 = new ArrayList<>();
            String sBusdataTable = null;
            for (BusFunction busFunction : busFunctionList) {
                sBusdataTable = busFunction.getSBusdataTable();
                String columnLists = oaFileService.getColumList(sBusdataTable, busFunction.getIId(),DBvalue);
                if (columnLists.equals("")){
                    continue;
                }
                //根据表名和业务模块id查询数据
                List<Map<String, Object>> oaBusdata = oaBusdataService.getOaBusdataList(columnLists,busFunction,DBvalue);
                if (oaBusdata.size() == 0) {  //执行下一循环
                    System.out.println("-----------无数据存入ES库！！！！！-----------");
                    continue;
                }

                List<Map<String,Object>> oaBusdataCan = oaFileService.getOaBusdata(sBusdataTable,oaBusdata,busFunction,DBvalue);
                for (Map<String, Object> map : oaBusdataCan) {
                    Map map1 = new HashedMap();
                    if (map.get("i_is_file") == null){
                        continue;
                    }
                    Set<String> set = map.keySet();
                    for (String key : set) {
                        Object value = map.get(key);
                        map1.put(key,value );
                    }
                    oaBusdata1.add(map1);
                }
                for (Map<String, Object> map : oaBusdataCan) {
                    map.remove("i_is_file");
                }
                oaBusdataList.addAll(oaBusdataCan);
            }
            if (oaBusdataList.size() != 0){
                try {
                    //索引名 = 库名+银行id+1

                    String indexName = "elasticsearch"+DBvalue+departId+1;
                    String indexType = "oaBusdate";
                    if (!searchService.existsIndex(indexName)){
                        logger.info(indexName+"索引开始创建！！！！");
                        searchService.createIndex(indexName,indexType);
                    }

                    Iterator<Map<String, Object>> iterator = oaBusdataList.iterator();
                    while (iterator.hasNext()) {
                        Map<String, Object> map = iterator.next();
                        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
                        if (map.get("d_create_time") != null && map.get("d_create_time") != "") {
                            String dCreateTime = map.get("d_create_time")+"";
                            String time = dCreateTime.substring(0,dCreateTime.lastIndexOf("."));
                            Date parse = dateFormat1.parse(time);
                            String format = dateFormat2.format(parse);
                            map.put("d_create_time", format);
                        }
                        if (map.get("d_update_time") != null && map.get("d_update_time") != "") {
                            String dCreateTime = map.get("d_update_time")+"";
                            String time = dCreateTime.substring(0,dCreateTime.lastIndexOf("."));
                            Date parse = dateFormat1.parse(time);
                            String format = dateFormat2.format(parse);
                            map.put("d_update_time", format);
                        }
                        if (map.get("d_datetime1") != null && map.get("d_datetime1") != "") {
                            String dCreateTime = map.get("d_datetime1")+"";
                            String time = dCreateTime.substring(0,dCreateTime.lastIndexOf("."));
                            Date parse = dateFormat1.parse(time);
                            String format = dateFormat2.format(parse);
                            map.put("d_datetime1", format);
                        }
                        if (map.get("d_datetime2") != null && map.get("d_datetime2") != "") {
                            String dCreateTime = map.get("d_datetime2")+"";
                            String time = dCreateTime.substring(0,dCreateTime.lastIndexOf("."));
                            Date parse = dateFormat1.parse(time);
                            String format = dateFormat2.format(parse);
                            map.put("d_datetime2", format);
                        }
                    }

                    for (Map<String, Object> map : oaBusdataList) {

                        String json = JSON.toJSONString(map);
                        RestStatus restStatus = searchService.saveOrUpdate(json, map.get("table_name").toString() + map.get("i_id").toString(), indexName, indexType);
                        Integer res = restStatus.getStatus();
                        System.out.println("----是否添加成功----" + restStatus);
                        if (res == 200) {
                            log.info("-----------------oa_busdata添加数据成功-------------------");
                        } else {
                            log.info("-----------------oa_busdata添加数据失败-------------------");
                            while (res != 200) {
                                restStatus = searchService.saveOrUpdate(json, map.get("table_name").toString() + map.get("i_id").toString(), indexName, indexType);
                                res = restStatus.getStatus();
                            }
                            log.info("-----------------oa_busdata添加数据成功-------------------");
                        }
                    }
                    //将数据库中修改为已添加ES库成功
                    iOaBusdataService.updateIsES(oaBusdataList,DBvalue);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            //====================================
            if (oaBusdata1.size() == 0){
                continue;
            }
            List<Map<String,Object>> oaFileListAll = new ArrayList<>();
            Iterator<Map<String, Object>> iterator = oaBusdata1.iterator();
            while (iterator.hasNext()){
                Map<String, Object> oaBusdatum = iterator.next();
                if (oaBusdatum.get("i_is_file") == null){
                    iterator.remove();
                    continue;
                }
                List<Map<String,Object>> oaFileList = oaFileService.getOaFileByTableAndTableId(uploadpath,null,oaBusdatum.get("i_id")+"",oaBusdatum.get("table_name")+"",DBvalue);
                oaFileListAll.addAll(oaFileList);
            }
            if (oaFileListAll.size() != 0){
                try {
                    //索引名 = 库名+银行id+2
                    String indexName = "elasticsearch"+DBvalue+departId+2;
                    String indexType = "oaFile";
                    if (!searchService.existsIndex(indexName)){
                        logger.info(indexName+"索引开始创建！！！！");
                        searchService.createIndex(indexName,indexType);
                    }
                    for (Map<String, Object> map : oaFileListAll) {
                        String json = JSON.toJSONString(map);
                        RestStatus restStatus = searchService.saveOrUpdate(json, map.get("i_id").toString(), indexName, indexType);
                        Integer res = restStatus.getStatus();
                        System.out.println("----是否添加成功----"+restStatus);
                        if (res == 200){
                            log.info("-----------------oa_busdata添加数据成功-------------------");
                        }else {
                            log.info("-----------------oa_busdata添加数据失败-------------------");
                            while (res != 200){
                                restStatus = searchService.saveOrUpdate(json, map.get("i_id").toString(), indexName, indexType);
                                res = restStatus.getStatus();
                            }
                            log.info("-----------------oa_busdata添加数据成功-------------------");
                        }
                        //将数据库中修改为已添加ES库成功
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void isNotNull(List<DictModel> dictModels ){
        for (DictModel dictModel : dictModels) {
            String DBtext = dictModel.getText();
            String DBvalue = dictModel.getValue();

            DBvalue = ANNOTATION+DBvalue+ANNOTATION_END;

            List<String> departIdList = sysDepartService.findSysDepartByParentIdAndOrgType(DBvalue);
            for (String departId : departIdList) {
                List<BusFunction> busFunctionList = busFunctionService.getBusFunctionListByDepartId(departId,DBvalue);
                List<Map<String, Object>> oaBusdataList = new ArrayList<>();
                List<Map<String, Object>> oaBusdata1 = new ArrayList<>();
                String sBusdataTable = null;
                for (BusFunction busFunction : busFunctionList) {
                    sBusdataTable = busFunction.getSBusdataTable();
                    String columnLists = oaFileService.getColumList(busFunction.getSBusdataTable(), busFunction.getIId(),DBvalue);
                    if (columnLists.equals("")){
                        continue;
                    }
                    //根据表名和业务模块id查询数据
                    List<Map<String, Object>> oaBusdata = oaBusdataService.getOaBusdataList(columnLists,busFunction,DBvalue);
                    if (oaBusdata.size() == 0) {  //执行下一循环
                        System.out.println("-----------无数据存入ES库！！！！！-----------");
                        continue;
                    }

                    List<Map<String,Object>> oaBusdataCan = oaFileService.getOaBusdata(sBusdataTable,oaBusdata,busFunction,DBvalue);
                    for (Map<String, Object> map : oaBusdataCan) {
                        Map map1 = new HashedMap();
                        if (map.get("i_is_file") == null){
                            continue;
                        }
                        Set<String> set = map.keySet();
                        for (String key : set) {
                            Object value = map.get(key);
                            map1.put(key,value );
                        }
                        oaBusdata1.add(map1);
                    }
                    for (Map<String, Object> map : oaBusdataCan) {
                        map.remove("i_is_file");
                    }
                    oaBusdataList.addAll(oaBusdataCan);
                }
                if (oaBusdataList.size() != 0){

                    try {
                        //索引名 = 库名+银行id+1
                        String DBvalue1 = DBvalue.substring(DBvalue.lastIndexOf("=") + 1, DBvalue.lastIndexOf("*"));
                        String indexName = "elasticsearch" + DBvalue1 + departId + 1;
                        String indexType = "oaBusdate";
                        if (!searchService.existsIndex(indexName)) {
                            logger.info(indexName + "索引开始创建！！！！");
                            searchService.createIndex(indexName, indexType);
                        }
                        Iterator<Map<String, Object>> iterator = oaBusdataList.iterator();
                        while (iterator.hasNext()) {
                            Map<String, Object> map = iterator.next();
                            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
                            if (map.get("d_create_time") != null && map.get("d_create_time") != "") {
                                String dCreateTime = map.get("d_create_time")+"";
                                String time = dCreateTime.substring(0,dCreateTime.lastIndexOf("."));
                                Date parse = dateFormat1.parse(time);
                                String format = dateFormat2.format(parse);
                                map.put("d_create_time", format);
                            }
                            if (map.get("d_update_time") != null && map.get("d_update_time") != "") {
                                String dCreateTime = map.get("d_update_time")+"";
                                String time = dCreateTime.substring(0,dCreateTime.lastIndexOf("."));
                                Date parse = dateFormat1.parse(time);
                                String format = dateFormat2.format(parse);
                                map.put("d_update_time", format);
                            }
                            if (map.get("d_datetime1") != null && map.get("d_datetime1") != "") {
                                String dCreateTime = map.get("d_datetime1")+"";
                                String time = dCreateTime.substring(0,dCreateTime.lastIndexOf("."));
                                Date parse = dateFormat1.parse(time);
                                String format = dateFormat2.format(parse);
                                map.put("d_datetime1", format);
                            }
                            if (map.get("d_datetime2") != null && map.get("d_datetime2") != "") {
                                String dCreateTime = map.get("d_datetime2")+"";
                                String time = dCreateTime.substring(0,dCreateTime.lastIndexOf("."));
                                Date parse = dateFormat1.parse(time);
                                String format = dateFormat2.format(parse);
                                map.put("d_datetime2", format);
                            }
                        }


                        for (Map<String, Object> map : oaBusdataList) {
                            String json = JSON.toJSONString(map);
                            RestStatus restStatus = searchService.saveOrUpdate(json, map.get("table_name").toString() + map.get("i_id").toString() , indexName, indexType);
                            int res = restStatus.getStatus();
                            if (res == 200) {
                                log.info("-----------------oa_busdata添加数据成功-------------------");
                            } else {
                                log.info("-----------------oa_busdata添加数据失败-------------------");
                                while (res != 200) {
                                    restStatus = searchService.saveOrUpdate(json, map.get("table_name").toString() + map.get("i_id").toString(), indexName, indexType);
                                    res = restStatus.getStatus();
                                }
                                log.info("-----------------oa_busdata添加数据成功-------------------");
                            }
                        }
                        //将数据库中修改为已添加ES库成功
                        iOaBusdataService.updateIsES(oaBusdataList,DBvalue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //====================================
                if (oaBusdata1.size() == 0){
                    continue;
                }
                List<Map<String,Object>> oaFileListAll = new ArrayList<>();
                Iterator<Map<String, Object>> iterator = oaBusdata1.iterator();
                String DBvalue2 = DBvalue.substring(DBvalue.indexOf("=")+1,DBvalue.lastIndexOf("*"));
                while (iterator.hasNext()){
                    Map<String, Object> oaBusdatum = iterator.next();
                    if (oaBusdatum.get("i_is_file") == null){
                        iterator.remove();
                        continue;
                    }
                    List<Map<String,Object>> oaFileList = oaFileService.getOaFileByTableAndTableId(uploadpath,DBvalue2,oaBusdatum.get("i_id")+"",oaBusdatum.get("table_name")+"",DBvalue);
                    oaFileListAll.addAll(oaFileList);
                }
                if (oaFileListAll.size() != 0){
                    try {
                        //索引名 = 库名+银行id+2

                        String indexName = "elasticsearch"+DBvalue2+departId+2;
                        String indexType = "oaFile";

                        if (!searchService.existsIndex(indexName)){
                            logger.info(indexName+"索引开始创建！！！！");
                            searchService.createIndex(indexName,indexType);
                        }
                        for (Map<String, Object> map : oaFileListAll) {
                            String json = JSON.toJSONString(map);
                            RestStatus restStatus = searchService.saveOrUpdate(json, map.get("tableName").toString() + map.get("tableId").toString()+map.get("id"), indexName, indexType);
                            int res = restStatus.getStatus();
                            System.out.println("----是否添加成功----"+restStatus);
                            if (res == 200){
                                log.info("-----------------oa_busdata添加数据成功-------------------");
                            }else {
                                log.info("-----------------oa_busdata添加数据失败-------------------");
                                while (res != 200){
                                    restStatus = searchService.saveOrUpdate(json, map.get("tableName").toString() + map.get("tableId").toString(), indexName, indexType);
                                    res = restStatus.getStatus();
                                }
                                log.info("-----------------oa_busdata添加数据成功-------------------");
                            }
                            //将数据库中修改为已添加ES库成功
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
