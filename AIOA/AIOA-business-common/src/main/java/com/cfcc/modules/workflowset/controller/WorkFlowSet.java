package com.cfcc.modules.workflowset.controller;

import com.cfcc.common.api.vo.Result;
import com.cfcc.common.exception.AIOAException;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.modules.workflowset.entity.CopyMsg;
import com.cfcc.modules.workflowset.service.WorkFlowSetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@Api(tags = "流程设置相关")
@RestController
@RequestMapping("/workflowSet")
public class WorkFlowSet {

    @Autowired
    private WorkFlowSetService workFlowSetService;


    @PostMapping("copy")
    @ApiOperation("复制流程")
    public Result copy(CopyMsg copyMsg) {
        //校验名称key是否已用

        try {
            Date date = new Date();
            copyMsg.setCopyKey("process"+date.getTime());
            String schema = MycatSchema.getSchema();
            workFlowSetService.copy(copyMsg,schema);

        } catch (AIOAException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("复制失败");
        }
        return Result.ok("复制成功");
    }


}
