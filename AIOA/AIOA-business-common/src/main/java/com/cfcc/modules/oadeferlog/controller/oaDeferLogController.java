package com.cfcc.modules.oadeferlog.controller;

import com.cfcc.common.api.vo.Result;
import com.cfcc.common.system.util.JwtUtil;
import com.cfcc.modules.oaBus.service.IoaCalendarService;
import com.cfcc.modules.oadeferlog.entity.oaDeferLog;
import com.cfcc.modules.oaBus.entity.oaCalendar;
import com.cfcc.modules.oadeferlog.service.IoaDeferLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Description: 任务类事务延期记录
 * @Author: jeecg-boot
 * @Date:   2020-04-23
 * @Version: V1.0
 */
@Slf4j
@Api(tags="任务类事务延期记录")
@RestController
@RequestMapping("/oadeferlog/oaDeferLog")
public class oaDeferLogController {
	@Autowired
	private IoaDeferLogService oaDeferLogService;


    @PostMapping(value = "/updataOadataAndaddOaDeferLog")
    public Result<Object> updataOadataAndaddOaDeferLog(@RequestBody Map<String,Object> map) {
        Result<Object> result = new Result<>();
        try {
            boolean flag = oaDeferLogService.updataOadataAndaddOaDeferLog(map);
            if (flag) {
                result.setSuccess(true);
            } else {
                result.setSuccess(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping(value = "/selecturgeLog")
    public Result<Object> selecturgeLog(@RequestParam(value = "sTable", required = true) String sTable,
                                        @RequestParam(value = "iTableId", required = true) Integer iTableId){
        Result<Object> result = new Result<>();
        try {
            List<oaDeferLog> list=oaDeferLogService.selecturgeLog(sTable,iTableId);
            if (list.size()!=0){
                result.setResult(list);
                result.setSuccess(true);
            }else {
                result.setSuccess(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
        }
        return result;
    }

    @GetMapping(value = "/selecturgeInform")
    public Result<Object> selecturgeInform(@RequestParam(value = "iTableId", required = true) Integer iTableId, HttpServletRequest request){
        Result<Object> result = new Result<>();
        try {
            //获取当前用户名称
            String token = request.getHeader("X-Access-Token");
            String username = JwtUtil.getUsername(token);
            List<oaCalendar> list=oaDeferLogService.selecturgeInform(iTableId,username);
            if (list.size()!=0){
                result.setResult(list);
                result.setSuccess(true);
            }else {
                result.setSuccess(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
        }
        return result;
    }

}
