package com.cfcc.modules.system.mapper;

import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.entity.SysUserAgent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 用户代理人设置
 * @Author: jeecg-boot
 * @Date: 2019-04-17
 * @Version: V1.0
 */
public interface SysUserAgentMapper extends BaseMapper<SysUserAgent> {

    @Select("select * from sys_user_agent where agent_user_name=#{value} and start_time<=NOW() and end_time>=NOW()")
    List<SysUserAgent> agentIsMe(String userName);

    @Select("select u.* from sys_user_agent a LEFT JOIN sys_user u on a.agent_user_name=u.username where user_name=#{v} limit 1")
    SysUser myAgentUserMsg(String username);
}
