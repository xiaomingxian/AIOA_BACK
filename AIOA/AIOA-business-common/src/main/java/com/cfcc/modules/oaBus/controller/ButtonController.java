package com.cfcc.modules.oaBus.controller;

import com.cfcc.common.api.vo.Result;
import com.cfcc.common.exception.AIOAException;
import com.cfcc.modules.oaBus.service.ButtonPermissionService;
import com.cfcc.modules.oaBus.service.OaBusDynamicTableService;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/bus/button")
public class ButtonController {


    @Autowired
    private ButtonPermissionService buttonPermissionService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private OaBusDynamicTableService dynamicTableService;


    @GetMapping("getButtons")
    public Result getButtons(String proSetId,
                             String table,
                             String id,
                             String taskDef,
                             String proInstanId,
                             String taskId,
                             String status,
                             HttpServletRequest request) {

        try {
            HashMap<String, Object> query = new HashMap<>();
            query.put("table", table);
            query.put("i_id", id);
            Map<String, Object> oaBusdata = dynamicTableService.queryDataById(query);
            String proKey=  oaBusdata.get("s_cur_proc_name")==null?null:oaBusdata.get("s_cur_proc_name").toString();

            LoginInfo loginInfo = userService.getLoginInfo(request);

            Map<String, Boolean> currentUserPermission = buttonPermissionService.currentUserPermission(proKey, oaBusdata, loginInfo,
                    taskDef, proInstanId, taskId, status);


            Map<String, Object> result = new HashMap<>();
            result.put("proSetId", proSetId);
            result.put("loginInfo", loginInfo);
            result.put("taskDefKey", taskDef);
            result.put("oaBusdata", oaBusdata);
            Map<String, Object> btns = buttonPermissionService.getBtnAndOpt(result, currentUserPermission);

            return Result.ok(btns);
        } catch (AIOAException e
        ) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询按钮失败");
        }
    }


}
