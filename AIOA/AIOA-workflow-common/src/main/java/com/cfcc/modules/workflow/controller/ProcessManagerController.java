package com.cfcc.modules.workflow.controller;

import com.cfcc.common.api.vo.Result;
import com.cfcc.common.exception.AIOAException;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.common.util.norepeat.NoRepeatSubmit;
import com.cfcc.modules.workflow.pojo.Activity;
import com.cfcc.modules.workflow.service.ProcessManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Api(tags = "工作流：通用流程管理")
@RestController
@RequestMapping("wf/process")
@Slf4j
@CrossOrigin
public class ProcessManagerController {
    @Autowired
    private ProcessManagerService processManagerService;


    @GetMapping("queryTaskDefkeys")
    public Result queryTaskDefkeys(String key) {
        List<String> defKeys = processManagerService.queryTaskDefkeys(key);

        return Result.ok(defKeys);
    }


    @PostMapping("deploy")
    @ApiOperation("发布流程")
    @NoRepeatSubmit
    public Result deploy(@RequestParam(value = "file", required = false) MultipartFile[] files) {


        if (files == null || (files != null && files.length <= 0)) {
            return Result.error("流程图上传未成功");
        }


        String schema = MycatSchema.getSchema();

        try {
            processManagerService.deploy(files, schema);

        } catch (AIOAException e) {
            log.error(e.toString());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error("流程发布失败");
        }

        return Result.ok("流程发布成功");
    }


    @GetMapping("defKv")
    @ApiOperation("查询所有流程定义k-V")
    public Map<String, String> actKV() {
        String schema = MycatSchema.getSchema();
        Map<String, String> actKv = processManagerService.defKv(schema);
        return actKv;
    }


    @GetMapping("processQuery")
    @ApiOperation("流程查询")
    public Result processQuery(ProcessDefinition vo, @RequestParam(defaultValue = "1") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize) {
        return processManagerService.processQuery(vo, pageNo, pageSize);
    }

    @GetMapping("lastVersionProc")
    @ApiOperation("流程查询")
    public Result lastVersionProc(String key) {
        try {
            return processManagerService.lastVersionProc(key);
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error("流程查询失败");
        }
    }

    @DeleteMapping("processDel")
    @ApiOperation("流程删除")
    @NoRepeatSubmit
    public Result processDel(String[] ids, @RequestParam(defaultValue = "false") boolean cascade) {
        String schema = MycatSchema.getSchema();
        return processManagerService.processDel(ids, cascade, schema);
    }


    @GetMapping("activeProcess")
    @ApiOperation("激活流程")
    @NoRepeatSubmit
    public Result activeProcess(ProcessDefinition vo) {
        String schema = MycatSchema.getSchema();

        return processManagerService.activeProcess(vo, schema);
    }

    @GetMapping("disactiveProcess")
    @ApiOperation("挂起流程")
    @NoRepeatSubmit
    public Result disactiveProcess(ProcessDefinition vo) {
        String schema = MycatSchema.getSchema();

        return processManagerService.disactiveProcess(vo, schema);
    }


    @ApiOperation("流程资源查询")
    @GetMapping(value = "/resource/read")
    public void loadByDeployment(@RequestParam("processDefinitionId") String processDefinitionId,
                                 @RequestParam("resourceType") String resourceType, HttpServletResponse response) {
        processManagerService.loadByDeployment(processDefinitionId, resourceType, response);
    }

    @ApiOperation("读取xml")
    @GetMapping(value = "/resource/xml")
    public Result readXml(@RequestParam("processDefinitionId") String processDefinitionId, HttpServletResponse response) {

        String xml = null;
        try {
            xml = processManagerService.loadByDeploymentXml(processDefinitionId);
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error("查询xml出错");
        }
        return Result.ok(xml);
    }

    @ApiOperation("保存xml")
    @PostMapping(value = "/xml/save")
    public Result saveXml(@RequestBody Map<String, String> map, HttpServletRequest request) {

        try {
            String name = map.get("name");
            String xml = map.get("xml");
            processManagerService.saveXml(name, xml);
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error("更新xml出错");
        }
        return Result.ok("更新xml成功");
    }


    @ApiOperation("节点列表")
    @GetMapping("actsList")
    public Result actsList(@RequestParam(value = "processDefinitionId", required = false) String processDefinitionId
            , @RequestParam(value = "key", required = false) String key) {
        try {
            List<Activity> acts = processManagerService.actsListPic(processDefinitionId, key, true);//展示所有节点
            //加一个结束节点 与流程无关 用于展示半截后的按钮配置
            Activity activity = new Activity();
            activity.setId("end");
            activity.setName("办结");
            activity.setProcDefKey(acts.get(0).getProcDefKey());
            activity.setType("AIOATYPE");
            acts.add(activity);


            return Result.ok(acts);
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error("查询节点失败");
        }
    }


    @GetMapping("queryActNameByKey")
    public Result queryActNameByKey(String key) {
        try {
            String name = processManagerService.queryActNameByKey(key);
            return Result.ok(name);
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error("查询失败");
        }
    }

}
