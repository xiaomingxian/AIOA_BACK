package com.cfcc.modules.oaBus.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.modules.oaBus.entity.OaBusdata;
import com.cfcc.modules.oaBus.entity.oaCalendar;
import com.cfcc.modules.oaBus.service.IoaCalendarService;
import com.cfcc.modules.oaBus.service.MeetingInformService;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/oaBus/meetingInform")
public class MeetingInformController {

    @Autowired
    private MeetingInformService meetingInformService;

    @Autowired
    private ISysUserService sysUserService;
    /**
     * 已读情况
     * @param oaBusdata
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/getReadCase")
    public Result<IPage<OaBusdata>> getMeetingInform(OaBusdata oaBusdata,
                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize){
        Result<IPage<OaBusdata>> result = new Result<>();
        IPage<OaBusdata> list=meetingInformService.queryMeetingInformList(oaBusdata,pageNo,pageSize);
        result.setSuccess(true);
        result.setResult(list);
        return result;
    }

    /**
     * 会议室使用情况
     * @param s_varchar1 会议室字段中的id
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/getMeetingRoom")
    public Result<IPage<OaBusdata>> getMeetingRoom(@RequestParam(name = "s_varchar1") String s_varchar1,
                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize){
        Result<IPage<OaBusdata>> result = new Result<>();
        IPage<OaBusdata> roomList=meetingInformService.queryMeetingList(s_varchar1,pageNo,pageSize);
        result.setSuccess(true);
        result.setResult(roomList);
        return result;
    }

    /**
     * 会议时间
     * @param meetingRoom
     * @return
     */
    @GetMapping(value = "/getMeetingTime")
    public Result<List<OaBusdata>> getMeetingTime(@RequestParam(name = "meetingRoom") String meetingRoom,
                                                  @RequestParam(name = "tableName") String tableName){
        Result<List<OaBusdata>> result=new Result<>();
        List<OaBusdata> meetingTimeList=meetingInformService.queryMeetingTime(meetingRoom,tableName);
        result.setSuccess(true);
        result.setResult(meetingTimeList);
        return result;
    }

    /**
     * 发送会议通知
     * @param map
     * @param request
     * @return
     */
    @PostMapping(value = "/sendMeetingInform")
    public Result sendMeetingInform(@RequestBody Map<String, Object> map,HttpServletRequest request){
        SysUser currentUser = sysUserService.getCurrentUser(request);

        String flag= meetingInformService.insertUserMeetingInform(map,currentUser);

        return Result.ok(flag);

    }
}
