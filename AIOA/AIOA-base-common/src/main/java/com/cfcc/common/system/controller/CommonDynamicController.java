package com.cfcc.common.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.system.service.CommonDynamicTableService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("common/dynamicTable")
public class CommonDynamicController {

    @Autowired
    private CommonDynamicTableService dynamicTableService;


    @ApiOperation(value = "动态分页查询(支持 equals/like)")
    @PostMapping("query")
    public Result<IPage<Map<String, Object>>> test(@RequestBody Map<String, Object> map, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Result<IPage<Map<String, Object>>> result = new Result<>();
        try {
            IPage<Map<String, Object>> page = dynamicTableService.queryByEquals(pageNo, pageSize, map);
            result.setResult(page);
        } catch (Exception e) {
            log.error(e.toString());
            result.setSuccess(false);
            result.setMessage("业务数据查询失败");
        }
        return result;
    }

    @PostMapping("insert")
    @ApiOperation("insert")
    public Result insert(@RequestBody Map<String, Object> map) {
        try {
            //提取字段-排除多余字段
            Map<String, Object> mapHaveId = dynamicTableService.insertData(map);
            return Result.ok(mapHaveId);
        } catch (Exception e) {

            e.printStackTrace();
            return Result.error("插入数据失败");
        }
    }


}
