package com.cfcc.modules.drawing.controller;

import com.cfcc.common.api.vo.Result;
import com.cfcc.modules.drawing.entity.ProcessMsg;
import com.cfcc.modules.drawing.service.ModelerService;
import com.cfcc.modules.workflow.pojo.TaskProcess;
import com.cfcc.modules.workflow.service.DepartWithTaskService;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "工作流模型在线编辑")
@Controller
@RequestMapping("/modeler")
@Slf4j
public class ModelerController {


    @Resource
    private ModelerService modelerService;

    @Autowired
    private DepartWithTaskService departWithTaskService;

    @GetMapping("test1")
    @ResponseBody
    public Result test1(String id){
        List<String> fids = new ArrayList<>();
        fids.add(id);

        Map<String, Integer> count = departWithTaskService.deptDone(fids);
        List<TaskProcess> taskProcesses = departWithTaskService.taskProcess(fids);
        HashMap<String, Object> data = new HashMap<>();
        data.put("count",count);
        data.put("taskProcesses",taskProcesses);
        return  Result.ok(data);
    }

    /**
     * 创建流程模型
     *
     * @return
     */
    @ApiOperation("创建流程模型")
    @PostMapping(value = "/create")
    @ResponseBody
    public Result<String> createModel(@RequestBody ProcessMsg processMsg) {
        Result<String> result = new Result<>();
        try {
            //创建空模型
            String modelId = modelerService.crateModel(processMsg.getName(), processMsg.getKey(), processMsg.getDescription());
            if (StringUtils.isBlank(modelId)) {
                throw new RuntimeException("创建modeler失败modelId:" + modelId);
            }

            result.setSuccess(true);
            result.setMessage(modelId);
            return result;
        } catch (Exception e) {
            log.error(e.toString());
            result.setSuccess(false);
            result.setMessage("创建model失败");
            return result;
        }
    }


    /**
     * 模型列表
     *
     * @param modelAndView
     * @return
     */
    @ApiOperation("模型列表")
    @GetMapping("/model/list")
    public ModelAndView modelList(ModelAndView modelAndView) {
        modelerService.queryModelList();
        return modelAndView;
    }
}