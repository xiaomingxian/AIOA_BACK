package com.cfcc.modules.oadeferlog.controller;

import com.cfcc.modules.oadeferlog.service.IoaDeferLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

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
	


}
