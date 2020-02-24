package com.cfcc.modules.oaBus.controller;

import com.cfcc.common.api.vo.Result;
import com.cfcc.common.aspect.annotation.AutoLog;
import com.cfcc.common.exception.AIOAException;
import com.cfcc.modules.oaBus.entity.OaBusdataOpinion;
import com.cfcc.modules.oaBus.service.ButtonPermissionService;
import com.cfcc.modules.oaBus.service.IOaBusdataOpinionService;
import com.cfcc.modules.system.entity.LoginInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Description: 业务数据意见表
 * @Author: jeecg-boot
 * @Date: 2019-11-18
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "业务数据意见表")
@RestController
@RequestMapping("/oaBus/oaBusdataOpinion")
public class OaBusdataOpinionController {
    @Autowired
    private IOaBusdataOpinionService oaBusdataOpinionService;

    @Autowired
    private ButtonPermissionService buttonPermissionService;

    @AutoLog(value = "业务按钮-查询意见列表")
    @ApiOperation(value = "业务按钮-查询意见列表", notes = "业务按钮-查询意见列表")
    @GetMapping(value = "/getOpinionList")
    public Result queryOpinion(OaBusdataOpinion oaBusdataOpinion, @RequestParam(name = "table") String table) {
        Result<List<Map<String, Object>>> result = new Result<List<Map<String, Object>>>();
        if (oaBusdataOpinion.getIBusdataId() == null || table == null) {
            result.error500("未找到对应实体");
        } else {
            List<OaBusdataOpinion> list = oaBusdataOpinionService.selectOpinionList(table, oaBusdataOpinion);
            List<String> name = new ArrayList<>();
           Set set = new HashSet();
           for (OaBusdataOpinion opinionName :list){
               if (set.add(opinionName.getOpinionName())){
                   name.add(opinionName.getOpinionName());
               }
           }
            //数组组装
            Map<String, OaBusdataOpinion> map = new HashMap<String, OaBusdataOpinion>();
            Map<String, List<OaBusdataOpinion>> map1 = new HashMap<>();
            for (OaBusdataOpinion oa : list) {
                if (map1.containsKey(oa.getOpinionName())) {
                    map1.get(oa.getOpinionName()).add(oa);
                } else {
                    List<OaBusdataOpinion> valueList = new ArrayList<OaBusdataOpinion>();
                    valueList.add(oa);
                    map1.put(oa.getOpinionName(), valueList);
                }
            }
            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
            int j = 0;
            for (String key : name) {
                Map<String, Object> map2 = new HashMap<>();
                map2.put("name", key);
                List<OaBusdataOpinion> opinon = map1.get(key);
                for (int i = 0; i < opinon.size(); i++) {
                    opinon.get(i).setIId(j++);
                }
                map2.put("oa", opinon);
                data.add(map2);
            }
            if (list.size() > 0) {
                result.success("操作成功!");
                result.setResult(data);
            }
        }
        return result;
    }

    /**
     * 填写意见
     *
     * @param opinion
     * @return
     */
    @AutoLog(value = "业务按钮-填写意见")
    @ApiOperation(value = "业务按钮-填写意见", notes = "业务按钮-填写意见")
    @GetMapping(value = "/putOpinion")
    public Result<OaBusdataOpinion> add(OaBusdataOpinion opinion, @RequestParam("busTable") String busTable) {
        Result<OaBusdataOpinion> result = new Result<OaBusdataOpinion>();
        try {
            boolean ok = oaBusdataOpinionService.putBusOpinion(opinion, busTable);
            if (ok) {
                result.success("添加成功！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    @AutoLog(value = "业务详情-意见刷新")
    @ApiOperation(value = "意见刷新", notes = "意见刷新")
    @PostMapping(value = "/reloadOpinionList")
    @ResponseBody
    public Result reloadOpinionList(@RequestBody Map<String, Object> map) {
        Result result = new Result();
        try {
            List<Map<String, Object>> showOpinion = buttonPermissionService.reloadOpinionList(map);
            result.setResult(showOpinion);
            return result;
        } catch (AIOAException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.toString());
            e.printStackTrace();
            return Result.error("查询业务详情失败");
        }

    }
}
