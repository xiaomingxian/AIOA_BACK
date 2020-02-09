package com.cfcc.modules.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.entity.SysUserOpinion;
import com.cfcc.modules.system.entity.SysUserSet;
import com.cfcc.modules.system.mapper.SysUserMapper;
import com.cfcc.modules.system.mapper.SysUserOpinionMapper;
import com.cfcc.modules.system.mapper.SysUserSetMapper;
import com.cfcc.modules.system.service.ISysUserSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 权限设置
 * @Author: jeecg-boot
 * @Date:   2019-10-17
 * @Version: V1.0
 */
@Service
public class SysUserSetServiceImpl extends ServiceImpl<SysUserSetMapper, SysUserSet> implements ISysUserSetService {
    @Autowired
    SysUserSetMapper sysUserSetMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public SysUserSet findById(Integer iId) {

        SysUserSet sysUserSet = sysUserSetMapper.findById(iId);

        return sysUserSet;
    }

    @Override
    public boolean updateBYIid(SysUserSet sysUserSet) {

        try {
            sysUserSetMapper.updateByIid(sysUserSet);
            return true;

        }catch (Exception e){
            return false;
        }

    }


    @Override
    public boolean deleteByIidd(String id) {
        try {
            sysUserSetMapper.deleteByIidd(id);
            return true;

        }catch (Exception e){
            return false;
        }
    }


    @Override
    public IPage<SysUserSet> findPage(Integer pageNo, Integer pageSize, SysUserSet sysUserSet) {
        int total = sysUserSetMapper.count(sysUserSet);
        List<SysUserSet> OpinionList =  sysUserSetMapper.findPage((pageNo-1)*pageSize,pageSize,sysUserSet);
        IPage<SysUserSet> pageList = new Page<SysUserSet>();
        pageList.setRecords(OpinionList);
        pageList.setTotal(total);
        pageList.setSize(pageSize) ;
        pageList.setCurrent(pageNo) ;
        return pageList;
    }

    @Override
    public List<SysUserSet> findList() {
        List<SysUserSet> list = sysUserSetMapper.findList();

        return list;
    }

    @Override
    public int  insert(SysUserSet sysUserSet) {
       int res= sysUserSetMapper.insertinto(sysUserSet);
       return  res;
    }

    @Override
    public SysUserSet findByUserId(String userId) {
        SysUserSet sysUserSet= sysUserSetMapper.findByUserId(userId);
        System.out.println(sysUserSet+"]]]]]]");
        if(sysUserSet == null){
            return null;
        }
        SysUser sysUser = sysUserMapper.selectById(sysUserSet.getSUserId());
        sysUserSet.setIp(sysUser.getAvatar());
        return sysUserSet;
    }

    @Override
    public List<SysUserSet> findByIId(Integer iId) {
        return sysUserSetMapper.findByIId(iId);
    }

    @Override
    public SysUserSet HomeAndDay(SysUserSet sysUserSet) {
        SysUserSet sysUserSet1 = sysUserSetMapper.HomeAndDay(sysUserSet);

        return  sysUserSet1;
    }

}
