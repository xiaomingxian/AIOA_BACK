package com.cfcc.modules.oadatafetailedinst.controller;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.util.DateUtils;
import com.cfcc.common.util.FileUtils;
import com.cfcc.modules.docnum.entity.DocNumSet;
import com.cfcc.modules.oadatafetailedinst.entity.OaDatadetailedInst;
import com.cfcc.modules.oadatafetailedinst.service.IOaDatadetailedInstService;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.service.ISysDictService;
import com.cfcc.modules.system.service.ISysUserService;
import com.cfcc.modules.workflow.service.TaskCommonService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    private ISysUserService isysUserService;
    @Autowired
    private ISysDictService sysDictService;
    @Value("${jeecg.path.upload}")
    private String savePath;
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
    @GetMapping("findJinZhanList")
    @ApiOperation("进展分析评价列表")
    public Result findJinZhanList(HttpServletRequest request,Integer tableid, String proInstId, @RequestParam(defaultValue = "", required = false) String endTime) {//流程实例id
        //环节 用户
        try {
            if (StringUtils.isBlank(endTime)) {
                List<Map<String, Object>> res = taskCommonService.workTrack(proInstId, true);
                if (res == null)
                {return Result.error("流程环节配置不完善请检查");}
                else{
                        for(int k=0;k<res.size();k++){
                            Map<String, Object> m = res.get(k);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                            String taskDefName =(String) m.get("taskDefName");
                            String userName = (String) m.get("userName");
                            String deptName = (String) m.get("deptName");
                            Date endTime1 = (Date)m.get("endTime");
                            String formatdate = null;
                            if(endTime1 != null){
                                 formatdate=sdf.format(endTime1);
                                if(formatdate.equals("")){
                                    formatdate = null;
                                }
                            }

                            Map<String,Object> maps = new HashMap<>();
                            Integer sum = 1;
                            Date date = DateUtils.getDate();
                            String Now = null;
                            Now=sdf.format(date);
                            long time1 = 0 ; //创建时间
                            long time2 = 0; //约定办理时间
                            long time = 0; //最后办理时间
                            long nowTime = 0;//当前时间

                            Integer count = oaDatadetailedInstService.findOpions(tableid,userName);  //字数
                            Integer file  = oaDatadetailedInstService.findIsFile(tableid,userName,deptName);  //附件
                            //Integer file = oaDatadetailedInstService.findIsFile(tableid);
                            Map<String,Object>  dateMap =  oaDatadetailedInstService.findDate(tableid); //时间
                            String createTime= null;
                            String dateTime = null;
                            Integer pinci = 0;
                            Integer total = 0;
                            if(dateMap!=null){
                                createTime = (String)dateMap.get("createTime");
                                dateTime =  (String)dateMap.get("dateTime");
                                pinci = (Integer)dateMap.get("s_varchar2");
                                if(pinci == null){
                                    pinci = 0;
                                }
                                total = oaDatadetailedInstService.getDateCount(createTime,dateTime,userName);
                                try {
                                    time1 = sdf.parse(createTime).getTime(); //创建时间
                                    time2 = sdf.parse(dateTime).getTime(); //约定办理时间
                                    nowTime = sdf.parse(Now).getTime(); //当前时间
                                    if(formatdate != null){
                                        time = sdf.parse(formatdate).getTime(); //最后办理时间
                                    }



                                    long between_days=(time2-time1)/(1000*3600*24);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
//                        DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
//                        String formatdate=df.format(dt);

                            //数据字典获取functionid
                            SysUser currentUser = isysUserService.getCurrentUser(request);
                            Map<String, Object> allUserMsg = isysUserService.getAllUserMsg(currentUser.getUsername());
                            String deptId = allUserMsg.get("deptId") + "";
                            String dictKey = "sup_parameter"; //数据字典-督办
                            List<Map<String, Object>> list = sysDictService.getDictByKeySer(dictKey, deptId);
                            for(int i=0;i<list.size();i++){
                                Map<String, Object> stringObjectMap = list.get(i);
                                String text = (String)stringObjectMap.get("text");
                                Integer status = (Integer) stringObjectMap.get("status");
                                String value = (String)stringObjectMap.get("value");
                                if (text.equals("count") &&  status==1) {
                                    String[] split = value.split(":");
                                    for (int j = 0; j < split.length; j++) {
                                        int s = Integer.parseInt(split[0]);
                                        if (s < count) {//表示字体大于配置的
                                            sum = sum+1;
                                        }
                                    }
                                }else if(text.equals("frequency")&& status==1){ //是否符合频次并且启动
                                    if(total>pinci){  //办理进展的条数>频次
                                        sum = sum+1;
                                    }

                                }else if(text.equals("after")&&  status==1){  //是否超时并且启动
                                    if(nowTime-time2>0&& time == 0){//当前时间大于约定的办理时间并且还没办理
                                        sum = sum-1;
                                    }
                                    else if(time - time2 > 0){//最后办理的时间-约定办理时间
                                        sum = sum-1;
                                    }

                                }else if(text.equals("before")&&  status==1){ //是否提前并且启动
                                    if(time - time2 < 0){//最后办理的时间- 约定办理时间<0
                                        long between_days=(time-time2)/(1000*3600*24);
                                        if(between_days>5){
                                            sum = sum+1;
                                        }

                                    }

                                }else if(text.equals("isfile")&& status==1){ //是否有附件并且启动
                                    if(file>0){//表示有附件
                                        sum = sum +1;
                                    }
                                }
                                m.put("sum",sum);
                            }
                        }
                    Result<Object> result = Result.ok("查询成功");
                    result.setResult(res);
                    return result;
                }

            } else {
                String timePath = endTime.replaceAll("-", "/");
                String fullPath = savePath + "/activiti/" + timePath + "/" + proInstId + "_trace";
                List<Map> maps = FileUtils.readJSONArrayOneLine(fullPath);
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                maps.stream().forEach(m -> {
                    Long endTimeStemp = (Long) m.get("endTime");
                    if (endTimeStemp!=null){
                        String format = sf.format(new Date(endTimeStemp));
                        m.put("endTime", format);
                    }

                });

                return Result.ok(maps);
            }
        } catch (Exception e) {
//            e.printStackTrace();
            log.error(e.toString());
            return Result.error("查询失败");
        }
    }
    @GetMapping("findRate")
    @ApiOperation("办结率计算")
    public Result findRate(String itableId,String proInstId, @RequestParam(defaultValue = "", required = false) String endTime) {//流程实例id
        //环节 用户
        try {

                List<Map<String, Object>> res = taskCommonService.workTrack(proInstId, true);

                Result<Object> result = Result.ok("查询成功");
                if (res == null) return Result.error("流程环节配置不完善请检查");
                Integer size = res.size();
                Integer count = oaDatadetailedInstService.getBanjieBydept(itableId);
                if(count == 0){
                    result.setResult(0);
                    return result;
                }else{
                    double i = size / count;
                    result.setResult(i);
                    return result;
                }


        } catch (Exception e) {
//            e.printStackTrace();
            log.error(e.toString());
            return Result.error("查询失败");
        }
    }


     @PostMapping(value = "/adddatadetailedInst")
     public Result<Object> adddatadetailedInst(@RequestBody Map<String,Object> map) {
         Result<Object> result = new Result<Object>();
         try {
             int id=oaDatadetailedInstService.addDetailed(map);
//         int id=oaDatadetailedInstService.addorupdataDetailed(OaDatadetailedInst);
             if (id!=0){
                 result.setResult(id);
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

     @GetMapping(value = "/selectdetailedInst")
     public Result<Object> selectdetailedInst(@RequestParam(value = "sTable", required = true) String sTable,
                                              @RequestParam(value = "iTableId", required = true) Integer iTableId,
                                              @RequestParam(value = "sCreateBy", required = true) String sCreateBy,
                                              @RequestParam(value = "sCreateDeptid", required = true) String sCreateDeptid){
         Result<Object> result = new Result<Object>();
         try {
             List<OaDatadetailedInst> list=oaDatadetailedInstService.seletdetailedInstList(sTable,iTableId,sCreateBy,sCreateDeptid);
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

    @GetMapping(value = "/selectSharedetailedInst")
    public Result<Object> selectsharedetailedInst(@RequestParam(value = "sTable", required = true) String sTable,
                                                  @RequestParam(value = "iTableId", required = true) Integer iTableId,
                                                  @RequestParam(value = "sCreateBy", required = true) String sCreateBy,
                                                  @RequestParam(value = "sCreateDeptid", required = true) String sCreateDeptid){
        Result<Object> result = new Result<Object>();
        try {
            List<OaDatadetailedInst> list=oaDatadetailedInstService.seletAlldetailedInstList(sTable,iTableId,sCreateBy,sCreateDeptid);
            if (list.size()!=0){
                result.setResult(list);
                result.setSuccess(true);
            }else {
                result.setSuccess(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping(value = "/selectAlldetailedInst")
    public Result<Object> selectAlldetailedInst(@RequestParam(value = "sTable", required = true) String sTable,
                                                  @RequestParam(value = "iTableId", required = true) Integer iTableId){
        Result<Object> result = new Result<Object>();
        try {
            List<OaDatadetailedInst> list=oaDatadetailedInstService.seletSharedetailedInstList(sTable,iTableId);
            if (list.size()!=0){
                result.setResult(list);
                result.setSuccess(true);
            }else {
                result.setSuccess(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping(value = "/updatadetailedInst")
    public Result<Object> updatadetailedInst(@RequestBody Map<String,Object> map){
        Result<Object> result = new Result<>();
        try {
            boolean flag=oaDatadetailedInstService.updatadetailedInst(map);
            if (flag){
                result.setSuccess(true);
            }else {
                result.setSuccess(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping(value = "/deteledetailedInst")
    public Result<Object> deteledetailedInst(@RequestParam(value = "iId", required = true) Integer iId){
        Result<Object> result = new Result<Object>();
        try {
            int num=oaDatadetailedInstService.deteledetailedInst(iId);
            if (num!=0){
                result.setSuccess(true);
            }else {
                result.setSuccess(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping(value = "/updataDetailedIsStats")
    public Result<Object> updataDetailedIsStats(@RequestParam(value = "iId", required = true) Integer iId,
                                                @RequestParam(value = "num", required = true) Integer num){
        Result<Object> result = new Result<Object>();
        try {
            boolean flag=oaDatadetailedInstService.updataDetailedIsStats(iId,num);
            if (flag){
                result.setSuccess(true);
            }else {
                result.setSuccess(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


 }

