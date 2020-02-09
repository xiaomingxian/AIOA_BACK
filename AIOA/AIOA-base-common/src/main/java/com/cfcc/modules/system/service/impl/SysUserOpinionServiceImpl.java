package com.cfcc.modules.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.modules.system.entity.SysUserOpinion;
import com.cfcc.modules.system.mapper.SysUserOpinionMapper;
import com.cfcc.modules.system.service.ISysUserOpinionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 快捷意见
 * @Author: jeecg-boot
 * @Date:   2019-10-12
 * @Version: V1.0
 */
@Service
public class SysUserOpinionServiceImpl extends ServiceImpl<SysUserOpinionMapper, SysUserOpinion> implements ISysUserOpinionService {


    @Autowired
    SysUserOpinionMapper sysUserOpinionMapper;

    @Override
    public SysUserOpinion findById(Integer iId) {

            SysUserOpinion sysUserOpinion = sysUserOpinionMapper.findById(iId);

            return sysUserOpinion;
    }

    @Override
    public boolean updateBYIid(SysUserOpinion sysUserOpinion) {

        try {
            sysUserOpinionMapper.updateByIid(sysUserOpinion);
            return true;

        }catch (Exception e){
            return false;
        }

    }


    @Override
    public boolean deleteByIidd(String id) {
        try {
            sysUserOpinionMapper.deleteByIidd(id);
            return true;

        }catch (Exception e){
            return false;
        }
    }


    @Override
    public IPage<SysUserOpinion> findPage(Integer pageNo, Integer pageSize, SysUserOpinion sysUserOpinion) {
        int total = sysUserOpinionMapper.count(sysUserOpinion);
        List<SysUserOpinion> OpinionList =  sysUserOpinionMapper.findPage((pageNo-1)*pageSize,pageSize,sysUserOpinion);
        IPage<SysUserOpinion> pageList = new Page<SysUserOpinion>();
        pageList.setRecords(OpinionList);
        pageList.setTotal(total);
        pageList.setSize(pageSize) ;
        pageList.setCurrent(pageNo) ;
        return pageList;
    }

    @Override
    public List<SysUserOpinion> getUserOpinion(SysUserOpinion sysUserOpinion) {
        return sysUserOpinionMapper.queryUserOpinion(sysUserOpinion);
    }

}
