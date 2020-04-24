package com.cfcc.modules.oadeferlog.service.impl;

import com.cfcc.modules.oaBus.entity.oaCalendar;
import com.cfcc.modules.oaBus.mapper.OaBusDynamicTableMapper;
import com.cfcc.modules.oaBus.mapper.oaCalendarMapper;
import com.cfcc.modules.oadatafetailedinst.entity.OaDatadetailedInst;
import com.cfcc.modules.oadeferlog.entity.oaDeferLog;
import com.cfcc.modules.oadeferlog.mapper.oaDeferLogMapper;
import com.cfcc.modules.oadeferlog.service.IoaDeferLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 任务类事务延期记录
 * @Author: jeecg-boot
 * @Date:   2020-04-23
 * @Version: V1.0
 */
@Service
public class oaDeferLogServiceImpl extends ServiceImpl<oaDeferLogMapper, oaDeferLog> implements IoaDeferLogService {

    @Autowired
    private OaBusDynamicTableMapper dynamicTableMapper;

    @Autowired
    private oaDeferLogMapper oaDeferLogMapper;

    @Autowired
    private oaCalendarMapper oaCalendarMapper;

    @Override
    public boolean updataOadataAndaddOaDeferLog(Map<String, Object> map) {
        Map<String, Object> mapdata = new HashMap<>();
        mapdata.put("table",map.get("s_table"));
        mapdata.put("i_id",map.get("i_id"));
        mapdata.put("d_datetime2",map.get("d_datetime2"));
        int num=dynamicTableMapper.updateData(mapdata);
        boolean log=true;
        if (num!=0){
            map.put("table","oa_defer_log");
            map.put("i_table_id",map.get("i_id"));
            Date date=new Date();
            map.put("d_create_time",date);
            map.remove("i_id");
            int flag=dynamicTableMapper.insertData(map);
            if (flag == 0){
                log=false;
            }
        }else {
            log=false;
        }
        return log;
    }

    @Override
    public List<oaDeferLog> selecturgeLog(String sTable,Integer iTableId) {
        List<oaDeferLog> list=oaDeferLogMapper.selecturgeLog(sTable,iTableId);
        return list;
    }

    @Override
    public List<oaCalendar> selecturgeInform(Integer iTableId, String username) {
        List<oaCalendar> list=oaCalendarMapper.selecturgeInform(iTableId,username);
        return list;
    }
}
