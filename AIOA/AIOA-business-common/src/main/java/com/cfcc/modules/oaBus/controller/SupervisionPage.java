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
import com.cfcc.modules.system.service.ISysUserService;
import com.cfcc.modules.system.service.ISysUserSetService;
import com.cfcc.modules.utils.IWfConstant;
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
        map.put("total",total);
        return map;
    }
    /**
     *主办数量和部门
     * @return
     */
    @GetMapping(value = "/HostNum")
    public Map<String,Object> HostNum(HttpServletRequest request) {
        //查询当前用户，作为assignee
        LoginInfo loginInfo = iSysUserService.getLoginInfo(request);
        Map<String,Object>  map = new HashMap<>();
        String UserId = loginInfo.getId();
        String parentId = loginInfo.getDepart().getParentId();
        String table = "oa_busdata11";
        int year = DateUtils.getYear();
        Map<String, Object> depart = oaDatadetailedInstService.findPret(parentId);
        List<Map<String, Object>> typeNum = oaDatadetailedInstService.findTypeNum(table, UserId, year, parentId);
        map.put("depart",depart);
        map.put("typeNum",typeNum);
        return map;
    }
 /**
     *协办数量和部门
     * @return
     */
    @GetMapping(value = "organizeNum")
    public Map<String,Object> organizeNum(HttpServletRequest request) {
        //查询当前用户，作为assignee
        LoginInfo loginInfo = iSysUserService.getLoginInfo(request);
        Map<String,Object>  map = new HashMap<>();
        String UserId = loginInfo.getId();
        String parentId = loginInfo.getDepart().getParentId();
        String table = "oa_busdata11";
        int year = DateUtils.getYear();
        Map<String, Object> depart = oaDatadetailedInstService.findPret(parentId);
        List<Map<String, Object>> typeNum = oaDatadetailedInstService.findorganizeNum(table, UserId, year, parentId);
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= depart.size(); i++) {
            Boolean b = true;
            for (int j = 0; j < typeNum.size(); j++) {
                if (depart.get("depart_name").equals(typeNum.get(j).get("organize"))) {
                    b = false;
                }
            }
            if (b) {
                list.add(i);
            }
        }
        list.forEach(i -> {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("organize",depart.get("depart_name") );
            map1.put("num", 0);
            typeNum.add(map1);
        });
        map.put("depart",depart);
        map.put("typeNum",typeNum);
        return map;
    }
    /**
     *行领导批示办结率
     * @return
     */
    @GetMapping(value = "lineLeaderRate")
    public Map<String,Object> lineLeaderRate() {
        String table = "oa_busdata11";
        Integer busModelId = 51;
        Integer busFunctionId = 163;
        Map<String, Object> rate = new HashMap<>();
        rate = oaDatadetailedInstService.lineLeaderRate(table,busModelId,busFunctionId);
        if(rate == null){
            rate.put("rate",0);
        }
        return rate;
    } /**
     *办结率
     * @return
     */
    @GetMapping(value = "Rate")
    public Map<String,Object> Rate() {
        String table = "oa_busdata11";
        Integer busModelId = 51;
        Map<String, Object> rate = new HashMap<>();
         rate = oaDatadetailedInstService.Rate(table,busModelId);
         if(rate == null){
             rate.put("rate",0);
         }
        return rate;
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

}
