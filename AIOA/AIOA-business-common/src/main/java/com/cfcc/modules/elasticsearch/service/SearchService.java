package com.cfcc.modules.elasticsearch.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: 李丽婷
 * @Date: 2019/11/6
 * @Time: 下午2:54
 */
public interface SearchService {


    //创建索引
    public   void  createIndex(String indexName, String indexType) throws  Exception;

    //删除索引
    public   void  deleteIndex(String indexName) throws  Exception;

    //该索引是否存在
    public   boolean  existsIndex(String indexName) throws  Exception;

    //添加单个数据
    public   void  addData(String json, String indexName, String indexType) throws  Exception;

    //有id  则 update   无id ：insert
    public RestStatus saveOrUpdate(String json, String id, String indexName, String indexType) throws  Exception;

    public RestStatus saveOrUpdate(List<Map<String,Object>> oaFileList, String indexName, String indexType) throws  Exception;

    //通过id删除
    public   Integer   deleteById(String id, String indexName, String indexType) throws  Exception;

    //全查
    public IPage<Map<String,Object>> queryAll(String indexName, Integer pageNo, Integer pageSize) throws  Exception;

    //高级查询
    public List<Map> search(String searchParams, String indexName, String indexType) throws IOException;


    //根据字段查询
    public   void   matchQuery(String keyword, String indexName, String indexType) throws  Exception;


    public   void   rangeQuery(String indexName,Long startTime,Long endTime) throws  Exception;

    public   void   fuzzyQuery(String keyword, String indexName) throws  Exception;


    public  void  sortById(String indexName) throws  Exception;

    public   void   queryByPage(int startIndex, int pageSize, String indexName)throws  Exception;

    //多字段查询
    public void queryMulitMatch(String keyword, String indexName) throws IOException;

    //组合查询
    //条件+范围+排序+分页+聚合+高亮
    public  IPage<Map<String,Object>>   query(String keyWord,Integer pageNo,Integer pageSize,String INDEX_NAME,String INDEX_TYPE);


}
