package com.cfcc.modules.system.service.impl;

import com.cfcc.modules.system.mapper.IOaIpMapper;
import com.cfcc.modules.system.mapper.SysUserMapper;
import com.cfcc.modules.system.service.IOaIpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IOaIpServiceImpl implements IOaIpService {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private IOaIpMapper iOaIpMapper;
    @Override
    public String getIpByUserName(String username,String ip) {
        String userId = sysUserMapper.getUserIdByUserName(username);
        String cIp = iOaIpMapper.getIpByUserId(userId);
        if (cIp == null){
            iOaIpMapper.addOaIp(userId,ip);
        }else{
            iOaIpMapper.updateOaIp(userId,ip);
        }

        return cIp;
    }
}
