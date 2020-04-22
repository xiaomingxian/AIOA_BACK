package com.cfcc.modules.oaBus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.common.constant.CacheConstant;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.oaBus.mapper.BusModelMapper;
import com.cfcc.modules.oaBus.service.IBusModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 业务模块表（业务分类表）
 * @Author: jeecg-boot
 * @Date: 2019-10-12
 * @Version: V1.0
 */
@Service
public class BusModelServiceImpl extends ServiceImpl<BusModelMapper, BusModel> implements IBusModelService {

    @Autowired
    BusModelMapper busModelMapper;

    @Override
    public BusModel getBusModelById(Integer iId) {

        BusModel busModel = busModelMapper.selectById(iId);

        return busModel;
    }

    @Override
    @CacheEvict(value = CacheConstant.MODEL_CACHE,allEntries=true)
    public boolean updateBusModelById(BusModel busModel, String schema) {
        boolean res = false;
        int updateRes = busModelMapper.updateBusModelByIdDao(busModel);
        if (updateRes > 0) {
            res = true;
        } else {
            res = false;
        }
        return res;
    }


    /**
     * 根据id删除一条数据
     *
     * @param id
     * @return
     */
    @Override
    @CacheEvict(value = CacheConstant.MODEL_CACHE,allEntries=true)
    public boolean removeBusModelById(String id, String schema) {

        boolean res = false;
        int delRes = busModelMapper.removeBusModelByIdDao(id);
        if (delRes > 0) {
            res = true;
        } else {
            res = false;
        }
        return res;
    }

    @Override
    public IPage<BusModel> getPage(Integer pageNo, Integer pageSize, BusModel busModel) {
        int total = busModelMapper.queryBusModelCount(busModel);
        List<BusModel> modelList = busModelMapper.queryBusModel((pageNo - 1) * pageSize, pageSize, busModel);
        IPage<BusModel> pageList = new Page<BusModel>();
        pageList.setRecords(modelList);
        pageList.setTotal(total);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;
    }

    @Override
    public List<BusModel> getModelList() {

        return busModelMapper.getModelListDao();
    }

    @Override
    @Cacheable(value = CacheConstant.MODEL_CACHE)
    public List<BusModel> findList( String schema ) {
        List<BusModel> busModelList = busModelMapper.findList();
        return busModelList;
    }

    @Override
    public List<BusModel> allPeopleSee() {

        List<BusModel> list = null;
        List<BusModel> busModels = busModelMapper.allPeopleSee();
        if (busModels == null) {
            list = busModelMapper.findList();
        } else {
            list = busModelMapper.findListNotInPermit();
        }
        if (list != null && list.size() > 0) busModels.addAll(list);

        return busModels;
    }

    @Override
    public List<BusModel> currentRoleSee(String type, Object rId) {
        return busModelMapper.queryByUserMsg(type, rId);
    }

    @Override
    public List<BusModel> currentDeptSee(String type, Object dId) {
        return busModelMapper.queryByUserMsg(type, dId);
    }

    @Override
    public List<BusModel> currentUserSee(String type, Object uId) {
        return busModelMapper.queryByUserMsg(type, uId);
    }

    @Override
    public List<BusModel> selectDocType() {
        return busModelMapper.selectDocType();
    }

    @Override
    public BusModel getModelIdByUrlSer(String str) {


        BusModel busModel = new BusModel();
        busModel.setSEnName(str);
        QueryWrapper<BusModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(busModel);
        busModel = busModelMapper.selectOne(queryWrapper);

        /*String res =  "";
        if(busModel.getIId() != null){
            res = busModel.getIId() + "";
        }*/
        return busModel;
    }


}
