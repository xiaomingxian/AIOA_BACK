package com.cfcc.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.modules.system.entity.SysUserDepart;
import com.cfcc.modules.system.entity.SysUserFun;
import com.cfcc.modules.system.mapper.SysUserFunMapper;
import com.cfcc.modules.system.service.ISysUserFunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName:SysUserFunServiceImpl
 * Package:com.cfcc.modules.system.service.impl
 * Description:<br/>
 *
 * @date:2020/3/31
 * @author:
 */
@Slf4j
@Service
public class SysUserFunServiceImpl extends ServiceImpl<SysUserFunMapper, SysUserFun> implements ISysUserFunService{
    @Autowired
    private SysUserFunMapper sysUserFunMapper;

    @Override
    public int addUserFun(String userId, Integer modelId, Integer functionId, Integer status) {
        int statuss=0;
        int num =0;
        if (status==0){
            statuss=1;
            sysUserFunMapper.addUserFun(userId,modelId,functionId,statuss);
        }else if (status==1){
            statuss=0;
            sysUserFunMapper.updateByUserId(userId,functionId,statuss);
        }
        return num;
    }

    @Override
    public List<String> showUserFun(String userId) {
        List<String> list=sysUserFunMapper.selecUserFuntList(userId);
        return list;
    }

    @Override
    public List<SysUserFun> showUserFunStatus(String userId) {
        List<SysUserFun> list=sysUserFunMapper.selectUserFunStatus(userId);
        return list;
    }
}
