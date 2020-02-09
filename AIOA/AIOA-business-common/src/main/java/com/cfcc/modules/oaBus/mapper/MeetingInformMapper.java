package com.cfcc.modules.oaBus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.oaBus.entity.OaBusdata;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MeetingInformMapper extends BaseMapper<OaBusdata> {

    int queryMeetingInformTotal(OaBusdata oaBusdata);

    List<OaBusdata> queryMeetingInform(@Param("mInform") OaBusdata oaBusdata, @Param("pageNo") int i, @Param("pageSize") Integer pageSize);

    int queryMeetingTotal(@Param("s_varchar1")String s_varchar1);

    List<OaBusdata> queryMeeting(@Param("s_varchar1") String s_varchar1,  @Param("pageNo") int i, @Param("pageSize") Integer pageSize);

    List<OaBusdata> queryMeetingTime(@Param("s_varchar1") String meetingRoom ,@Param("tableName") String tableName);
}
