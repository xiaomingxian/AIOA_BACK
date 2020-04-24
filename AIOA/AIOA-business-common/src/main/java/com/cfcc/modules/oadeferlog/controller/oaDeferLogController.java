package com.cfcc.modules.oadeferlog.controller;

import com.cfcc.common.api.vo.Result;
import com.cfcc.modules.oadeferlog.service.IoaDeferLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

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
}
