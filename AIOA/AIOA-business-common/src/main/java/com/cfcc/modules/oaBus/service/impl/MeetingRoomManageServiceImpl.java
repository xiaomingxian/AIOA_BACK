package com.cfcc.modules.oaBus.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.modules.oaBus.entity.OaBusdata;
import com.cfcc.modules.oaBus.mapper.MeetingRoomManageMapper;
import com.cfcc.modules.oaBus.service.IMeetingRoomManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MeetingRoomManageServiceImpl extends ServiceImpl<MeetingRoomManageMapper, OaBusdata> implements IMeetingRoomManageService {

    @Autowired
    private MeetingRoomManageMapper meetingRoomManageMapper;

    @Override
    public IPage<OaBusdata> queryMeetingRoomList(OaBusdata meetingRoom, Integer pageNo, Integer pageSize) {
        int total = meetingRoomManageMapper.selectMeetingRoomTotal(meetingRoom);
        List<OaBusdata> busdataList = meetingRoomManageMapper.selectMeetingRoomList(meetingRoom, (pageNo - 1) * pageSize, pageSize);
        IPage<OaBusdata> pageList = new Page<OaBusdata>();
        pageList.setRecords(busdataList);
        pageList.setTotal(total);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;
    }
}
