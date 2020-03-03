package com.cfcc.modules.oaBus.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.oaBus.entity.BusPageDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: 业务页面详情表（实际业务字段含义说明）
 * @Author: jeecg-boot
 * @Date:   2019-10-18
 * @Version: V1.0
 */
public interface IBusPageDetailService extends IService<BusPageDetail> {

//    查询含义表所有列-lvjian
    List<Map<String,String>>  getAllColumsList(Integer functionId,Integer iPageId);

    List<BusPageDetail>  getAllColumsListPageDtail(Integer functionId,Integer iPageId);

    IPage<BusPageDetail> getBusPageDetailpage(Integer pageNo, Integer pageSize, BusPageDetail busPageDetail);

    void saveBusPageDetail(BusPageDetail busPageDetail);

    String getColums(String table);

    List<BusPageDetail> queryById(int iBusFunctionId);
    List<BusPageDetail> queryConditionById(int iBusFunctionId);
    List<BusPageDetail> findList(String schema);

    List<Map<String,String>> getConColums(int functionId, Integer iPageId);
//根据表明查询有哪些列
    List<Map<String,String>> getColumsByTableName(String tableName,String functionId);


    List<BusPageDetail> getListByFunID(String functionId);

    int getBusPageDetailCount(String functionId, String pageId, String tableName);

    boolean updatePageDetail(BusPageDetail busPageDetail);

    /**
     * 根据functionId和VersionId查询出对应的iPageId,在根据IPageId和functionId查询出对应的pageDetetail
     * @param functionId
     * @param iPageId
     * @return
     */
    List<BusPageDetail> getListByFunIDAndIPageId(String functionId, String iPageId);
}


