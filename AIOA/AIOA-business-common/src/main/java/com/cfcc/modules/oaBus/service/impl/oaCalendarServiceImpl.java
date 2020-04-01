package com.cfcc.modules.oaBus.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.common.util.DateUtils;
import com.cfcc.modules.oaBus.entity.*;
import com.cfcc.modules.oaBus.mapper.oaCalendarMapper;
import com.cfcc.modules.oaBus.service.IBusModelService;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.cfcc.modules.oaBus.service.IOaFileService;
import com.cfcc.modules.oaBus.service.IoaCalendarService;
import com.cfcc.modules.shiro.vo.DefContants;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.mapper.SysUserMapper;
import com.cfcc.modules.system.model.TreeModel;
import com.cfcc.modules.system.service.ISysUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.servlet.http.HttpServletRequest;
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

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Autowired
    private oaCalendarMapper oaCalendarMapper;
    @Autowired
    private ISysUserService userService;

    @Autowired
    IOaFileService iOaFileService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private IOaBusdataService oaBusdataService;
    
    @Autowired
    private IBusModelService iBusModelService;

    @Override
    public oaCalendar findById(Integer iId) {
        oaCalendar oaCalendar = oaCalendarMapper.findById(iId);
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
        for (oaCalendar   calendar :CalendarList) {
            String state = calendar.getState();
            if(state.equals("0")){//0,普通日程、1,办件（未办）、2，办件（已办），3，阅件\\抄送（未阅），4，阅件阅件\\抄送（已阅）',
                calendar.setStateName("");
            }else if(state.equals("1")){
                calendar.setStateName("【待办】");
            }else if(state.equals("2")){
                calendar.setStateName("【已办】");
            }else if(state.equals("3")){
                calendar.setStateName("【未阅】");
            }else if(state.equals("4")){
                calendar.setStateName("【已阅】");
            }
        }
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

            String state = oaCalendar.getState();
            if(state.equals("0")){//0,普通日程、1,办件（未办）、2，办件（已办），3，阅件\\抄送（未阅），4，阅件阅件\\抄送（已阅）',
                oaCalendar.setStateName("");
            }else if(state.equals("1")){
                oaCalendar.setStateName("【待办】");
            }else if(state.equals("2")){
                oaCalendar.setStateName("【已办】");
            }else if(state.equals("3")){
                oaCalendar.setStateName("【未阅】");
            }else if(state.equals("4")){
                oaCalendar.setStateName("【已阅】");
            }

        String[] UserIdList = oaCalendar.getSUserNames().split(",");
        Set<String> set = new HashSet<>() ;
        for (int j = 0; j <UserIdList.length; j++) {
            SysUser sysUser =  sysUserMapper.getUserByName(UserIdList[j]);
            if(! (sysUser == null)){
                String Id = sysUser.getId(); //取id
                set.add(Id);
            }
        }
        Integer iBusModelId = oaCalendar.getIBusModelId();
        if(iBusModelId!=null){
            BusModel busModel = iBusModelService.getBusModelById(iBusModelId);
            String sBusdataTable = busModel.getSBusdataTable();
            oaCalendar.setTableName(sBusdataTable);
        }

        oaCalendar.setSUserNameid(set);
        return oaCalendar;
    }



   /* @Override
    public List<SysUser> getByShowOrder(int showOrder) {
        return oaCalendarMapper.getByShowOrder(showOrder);
    }
*/
    @Override
    public IPage<oaCalendar> findWait(Integer pageNo, Integer pageSize, oaCalendar oaCalendar) {
        int total = oaCalendarMapper.countIsWait(oaCalendar);
        List<oaCalendar> CalendarList=oaCalendarMapper.findWait((pageNo - 1) * pageSize,pageSize,oaCalendar);
        IPage<oaCalendar> pageList = new Page<oaCalendar>();
        pageList.setRecords(CalendarList);
        pageList.setTotal(total);
        pageList.setSize(pageSize) ;
        pageList.setCurrent(pageNo) ;
        return pageList;
    }

    @Override
    public List<String> getUserId(String id) {

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
        List<BusFunction> busFunctions = oaCalendarMapper.busFunctionList();
        for (BusFunction busFunction: busFunctions) {
            Integer iBusModelId = busFunction.getIBusModelId();
            BusModel busModelById = iBusModelService.getBusModelById(iBusModelId);
            String sName = busModelById.getSName();
            busFunction.setBusModelName(sName);
        }
        return busFunctions;
    }

    @Override
        public void MostUserLink(HttpServletResponse response, HttpServletRequest request, String id, String resourceType)  {

        try {
            String filePath = oaCalendarMapper.selectPath(Integer.parseInt(id));
          //  String fileName =  oaCalendarMapper.selectName(Integer.parseInt(id));
          //  filePath = uploadpath+"\\"+filePath.substring(0,filePath.lastIndexOf("\\")+1)+fileName;

//            LoginInfo loginInfo = userService.getLoginInfo(request);
            String orgSchema = MycatSchema.getSchema();
            if (StringUtils.isNotBlank(orgSchema)) {
                filePath = uploadpath + File.separator + orgSchema + File.separator + filePath;
            } else {
                filePath = uploadpath + File.separator + filePath;
            }
            System.out.println(filePath+"------------------");
            File file = new File(filePath);
            FileInputStream stream = new FileInputStream(file);
            byte[] b = new byte[1024];
            int len = -1;
            while ((len = stream.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

  /*  @Override
    public List<Map<String, Object>>  LinkList(HttpServletRequest request)  {
        LoginInfo loginInfo = userService.getLoginInfo(request);
        String sCreateBy = loginInfo.getId();
        List<Map<String, Object>> list = oaCalendarMapper.LinkList(sCreateBy);
        return list;
    }
*/
    @Override
    public oaCalendar findByTaskUserId(String taskUserId) {
        return oaCalendarMapper.findByTaskUserId(taskUserId);
    }


}
