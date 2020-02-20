package com.cfcc.modules.oaBus.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.service.IBusFunctionPermitService;
import com.cfcc.modules.oaBus.service.IBusFunctionService;
import com.cfcc.modules.oaBus.service.IBusModelService;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.entity.SysUserSet;
import com.cfcc.modules.system.service.ISysUserService;
import com.cfcc.modules.system.service.ISysUserSetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "页面查询")
@Slf4j
@RestController
@RequestMapping("/oaBus/homeAnalysis")
public class HomeAnalysis {
    @Autowired
    private ISysUserSetService sysUserSetService;

    @Autowired
    private IBusModelService iBusModelService;

    @Autowired
    private ISysUserService iSysUserService;

    @Autowired
    private IBusFunctionService iBusFunctionService;

    @Autowired
    private IOaBusdataService oaBusdataService;;

    @Autowired
    private IBusFunctionPermitService iBusFunctionPermitService;

    @Value("${home.model}")
    private String busFunction;
    /**
     * 传统首页查询
     * @param
     * @param
     * @return
     */
    @ApiOperation("dasdasdads")
    @GetMapping(value = "/HomeList")
    public Map<String, Map<String,Object>> HomeList(@RequestParam(name="userId",required=false)String userId,@RequestParam(name="createTime",required=false)String createTime) {
        String[] modellist = busFunction.split(",");  //切割配置文件里面的id
        List<SysUserSet> userSetList= sysUserSetService.findList();
        SysUser sysUser = iSysUserService.getById(userId);
        String schema= MycatSchema.getSchema();
        String username = sysUser.getUsername();
        String schema1 = MycatSchema.getSchema();
        Map<String,Map<String,Object>> map =new HashMap<String,Map<String,Object>>();
            for (int i = 0; i <userSetList.size() ; i++) {
                SysUserSet userSet = userSetList.get(i);
                if(userId.equals(userSet.getSUserId())){
                    List<BusFunction> busFunctionlList = iBusFunctionService.findList(schema1);
                    for (BusFunction busFunction :busFunctionlList) {
                        Integer iBus1Id = userSet.getIBus1Id();
                        Integer iBus2Id = userSet.getIBus2Id();
                        Integer iBus3Id = userSet.getIBus3Id();
                        Integer iBus4Id = userSet.getIBus4Id();

                        if( iBus1Id == null ?(modellist.length >0 && StringUtils.equals(busFunction.getIId().toString(),modellist[0])) : busFunction.getIId() == iBus1Id )
                        {
                            getIdAndSname(busFunction,map,"model1",username,createTime);
                        }
                        if( iBus2Id == null ?(modellist.length >1 && StringUtils.equals(busFunction.getIId().toString(),modellist[1])) : busFunction.getIId() == iBus2Id)
                        {
                            getIdAndSname(busFunction,map,"model2",username,createTime);
                        }if( iBus3Id == null ?(modellist.length >2 && StringUtils.equals(busFunction.getIId().toString(),modellist[2])) : busFunction.getIId() == iBus3Id)
                        {
                            getIdAndSname(busFunction,map,"model3",username,createTime);
                        }if( iBus4Id == null ?(modellist.length >3 && StringUtils.equals(busFunction.getIId().toString(),modellist[3])) : busFunction.getIId() == iBus4Id)
                        {
                            getIdAndSname(busFunction,map,"model4",username,createTime);
                        }

                    }
                    break;
                }else{
                    List<BusFunction> busFunctionlList = iBusFunctionService.findList(schema);
                    for (BusFunction busFunction :busFunctionlList) {
                        Integer iBus1Id = null;
                        Integer iBus2Id = null;
                        Integer iBus3Id = null;
                        Integer iBus4Id = null;

                        if( iBus1Id == null ?(modellist.length >0 && StringUtils.equals(busFunction.getIId().toString(),modellist[0])) : busFunction.getIId() == iBus1Id )
                        {
                            getIdAndSname(busFunction,map,"model1",username,createTime);
                        }
                        if( iBus2Id == null ?(modellist.length >1 && StringUtils.equals(busFunction.getIId().toString(),modellist[1])) : busFunction.getIId() == iBus2Id)
                        {
                            getIdAndSname(busFunction,map,"model2",username,createTime);
                        }if( iBus3Id == null ?(modellist.length >2 && StringUtils.equals(busFunction.getIId().toString(),modellist[2])) : busFunction.getIId() == iBus3Id)
                        {
                            getIdAndSname(busFunction,map,"model3",username,createTime);
                        }if( iBus4Id == null ?(modellist.length >3 && StringUtils.equals(busFunction.getIId().toString(),modellist[3])) : busFunction.getIId() == iBus4Id)
                        {
                            getIdAndSname(busFunction,map,"model4",username,createTime);
                        }

                    }
                }


        }

        return map;
    }
    //获取
    private void getIdAndSname(BusFunction busFunction , Map<String,Map<String,Object>> map , String str ,String username,String createTime){
        Map<String,Object> busMap = new HashMap<>();
        BusFunction function = iBusFunctionService.getById(busFunction.getIId());
            List<Map<String, Object>> oaList = new ArrayList<>() ;
            String url = "";
            StringBuffer strBuf = new StringBuffer("") ;
            /*{"modelId":"1","condition":{"function_id":"","i_is_state":"","selType":1,"s_create_name":"","d_create_time":""}}*/
            strBuf.append("{\"modelId\":");
            strBuf.append(function.getIBusModelId()) ;
            strBuf.append(",\"pageSize\":");
            strBuf.append(10);
            strBuf.append(",\"pageNo\":");
            strBuf.append(1);
            strBuf.append(",\"condition\":{") ;
            strBuf.append("\"function_id\":") ;
            strBuf.append(busFunction.getIId()) ;
            strBuf.append(",\"d_create_time\":\"" + createTime + "\"") ;
            strBuf.append("}} ") ;
            Result<IPage<Map<String, Object>>> byModelId = oaBusdataService.getByModelId(strBuf.toString(), username, username);
            log.info(byModelId.toString());
            List<Map<String, Object>> oaList1 = byModelId.getResult().getRecords() ;
            oaList.addAll(oaList1 ) ;
            String TableName= iBusFunctionPermitService.findTabelName(function.getIBusModelId());
            String sName = busFunction.getSName();
            if(function.getIBusModelId()==1){ //发文管理
                url ="ioaBus/busModel/sendFile";
            } /*if(function.getIBusModelId()==2){ //签报管理
                url ="ioaBus/busModel/sendFile";
            }*/ if(function.getIBusModelId()==3){ //收文管理
                url ="ioaBus/busModel/receiveFile";
            } /*if(function.getIBusModelId()==4){ //司局收文
                url ="ioaBus/busModel/sendFile";
            } if(function.getIBusModelId()==5){ //县行收文
                url ="ioaBus/busModel/sendFile";
            }*/if(function.getIBusModelId()==6){ //公文传阅
                url ="oaBus/insideReading";
            }/*if(function.getIBusModelId()==7){ //工作简报
                url ="ioaBus/busModel/sendFile";
            }if(function.getIBusModelId()==8){ //工作周报
                url ="ioaBus/busModel/sendFile";
            }*/if(function.getIBusModelId()==16){ //会议管理
                url ="meetingManage/meeting";
            }if(function.getIBusModelId()==17){ //综合管理
                url ="synthesize/synthesizeFile";
            }if(function.getIBusModelId()==49){ //公共信息
                url ="publicMessage/electronicFile";
            }
            busMap.put("list",oaList) ;
            busMap.put("url",url);
            busMap.put("sName",sName);
            busMap.put("tableName",TableName);
            map.put(str,busMap);
    }
}
