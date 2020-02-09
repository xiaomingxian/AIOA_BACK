package com.cfcc.modules.elasticsearch.utils;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

/**
 * @Description:
 * @Company:
 * @Author: 李丽婷
 * @Date: 2019/11/6
 * @Time: 下午2:58
 */
public class ESUtils {


    /**
     * --  创建索引
     *
     * PUT shop
     * {
     *   "settings": {
     *     "number_of_shards": 3,
     *     "number_of_replicas": 2
     *
     *   }
     * }
     */
    public  static  void  buildSettings(CreateIndexRequest request){

        request.settings(Settings.builder().put("index.number_of_shards",3));
        request.settings(Settings.builder().put("index.number_of_replicas",2));

    }
    /**
     * --   建立类型(table)
     *
     * PUT  /shop/_mapping/tb_item
     * {
     *   "properties": {
     *     "title":{
     *       "index": true,
     *       "store": true,
     *       "analyzer": "ik_smart",
     *       "type": "text"
     *     },
     *     "price":{
     *       "type": "double"
     *     },
     *     "sell_point":{
     *       "analyzer": "ik_smart",
     *       "type": "text"
     *     }
     *   }
     * }
     */
    public   static  void  buildMappings(CreateIndexRequest request,String typeName) throws  Exception{
        if (typeName.equals("elasticsearch1")){
            createOaBusDataMappingBuilder(request,typeName);
        }else if (typeName.equals("elasticsearch2")){
            createOaFileMappingBuilder(request,typeName);
        }else {
            System.out.println("不能创建该索引！！！！");
        }
    }

    public static void createOaFileMappingBuilder(CreateIndexRequest request,String typeName)throws  Exception{
        XContentBuilder xContentBuilder = JsonXContent.contentBuilder()
                .startObject()
                    .startObject("properties")
                        .startObject("iId")
                        .field("index",false)
                        .field("type","integer")
                        .endObject()

                        .startObject("sTable")
                        .field("index",false)
                        .field("type","keyword")
                        .endObject()

                     /*   .startObject("iTableId")
                        .field("index",false)
                        .field("type","integer")
                        .endObject()

                        .startObject("sFileType")
                        .field("index",false)
                        .field("type","keyword")
                        .endObject()

                        .startObject("iOrder")
                        .field("index",false)
                        .field("type","integer")
                        .endObject()

                        .startObject("sFileName")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .endObject()

                        .startObject("sFilePath")
                        .field("index",false)
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .endObject()

                        .startObject("sCreateBy")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .endObject()

                        .startObject("dCreateTime")
                        .field("index",false)
                        .field("format","yyyy-MM-dd HH:mm:ss || yyyy-MM-dd || yyyy/MM/dd HH:mm:ss || yyyy/MM/dd ||epoch_millis")
                        .field("analyzer","ik_smart")
                        .endObject()

                        .startObject("sUpdateBy")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .endObject()

                        .startObject("dUpdateTime")
                        .field("index",false)
                        .field("format","yyyy-MM-dd HH:mm:ss || yyyy-MM-dd || yyyy/MM/dd HH:mm:ss || yyyy/MM/dd ||epoch_millis")
                        .field("analyzer","ik_smart")
                        .endObject()

                        .startObject("sContent")
                        .field("analyzer","ik_smart")
                        .field("type","keyword")
                        .endObject()

                        .startObject("sTitle")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .endObject()

*/
                    .endObject()
                .endObject();
        request.mapping(typeName,xContentBuilder);
    }

    public static void createOaBusDataMappingBuilder(CreateIndexRequest request,String typeName)throws  Exception{
        XContentBuilder xContentBuilder = JsonXContent.contentBuilder()
                .startObject()
                    .startObject("properties")
                        .startObject("i_id")
                        .field("index",false)
                        .field("type","integer")
                        .endObject()
/*
                        .startObject("i_bus_model_id")
                        .field("index",false)
                        .field("type","integer")
                        .endObject()

                        .startObject("table_name")
                        .field("index",false)
                        .field("type","integer")
                        .endObject()

                        .startObject("i_bus_function_id")
                        .field("type","integer")
                        .field("index",false)
                        .field("store",true)
                        .endObject()

                        .startObject("PROC_INST_ID")
                        .field("type","keyword")
                        .field("index",false)
                        .field("store",true)
                        .endObject()

                        .startObject("s_cur_task_name")
                        .field("type","keyword")
                        .field("index",false)
                        .field("store",true)
                        .endObject()

                        .startObject("i_safetylevel")
                        .field("type","keyword")
                        .field("index",false)
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("i_urgency")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_main_unit_names")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_cc_unit_names")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_inside_deptnames")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_report_nuit_names")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_report_nuit_names")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_crc_deptnames")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_title")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_left_parameter")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_unit_name")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_dept_name")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_middle_parameter")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_right_parameter")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_file_num")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("i_is_state")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("i_is_sealdate")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("i_is_typeset")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("i_is_approve")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("i_is_archives")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("i_is_es")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("i_is_send")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("i_is_display")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_remarks")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("i_create_year")
                        .field("type","integer")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("i_create_month")
                        .field("type","integer")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("i_create_day")
                        .field("type","integer")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("i_create_day")
                        .field("type","integer")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("i_phone")
                        .field("type","integer")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_create_name")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_create_by")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_create_dept")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_create_deptid")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_create_unitid")
                        .field("type","keyword")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()

                        .startObject("s_create_unitid")
                        .field("format","yyyy-MM-dd HH:mm:ss || yyyy-MM-dd || yyyy/MM/dd HH:mm:ss || yyyy/MM/dd ||epoch_millis")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()*/

                        .startObject("s_create_unitid")
                        .field("format","yyyy-MM-dd HH:mm:ss || yyyy-MM-dd || yyyy/MM/dd HH:mm:ss || yyyy/MM/dd ||epoch_millis")
                        .field("analyzer","ik_smart")
                        .field("store",true)
                        .endObject()
                    .endObject()
                .endObject();
        request.mapping(typeName,xContentBuilder);
    }


    public  static SearchSourceBuilder getSearchBuilder(String searchParams){
        //JSONObject jsonObject = JSONObject.parseObject(searchParams);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermQueryBuilder termQueryBuilder = null;
        if (searchParams!=null){
            termQueryBuilder = QueryBuilders.termQuery("title",searchParams);
        }
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (termQueryBuilder!=null){
            queryBuilder.must(termQueryBuilder);
        }
        searchSourceBuilder.query(queryBuilder);
        return searchSourceBuilder;
    }
}
