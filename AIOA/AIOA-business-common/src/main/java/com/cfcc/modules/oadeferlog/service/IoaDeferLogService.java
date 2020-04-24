package com.cfcc.modules.oadeferlog.service;

import com.cfcc.modules.oaBus.entity.oaCalendar;
import com.cfcc.modules.oadatafetailedinst.entity.OaDatadetailedInst;
import com.cfcc.modules.oadeferlog.entity.oaDeferLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: 任务类事务延期记录
 * @Author: jeecg-boot
 * @Date:   2020-04-23
 * @Version: V1.0
 */
public interface IoaDeferLogService extends IService<oaDeferLog> {

    boolean updataOadataAndaddOaDeferLog(Map<String, Object> map);

    List<oaDeferLog> selecturgeLog(String sTable,Integer iTableId);

    List<oaCalendar> selecturgeInform(Integer iTableId, String username);
}
