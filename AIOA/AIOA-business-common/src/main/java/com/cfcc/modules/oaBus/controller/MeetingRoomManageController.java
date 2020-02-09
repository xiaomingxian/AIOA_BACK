package com.cfcc.modules.oaBus.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.modules.oaBus.entity.OaBusdata;
import com.cfcc.modules.oaBus.service.IMeetingRoomManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Api(tags="业务数据表-会议室管理")
@RestController
@RequestMapping("/oaBus/meetingRoom")
public class MeetingRoomManageController {

    @Autowired
    private IMeetingRoomManageService meetingRoomManageService;

    /**
     * 分页列表查询
     * @param meetingRoom
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "会议室管理-分页列表查询")
    @ApiOperation(value="会议室管理-分页列表查询", notes="会议室管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<OaBusdata>> queryPageList(OaBusdata meetingRoom,
                                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                   HttpServletRequest req) {
        Result<IPage<OaBusdata>> result = new Result<IPage<OaBusdata>>();
        IPage<OaBusdata> pageList = meetingRoomManageService.queryMeetingRoomList(meetingRoom, pageNo,pageSize);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }
}
