package com.cfcc.modules.oaTeamwork.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.util.DateUtils;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.oaBus.mapper.BusFunctionMapper;
import com.cfcc.modules.oaBus.mapper.BusModelMapper;
import com.cfcc.modules.oaTeamwork.entity.oaTeamwork;
import com.cfcc.modules.oaTeamwork.entity.oaTeamworkSet;
import com.cfcc.modules.oaTeamwork.mapper.oaTeamworkMapper;
import com.cfcc.modules.oaTeamwork.mapper.oaTeamworkSetMapper;
import com.cfcc.modules.oaTeamwork.service.IoaTeamworkService;
import com.cfcc.modules.system.entity.SysUserOpinion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 个人协同办公业务配置分类
 * @Author: jeecg-boot
 * @Date:   2019-12-30
 * @Version: V1.0
 */
@Service
public class oaTeamworkServiceImpl extends ServiceImpl<oaTeamworkMapper, oaTeamwork> implements IoaTeamworkService {

    @Autowired
    private oaTeamworkMapper oaTeamworkMapper;

    @Autowired
    private oaTeamworkSetMapper oaTeamworkSetMapper;

    @Autowired
    private BusFunctionMapper busFunctionMapper;

    @Override
    public boolean deleteById(String id) {
      return  oaTeamworkMapper.deleteByIid(id);
    }

    @Override
    public oaTeamwork findById(Integer iId) {
        return oaTeamworkMapper.findById(iId);
    }

    @Override
    public IPage<oaTeamwork> findPage(Integer pageNo, Integer pageSize, oaTeamwork oaTeamwork) {
        int total = oaTeamworkMapper.count(oaTeamwork);
        List<oaTeamwork> OpinionList =  oaTeamworkMapper.findPage((pageNo-1)*pageSize,pageSize,oaTeamwork);
       /* String str = "";
        for(int i=0;i<OpinionList.size();i++){
            Integer iId = OpinionList.get(i).getIId();
            List<oaTeamworkSet> oaTeamworkSetList = oaTeamworkSetMapper.findTeamworkSet(iId);
            for(int j=0;j<oaTeamworkSetList.size();j++){
                Integer iBusFunctionId = oaTeamworkSetList.get(i).getIBusFunctionId();
                BusFunction function = busFunctionMapper.selectById(iBusFunctionId);
                if(function != null){
                    String sName = function.getSName();
                    str = sName+" " +str;
                }
                OpinionList.get(i).setTeamworkName(str);
            }

        }*/
        IPage<oaTeamwork> pageList = new Page<oaTeamwork>();
        pageList.setRecords(OpinionList);
        pageList.setTotal(total);
        pageList.setSize(pageSize) ;
        pageList.setCurrent(pageNo) ;
        return pageList;
    }
    @Override
    public boolean updateByIid(oaTeamwork oaTeamwork) {
        try {
            oaTeamwork.setDUpdateTime(DateUtils.getDate());
            oaTeamworkMapper.updateByIid(oaTeamwork);
            return true;

        }catch (Exception e){
            return false;
        }
    }

    @Override
    public int Insert(oaTeamwork oaTeamwork) {
        oaTeamwork.setDCreateTime(DateUtils.getDate());
       return  oaTeamworkMapper.Insert(oaTeamwork);
    }

    @Override
    public List<oaTeamwork> findTeamworkName(String id) {
        return oaTeamworkMapper.findTeamworkName(id);
    }




}
