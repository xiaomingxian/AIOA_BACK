package com.cfcc.modules.oaBus.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.OaFile;
import com.cfcc.modules.oaBus.entity.oaCalendar;
import com.cfcc.modules.system.entity.SysUser;

import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 日程管理表
 * @Author: jeecg-boot
 * @Date:   2019-11-21
 * @Version: V1.0
 */
public interface IoaCalendarService extends IService<oaCalendar> {

    oaCalendar findById(Integer iId);

    boolean updateByIid(oaCalendar oaCalendar);

    void deleteByIid(String id);

    int saveCalendar(oaCalendar oaCalendar);

    IPage<oaCalendar> findPage(Integer pageNo, Integer pageSize, oaCalendar oaCalendar);

    IPage<oaCalendar> findByLeader(Integer pageNo, Integer pageSize,oaCalendar oaCalendar);

    IPage<oaCalendar> queryPageList(Integer pageNo, Integer pageSize,oaCalendar oaCalendar);

    oaCalendar getByIid(String id);


    List<SysUser> getByShowOrder(int showOrder);

    IPage<oaCalendar> findWait(Integer pageNo, Integer pageSize, oaCalendar oaCalendar);

    String getUserId(String id);

    String getDepartId(String id);


    List<String> getDepartIdList(String userNameId);

    List<BusFunction> busFunctionList();

    void MostUserLink(HttpServletResponse response,String id,String resourceType) throws IOException;

    List<Map<String, Object>>  LinkList() ;


}
