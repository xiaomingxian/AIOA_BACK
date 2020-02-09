package com.cfcc.modules.workflowset.controller;

import com.cfcc.common.api.vo.Result;
import com.cfcc.common.exception.AIOAException;
import com.cfcc.modules.workflowset.service.WorkFlowSetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "流程设置相关")
@RestController
@RequestMapping("/workflowSet")
public class WorkFlowSet {

    @Autowired
    private WorkFlowSetService workFlowSetService;


    @PostMapping("copy")
    @ApiOperation("复制流程")
    @CacheEvict(value = "defKv" , allEntries = true)
    public Result copy(String copyKey, String copyName, String sourceDefId) {
        //校验名称key是否已用

        try {
            workFlowSetService.copy(copyKey, copyName, sourceDefId);

        } catch (AIOAException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("复制失败");
        }
        return Result.ok("复制成功");
    }


}
