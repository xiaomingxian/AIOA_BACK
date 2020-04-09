package com.cfcc.modules.oaBus.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.util.DatesUtils;
import com.cfcc.modules.oaBus.entity.OaBusdata;
import com.cfcc.modules.oaBus.entity.oaCalendar;
import com.cfcc.modules.oaBus.mapper.BusdataPermitMapper;
import com.cfcc.modules.oaBus.mapper.MeetingInformMapper;
import com.cfcc.modules.oaBus.mapper.oaCalendarMapper;
import com.cfcc.modules.oaBus.service.MeetingInformService;
import com.cfcc.modules.oaBus.service.OaBusDynamicTableService;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.mapper.SysUserMapper;
import com.cfcc.modules.system.service.ISysDepartService;
import com.cfcc.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MeetingInformServiceImpl extends ServiceImpl<MeetingInformMapper, OaBusdata> implements MeetingInformService {

    @Autowired
    private MeetingInformMapper meetingInformMapper;

    @Autowired
    private BusdataPermitMapper busdataPermitMapper;

    @Autowired
    private ISysDepartService sysDepartService;

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private OaBusDynamicTableService dynamicTableService;

    @Override
    public IPage<OaBusdata> queryMeetingInformList(OaBusdata oaBusdata, Integer pageNo, Integer pageSize) {
        int total=meetingInformMapper.queryMeetingInformTotal(oaBusdata);

        List<OaBusdata> oaBusdataList=meetingInformMapper.queryMeetingInform(oaBusdata,(pageNo-1)*pageSize,pageSize);

        IPage<OaBusdata> oaBusdataIPage=new Page<>();
        oaBusdataIPage.setTotal(total);
        oaBusdataIPage.setRecords(oaBusdataList);
        oaBusdataIPage.setSize(pageSize);
        oaBusdataIPage.setPages(pageNo);
        return oaBusdataIPage;
    }

    @Override
    public IPage<OaBusdata> queryMeetingList(String s_varchar1, Integer pageNo, Integer pageSize,String sTime,String eTime) {
        int total=meetingInformMapper.queryMeetingTotal(s_varchar1);
        Date sDate= DatesUtils.stringToDate(sTime);
        Date eDate=DatesUtils.stringToDate(eTime);
        List<OaBusdata> MeetingList = meetingInformMapper.queryMeeting(s_varchar1,(pageNo-1)*pageSize,pageSize);
        MeetingList.forEach(funcation ->{
            Date dDatetime1 = funcation.getDDatetime1();
            Date dDatetime2 = funcation.getDDatetime2();
            if (DatesUtils.getDayCoincidence(sDate,dDatetime1,dDatetime2,eDate) == 0){
                funcation.setConflict(1);
            }else {
                funcation.setConflict(0);
            }
        });
        IPage<OaBusdata> oaBusdataIPage=new Page<>();
        oaBusdataIPage.setTotal(total);
        oaBusdataIPage.setRecords(MeetingList);
        oaBusdataIPage.setSize(pageSize);
        oaBusdataIPage.setPages(pageNo);
        return oaBusdataIPage;
    }

    /**
     * 会议室可使用时间
     * @param meetingRoom
     * @return
     */
    @Override
    public List<OaBusdata> queryMeetingTime(String meetingRoom,String tableName) {
        List<OaBusdata> timeList=meetingInformMapper.queryMeetingTime(meetingRoom,tableName);
        return timeList;
    }

    @Override
    @Transactional
    public Result<String> insertUserMeetingInform(Map<String, Object> map, SysUser currentUser) {
        Result<String> result = new Result();
        try{
            String iIsLimits= map.get("iIsLimits")+"";
            String busdataId = map.get("busdataId") + "";
            String userRealName=map.get("userRealName")+"";
            String departId = map.get("departId") + "";
            String tableName = map.get("tableName") + "_permit";
            String functionId = map.get("functionId") + "";
            String meetingUserId = map.get("meetingUserId") + "";
            String sTittle = map.get("sTittle") + "";
            String sAddress = map.get("sAddress") + "";
            String dStartTime = map.get("dStartTime") + "";
            String dEndTime = map.get("dEndTime") + "";
//        String sCreateBy = map.get("sCreateBy") + "";
//        String dCreateTime = map.get("dCreateTime") + "";
            SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :HH:mm:ss");
            String dCreateTime = dateFormat.format(new Date());
            String iBusModelId = map.get("iBusModelId") + "";
            if ("1".equals(iIsLimits)){
                SysDepart sysDepart = sysDepartService.queryUserDepartByOrgCode(currentUser.getOrgCode());

                SysDepart maxUnitByDeptId = sysDepartService.getMaxUnitByDeptId(sysDepart.getId());

                Map<String,Object> mapPermit = new HashMap<>();
                mapPermit.put("table",tableName) ;
                mapPermit.put("i_bus_function_id",functionId) ;
                mapPermit.put("i_busdata_id",busdataId) ;
                mapPermit.put("s_userunit_id",maxUnitByDeptId.getId()) ;
                dynamicTableService.insertData(mapPermit);
                Map<String,Object> mapCalendar = new HashMap<>();
                String table="oa_calendar";
                mapCalendar.put("table",table);
                mapCalendar.put("s_title",sTittle);
                mapCalendar.put("s_user_names",maxUnitByDeptId.getDepartName());
                mapCalendar.put("s_address",sAddress);
                mapCalendar.put("i_open_type",1);
                mapCalendar.put("d_start_time",dStartTime);
                mapCalendar.put("d_end_time",dEndTime);
                mapCalendar.put("s_create_by",currentUser.getUsername());
                mapCalendar.put("d_create_time",dCreateTime);
                mapCalendar.put("i_bus_model_id",iBusModelId);
                mapCalendar.put("i_bus_function_id",functionId);
                dynamicTableService.insertData(mapCalendar);

            }else if ("2".equals(iIsLimits)){
                String depts[] = departId.split(",") ;
                for(int i = 0 ; i < depts.length ; i++){
                    SysDepart unitByDept = sysDepartService.getUnitByDeptId(depts[i]);
                    Map<String,Object> mapPermit = new HashMap<>();
                    mapPermit.put("table",tableName) ;
                    mapPermit.put("i_bus_function_id",functionId) ;
                    mapPermit.put("i_busdata_id",busdataId) ;
                    mapPermit.put("s_userunit_id",unitByDept.getId()) ;
                    mapPermit.put("s_userdept_id",depts[i]) ;
                    mapPermit.put("i_is_read",1);
                    dynamicTableService.insertData(mapPermit);

                    List<String> userList=sysUserMapper.getUserNameByDeptId(depts[i]);

                    for (int j=0;j<userList.size();j++){
                        String username=userList.get(i);
                        Map<String,Object> mapCalendar = new HashMap<>();
                        String table="oa_calendar";
                        mapCalendar.put("table",table);
                        mapCalendar.put("s_title",sTittle);
                        mapCalendar.put("s_user_names",username);
                        mapCalendar.put("s_address",sAddress);
                        mapCalendar.put("i_open_type",1);
                        mapCalendar.put("d_start_time",dStartTime);
                        mapCalendar.put("d_end_time",dEndTime);
                        mapCalendar.put("s_create_by",currentUser.getUsername());
                        mapCalendar.put("d_create_time",dCreateTime);
                        mapCalendar.put("i_bus_model_id",iBusModelId);
                        mapCalendar.put("i_bus_function_id",functionId);
                        dynamicTableService.insertData(mapCalendar);
                    }
                }

            }else if ("3".equals(iIsLimits)){
                String UserId[] = meetingUserId.split(",") ;
                String userReName[]=userRealName.split(",");
                for(int i = 0 ; i < UserId.length ; i++){
                    String userId=UserId[i];
                    List<SysDepart> departs = sysDepartService.queryUserDeparts(userId);
                    SysUser sysUser=sysUserMapper.getUserByUserId(userId);
                    Map<String,Object> mapCalendar = new HashMap<>();
                    String table="oa_calendar";
                    mapCalendar.put("table",table);
                    mapCalendar.put("s_title",sTittle);
                    mapCalendar.put("s_user_names",sysUser.getUsername());
                    mapCalendar.put("s_address",sAddress);
                    mapCalendar.put("i_open_type",1);
                    mapCalendar.put("d_start_time",dStartTime);
                    mapCalendar.put("d_end_time",dEndTime);
                    mapCalendar.put("s_create_by",currentUser.getUsername());
                    mapCalendar.put("d_create_time",dCreateTime);
                    mapCalendar.put("i_bus_model_id",iBusModelId);
                    mapCalendar.put("i_bus_function_id",functionId);
                    dynamicTableService.insertData(mapCalendar);
                    departs.forEach(dept->{
                        SysDepart unitByDept = sysDepartService.getUnitByDeptId(dept.getId());
                        Map<String,Object> mapPermit = new HashMap<>();
                        mapPermit.put("table", tableName) ;
                        mapPermit.put("i_bus_function_id",functionId) ;
                        mapPermit.put("i_busdata_id",busdataId) ;
                        mapPermit.put("s_userunit_id",unitByDept.getId()) ;
                        mapPermit.put("s_userdept_id",dept.getId()) ;
                        mapPermit.put("s_user_id",userId) ;
                        mapPermit.put("i_is_read",1);
                        dynamicTableService.insertData(mapPermit);
                    });
                }
            }
            result.setSuccess(true);
        } catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
        }
        return result;
    }


}
