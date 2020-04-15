package com.cfcc.modules.oaBus.controller;

import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.exception.AIOAException;
import com.cfcc.modules.oaBus.mapper.OaBusDynamicTableMapper;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Description: 业务数据表
 * @Author: jeecg-boot
 * @Date: 2019-10-21
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "业务数据表")
@RestController
@RequestMapping("/registerNoShiro")
public class OaBusdataControllerNoShiro {
    @Autowired
    private IOaBusdataService oaBusdataService;
    @Autowired
    private ISysUserService isysUserService;
    @Value("${system.runDate}")
    private String runDate;

    @Autowired
    private OaBusDynamicTableMapper dynamicTableMapper;

    @GetMapping("insertData")
    public Result insertData(String enName) {
        try {
            //
            if (StringUtils.isBlank(enName)) return Result.error("传入参数为空,请检查参数信息");
            Map<String, Object> modelAndFunction = dynamicTableMapper.queryModelAndFunIdByEnName(enName);
            if (modelAndFunction == null) return Result.error("此项业务未配置,请检查数据信息");
            String mid = modelAndFunction.get("mid")==null?"":modelAndFunction.get("mid")+"";
            if (StringUtils.isBlank(mid)) return Result.error("未配置model信息");
            String fid = modelAndFunction.get("fid")==null?"":modelAndFunction.get("fid")+"";
            if (StringUtils.isBlank(fid)) return Result.error("未配置function信息");

            Result res = oaBusdataService.queryDataByModelAndFunctionId(mid, fid, null);

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建临时数据失败");
        }

    }


    @PostMapping("updata")
    public Result updata(@RequestBody Map<String,Object> map){
        try {

            dynamicTableMapper.updateData(map);
            return Result.ok("注册成功");
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("注册失败");
        }

    }


    /**
     * 查询出具体的业务数据
     *
     * @param
     * @return
     */
    @AutoLog(value = "业务数据表-查询出具体的业务数据")
    @ApiOperation(value = "业务数据表-查询出具体的业务数据", notes = "业务数据表-查询出具体的业务数据")
    @PostMapping(value = "/queryBusdataById")
    @ResponseBody
    public Result queryBusdataById(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        try {
            // 查询当前用户，作为assignee
            LoginInfo loginInfo = isysUserService.getLoginInfo(request);

            // 查询结果
            Map<String, Object> result = oaBusdataService.getBusDataAndDetailById(map, loginInfo);
            if (result.containsKey("error")) {
                return Result.error(result.get("error").toString());
            }
            return Result.ok(result);
        } catch (AIOAException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.toString());
            e.printStackTrace();
            return Result.error("查询业务详情失败");
        }

    }


    @AutoLog(value = "新建临时任务")
    @ApiOperation(value = "新建临时任务", notes = "新建临时任务")
    @PostMapping(value = "/queryNewTaskMsg")
    public Result queryDataByModelAndFunctionId(@RequestBody Map<String, Object> map, HttpServletRequest request) {

        try {
            String modelId = map.get("modelId") + "";
            String functionId = map.get("functionId") + "";
            //SysUser currentUser = isysUserService.getCurrentUser(request);
            LoginInfo loginInfo = isysUserService.getLoginInfo(request);
            Result res = oaBusdataService.queryDataByModelAndFunctionId(modelId, functionId, loginInfo);

            return res;
        } catch (AIOAException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("新增数据失败:" + e.toString());
            return Result.error("新增数据失败");
        }
    }


}
