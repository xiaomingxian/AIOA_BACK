package com.cfcc.modules.elasticsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.modules.elasticsearch.service.SearchService;
import com.cfcc.modules.elasticsearch.utils.ESUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description:
 * @Author: 李丽婷
 * @Date: 2019/11/6
 * @Time: 下午2:55
 */

@Service
public class SearchServiceImpl implements SearchService {

    @Resource
    private RestHighLevelClient restHighLevelClient;
    private Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Override
    public void createIndex(String indexName, String indexType) throws  Exception {

        if (!existsIndex(indexName)){
            //1,创建请求对象
            CreateIndexRequest request = new CreateIndexRequest(indexName);
            //2,构建系统设置    分片，  副本
            ESUtils.buildSettings(request);
            ESUtils.buildMappings(request,indexType);
            CreateIndexResponse response = restHighLevelClient.indices().create(request,RequestOptions.DEFAULT);
            System.out.println(response.isAcknowledged());

        }else{
            logger.info(indexName+"所有已经存在，不能再次创建！！");
        }

    }

    @Override
    public boolean existsIndex(String indexName) throws Exception {
        //1,创建请求对象
        GetIndexRequest request = new GetIndexRequest();
        request.indices(indexName);
        //2,判断
        boolean f =  restHighLevelClient.indices().exists(request,RequestOptions.DEFAULT);
        logger.info(indexName+"是否存在："+f);
        return f;
    }

    @Override
    public void deleteIndex(String indexName) throws  Exception {

        DeleteIndexRequest request = new DeleteIndexRequest(indexName);

        AcknowledgedResponse response = restHighLevelClient.indices().delete(request,RequestOptions.DEFAULT);

        logger.info(indexName+"删除成功与否："+response.isAcknowledged());
    }

   //有id  则 update   无id ：insert
    @Override
    public RestStatus saveOrUpdate(List<Map<String,Object>> oaFileList,String indexName, String indexType) throws Exception {
        BulkRequest bulkRequest = new BulkRequest();
        for (Map<String, Object> map : oaFileList) {
            IndexRequest request = new IndexRequest(indexName,indexType, map.get("i_id").toString() + map.get("table_name").toString());
            request.source(JSON.toJSONString(map),XContentType.JSON);//指定添加的数据
            bulkRequest.add(request);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        logger.info("新增/修改："+bulk.status());
        return bulk.status();
    }

//    @Override
//    public RestStatus saveOrUpdate(String json, String id,String indexName, String indexType) throws Exception {
//        BulkRequest bulkRequest = new BulkRequest();
//        List<Map<String,Object>> oaFileList
//
//
//        IndexRequest request = new IndexRequest(indexName,indexType,id);
//        System.out.println("-------"+json);
//        request.source(json,XContentType.JSON);//指定添加的数据
//
//        //IndexRequest indexRequest, RequestOptions options
//        IndexResponse indexResponse =  restHighLevelClient.index(request,RequestOptions.DEFAULT);
//
//        logger.info("新增/修改："+indexResponse.status());
//
//        RestStatus status = indexResponse.status();
//        return status;
//    }



    @Override
    public IPage<Map<String,Object>> queryAll(String indexName, Integer pageNo, Integer pageSize) throws Exception {
        //searchRequest  搜索请求对象
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.types(indexName);//索引
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from((pageNo - 1)* pageSize);
        searchSourceBuilder.size(pageSize);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response  =  restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        SearchHits searchHits = response.getHits();
        long  total = searchHits.getTotalHits();
        List<Map<String,Object>> sourceList = new ArrayList<>();
        SearchHit hit [] = searchHits.getHits();
        for (SearchHit searchHit:hit){
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            sourceList.add(sourceAsMap);
        }
        IPage<Map<String,Object>> pageList = new Page<Map<String,Object>>();
        pageList.setRecords(sourceList);
        pageList.setTotal(total);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;
    }

    /**
     *组合查询
     *全文+范围+排序+分页+聚合+高亮
     * @throws Exception
     */
    @Override
    public IPage<Map<String,Object>> query(String keyWord,Integer pageNo,Integer pageSize,String INDEX_NAME,String INDEX_TYPE )  {
        System.out.println("-----------keyword:"+keyWord);
        System.out.println("-----------INDEX_NAME:"+INDEX_NAME);
//        keyWord = "admin";
//        pageNo = 3;
//        pageSize = 10;
//        INDEX_NAME = "elasticsearch2";
        //searchRequest  搜索请求对象
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.types(INDEX_NAME);//索引
        //查询
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //分页部分
        Integer start = (pageNo - 1) * pageSize;
        searchSourceBuilder.size(pageSize);//pageSize
        searchSourceBuilder.from(start);//起始记录
        //查询条件
        searchSourceBuilder.query(QueryBuilders.queryStringQuery(keyWord));
        //高亮查询
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("*").requireFieldMatch(false);//高亮字段
        //highlightBuilder.fields();
        highlightBuilder.preTags("<big style=\"color:red;\">");
        highlightBuilder.postTags("</big>");
        searchSourceBuilder.highlighter(highlightBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SearchHits searchHits = response.getHits();
        //总数据
        long total = searchHits.getTotalHits();
        List<Map<String, Object>> sourceList = new ArrayList<>();
        SearchHit hit[] = searchHits.getHits();
        for (SearchHit searchHit : hit) {
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            Set<String> set = highlightFields.keySet();
            for (String key : set) {
                //获取每一个Fragments中的高亮后的数据
                String highlightField = highlightFields.get(key).getFragments()[0].toString();
                sourceAsMap.put(key, highlightField);
            }
            sourceList.add(sourceAsMap);
        }

        for (Map<String, Object> map : sourceList) {
            System.out.println(map);
        }
        System.out.println("总条数：" + total);

        IPage<Map<String,Object>> pageList = new Page<Map<String,Object>>();
        pageList.setRecords(sourceList);
        pageList.setTotal(total);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;

    }


    //======================================================================


    @Override
    public void addData(String json,String indexName, String indexType) throws  Exception {

        IndexRequest request = new IndexRequest(indexName,indexType);
        request.source(json,XContentType.JSON);//指定添加的数据

        //IndexRequest indexRequest, RequestOptions options
        IndexResponse indexResponse =  restHighLevelClient.index(request,RequestOptions.DEFAULT);

        logger.info("新增："+indexResponse.status());

    }

    @Override
    public RestStatus saveOrUpdate(String json, String id, String indexName, String indexType) throws Exception {
        IndexRequest request = new IndexRequest(indexName,indexType,id);
//        System.out.println("-------"+json);
        request.source(json,XContentType.JSON);//指定添加的数据

        //IndexRequest indexRequest, RequestOptions options
        IndexResponse indexResponse =  restHighLevelClient.index(request,RequestOptions.DEFAULT);

        logger.info("新增/修改："+indexResponse.status());

        RestStatus status = indexResponse.status();
        return status;
    }


    @Override
    public void deleteById(String id,String indexName, String indexType) throws Exception {

        //(String index, String type, String id
        DeleteRequest request = new DeleteRequest(indexName,indexType,id);

        DeleteResponse response =  restHighLevelClient.delete(request,RequestOptions.DEFAULT);

        logger.info("根据id删除document："+response.status());
    }


    @Override
    public List<Map> search(String searchParams,String indexName, String indexType) throws IOException {
        boolean isShowHighLight = false;
        ArrayList<Map> list = new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder = ESUtils.getSearchBuilder(searchParams);
        JSONObject jsonObject = JSONObject.parseObject(searchParams);
        Integer start = jsonObject.getInteger("start");
        Integer rows = jsonObject.getInteger("rows");
        searchSourceBuilder.from(start);
        searchSourceBuilder.size(rows);
        String highLightPreTag = jsonObject.getString("highLightPreTag");
        String highLightPostTag = jsonObject.getString("highLightPostTag");

        // 高亮设置
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.requireFieldMatch(false).field("title").preTags(highLightPreTag).postTags(highLightPostTag);
        searchSourceBuilder.highlighter(highlightBuilder);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchSourceBuilder);
        //3、发送请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);

        //处理搜索命中文档结果
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        float maxScore = hits.getMaxScore();
        logger.info("totalHits = " + totalHits);
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            //取_source字段值
            String sourceAsString = hit.getSourceAsString(); //取成json串
            Map<String, Object> sourceAsMap = hit.getSourceAsMap(); // 取成map对象
            logger.info("index:" + index + "  type:" + type + "  id:" + id);
            logger.info(sourceAsString);
            //取高亮结果
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField highlight = highlightFields.get("title");
            if (highlight != null) {
                Text[] fragments = highlight.fragments();//多值的字段会有多个值
                if (fragments != null) {
                    String fragmentString = fragments[0].string();
                    logger.info("title highlight : " + fragmentString);
                    //可用高亮字符串替换上面sourceAsMap中的对应字段返回到上一级调用
                    sourceAsMap.put("requestContent", fragmentString);
                    logger.info(JSON.toJSONString(sourceAsMap));
                    list.add(sourceAsMap);
                }
            }
        }
        StringBuilder info = new StringBuilder();
        info.append(",index：").append(indexName);
        info.append(",type：").append(indexType);
        info.append(",查询语句：").append(searchParams);
        info.append(",开始位置：").append(start);
        info.append(",显示行数：").append(rows);
        info.append("显示的指定字段为：显示全部字段");
        info.append(",高亮显示的字段为：title,高亮显示前缀：" + highLightPreTag + ",高亮显示后缀：" + highLightPostTag);
        logger.info(list.size() == 0 ? info + "，本次无查询结果！" : info + "，此次查询共有" + totalHits + "条记录,分页显示:" + list.size() + "条。");
        logger.info("================================================================================");
        logger.info("query result:" + JSON.toJSONString(list));
        return list;
    }

    @Override
    public void matchQuery(String keyword,String indexName, String indexType) throws Exception {

        //searchRequest  搜索请求对象
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.types(indexName);//索引
        //查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //match 查询
        //QueryBuilders. 指定查询的类型

        QueryBuilders.matchAllQuery();   //无条件全查
        searchSourceBuilder.query(QueryBuilders.matchQuery("title",keyword));  //确定字段后查询

        searchRequest.source(searchSourceBuilder);

        SearchResponse response  =  restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

        /**
         * "hits": {
         *     "total": 9,
         *     "max_score": 1,
         *     "hits": [
         *       {}]
         */
        SearchHits searchHits = response.getHits();

        long  total = searchHits.getTotalHits();

        System.out.println("total:"+total);

        SearchHit hit [] = searchHits.getHits();
        for (SearchHit searchHit:hit){
            System.out.println(searchHit.getSourceAsMap());
        }
    }

    @Override
    public void rangeQuery(String indexName,Long startTime,Long endTime) throws Exception {
        SearchRequest request = new SearchRequest();
        request.types(indexName);//索引  database

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //QueryBuilder
        searchSourceBuilder.query(QueryBuilders.rangeQuery("startTime").gte(startTime).lte(endTime));
        //查询条件
        request.source(searchSourceBuilder);


        SearchResponse response =   restHighLevelClient.search(request,RequestOptions.DEFAULT);

        //hit   s  hits
       SearchHits searchHits =   response.getHits();
        System.out.println(searchHits.getTotalHits());
        SearchHit searchHit[] = searchHits.getHits();

        for (SearchHit hit : searchHit) {
            System.out.println(hit.getSourceAsMap());
        }

    }

    @Override
    public void fuzzyQuery(String keyword,String indexName) throws Exception {
        SearchRequest request = new SearchRequest();
        request.types(indexName);//索引  database

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //QueryBuilder
        searchSourceBuilder.query(QueryBuilders.fuzzyQuery("title",keyword));
        //查询条件
        request.source(searchSourceBuilder);


        SearchResponse response =   restHighLevelClient.search(request,RequestOptions.DEFAULT);

        //hits  hits
        SearchHits searchHits =   response.getHits();
        System.out.println(searchHits.getTotalHits());
        SearchHit searchHit[] = searchHits.getHits();

        for (SearchHit hit : searchHit) {
            System.out.println(hit.getSourceAsMap());
        }



    }

    @Override
    public void sortById(String indexName)  throws  Exception {
        SearchRequest request = new SearchRequest();
        request.types(indexName);//索引  database

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();


        searchSourceBuilder.sort("id",SortOrder.DESC);
        //查询条件
        request.source(searchSourceBuilder);



        SearchResponse response =   restHighLevelClient.search(request,RequestOptions.DEFAULT);

        //hits  hits
        SearchHits searchHits =   response.getHits();
        System.out.println(searchHits.getTotalHits());
        SearchHit searchHit[] = searchHits.getHits();

        for (SearchHit hit : searchHit) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    @Override
    public void queryByPage(int startIndex, int pageSize,String indexName) throws Exception {

        SearchRequest request = new SearchRequest();
        request.types(indexName);//索引  database


        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(pageSize);//pageSize
        searchSourceBuilder.from(startIndex);//起始记录

        //查询条件
        request.source(searchSourceBuilder);

        SearchResponse response =   restHighLevelClient.search(request,RequestOptions.DEFAULT);

        //hits  hits
        SearchHits searchHits =   response.getHits();
        System.out.println(searchHits.getTotalHits());
        SearchHit searchHit[] = searchHits.getHits();

        for (SearchHit hit : searchHit) {
            System.out.println(hit.getSourceAsMap());
        }


    }

    @Override
    public void queryMulitMatch(String keyword,String indexName) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.types(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(keyword,"title","author"));

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse =  restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

        SearchHits hits = searchResponse.getHits();
        System.out.println("count:"+hits.totalHits);
        SearchHit[] h =  hits.getHits();
        for (SearchHit hit : h) {
            System.out.println("结果"+hit.getSourceAsMap());
        }
    }


}
