package com.cfcc.modules.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.exception.AIOAException;
import com.cfcc.common.system.vo.DictModel;
import com.cfcc.modules.app.service.IAppDataListService;
import com.cfcc.modules.oaBus.entity.BusModel;
import com.cfcc.modules.oaBus.entity.BusPage;
import com.cfcc.modules.oaBus.entity.BusPageDetail;
import com.cfcc.modules.oaBus.entity.BusProcSet;
import com.cfcc.modules.oaBus.mapper.OaBusdataMapper;
import com.cfcc.modules.oaBus.service.*;
import com.cfcc.modules.system.entity.LoginInfo;
import com.cfcc.modules.system.entity.SysDictItem;
import com.cfcc.modules.system.service.ISysDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AppDataListService implements IAppDataListService {

    @Autowired
    private OaBusdataMapper oaBusdataMapper;
    @Autowired
    private IBusModelService iBusModelService;
    @Autowired
    private IOaBusdataService iOaBusdataService;
    @Autowired
    private IBusProcSetService iBusProcSetService;
    @Autowired
    private IBusPageDetailService ibusPageDetailService;
    @Autowired
    private IBusPageService iBusPageService;
    @Autowired
    private ISysDictService sysDictService;

    @Override
    public Map<String, Object> queryBusDataByFunctionId(Integer functionId, Integer modelId, LoginInfo loginInfo, Integer pageNo, Integer pageSize,Map<String, Object> condition) {

        Map<String, Object> result = new HashMap<>() ;
        String col = "i_id,s_title,d_create_time" ;
        String orderFlag = "" ;
        //Map<String, Object> condition = new HashMap<>() ;

        BusModel busModel =  iBusModelService.getById(modelId);
        String tableName = busModel.getSBusdataTable() ;
        Map<String, String> permitData = iOaBusdataService.permit(functionId, tableName, loginInfo.getUsername());       //根据权限，查询出对应的查询条件
        String userId = permitData.get("userId");
        String userUnit = permitData.get("userUnit");
        String userDepart = permitData.get("userDepart");
        permitData.remove("userId");
        permitData.remove("userUnit");
        permitData.remove("userDepart");
        log.info(permitData.toString());
        List<Map<String, Object>> dataList = oaBusdataMapper.getBusdataByMap((pageNo - 1) * pageSize,
                pageSize, col, tableName, condition, permitData, userId, userUnit, userDepart, orderFlag);
        int total = oaBusdataMapper.getBusdataByMapTotal(tableName, condition, permitData, userId, userUnit, userDepart);

        result.put("list",dataList) ;
        result.put("tatal" ,total) ;
        //System.out.println(dataList);
        return result;
    }

    /**
     * 获取详情的数据
     * @param loginInfo
     * @param json
     * @return
     */
    @Override
    public Map<String, Object> getDetailSer(LoginInfo loginInfo, String json) {
        Map<String,Object> param = (Map<String,Object> ) JSONObject.parse(json);
        // 业务数据表名
        String tableName = param.get("tableName") == null ? null : param.get("tableName") + "";
        if (tableName == null) {
            throw new AIOAException("查询失败：表名为空");
        }
        // 业务数据id
        String busdataId = param.get("busdataId") == null ? null : param.get("busdataId") + "";
        if (busdataId == null) {
            throw new AIOAException("查询失败：数据id为空");
        }

        // 任务实例id
        String taskId = param.get("taskId") == null ? null : param.get("taskId") + "";
        // 流程实例id
        String proInstanId = param.get("proInstanId") == null ? null : param.get("proInstanId") + "";
        // 待办、已办...
        String status = param.get("status") == null ? null : param.get("status") + "";


        Map<String, Object> result = new HashMap<>();
        result.put("busdataId", busdataId);
        result.put("loginInfo", loginInfo);
        result.put("proInstanId", proInstanId);

        // 查询业务数据详情
        Map<String, Object> oaBusdata = oaBusdataMapper.getBusdataMapByIdDao(tableName, busdataId);
        if (null == oaBusdata) {
            // 数据不存在
            throw new AIOAException("数据不存在可能已被删除");
        }

        result.put("oaBusdata", oaBusdata);
        // 查出对应列的名字数据
        String functionId = oaBusdata.get("i_bus_function_id") + "";
        //获取对应的版本号
        String iFunVersion = oaBusdata.get("i_fun_version") + "";
        //增加根据版本查询对应点的IPageID，通过IPageId，和FunctionID查询出对应的字段含义
        //为兼容以前的版本，先查询iPageId，如果有iPageId的话，则用iPageId查询，如果没有的话，就直接查
        QueryWrapper<BusProcSet> queryWrapper = new QueryWrapper<>();
        BusProcSet busProcSet1 = new BusProcSet();
        busProcSet1.setIBusFunctionId(Integer.parseInt(functionId));
        busProcSet1.setIVersion(Integer.parseInt(iFunVersion));
        queryWrapper.setEntity(busProcSet1);
        busProcSet1 = iBusProcSetService.getOne(queryWrapper);
        List<BusPageDetail> busPageDetailList = new ArrayList<BusPageDetail>();
        if (busProcSet1.getIPageId() == null) {
            busPageDetailList = ibusPageDetailService.getListByFunID(functionId);
            Map<String, Object> pageMap = oaBusdataMapper.getPageUrlDao(functionId);;
            result.put("pageRef", pageMap.get("pageRef"));
            result.put("actShow", pageMap.get("actShow"));
        } else { // 不为空的话
            String iPageId = busProcSet1.getIPageId() + "";
            busPageDetailList = ibusPageDetailService.getListByFunIDAndIPageId(functionId, iPageId);
            BusPage busPage = iBusPageService.getById(iPageId);
            result.put("pageRef", busPage.getSPagePath());
            result.put("actShow", busPage.getActShow());
        }
        result.put("busPageDetailList",busPageDetailList) ;

        Map<String,Object> optionMap = new HashMap<>() ;
        busPageDetailList.forEach(entry -> {

            //如果配置字典的话，就将数据查出来放到下拉框中，然后放到map
            if (entry.getSDictId() != null && !"".equals(entry.getSDictId())) {
                List<DictModel> dictMOdelList = sysDictService.getDictByCode(entry.getSDictId());
                optionMap.put(entry.getSTableColumn() + "_option", dictMOdelList);
            }
            //如果字典sql中有值的话，就先查出sql，再执行sql取查
            else if (entry.getSDictSqlKey() != null && !"".equals(entry.getSDictSqlKey())) {
                SysDictItem itemByCode = sysDictService.getDictItemByCode("sql", entry.getSDictSqlKey());
                if (itemByCode != null || !"".equals(itemByCode)) {
                    List<DictModel> dictMOdelList = sysDictService.getSqlValue(itemByCode.getDescription(),
                            loginInfo.getId(), loginInfo.getDepart().getId(), loginInfo.getDepart().getParentId());
                    optionMap.put(entry.getSTableColumn() + "_option", dictMOdelList);
                }
            }
        });
        result.put("optionMap",optionMap) ;
        return  result;
    }
}
