package com.cfcc.modules.oadatafetailedinst.controller;
import com.cfcc.common.api.vo.Result;
import com.cfcc.modules.oadatafetailedinst.entity.OaDatadetailedInst;
import com.cfcc.modules.oadatafetailedinst.service.IOaDatadetailedInstService;
import com.cfcc.modules.workflow.service.TaskCommonService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
import java.util.List;
import java.util.Map;

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
	@Autowired
	private TaskCommonService taskCommonService;
	@GetMapping("queryTaskUnDoCurrent")
	@ApiOperation("查看催办代办信息")
	public Result queryTask(String procInstId, Integer iTableId) {//流程实例id
		//环节 用户
		try {
			List<Map<String, Object>> tasks = taskCommonService.workTrack(procInstId, false);

			if (tasks == null){ return Result.error("环节配置信息不完善请检查");}
			else {
				for (int i=0;i<tasks.size();i++) {
					Map<String, Object> stringObjectMap = tasks.get(i);
					String sCreateName = stringObjectMap.get("userName").toString();
					Map<String,Object>  DataList= oaDatadetailedInstService.findByTableId(iTableId,sCreateName);

					if(DataList == null){
						tasks.get(i).put("iIs1","");
						tasks.get(i).put("dCreateTime","");
					}else{
						tasks.get(i).put("iIs1",DataList.get("i_is_1"));
						tasks.get(i).put("dCreateTime",DataList.get("d_create_time"));
					}

				}
			}
			return Result.ok(tasks);
		} catch (Exception e) {
			log.error(e.toString());
			return Result.error("查询失败");
		}
	}


     @PostMapping(value = "/adddatadetailedInst")
     public Result<Object> adddatadetailedInst(@RequestBody Map<String,Object> map) {
         Result<Object> result = new Result<Object>();
         int num=oaDatadetailedInstService.addorupdataDetailed(map);
         if (num!=0){
             result.setSuccess(true);
         }else {
             result.setSuccess(false);
         }
         return result;
     }

     @GetMapping(value = "/selectdetailedInst")
     public Result<Object> selectdetailedInst(@RequestParam(value = "sCreateBy", required = true) String sCreateBy,
                                              @RequestParam(value = "sCreateDeptid", required = true) String sCreateDeptid){
         Result<Object> result = new Result<Object>();
         List<OaDatadetailedInst> list=oaDatadetailedInstService.seletdetailedInstList(sCreateBy,sCreateDeptid);
         if (list.size()!=0){
             result.setResult(list);
             result.setSuccess(true);
         }else {
             result.setSuccess(false);
         }
         return result;
     }

    @GetMapping(value = "/selectSharedetailedInst")
    public Result<Object> selectsharedetailedInst(@RequestParam(value = "sTable", required = true) String sTable,
                                                  @RequestParam(value = "iTableId", required = true) Integer iTableId,
                                                  @RequestParam(value = "sCreateBy", required = true) String sCreateBy,
                                                  @RequestParam(value = "sCreateDeptid", required = true) String sCreateDeptid){
        Result<Object> result = new Result<Object>();
        List<OaDatadetailedInst> list=oaDatadetailedInstService.seletAlldetailedInstList(sTable,iTableId,sCreateBy,sCreateDeptid);
        if (list.size()!=0){
            result.setResult(list);
            result.setSuccess(true);
        }else {
            result.setSuccess(false);
        }
        return result;
    }

    @GetMapping(value = "/selectAlldetailedInst")
    public Result<Object> selectAlldetailedInst(@RequestParam(value = "sTable", required = true) String sTable,
                                                  @RequestParam(value = "iTableId", required = true) Integer iTableId){
        Result<Object> result = new Result<Object>();
        List<OaDatadetailedInst> list=oaDatadetailedInstService.seletSharedetailedInstList(sTable,iTableId);
        if (list.size()!=0){
            result.setResult(list);
            result.setSuccess(true);
        }else {
            result.setSuccess(false);
        }
        return result;
    }


 }

