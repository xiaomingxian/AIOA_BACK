package com.cfcc.modules.oadatafetailedinst.controller;
import com.cfcc.modules.oadatafetailedinst.service.IOaDatadetailedInstService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

 /**
 * @Description: 明细存储
 * @Author: jeecg-boot
 * @Date:   2020-04-16
 * @Version: V1.0
 */
@Slf4j
@Api(tags="明细存储")
@RestController
@RequestMapping("/oadatafetailedinst/oaDatadetailedInst")
public class OaDatadetailedInstController {
	@Autowired
	private IOaDatadetailedInstService oaDatadetailedInstService;
	

	


}
