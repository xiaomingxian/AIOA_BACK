package com.cfcc.modules.oaTeamwork.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.util.DateUtils;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.oaBus.mapper.BusFunctionMapper;
import com.cfcc.modules.oaBus.mapper.BusFunctionPermitMapper;
import com.cfcc.modules.oaBus.mapper.BusModelMapper;
import com.cfcc.modules.oaTeamwork.entity.oaTeamwork;
import com.cfcc.modules.oaTeamwork.entity.oaTeamworkInst;
import com.cfcc.modules.oaTeamwork.entity.oaTeamworkSet;
import com.cfcc.modules.oaTeamwork.mapper.oaTeamworkInstMapper;
import com.cfcc.modules.oaTeamwork.mapper.oaTeamworkMapper;
import com.cfcc.modules.oaTeamwork.mapper.oaTeamworkSetMapper;
import com.cfcc.modules.oaTeamwork.service.IoaTeamworkSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 个人协同办公业务配置明细
 * @Author: jeecg-boot
 * @Date:   2019-12-30
 * @Version: V1.0
 */
@Service
public class oaTeamworkSetServiceImpl extends ServiceImpl<oaTeamworkSetMapper, oaTeamworkSet> implements IoaTeamworkSetService {

    @Autowired
    private oaTeamworkSetMapper oaTeamworkSetMapper;

    @Autowired
    private BusModelMapper busModelMapper;

    @Autowired
    private BusFunctionMapper busFunctionMapper;

    @Autowired
    private oaTeamworkMapper oaTeamworkMapper;

    @Autowired
    private oaTeamworkInstMapper oaTeamworkInstMapper;

    @Autowired
    private BusFunctionPermitMapper busFunctionPermitMapper;

    @Override
    public List<Integer> findorder() {
        return oaTeamworkSetMapper.findorder();
    }

    @Override
    public List<oaTeamworkSet> findListByteamworkId(String TeamworkName) {
        return oaTeamworkSetMapper.findListByteamworkId(TeamworkName);
    }

    @Override
    public IPage<oaTeamworkSet> queryList(Integer pageNo, Integer pageSize, oaTeamworkSet oaTeamworkSet) {
        int total = oaTeamworkSetMapper.count(oaTeamworkSet);
        List<oaTeamworkSet> OpinionList =  oaTeamworkSetMapper.queryList((pageNo-1)*pageSize,pageSize,oaTeamworkSet);
        for(int i=0;i<OpinionList.size();i++){
            System.out.println(OpinionList.get(i).getITeamworkId());
            Integer iTeamworkId = OpinionList.get(i).getITeamworkId();
            oaTeamwork  oaTeamwork = oaTeamworkMapper.findById(iTeamworkId);
            if(oaTeamwork != null){
                OpinionList.get(i).setITeamworkName(oaTeamwork.getSTeamworkName());
            }
            List<String> oaTeamworkSetList = oaTeamworkSetMapper.findByTeamworkId(iTeamworkId);
            List<String> oaTeamworkSetfunctionList = oaTeamworkSetMapper.findByFunctionId(iTeamworkId);
            List<String> oaTeamworkSetModelList = oaTeamworkSetMapper.findByModelId(iTeamworkId);
            Integer max = oaTeamworkSetMapper.findMax(iTeamworkId);
            String tabelName = busFunctionPermitMapper.findTabelName(OpinionList.get(i).getIBusModelId());
            String TeamworkName = String.join(",",oaTeamworkSetList);
            String TeamworkFunctionId = String.join(",",oaTeamworkSetfunctionList);
            String TeamworkModelId = String.join(",",oaTeamworkSetModelList);
            OpinionList.get(i).setTableName(tabelName);

            OpinionList.get(i).setOrderName(TeamworkName);
            OpinionList.get(i).setOrderFunctionId(TeamworkFunctionId);
            OpinionList.get(i).setOrderModelId(TeamworkModelId);
            OpinionList.get(i).setMaxOrder(max);
            OpinionList.get(i).setTableName(tabelName);

        }
        IPage<oaTeamworkSet> pageList = new Page<oaTeamworkSet>();
        pageList.setRecords(OpinionList);
        pageList.setTotal(total);
        pageList.setSize(pageSize) ;
        pageList.setCurrent(pageNo) ;
        return pageList;
    }

    @Override
    public oaTeamworkSet select(oaTeamworkSet oaTeamworkSet) {
        return oaTeamworkSetMapper.select(oaTeamworkSet);
    }

    @Override
    public Integer findMaxId() {
        return oaTeamworkSetMapper.findMaxId();
    }

    @Override
    public boolean deleteById(String id) {
        return oaTeamworkSetMapper.deleteByIid(id);
    }



    @Override
    public IPage<oaTeamworkSet> findPage(Integer pageNo, Integer pageSize, oaTeamworkSet oaTeamworkSet) {
        int total = oaTeamworkSetMapper.count(oaTeamworkSet);
        List<oaTeamworkSet> OpinionList =  oaTeamworkSetMapper.findPage((pageNo-1)*pageSize,pageSize,oaTeamworkSet);

        for(int i=0;i<OpinionList.size();i++){

            Integer max = oaTeamworkInstMapper.findMax(OpinionList.get(i).getITeamworkId());
            if(max != null){
                if(OpinionList.get(i).getIOrder()<max){
                    String busdata =  oaTeamworkMapper.getBusData2(OpinionList.get(i).getITeamworkId(),OpinionList.get(i).getIOrder());
                    String  Stitle= oaTeamworkMapper.getTitle1(busdata,OpinionList.get(i).getIOrder(),OpinionList.get(i).getIId(),OpinionList.get(i).getITeamworkId());
                    OpinionList.get(i).setSTitle(Stitle);
                }else{
                    OpinionList.get(i).setSTitle("");
                }
            }

            BusModel model = busModelMapper.selectById(OpinionList.get(i).getIBusModelId());
            if(model != null){
                OpinionList.get(i).setBusModelName(model.getSName());
            }
            BusFunction function = busFunctionMapper.selectById(OpinionList.get(i).getIBusFunctionId());
            if(function != null){
                OpinionList.get(i).setBusFunctionName(function.getSName());
            }
            String tabelName = busFunctionPermitMapper.findTabelName(OpinionList.get(i).getIBusModelId());
            OpinionList.get(i).setTableName(tabelName);
        }
        IPage<oaTeamworkSet> pageList = new Page<oaTeamworkSet>();
        pageList.setRecords(OpinionList);
        pageList.setTotal(total);
        pageList.setSize(pageSize) ;
        pageList.setCurrent(pageNo) ;
        return pageList;
    }

    @Override
    public boolean updateByIid(oaTeamworkSet oaTeamworkSet) {
        try {
            oaTeamworkSet.setDUpdateTime(DateUtils.getDate());
            oaTeamworkSetMapper.updateByIid(oaTeamworkSet);
            return true;

        }catch (Exception e){
            return false;
        }
    }

    @Override
    public void Insert(oaTeamworkSet oaTeamworkSet) {
        oaTeamworkSetMapper.Insert(oaTeamworkSet);
    }

    @Override
    public oaTeamworkSet findByid(Integer iId) {
            return oaTeamworkSetMapper.findById(iId);
    }

    @Override
    public int insert(oaTeamworkSet oaTeamworkSet) {
        oaTeamworkSet.setDCreateTime(DateUtils.getDate());
        return oaTeamworkSetMapper.Insert(oaTeamworkSet);
    }

    @Override
    public Integer findMax(Integer iTeamworkId) {
        Integer max = oaTeamworkSetMapper.findMax(iTeamworkId);
        if(max == null){
            return 0;
        }
        return max;
    }

    @Override
    public List<oaTeamworkSet> findTeamworkSet(Integer iTeamworkId) {
        return oaTeamworkSetMapper.findTeamworkSet(iTeamworkId);
    }
}
