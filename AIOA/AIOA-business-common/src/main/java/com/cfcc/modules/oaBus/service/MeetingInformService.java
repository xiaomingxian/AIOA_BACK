package com.cfcc.modules.oaBus.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.oaBus.entity.OaBusdata;
import com.cfcc.modules.system.entity.SysUser;

import java.util.List;
import java.util.Map;

public interface MeetingInformService extends IService<OaBusdata> {
    IPage<OaBusdata> queryMeetingInformList(OaBusdata oaBusdata, Integer pageNo, Integer pageSize);

    IPage<OaBusdata> queryMeetingList(String s_varchar1, Integer pageNo, Integer pageSize);

    List<OaBusdata> queryMeetingTime(String meetingRoom,String tableName);


    String insertUserMeetingInform(Map<String, Object> map, SysUser currentUser);
}
