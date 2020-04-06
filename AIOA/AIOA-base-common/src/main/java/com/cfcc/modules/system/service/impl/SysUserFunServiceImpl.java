package com.cfcc.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.modules.system.entity.SysUserFun;
import com.cfcc.modules.system.mapper.SysUserFunMapper;
import com.cfcc.modules.system.service.ISysUserFunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
            SysUserFun sysUserFun=sysUserFunMapper.selectUserFun(userId,modelId,functionId);
            if (sysUserFun == null){
                num=sysUserFunMapper.addUserFun(userId,modelId,functionId,statuss);
            }else {
                num=1;
                sysUserFunMapper.updateByUserId(userId,functionId,modelId,statuss);
            }
        }else if (status==1){
            statuss=0;
            sysUserFunMapper.updateByUserId(userId,functionId,modelId,statuss);
        }
        return num;
    }

    @Override
    public List<Map<String,Object>> showUserFun(String userId) {
        List<Map<String,Object>> list=sysUserFunMapper.selecUserFuntList(userId);
        return list;
    }

    @Override
    public List<SysUserFun> showUserFunStatus(String userId) {
        List<SysUserFun> list=sysUserFunMapper.selectUserFunStatus(userId);
        return list;
    }

    @Override
    public List<SysUserFun> queryListByUserIdSer(String userId) {

        return sysUserFunMapper.queryListByUserIdDao(userId);
    }

    @Override
    public List<Map<String, Object>> queryListMapByUserIdSer(String userId) {
        return sysUserFunMapper.queryListMapByUserIdDao(userId);
    }

    @Override
    public List<SysUserFun> showUserFunMF(String userId) {
        return sysUserFunMapper.selectUserFunStatus(userId);
    }
}
