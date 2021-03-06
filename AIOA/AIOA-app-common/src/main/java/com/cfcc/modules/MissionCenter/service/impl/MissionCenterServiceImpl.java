package com.cfcc.modules.MissionCenter.service.impl;

import com.cfcc.modules.MissionCenter.service.MissionCenterService;
import com.cfcc.modules.oaBus.entity.oaCalendar;
import com.cfcc.modules.oaBus.mapper.oaCalendarMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MissionCenterServiceImpl implements MissionCenterService {

    @Autowired
    private oaCalendarMapper oaCalendarMapper;

    @Override
    public Map<String, Object> queryListMap(oaCalendar oaCalendar, Integer pageNo, Integer pageSize) {
        Map<String, Object> res = new HashMap<>() ;
        List<Map<String, Object>> list = oaCalendarMapper.findList(oaCalendar, (pageNo-1)* pageSize, pageSize);
        int total =  oaCalendarMapper.appCount(oaCalendar);
        res.put("total",total);
        res.put("list",list);
        return res;
    }
}
