package com.cfcc.modules.oaBus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.oaBus.entity.OaBusdata;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface MeetingRoomManageMapper extends BaseMapper<OaBusdata> {

    List<OaBusdata> selectMeetingRoomList(@Param("meet") OaBusdata meetingRoom, @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);

    int selectMeetingRoomTotal(OaBusdata meetingRoom);
}
