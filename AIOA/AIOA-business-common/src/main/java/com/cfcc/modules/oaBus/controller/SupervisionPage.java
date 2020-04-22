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
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysDepart;
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

}
