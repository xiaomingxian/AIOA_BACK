package com.cfcc.modules.message.service.impl;

import com.cfcc.common.system.base.service.impl.JeecgServiceImpl;
import com.cfcc.modules.message.entity.SysMessage;
import com.cfcc.modules.message.mapper.SysMessageMapper;
import com.cfcc.modules.message.service.ISysMessageService;
import org.springframework.stereotype.Service;

/**
 * @Description: 消息
 * @Author: jeecg-boot
 * @Date:  2019-04-09
 * @Version: V1.0
 */
@Service
public class SysMessageServiceImpl extends JeecgServiceImpl<SysMessageMapper, SysMessage> implements ISysMessageService {

}
