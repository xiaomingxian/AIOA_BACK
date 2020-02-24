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

    @Autowired
    private IOaBusdataService iOaBusdataService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        /*
        * 一、首先去主库的数据库字典表根据dict_code查询item_text
        * text:库名
        * */
        String dictCode = "org_schema";
        List<DictModel> dictModels = sysDictService.queryDictItemsByCode(dictCode);
        for (DictModel dictModel : dictModels) {
            String DBtext = dictModel.getText();
            String DBvalue = dictModel.getValue();
//            if ()
            List<String> departIdList = sysDepartService.findSysDepartByParentIdAndOrgType(DBvalue);
            for (String departId : departIdList) {
                List<BusFunction> busFunctionList = busFunctionService.getBusFunctionListByDepartId(departId,DBvalue);
                List<Map<String, Object>> oaBusdataList = new ArrayList<>();
                List<Map<String, Object>> oaBusdata1 = new ArrayList<>();
                String sBusdataTable = null;
                for (BusFunction busFunction : busFunctionList) {
                    sBusdataTable = busFunction.getSBusdataTable();
                    String columnLists = oaFileService.getColumList("oa_busdata30", 64, DBvalue);
//                    String columnLists = oaFileService.getColumList(sBusdataTable, busFunction.getIId());
                    if (columnLists.equals("")){
                        continue;
                    }

//                    BusFunction busFunction = new BusFunction();
//                    busFunction.setIId(64);
//                    busFunction.setIBusModelId(49);
//                    busFunction.setSBusdataTable("oa_busdata30");
//                    busFunction.setSName("电子公告");

                    //根据表名和业务模块id查询数据
                    List<Map<String, Object>> oaBusdata = oaBusdataService.getOaBusdataList(columnLists,busFunction,DBvalue);
                    if (oaBusdata.size() == 0) {  //执行下一循环
                        System.out.println("-----------无数据存入ES库！！！！！-----------");
                        continue;
                    }
                    for (Map<String, Object> map : oaBusdata) {
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
                    for (Map<String, Object> map : oaBusdata) {
                        map.remove("i_is_file");
                    }
                    List<Map<String,Object>> oaBusdataCan = oaFileService.getOaBusdata(sBusdataTable,oaBusdata,busFunction,DBvalue);
                    oaBusdataList.addAll(oaBusdataCan);
                }
                if (oaBusdataList.size() != 0){
                    try {
                        //索引名 = 库名+银行id+1
                        String indexName = DBtext+departId+1;
                        String indexType = "oaBusdate";
                        if (!searchService.existsIndex(indexName)){
                            logger.info(indexName+"索引开始创建！！！！");
                            searchService.createIndex(indexName,indexType);
                        }
                        for (Map<String, Object> map : oaBusdataList) {
                            String json = JSON.toJSONString(map);
                            RestStatus restStatus = searchService.saveOrUpdate(json, map.get("table_name").toString() + map.get("i_id").toString() , indexName, indexType);
                            System.out.println("----是否添加成功----"+restStatus);
                            if (restStatus.equals("OK")){
                                log.info("-----------------oa_busdata添加数据成功-------------------");
                            }else {
                                log.info("-----------------oa_busdata添加数据失败-------------------");
                                while (restStatus.equals("OK")){
                                    restStatus = searchService.saveOrUpdate(json, map.get("table_name").toString() + map.get("i_id").toString() , indexName, indexType);
                                }
                                log.info("-----------------oa_busdata添加数据成功-------------------");
                            }
                            //将数据库中修改为已添加ES库成功
                        }
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
                    List<Map<String,Object>> oaFileList = oaFileService.getOaFileByTableAndTableId(oaBusdatum.get("i_id")+"",sBusdataTable,DBvalue);
                    oaFileListAll.addAll(oaFileList);
                }
                if (oaFileListAll.size() != 0){
                    try {
                        //索引名 = 库名+银行id+2
                        String indexName = DBtext+departId+2;
                        String indexType = "oaFile";
                        if (!searchService.existsIndex(indexName)){
                            logger.info(indexName+"索引开始创建！！！！");
                            searchService.createIndex(indexName,indexType);
                        }
                        for (Map<String, Object> map : oaFileListAll) {
                            String json = JSON.toJSONString(map);
                            RestStatus restStatus = searchService.saveOrUpdate(json, map.get("i_id").toString(), indexName, indexType);
                            System.out.println("----是否添加成功----"+restStatus);
                            if (restStatus.equals("OK")){
                                log.info("-----------------oa_busdata添加数据成功-------------------");
                            }else {
                                log.info("-----------------oa_busdata添加数据失败-------------------");
                                while (restStatus.equals("OK")){
                                    restStatus = searchService.saveOrUpdate(json, map.get("i_id").toString(), indexName, indexType);
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
