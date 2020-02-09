package com.cfcc.modules.elasticsearch.controller;/*
 *
 *
 */

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.modules.elasticsearch.entity.EsSearch;
import com.cfcc.modules.elasticsearch.service.EsSearchService;
import com.cfcc.modules.elasticsearch.service.SearchService;
import com.cfcc.modules.oaBus.service.IOaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags="")
@RestController
@RequestMapping("/oaEs/oaelasticsearch")
public class OaElasticsearchController {

    @Value("${spring.data.elasticsearch.indexName1}")
    private String INDEX_NAME1;  //elasticsearch1

    @Value("${spring.data.elasticsearch.typeName1}")
    private String INDEX_TYPE1;  //oa_busdata

    @Value("${spring.data.elasticsearch.indexName2}")
    private String INDEX_NAME2;  //elasticsearch2

    @Value("${spring.data.elasticsearch.typeName2}")
    private String INDEX_TYPE2;  //oa_file

    @Autowired
    private SearchService searchService;

    @Autowired
    private EsSearchService esSearchService;

    @Autowired
    private IOaFileService iOaFileService;

    @AutoLog(value = "全文检索-统计搜索数据")
    @ApiOperation(value="业务配置表【全文检索】-搜索数据添加", notes="业务配置表【全文检索】-搜索数据添加")
    @RequestMapping(value = "/addsearch")
    public Result<List<String>> addEsSearch(String keyWord){
        Result<List<String>> result = new Result<List<String>>();
        esSearchService.addSearchfrequency(keyWord);
        result.setSuccess(true);
        return result;
    }

    @AutoLog(value = "全文检索-搜索数据查询")
    @ApiOperation(value="业务配置表【全文检索】-搜索数据查询", notes="业务配置表【全文检索】-搜索数据查询")
    @RequestMapping(value = "/getsearch")
    public Result<List<EsSearch>> getEsSearchList(@RequestBody String keyWord){
        Result<List<EsSearch>> result = new Result<List<EsSearch>>();
        Map map = (Map) JSONObject.parse(keyWord);
        String keyWord1 =  map.get("keyWord").toString().trim();

        List<EsSearch> esSearchList = esSearchService.getEsSearchList(keyWord1);
        if (esSearchList.size() <= 0){
            log.info("-----------------全文检索【搜索框】：暂无搜索数据-------------------");
        }
        result.setSuccess(true);
        result.setResult(esSearchList);
        return result;
    }


    /**
     * 分页列表查询
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "全文检索-分页列表查询")
    @ApiOperation(value="业务配置表[全文检索]-分页列表查询", notes="业务配置表[全文检索]-分页列表查询")
    @RequestMapping(value = "/list")
    public Result<IPage<Map<String, Object>>> queryOaBusDataList(@RequestBody String keyWord,
                                                    @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                    @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        Result<IPage<Map<String, Object>>> result = new Result<IPage<Map<String, Object>>>();
        IPage<Map<String, Object>> sourceList = null;
        try {
            if (keyWord.equals("")){   //输入查询的数据为空时，则为全查
                sourceList = searchService.queryAll(INDEX_NAME1,pageNo,pageSize);
            }else {
                sourceList = searchService.query(keyWord,pageNo,pageSize,INDEX_NAME1,INDEX_TYPE1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //存入频繁搜索数据
        if (sourceList != null){
            esSearchService.addSearchfrequency(keyWord);
        }
        result.setResult(sourceList);
        result.setSuccess(true);
        System.out.println("------------附件检索完毕-------------");
        return result;
    }


    @AutoLog(value = "全文检索-分页列表查询")
    @ApiOperation(value="业务配置表[全文检索]-分页列表查询", notes="业务配置表[全文检索]-分页列表查询")
    @RequestMapping(value = "/oafile")
    public Result<IPage<Map<String, Object>>> queryOaFileList(@RequestBody String keyWord,
                                                           @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                           @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        Result<IPage<Map<String, Object>>> result = new Result<IPage<Map<String, Object>>>();
//        Map map = (Map) JSONObject.parse(json);
//        String keyWord =  map.get("keyWord").toString().trim();
        IPage<Map<String, Object>> sourceList = null;
        System.out.println("---oafile------"+keyWord);
        try {
            if (keyWord.equals("")){  //输入查询的数据为空时，则为全查
                sourceList = searchService.queryAll(INDEX_NAME2,(pageNo-1)*pageSize,pageSize);
            }else {
//                map.put("pageNo", (pageNo-1)*pageSize);
//                map.put("pageSize", pageSize);
//                map.put("indexName", INDEX_NAME2);  //索引
//                map.put("indexType", INDEX_TYPE2);
                sourceList = searchService.query(keyWord,pageNo,pageSize,INDEX_NAME2,INDEX_TYPE2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.setResult(sourceList);
        result.setSuccess(true);
        System.out.println("------------全文检索完毕-------------");
        return result;
    }
}
