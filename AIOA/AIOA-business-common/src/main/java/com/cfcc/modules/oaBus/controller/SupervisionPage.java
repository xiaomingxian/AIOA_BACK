package com.cfcc.modules.oaBus.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.common.util.DateUtils;
import com.cfcc.modules.oaBus.entity.BusFunction;
import com.cfcc.modules.oaBus.service.IBusFunctionPermitService;
import com.cfcc.modules.oaBus.service.IBusFunctionService;
import com.cfcc.modules.oaBus.service.IBusModelService;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.cfcc.modules.oadatafetailedinst.entity.OaDatadetailedInst;
import com.cfcc.modules.oadatafetailedinst.service.IOaDatadetailedInstService;
import com.cfcc.modules.system.entity.*;
import com.cfcc.modules.system.service.ISysDepartService;
import com.cfcc.modules.system.service.ISysDictService;
import com.cfcc.modules.system.service.ISysUserService;
import com.cfcc.modules.system.service.ISysUserSetService;
import com.cfcc.modules.utils.IWfConstant;
import com.cfcc.modules.workflow.pojo.TaskProcess;
import com.cfcc.modules.workflow.service.DepartWithTaskService;
import com.cfcc.modules.workflow.service.TaskCommonService;
import com.cfcc.modules.workflow.vo.TaskInfoVO;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "督办页面查询")
@Slf4j
@RestController
@RequestMapping("/oaBus/supervisionPage")
public class SupervisionPage {
    @Autowired
    private ISysUserService iSysUserService;

    @Autowired
    private IOaBusdataService oaBusdataService;

    @Autowired
    private TaskCommonService taskCommonService;

    @Autowired
    private IOaDatadetailedInstService oaDatadetailedInstService;

    @Autowired
    private DepartWithTaskService departWithTaskService;

    @Autowired
    private ISysDictService sysDictService;

    @Autowired
    private ISysDepartService iSysDepartService;
    /**
     * 督办件
     * @param
     * @param
     * @return
     */
    @GetMapping(value = "/SupervisorSum")
    public Map<String,Object> SupervisorSum(HttpServletRequest request) {
        Map<String,Object> map = new HashMap<>();
        StringBuffer strBuf = new StringBuffer("") ;
        //查询当前用户，作为assignee
        LoginInfo loginInfo = iSysUserService.getLoginInfo(request);
        String username = loginInfo.getUsername();
        String realname = loginInfo.getRealname();
        Integer modelId = 51;
        Integer functionId = 159;
        /*{"modelId":"1","condition":{"function_id":"","i_is_state":"","selType":1,"s_create_name":"","d_create_time":""}}*/
        strBuf.append("{\"modelId\":");
        strBuf.append(modelId) ;
        strBuf.append(",\"pageSize\":");
        strBuf.append(10);
        strBuf.append(",\"pageNo\":");
        strBuf.append(1);
        strBuf.append(",\"condition\":{") ;
        strBuf.append("\"function_id\":") ;
        strBuf.append(functionId) ;
        strBuf.append("}} ") ;
        Result<IPage<Map<String, Object>>> byModelId = oaBusdataService.getByModelId(strBuf.toString(), realname, username);
        long total = byModelId.getResult().getTotal();
        String table = "oa_busdata11";
        Map<String, Object> hangrate = oaDatadetailedInstService.lineLeaderRate(table,modelId,functionId);
        Map<String, Object> rate = oaDatadetailedInstService.Rate(table, modelId);
        if(rate == null){
            map.put("rate",0);
        }else{
            map.put("rate",rate.get("rate"));
        }
        if(hangrate == null){
            map.put("hangrate",0);
        }else{
            map.put("hangrate",hangrate.get("rate"));
        }
        map.put("total",total);
        return map;
    }
    /**
     *主办数量和部门
     * @return
     */
    @GetMapping(value = "/HostNum")
    public List<Map<String,Object>> HostNum(HttpServletRequest request) {
        //查询当前用户，作为assignee
        LoginInfo loginInfo = iSysUserService.getLoginInfo(request);
        List<Map<String,Object>> list = new ArrayList<>();
        String UserId = loginInfo.getId();
        String parentId = loginInfo.getDepart().getParentId();
        String table = "oa_busdata11";
        int year = DateUtils.getYear();
        List<Map<String, Object>> depart = oaDatadetailedInstService.findPret(parentId);
        List<Map<String, Object>> typeNum = oaDatadetailedInstService.findTypeNum(table, UserId, year, parentId);
        String depart_name = null ;
        for(int i=0;i<depart.size();i++){
            boolean flag = false ;
            Map<String,Object> map =new HashMap<>();
            int index = -1 ;
            for(int j=0;j<typeNum.size();j++){
                depart_name = (String)depart.get(i).get("depart_name");
                String organize = (String)typeNum.get(j).get("host");
                if(depart_name.equals(organize)){
                    flag = true ;
                    index = j ;
                    break ;
                }
            }
            if(flag){
                map.put("departName",depart_name);
                map.put("count",typeNum.get(index).get("num")) ;
                list.add(map);
            }else{
                map.put("departName",depart_name);
                map.put("count",0) ;
                list.add(map);
            }
        }
        return list;
    }
 /**
     *协办数量和部门
     * @return
     */
    @GetMapping(value = "organizeNum")
    public List<Map<String,Object>> organizeNum(HttpServletRequest request) {
        //查询当前用户，作为assignee
        LoginInfo loginInfo = iSysUserService.getLoginInfo(request);
        List<Map<String,Object>> list = new ArrayList<>();
        String UserId = loginInfo.getId();
        String parentId = loginInfo.getDepart().getParentId();
        String table = "oa_busdata11";
        int year = DateUtils.getYear();
        List<Map<String, Object>> depart = oaDatadetailedInstService.findPret(parentId); //部门
        List<Map<String, Object>> typeNum = oaDatadetailedInstService.findorganizeNum(table, UserId, year, parentId);
        String depart_name = null ;
        for(int i=0;i<depart.size();i++){
            depart_name = (String)depart.get(i).get("depart_name");
            boolean flag = false ;
            Map<String,Object> map =new HashMap<>();
            int index = -1 ;
            for(int j=0;j<typeNum.size();j++){
                String organize = (String)typeNum.get(j).get("organize");
                if(depart_name.equals(organize)){
                    flag = true ;
                    index = j ;
                    break ;
                }
            }
            if(flag){
                map.put("departName",depart_name);
                map.put("count",typeNum.get(index).get("num")) ;
                list.add(map);
            }else{
                map.put("departName",depart_name);
                map.put("count",0) ;
                list.add(map);
            }
        }
        return list;
    }
    /**
     *办结数量
     * @return
     */
    @GetMapping(value = "RateNum")
    public List<Map<String,Object>> RateNum(HttpServletRequest request) {
        LoginInfo loginInfo = iSysUserService.getLoginInfo(request);
        List<Map<String,Object>> list = new ArrayList<>();
        String parentId = loginInfo.getDepart().getParentId();
        String table = "oa_busdata11";
        int year = DateUtils.getYear();
        List<Map<String, Object>> depart = oaDatadetailedInstService.findPret(parentId); //部门
        String depart_name = null;
        Integer busModelId = 51;
        List<String> functionIds = oaDatadetailedInstService.findFunctionIds(busModelId);
        List<Map<String, Integer>> stringIntegerMap = departWithTaskService.deptDone(functionIds);
        for(int i=0;i<depart.size();i++){
            depart_name = (String)depart.get(i).get("depart_name");
            boolean flag = false ;
            Map<String,Object> map =new HashMap<>();
            int index = -1 ;
            for(int j=0;j<stringIntegerMap.size();j++){
                Integer departId = stringIntegerMap.get(i).get("did");
                String departName = oaDatadetailedInstService.findById(departId);
                if(depart_name.equals(departName)){
                    flag = true ;
                    index = j ;
                    break ;
                }
            }
            if(flag){
                map.put("departName",depart_name);
                map.put("count",stringIntegerMap.get(index).get("num")) ;
                list.add(map);
            }else{
                map.put("departName",depart_name);
                map.put("count",0) ;
                list.add(map);
            }
        }

        return list;
    }
    /**
     *延期数量
     * @return
     */
    @GetMapping(value = "ExtensionsNum")
    public List<Map<String,Object>> ExtensionsNum(HttpServletRequest request) {
        LoginInfo loginInfo = iSysUserService.getLoginInfo(request);
        List<Map<String,Object>> list = new ArrayList<>();
        String parentId = loginInfo.getDepart().getParentId();
        String table = "oa_busdata11";
        int year = DateUtils.getYear();
        List<Map<String, Object>> depart = oaDatadetailedInstService.findPret(parentId); //部门
        String depart_name = null ;
        Integer busModelId = 51;
        List<Map<String,Object>> extensionsNum = oaDatadetailedInstService.findExtensionsNum(busModelId,table);
        for(int i=0;i<depart.size();i++){
            depart_name = (String)depart.get(i).get("depart_name");
            boolean flag = false ;
            Map<String,Object> map =new HashMap<>();
            int index = -1 ;
            for(int j=0;j<extensionsNum.size();j++){
                String s_create_dept =(String) extensionsNum.get(j).get("s_create_dept");
                if(depart_name.equals(s_create_dept)){
                    flag = true ;
                    index = j ;
                    break ;
                }
            }
            if(flag){
                map.put("departName",depart_name);
                map.put("count",extensionsNum.get(index).get("num")) ;
                list.add(map);
            }else{
                map.put("departName",depart_name);
                map.put("count",0) ;
                list.add(map);
            }
        }
        return list;
    }
    @GetMapping("queryTask")
    @ApiOperation("任务查询")
    public Result queryTask(TaskInfoVO taskInfoVO, @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                            HttpServletRequest request) {
        try {

            LoginInfo loginInfo = iSysUserService.getLoginInfo(request);
            String operstatus = taskInfoVO.getOperstatus();

            //判断查询条件是否有用户(作为查询条件)
            if (taskInfoVO.getUserId() != null) {
                //有任何一个就去填充完整
                if (taskInfoVO.getUserName() == null)
                    taskInfoVO.setUserName(iSysUserService.selectUserNameById(taskInfoVO.getUserId()));
            } else {
                //查询当前用户，作为assignee
                if (operstatus != null && !operstatus.equals(IWfConstant.JUMP)) {//重置是管理员权限(不用必须加用户)

                    taskInfoVO.setUserId(loginInfo.getId());
                    taskInfoVO.setUserName(loginInfo.getUsername());
                }
            }


            Result result = null;
            Integer modelId = 51;
            List<String> functionIds = oaDatadetailedInstService.findFunctionIds(modelId);
            taskInfoVO.setFunctionIds(functionIds);
            switch (operstatus) {
                //待办(部门类型已加)
                case IWfConstant.TASK_TODO:
                    result = taskCommonService.queryTaskToDo(taskInfoVO, pageNo, pageSize);
                    break;
                //已办
                case IWfConstant.TASK_DONE:
                    result = taskCommonService.queryTaskDone(taskInfoVO, pageNo, pageSize);
                    break;
                //流程监控数据
                case IWfConstant.TASK_MONITOR:
                    //判断是不是超级管理员 是的话展示所有人
                    List<SysRole> roles = loginInfo.getRoles();
                    boolean isAdmin = false;
                    for (SysRole role : roles) {
                        String roleName = role.getRoleName();
                        if ("系统管理员".equalsIgnoreCase(roleName)) {
                            isAdmin = true;
                            break;
                        }
                    }
                    result = taskCommonService.queryTaskMonitor(taskInfoVO, pageNo, pageSize, isAdmin);
                    break;
                //我的委托
                case IWfConstant.MY_AGENT:
                    result = taskCommonService.queryTaskMyAgent(taskInfoVO, pageNo, pageSize);
                    break;
                //重置(所有未完成-不区分用户[查询条件可以用区分])
                case IWfConstant.JUMP:
                    result = taskCommonService.queryTaskToDo(taskInfoVO, pageNo, pageSize);
                    break;
                case IWfConstant.SHHIFT://任务移交-以人为准
                    result = taskCommonService.queryTaskShift(taskInfoVO, pageNo, pageSize);
                    break;
            }
            return result;
        } catch (
                Exception e) {
            log.error("任务查询失败" + e.toString());
            return Result.error("任务查询失败");
        }

    }
    /**
     *分析维度
     * @return
     */
    @GetMapping(value = "/AnalysisDimension")
    public List<Map<String,Object>> AnalysisDimension(HttpServletRequest request) {
       // List<Map<String,Object>> list = new ArrayList<>();
        String table = "oa_busdata11";
        int year = DateUtils.getYear();
        //数据字典获取functionid
        SysUser currentUser = iSysUserService.getCurrentUser(request);
        Map<String, Object> allUserMsg = iSysUserService.getAllUserMsg(currentUser.getUsername());
        String parentId = (String)allUserMsg.get("parentId");//机构id
        String deptId = allUserMsg.get("deptId") + ""; //部门id
        List<SysRole> role = (List<SysRole>)allUserMsg.get("role");
        for(int i=0;i<role.size();i++){
            String roleCode = role.get(i).getRoleCode();

            if(roleCode.equals(""))//督办员
            {

            }else if(roleCode.equals("")) //行领导
            {

            }else if(roleCode.equals("308")){//部门负责人

            }else if(roleCode.equals("010")){//一般人员

            }
        }
        String dictKey = "sup_parameter"; //数据字典-督办
        List<Map<String, Object>> list = sysDictService.getDictByKeySer(dictKey, deptId);
        return list;
    }

}
