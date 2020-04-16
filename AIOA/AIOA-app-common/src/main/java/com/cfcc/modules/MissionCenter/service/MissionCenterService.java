package com.cfcc.modules.MissionCenter.service;

import com.cfcc.modules.oaBus.entity.oaCalendar;
import com.cfcc.modules.system.entity.LoginInfo;

import java.util.List;
import java.util.Map;

public interface MissionCenterService {

    Map<String, Object>queryListMap(oaCalendar oaCalendar, Integer pageNo, Integer pageSize);

}
