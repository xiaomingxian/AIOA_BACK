package com.cfcc.modules.MissionCenter.controller;

import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.modules.MissionCenter.service.MissionCenterService;

import com.cfcc.modules.oaBus.entity.oaCalendar;
import com.cfcc.modules.system.entity.LoginInfo;

import com.cfcc.modules.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @Author
 * @since
 */
@Slf4j
@Api(tags = "App任务中心相关查询")
@RestController
@RequestMapping("/app/missionCenter/oaCalender")
public class MissionCenterController {
    @Autowired
    private MissionCenterService missionCenterService;
    @Autowired
    private ISysUserService isysUserService;

    @ApiOperation(value = "日程表-查询任务中心的列表", notes = "日程表-查询任务中心的列表")
    @GetMapping(value = "/queryList")
    public Result<Map<String, Object>> queryListByUserId(oaCalendar oaCalendar,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         HttpServletRequest request) {
        // 查询当前用户，作为assignee
        LoginInfo loginInfo = isysUserService.getLoginInfo(request);
        Result<Map<String, Object>> result = new Result<>();
        oaCalendar.setSUserNames(loginInfo.getUsername());
        Map<String, Object> CenterList = missionCenterService.queryListMap(oaCalendar, pageNo, pageSize);
        if (CenterList == null) {
            result.error500("未找到对应实体");
        }
        result.setResult(CenterList);
        return result;
    }

}
