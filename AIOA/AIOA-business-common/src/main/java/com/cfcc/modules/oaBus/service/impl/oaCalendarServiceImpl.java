package com.cfcc.modules.oaBus.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.common.util.DateUtils;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.entity.OaBusdata;
import com.cfcc.modules.oaBus.entity.OaFile;
import com.cfcc.modules.oaBus.entity.oaCalendar;
import com.cfcc.modules.oaBus.mapper.oaCalendarMapper;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.cfcc.modules.oaBus.service.IoaCalendarService;
import com.cfcc.modules.shiro.vo.DefContants;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.mapper.SysUserMapper;
import com.cfcc.modules.system.model.TreeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * @Description: 日程管理表
 * @Author: jeecg-boot
 * @Date:   2019-11-21
 * @Version: V1.0
 */
@Service
public class oaCalendarServiceImpl extends ServiceImpl<oaCalendarMapper, oaCalendar> implements IoaCalendarService {

    @Autowired
    private oaCalendarMapper oaCalendarMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private IOaBusdataService oaBusdataService;;

    @Override
    public oaCalendar findById(Integer iId) {
        oaCalendar oaCalendar = oaCalendarMapper.findById(iId);
        String[] UserIdList = oaCalendar.getSUserNames().split(",");
        Set<Integer> set = new HashSet<>() ;
        for (int j = 0; j <UserIdList.length; j++) {
            SysUser sysUser =  sysUserMapper.getUserByName(UserIdList[j]);
            if(! (sysUser == null)){
                String Id = sysUser.getId(); //取id
                set.add(Integer.valueOf(Id));
            }
        }
        oaCalendar.setSUserNameid(set);
        return oaCalendarMapper.findById(iId);
    }

    @Override
    public boolean updateByIid(oaCalendar oaCalendar) {
        try {
            oaCalendarMapper.updateByIid(oaCalendar);
            return true;

        }catch (Exception e){
            return false;
        }
    }

    @Override
    public void deleteByIid(String id) {
        oaCalendarMapper.deleteByIid(id);
    }

    @Override
    public int saveCalendar(oaCalendar calendar) {
        calendar.setDCreateTime(DateUtils.getDate());
        return oaCalendarMapper.saveCalendar(calendar);
    }

    @Override
    public IPage<oaCalendar> findPage(Integer pageNo, Integer pageSize, oaCalendar oaCalendar) {
        int total = oaCalendarMapper.count(oaCalendar);
        List<oaCalendar> CalendarList = oaCalendarMapper.findPage((pageNo - 1) * pageSize,pageSize,oaCalendar);
        IPage<oaCalendar> pageList = new Page<oaCalendar>();
        pageList.setRecords(CalendarList);
        pageList.setSize(pageSize);
        pageList.setTotal(total);
        pageList.setCurrent(pageNo);
        return pageList;


    }

    @Override
    public IPage<oaCalendar> findByLeader(Integer pageNo, Integer pageSize, oaCalendar oaCalendar) {
        int total = oaCalendarMapper.countIsLeader(oaCalendar);
        List<oaCalendar> CalendarList=oaCalendarMapper.findByLeader((pageNo - 1) * pageSize,pageSize,oaCalendar);
        IPage<oaCalendar> pageList = new Page<oaCalendar>();
        pageList.setRecords(CalendarList);
        pageList.setTotal(total);
        pageList.setSize(pageSize) ;
        pageList.setCurrent(pageNo) ;
        return pageList;
    }

    @Override
    public IPage<oaCalendar> queryPageList(Integer pageNo, Integer pageSize,oaCalendar oaCalendar) {
        int total = oaCalendarMapper.countIsNoLeader(oaCalendar);
        List<oaCalendar> CalendarList=oaCalendarMapper.queryPageList((pageNo - 1) * pageSize,pageSize,oaCalendar);
        IPage<oaCalendar> pageList = new Page<oaCalendar>();
        pageList.setRecords(CalendarList);
        pageList.setTotal(total);
        pageList.setSize(pageSize) ;
        pageList.setCurrent(pageNo) ;
        return pageList;
    }

    @Override
    public oaCalendar getByIid(String id) {
        oaCalendar oaCalendar = oaCalendarMapper.getByIid(id);
        String[] UserIdList = oaCalendar.getSUserNames().split(",");
        Set<String> set = new HashSet<>() ;
        for (int j = 0; j <UserIdList.length; j++) {
            SysUser sysUser =  sysUserMapper.getUserByName(UserIdList[j]);
            if(! (sysUser == null)){
                String Id = sysUser.getId(); //取id
                set.add(Id);
            }
        }
        oaCalendar.setSUserNameid(set);
        return oaCalendar;
    }



    @Override
    public List<SysUser> getByShowOrder(int showOrder) {
        return oaCalendarMapper.getByShowOrder(showOrder);
    }

    @Override
    public IPage<oaCalendar> findWait(Integer pageNo, Integer pageSize, oaCalendar oaCalendar) {
        int total = oaCalendarMapper.countIsWait(oaCalendar);
        List<oaCalendar> CalendarList=oaCalendarMapper.findWaitLimit(oaCalendar);
        IPage<oaCalendar> pageList = new Page<oaCalendar>();
        pageList.setRecords(CalendarList);
        pageList.setTotal(total);
        pageList.setSize(pageSize) ;
        pageList.setCurrent(pageNo) ;
        return pageList;
    }

    @Override
    public String getUserId(String id) {
        return oaCalendarMapper.getByUserId(id);
    }

    @Override
    public String getDepartId(String id) {
        return oaCalendarMapper.getDepartId(id);
    }

    @Override
    public List<String> getDepartIdList(String userNameId) {
        return oaCalendarMapper.getDepartIdList(userNameId);
    }

    @Override
    public List<BusFunction> busFunctionList() {
        return oaCalendarMapper.busFunctionList();
    }

    @Override
        public Map<String, Object> MostUserLink(HttpServletResponse response,String id) throws IOException {
             Map<String, Object> busdata = oaCalendarMapper.findMostUser(id);
             String url = oaCalendarMapper.selectUrl(Integer.parseInt(id));
             String path = oaCalendarMapper.selectPath(Integer.parseInt(id));
             File file = new File(path);
             FileInputStream stream = new FileInputStream(file);
             byte[] b = new byte[1024];
             int len = -1;
             while ((len = stream.read(b, 0, 1024)) != -1) {
                 response.getOutputStream().write(b, 0, len);
             }
              busdata.put("url",url) ;

            /*  List<Map<String, Object>> list = oaCalendarMapper.findMostUser();
             for(int i=0;i< list.size();i++){
                 String url = oaCalendarMapper.selectUrl(Integer.parseInt(list.get(i).id);
                 String path = oaCalendarMapper.selectPath(Integer.parseInt(list.get(i).id));
                 File file = new File(path);
                 FileInputStream stream = new FileInputStream(file);
                 byte[] b = new byte[1024];
                 int len = -1;
                 while ((len = stream.read(b, 0, 1024)) != -1) {
                     response.getOutputStream().write(b, 0, len);
                 }
                 list.get(i).put("url",url);
             }*/

        return busdata;
    }


    @Override
    public List<String> LinkList() {

        return oaCalendarMapper.LinkList();
    }


}
