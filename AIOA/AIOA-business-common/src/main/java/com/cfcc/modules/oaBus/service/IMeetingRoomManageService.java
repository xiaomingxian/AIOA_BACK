package com.cfcc.modules.oaBus.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.modules.oaBus.entity.OaBusdata;
import org.springframework.stereotype.Service;

@Service
public interface IMeetingRoomManageService {

     IPage<OaBusdata> queryMeetingRoomList(OaBusdata meetingRoom, Integer pageNo, Integer pageSize);
}
