package com.cfcc.modules.oadeferlog.service.impl;

import com.cfcc.modules.oadeferlog.entity.oaDeferLog;
import com.cfcc.modules.oadeferlog.mapper.oaDeferLogMapper;
import com.cfcc.modules.oadeferlog.service.IoaDeferLogService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 任务类事务延期记录
 * @Author: jeecg-boot
 * @Date:   2020-04-23
 * @Version: V1.0
 */
@Service
public class oaDeferLogServiceImpl extends ServiceImpl<oaDeferLogMapper, oaDeferLog> implements IoaDeferLogService {

}
