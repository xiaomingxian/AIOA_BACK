package com.cfcc.modules.oaBus.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.constant.workflow.TaskConstant;
import com.cfcc.common.exception.AIOAException;
import com.cfcc.common.mycat.MycatSchema;
import com.cfcc.modules.elasticsearch.service.SearchService;
import com.cfcc.modules.oaBus.entity.OaOutLog;
import com.cfcc.modules.oaBus.service.IOaBusdataService;
import com.cfcc.modules.oaBus.service.IOaFileService;
import com.cfcc.modules.oaBus.service.OaBusDynamicTableService;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.entity.SysUser;
import com.cfcc.modules.system.service.ISysDepartService;
import com.cfcc.modules.system.service.ISysUserService;
import com.cfcc.modules.workflow.service.OaBusDataPermitService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("oaBus/dynamic")
public class DynamicTableController {
    @Autowired
    private OaBusDynamicTableService dynamicTableService;

    @Autowired
    private OaBusDataPermitService oaBusDataPermitService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private ISysUserService isysUserService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private IOaBusdataService oaBusdataService;

    @Autowired
    private IOaFileService oaFileService;

    @Autowired
    private ISysDepartService sysDepartService;

    @ApiOperation(value = "动态分页查询(支持 equals/like)")
    @PostMapping("query")
    public Result<IPage<Map<String, Object>>> test(@RequestBody Map<String, Object> map, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Result<IPage<Map<String, Object>>> result = new Result<>();
        try {
            IPage<Map<String, Object>> page = dynamicTableService.queryByEquals(pageNo, pageSize, map);
            result.setResult(page);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("业务数据查询失败");
        }
        return result;
    }

    @ApiOperation(value = "根据id查询")
    @PostMapping("queryById")
    public Result<Map<String, Object>> queryById(@RequestBody Map<String, Object> map) {
        Result<Map<String, Object>> result = new Result<>();
        try {
            Map<String, Object> data = dynamicTableService.queryDataById(map);
            result.setResult(data);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("业务数据查询失败");
        }
        return result;
    }

    @ApiOperation(value = "根据业务id查询模块信息")
    @GetMapping("queryModelByFunctionId")
    public Result<Map<String, Object>> queryModelByFunctionId(String functionId) {
        Result<Map<String, Object>> result = new Result<>();
        try {
            Map<String, Object> data = dynamicTableService.queryModelByFunctionId(functionId);
            result.setResult(data);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("业务数据查询失败");
        }
        return result;
    }


    @PostMapping("insert")
    @ApiOperation("仅插入业务数据")
    public Result insert(@RequestBody Map<String, Object> map) {
        try {
            //提取字段-排除多余字段
            for (String remove : TaskConstant.REMOVEFILEDS) {
                map.remove(remove);
            }
            Map<String, Object> mapHaveId = dynamicTableService.insertData(map);
            return Result.ok(mapHaveId);
        } catch (Exception e) {

            e.printStackTrace();
            return Result.error("插入数据失败");
        }
    }

    /**
     * updateData  普通数据根据 i_id  ，数据中必须包含i_id
     *
     * @param map
     * @return
     */
    @PostMapping("/updateData")
    @ApiOperation("根据ID更新业务数据")
    public Result updateData(@RequestBody Map<String, Object> map) {
        Result result = new Result();
        try {
            if (map.size() <= 0) {
                return Result.error("更新数据失败,请确实数据的完整性！！！");
            }
            int mapHaveId = 0;
            Map<String, Object> busdataMap = new HashMap<>();
            mapHaveId = dynamicTableService.updateData(map);
            if (mapHaveId != 0) {
                Map<String, Object> queryMap = new HashMap<>();
                queryMap.put("i_id", map.get("i_id") + "");
                queryMap.put("table", map.get("table"));
                busdataMap = dynamicTableService.queryDataById(queryMap);
                busdataMap.put("table", map.get("table"));
            }
            result.setResult(busdataMap);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新数据失败");
        }
    }


    /**
     * update 数据根据 i_id  ，数据中必须包含i_id
     *
     * @param map
     * @return
     */
    @PostMapping("/update")
    @ApiOperation("根据ID更新业务数据")
    public Result update(@RequestBody Map<String, Object> map) {
        try {
            //提取字段-排除多余字段
            for (String remove : TaskConstant.REMOVEFILEDS) {
                if (map.containsKey(remove)) {
                    map.remove(remove);
                }
                if (map.containsKey("updateBusdata")) {
                    if (((Map<String, Object>) map.get("updateBusdata")).containsKey(remove)) {
                        ((Map<String, Object>) map.get("updateBusdata")).remove(remove);
                    }
                }
            }
            if (map.size() <= 0) {
                return Result.error("更新数据失败,请确实数据的完整性！！！");
            }

            int mapHaveId = 0;
            if (map.containsKey("updateBusdata")) {
                map = (Map<String, Object>) map.get("updateBusdata");
                mapHaveId = dynamicTableService.updateData(map);
                //System.out.println(map);
            } else {
                mapHaveId = dynamicTableService.updateData(map);
                //System.out.println(map);
            }
            //判断当前数据有没有流程

            dynamicTableService.checkProc(map);


            return Result.ok(mapHaveId);

        } catch (AIOAException e) {
            return Result.error(e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();
            return Result.error("更新数据失败");
        }
    }
    /**
     * update 数据根据 i_id  ，数据中必须包含i_id
     * @param map
     * @return
     */
    @PostMapping("/updateNoCheck")
    @ApiOperation("根据ID更新业务数据")
    public Result updateNoCheck(@RequestBody Map<String, Object> map) {
        try {
            //提取字段-排除多余字段
            for (String remove : TaskConstant.REMOVEFILEDS) {
                if (map.containsKey(remove)) {
                    map.remove(remove);
                }
            }
            if (map.size() <= 0) {
                return Result.error("更新数据失败,请确实数据的完整性！！！");
            }
            int mapHaveId = 0;
            mapHaveId = dynamicTableService.updateData(map);
            return Result.ok(mapHaveId);
        } catch (AIOAException e) {
            return Result.error(e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();
            return Result.error("更新数据失败");
        }
    }

    @PostMapping("/deletePremit")
    @ApiOperation("根据ID更新业务数据")
    public Result deletePremit(@RequestBody Map<String, Object> map) {

        try {
            Map<String, Object> Permitmap = (Map<String, Object>) map.get("deletePremit");
            String table = Permitmap.get("table") + "_permit";
            Integer busdataId = (Integer) Permitmap.get("i_id");
            Integer funcationId = (Integer) Permitmap.get("i_bus_function_id");

            boolean total = oaBusDataPermitService.deleteBusDataPermitByBusDataIdAndFuncationId(table, busdataId, funcationId);

            if (total) {
                table = table.replace("_permit", "");
                Permitmap.put("table", table);
                Permitmap.remove("i_busdata_id");
                Permitmap.remove("i_bus_function_id");
                int updateData = dynamicTableService.updateData(Permitmap);
                if (updateData == 0) {
                    return Result.error("更新失败");
                }
            } else {
                return Result.error("更新失败");
            }
            return Result.ok("删除发布权限表中数据成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除发布权限表中数据失败");
        }

    }


    @PostMapping("/delete")
    @ApiOperation("仅删除业务数据")
    public Result delete(@RequestBody Map<String, Object> map) {
        try {
            boolean ok = dynamicTableService.deleteData(map);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除数据失败");
        }
        return Result.ok("删除成功");
    }

    /**
     * 删除按钮（带流程/不带流程）
     * @param map
     * @return
     */
    @PostMapping("/deleteBusdata")
    @ApiOperation("删除按钮")
    public Result deleteBusdata(@RequestBody Map<String, Object> map,HttpServletRequest request) {
        try {
            boolean ok = dynamicTableService.deleteBusdata(map);
//            Boolean ok = true;
            if (ok){
                String indexName = "";
                String table = map.get("table")+"";
                String tableId = map.get("i_id")+"";
                String id = table+tableId;
                String DBvalue = MycatSchema.getMycatAnnot();
                if (DBvalue != ""){
                    DBvalue = DBvalue.substring(DBvalue.indexOf("=")+1,DBvalue.lastIndexOf("*"));
                }
                SysUser currentUser = sysUserService.getCurrentUser(request);
                String username = currentUser.getUsername();
                //获取用户所属支行id
                String departId = sysUserService.getdeptIdByUser(username);

                //根据oa_busdata表中的id和table去查询数据该业务是否纳入全文检索，
                // 如果纳入了则删除es库中的数据，如果没有纳入，则不会执行删除es数据
//                Boolean flag = oaBusdataService.getFuncitionByBusdata(tableId,table);
//                if (flag){
                    indexName = "elasticsearch"+DBvalue+departId+1;
                    String indexType = "oaBusdata";
                    try {
                        Integer res = searchService.deleteById(id, indexName, indexType);
                        if (res != 200){
                            res = searchService.deleteById(id, indexName, indexType);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                }
                List<Map<String,Object>> oaFileList = oaFileService.getOaFileByIdAndTable(tableId,table);
                if (oaFileList.size() > 0){
                    for (Map<String, Object> oaFileMap : oaFileList) {
                        indexName = "elasticsearch"+DBvalue+departId+2;
                        String indexType2 = "oaFile";
                        id = id+oaFileMap.get("i_id");
                        try {
                            Integer res = searchService.deleteById(id, indexName, indexType2);
                            if (res != 200){
                                res = searchService.deleteById(id, indexName, indexType2);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除数据失败");
        }
        return Result.ok("删除成功");
    }


    @ApiOperation(value = "根据taskid判断填写意见")
    @PostMapping("updateOpinion")
    public Result<Map<String, Object>> updateOpinion(@RequestBody Map<String, Object> map) {
        Result<Map<String, Object>> result = new Result<>();
        try {
            Map<String, Object> data = dynamicTableService.updateOpinionByTaskId(map);
            result.setResult(data);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("更新意见失败");
        }
        return result;
    }

    @ApiOperation(value = "根据用户信息获取下发机构")
    @PostMapping("queryUnitsByUser")
    public Result queryUnitsByUser(HttpServletRequest request) {
        LoginInfo loginInfo = isysUserService.getLoginInfo(request);
        String orgCode = loginInfo.getOrgCode();
        Result<List<Map<String, Object>>> result = new Result<>();
        try {
            List<Map<String, Object>> maps = dynamicTableService.queryUnitsByUser(orgCode);
            result.setResult(maps);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("查询机构失败");
        }
        return result;
    }

    @ApiOperation(value = "上报")
    @PostMapping("upSendFile")
    public Result upSendFile(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        Result result = new Result<>();
        try {
            Map<String, Object> busdata = (Map<String, Object>) map.get("busdata");//业务数据
            LoginInfo loginInfo = isysUserService.getLoginInfo(request);
            SysDepart depart = sysDepartService.getMaxUnitByDeptId(loginInfo.getDepart().getId());
            Map<String, List<Map<String, Object>>> dataMap = dynamicTableService.upSendFile(depart, request);
            result.setResult(dataMap);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("上报失败");
        }
        return result;
    }


    @ApiOperation(value = "下发")
    @PostMapping("downSendFile")
    public Result downSendFile(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        Result result = new Result<>();
        try {
            Map<String, Object> busdata = (Map<String, Object>) map.get("busdata");//业务数据
            List<String> unit = (List<String>) map.get("unit");//机构数据
            Map<String, List<Map<String, Object>>> dataMap = dynamicTableService.downSendData(unit, request);
            result.setResult(dataMap);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("下发失败");
        }
        return result;
    }

    @ApiOperation(value = "业务数据权限写入")
    @PostMapping("insertPermit")
    public Result insertPermit(@RequestBody String param) {
        Result result = new Result<>();
        try {
            dynamicTableService.insertUserPermit(param);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("用户权限写入失败");
        }
        return result;
    }

    @ApiOperation(value = "省地收文")
    @PostMapping("provinceToCity")
    public Result provinceToCity(@RequestBody Map<String,Object> map,HttpServletRequest request) {
        Result result = new Result<>();
        try {
            String ok = dynamicTableService.provinceToCityReceiveFile(map,request);
            if (ok.contains("200")){
                result.setResult(200);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("地市接口调用失败");
        }
        return result;
    }

    @ApiOperation(value = "省会地市收文传输接口")
    @PostMapping("provinceToCityReceviceClient")
    public Result provinceToCityReceviceClient(HttpServletRequest request) {
        Result result = new Result<>();
        try {
            dynamicTableService.provinceToCityClient(request);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("服务调用失败");
        }
        return result;
    }

    @ApiOperation(value = "省地传阅")
    @PostMapping("provinceToCityInside")
    public Result provinceToCityInside(@RequestBody Map<String,Object> map,HttpServletRequest request) {
        Result result = new Result<>();
        try {
            String ok = dynamicTableService.provinceToCityInsideFile(map,request);
            if (ok.contains("200")){
                result.setResult(200);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("地市接口调用失败");
        }
        return result;
    }


    @ApiOperation(value = "省会地市传阅传输接口")
    @PostMapping("provinceToCityInsideClient")
    public Result provinceToCityInsideClient(HttpServletRequest request) {
        Result result = new Result<>();
        try {
            dynamicTableService.provinceToCityInsideClient(request);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("服务调用失败");
        }
        return result;
    }

    @ApiOperation(value = "共享文件")
    @PostMapping("shareFile")
    public Result shareFile(@RequestBody Map<String,Object> map,HttpServletRequest request) {
        Result result = new Result<>();
        LoginInfo loginInfo = isysUserService.getLoginInfo(request);
        try {
            boolean ok = dynamicTableService.shareFile(map,loginInfo,request);
            if (ok){
                result.setResult(ok);
            }else{
                result.setResult(!ok);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("共享失败");
        }
        return result;
    }

    @ApiOperation(value = "新增对外传输数据日志")
    @PostMapping("insertOaOutLog")
    public Result insertOaOutLog(@RequestBody Map<String,Object> map,HttpServletRequest request) {
        Result result = new Result<>();
        try {
             dynamicTableService.insertOaOutLog(map,request);
             result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.error500("新增传输日志失败");
        }
        return result;
    }

    @ApiOperation(value = "根据业务信息查询外传输日志")
    @GetMapping("queryOaOutLogById")
    public Result queryOaOutLogById(OaOutLog oaOutLog) {
        Result result = new Result<>();
        try {
            List<String> strings = dynamicTableService.queryOaOutLogById(oaOutLog);
            result.setSuccess(true);
            result.setResult(strings);
        } catch (Exception e) {
            e.printStackTrace();
            result.error500("查询传输日志失败");
        }
        return result;
    }
}
