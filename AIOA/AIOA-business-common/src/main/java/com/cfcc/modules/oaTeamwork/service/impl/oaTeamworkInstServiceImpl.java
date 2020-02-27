package com.cfcc.modules.oaTeamwork.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.oaBus.mapper.BusFunctionMapper;
import com.cfcc.modules.oaBus.mapper.BusModelMapper;
import com.cfcc.modules.oaTeamwork.entity.oaTeamwork;
import com.cfcc.modules.oaTeamwork.entity.oaTeamworkInst;
import com.cfcc.modules.oaTeamwork.mapper.oaTeamworkInstMapper;
import com.cfcc.modules.oaTeamwork.mapper.oaTeamworkMapper;
import com.cfcc.modules.oaTeamwork.service.IoaTeamworkInstService;
import com.cfcc.modules.system.entity.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 个人协同办公业务实例
 * @Author: jeecg-boot
 * @Date:   2020-01-02
 * @Version: V1.0
 */
@Service
public class oaTeamworkInstServiceImpl extends ServiceImpl<oaTeamworkInstMapper, oaTeamworkInst> implements IoaTeamworkInstService {

    @Autowired
    private oaTeamworkInstMapper oaTeamworkInstMapper;
    @Autowired
    private oaTeamworkMapper  oaTeamworkMapper;
    @Autowired
    private BusModelMapper busModelMapper;
    @Autowired
    private BusFunctionMapper busFunctionMapper;

    @Override
    public boolean deleteById(String id) {
        return oaTeamworkInstMapper.deleteByid(id);
    }

    @Override
    public oaTeamworkInst findById(Integer iId) {
        return oaTeamworkInstMapper.findById(iId);
    }

    @Override
    public IPage<oaTeamworkInst> findPage(Integer pageNo, Integer pageSize, oaTeamworkInst oaTeamworkInst) {
        int total = oaTeamworkInstMapper.count(oaTeamworkInst);
        String teamworkName = oaTeamworkInst.getTeamworkName();
       /*// Integer teamworkIId= null;
        if(teamworkName != null){
            oaTeamwork oaTeamwork=oaTeamworkMapper.selectByName(teamworkName);
            if(oaTeamwork != null){
               teamworkIId = oaTeamwork.getIId();
            }
        }*/
        //oaTeamworkInst.setITeamworkId(oaTeamworkInst.);
        List<oaTeamworkInst> oaTeamworkInstList =  oaTeamworkInstMapper.findPage((pageNo-1)*pageSize,pageSize,oaTeamworkInst);
        for(int i=0;i<oaTeamworkInstList.size();i++){
            oaTeamwork oaTeamwork = oaTeamworkMapper.findById(oaTeamworkInstList.get(i).getITeamworkId());
            Integer sumorder = oaTeamworkInstMapper.SumOrder((oaTeamworkInstList.get(i).getITeamworkId()));
            Integer lastOrder = oaTeamworkInstMapper.LastOrder(oaTeamworkInstList.get(i).getITeamworkId());
            String modelName  = oaTeamworkMapper.getfirstModel(oaTeamworkInstList.get(i).getITeamworkId());

            oaTeamworkInstList.get(i).setBusModelName(modelName);
            oaTeamworkInstList.get(i).setOrders(sumorder.toString());//总步骤
            oaTeamworkInstList.get(i).setLastOrder(lastOrder.toString());//当前步骤
            if(oaTeamwork != null){
                oaTeamworkInstList.get(i).setITeamworkName(oaTeamwork.getSTeamworkName());
                oaTeamworkInstList.get(i).setDCreateTime(oaTeamwork.getDCreateTime());
            }
                BusModel model = busModelMapper.selectById(oaTeamworkInstList.get(i).getIBusModelId());
                if(model != null){
                    oaTeamworkInstList.get(i).setBusModelName(model.getSName());
                }
                BusFunction function = busFunctionMapper.selectById(oaTeamworkInstList.get(i).getIBusFunctionId());
                if(function != null){
                    oaTeamworkInstList.get(i).setBusFunctionName(function.getSName());
                }
        }
        IPage<oaTeamworkInst> pageList = new Page<oaTeamworkInst>();
        pageList.setRecords(oaTeamworkInstList);
        pageList.setTotal(total);
        pageList.setSize(pageSize) ;
        pageList.setCurrent(pageNo) ;
        return pageList;
    }

    @Override
    public boolean updateByIid(oaTeamworkInst oaTeamworkInst) {
        return  oaTeamworkInstMapper.updateByIid(oaTeamworkInst);
    }

    @Override
    public int Insert(oaTeamworkInst oaTeamworkInst) {
        return oaTeamworkInstMapper.Insert(oaTeamworkInst);
    }

    @Override
    public Integer findMax(Integer iteamworkId) {
        return oaTeamworkInstMapper.findMax(iteamworkId);
    }

    @Override
    public List<oaTeamworkInst> findList(oaTeamworkInst oaTeamworkInst) {
        List<oaTeamworkInst> oaTeamworkInstList = oaTeamworkInstMapper.findList(oaTeamworkInst);
        for(int i=0;i<oaTeamworkInstList.size();i++){
            oaTeamwork oaTeamwork = oaTeamworkMapper.findById(oaTeamworkInstList.get(i).getITeamworkId());
            Integer sumorder = oaTeamworkInstMapper.SumOrder((oaTeamworkInstList.get(i).getITeamworkId()));
            Integer lastOrder = oaTeamworkInstMapper.LastOrder(oaTeamworkInstList.get(i).getITeamworkId());
            oaTeamworkInstList.get(i).setOrders(sumorder.toString());//总步骤
            oaTeamworkInstList.get(i).setLastOrder(lastOrder.toString());//当前步骤
            if(oaTeamwork != null){
                oaTeamworkInstList.get(i).setITeamworkName(oaTeamwork.getSTeamworkName());
                oaTeamworkInstList.get(i).setDCreateTime(oaTeamwork.getDCreateTime());
            }
            BusModel model = busModelMapper.selectById(oaTeamworkInstList.get(i).getIBusModelId());
            if(model != null){
                oaTeamworkInstList.get(i).setBusModelName(model.getSName());
            }
            BusFunction function = busFunctionMapper.selectById(oaTeamworkInstList.get(i).getIBusFunctionId());
            if(function != null){
                oaTeamworkInstList.get(i).setBusFunctionName(function.getSName());
            }
            Integer busDataId = oaTeamworkInstMapper.getDataId(oaTeamworkInstList.get(i).getIBusFunctionId(),oaTeamworkInstList.get(i).getIVersion());
            oaTeamworkInstList.get(i).setBusDataId(busDataId);
        }
    return oaTeamworkInstList;
  }

    @Override
    public List<Integer> findOrder(Integer iteamworkId) {
        return oaTeamworkInstMapper.findOrder(iteamworkId);
    }
}
