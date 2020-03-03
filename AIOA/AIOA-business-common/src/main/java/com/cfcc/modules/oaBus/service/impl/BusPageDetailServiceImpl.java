package com.cfcc.modules.oaBus.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.common.constant.CacheConstant;
import com.cfcc.modules.oaBus.entity.BusPageDetail;
import com.cfcc.modules.oaBus.mapper.BusPageDetailMapper;
import com.cfcc.modules.oaBus.service.IBusPageDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 业务页面详情表（实际业务字段含义说明）
 * @Author: jeecg-boot
 * @Date: 2019-10-18
 * @Version: V1.0
 */
@Service
public class BusPageDetailServiceImpl extends ServiceImpl<BusPageDetailMapper, BusPageDetail> implements IBusPageDetailService {

    @Autowired
    BusPageDetailMapper busPageDetailMapper;

    @Override
    public List<Map<String,String>> getAllColumsList(Integer functionId, Integer iPageId) {
        return busPageDetailMapper.getAllColumsList(functionId,iPageId);
    }

    @Override
    public List<BusPageDetail> getAllColumsListPageDtail(Integer functionId, Integer iPageId) {
        return busPageDetailMapper.getAllColumsListPageDtail(functionId,iPageId);
    }

    @Override
    public IPage<BusPageDetail> getBusPageDetailpage(Integer pageNo, Integer pageSize, BusPageDetail busPageDetail) {
        int total = busPageDetailMapper.queryBusPageDetailCount(busPageDetail);
        List<BusPageDetail> modelList = busPageDetailMapper.queryBusPageDetail((pageNo - 1) * pageSize, pageSize, busPageDetail);
        IPage<BusPageDetail> pageList = new Page<BusPageDetail>();
        pageList.setRecords(modelList);
        pageList.setTotal(total);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;
    }

    @Override
    public void saveBusPageDetail(BusPageDetail busPageDetail) {
        int res = busPageDetailMapper.insertBusPageDetail(busPageDetail);
    }

    @Override
    public String getColums(String table) {
        return busPageDetailMapper.getColums(table);
    }

    @Override
    public List<BusPageDetail> queryById(int iBusFunctionId) {

        return busPageDetailMapper.queryByIdDao(iBusFunctionId);
    }

    /**
     * 根据function_id查询相关的列条件
     * @param iBusFunctionId
     * @return
     */
    @Override
    public List<BusPageDetail> queryConditionById(int iBusFunctionId) {
        return busPageDetailMapper.queryConditionByIdDao(iBusFunctionId);
    }

    @Override
    public List<Map<String, String>> getColumsByTableName(String tableName,String functionId) {
        return busPageDetailMapper.getColumsByTableName(tableName,functionId);
    }

    @Override
    @Cacheable(value = CacheConstant.PAGEdETAIL_CACHE)
    public List<BusPageDetail> findList(String schema) {
        return busPageDetailMapper.findList();
    }

    /**
     * 根据functioId查询出要显示的列
     * @param functionId
     * @param iPageId
     * @return
     */
    @Override
    public List<Map<String,String>> getConColums(int functionId, Integer iPageId) {
        return busPageDetailMapper.getConColumsDao(functionId,iPageId);
    }

    @Override
    public List<BusPageDetail> getListByFunID(String functionId) {
        return busPageDetailMapper.getListByFunIDDao(functionId);
    }

    @Override
    public int getBusPageDetailCount(String functionId, String pageId, String tableName) {
        return busPageDetailMapper.getBusPageDetailCount(functionId,pageId,tableName);
    }

    @Override
    public boolean updatePageDetail(BusPageDetail busPageDetail) {
        return busPageDetailMapper.updatePageDetail(busPageDetail) ;
    }

    @Override
    public List<BusPageDetail> getListByFunIDAndIPageId(String functionId, String iPageId) {

        return busPageDetailMapper.getListByFunIDAndIPageIdDao(functionId,iPageId);
    }

    public void test() {
        List<String> funList = new LinkedList<String>();//缓存
        List depts = new LinkedList();




    }



}
