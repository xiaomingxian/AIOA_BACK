package com.cfcc.modules.oaBus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.common.api.vo.Result;
import com.cfcc.common.constant.CacheConstant;
import com.cfcc.common.system.vo.DictModel;
import com.cfcc.common.util.StringUtil;
import com.cfcc.modules.oaBus.entity.*;
import com.cfcc.modules.oaBus.mapper.BusFunctionMapper;
import com.cfcc.modules.oaBus.mapper.BusModelMapper;
import com.cfcc.modules.oaBus.mapper.BusPageDetailMapper;
import com.cfcc.modules.oaBus.service.*;
import com.cfcc.modules.oabutton.entity.OaProcButton;
import com.cfcc.modules.oabutton.entity.OaProcOpinion;
import com.cfcc.modules.oabutton.service.IOaProcButtonService;
import com.cfcc.modules.oabutton.service.IOaProcOpinionService;
import com.cfcc.modules.system.service.ISysDepartService;
import com.cfcc.modules.system.service.ISysDictService;
import com.cfcc.modules.system.service.ISysRoleService;
import com.cfcc.modules.workflow.service.ProcessManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Description: 业务配置表
 * @Author: jeecg-boot
 * @Date: 2019-10-15
 * @Version: V1.0
 */
@Service
public class BusFunctionServiceImpl extends ServiceImpl<BusFunctionMapper, BusFunction> implements IBusFunctionService {

    @Autowired
    BusFunctionMapper busFunctionMapper;
    @Autowired
    BusPageDetailMapper busPageDetailMapper;
    @Autowired
    BusModelMapper busModelMapper;
    @Autowired
    IBusModelService iBusModelService;
    @Autowired
    IBusProcSetService iBusProcSetService;
    @Autowired
    IBusFunctionViewService iBusFunctionViewService;
    @Autowired
    IBusFunctionUnitService iBusFunctionUnitService;
    @Autowired
    IOaProcButtonService ioaProcButtonService;
    @Autowired
    IOaProcOpinionService ioaProcOpinionService;
    @Autowired
    ProcessManagerService iprocessManageService;
    @Autowired
    ISysRoleService iroleService;
    @Autowired
    ISysDepartService iSysDepartService;
    @Autowired
    ISysDictService isysDictService;


    @Override
    public IPage<BusFunction> getPage(Integer pageNo, Integer pageSize, BusFunction busFunction, String column, String order) {
        int total = busFunctionMapper.queryBusFunctionCount(busFunction);
        List<BusFunction> functions = busFunctionMapper.queryBusFunction((pageNo - 1) * pageSize, pageSize, busFunction,column,order);
        IPage<BusFunction> pageList = new Page<BusFunction>();
        pageList.setRecords(functions);
        pageList.setTotal(total);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;
    }

    @Override
    @CacheEvict(value = CacheConstant.FUNCTION_CACHE, allEntries = true)
    public int removeBusFunctionById(String id,String schema) {
        return busFunctionMapper.delBusFunctionById(id);
    }


    /**
     * 插入busFunction和对应的页面详情（busPageDetail）
     *
     * @param busFunction
     * @param busPageDetailList
     */
   /* @Transactional
    @Override
    public void saveMain(BusFunction busFunction, List<BusPageDetail> busPageDetailList) {
        busFunction.setDCreateTime(new Date());
        busFunctionMapper.insert(busFunction);
        QueryWrapper<BusFunction> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(busFunction);
        BusFunction queryBusFunction = this.getOne(queryWrapper);        //查询刚刚插入的那条数据的id
        BusModel busModel = iBusModelService.getBusModelById(queryBusFunction.getIBusModelId());
        String tableName = busModel.getSBusdataTable();
        if (busPageDetailList != null) {
            busPageDetailMapper.insertBusPageDetailBatch(busPageDetailList, queryBusFunction.getIId(), queryBusFunction.getIPageId(), tableName);
        }
    }
*/

    /**
     * 查询业务要显示的查询条件
     *
     * @param id
     * @return
     */
    @Override
    public List<BusPageDetail> queryConditionSer(int id) {

        List<BusPageDetail> conditionList = busFunctionMapper.queryConditionDao(id);

        return conditionList;
    }

    @Override
    @Cacheable(value = CacheConstant.FUNCTION_CACHE)
    public List<BusFunction> findList(String schema) {
        List<BusFunction> busFunctionList = busFunctionMapper.findList();
        return busFunctionList;
    }

    @Override
    public List<OaProcOpinion> queryoaProcOpinionSer(String key) {
        return (List<OaProcOpinion>) busFunctionMapper.queryProcOpinionDao(key);
    }


    /**
     * 插入相关业务，先插入ob_bus_function表，取到function_id,再将besPagedetail中的数据插入到
     * oa_bus_page_detal表中，再将权限按钮插入到对应oa_bus_proc_set表中，将数据查看权限插入到
     * oa_bus_function_view表中，将业务所属机构插入到oa_bus_function_unit表中。
     */
    @Override
    @Transactional
    @CacheEvict(value = CacheConstant.FUNCTION_CACHE, allEntries = true)
    public String saveMain(BusFunction busFunction,
                           List<BusFunctionUnit> busFunctionUnit,
                           BusProcSet busProcSet,
                           List<BusFunctionView> busFunctionView, String schema) {

        String result  = "" ;
        StringUtil stringUtil = new StringUtil();
        busProcSet = (BusProcSet) stringUtil.changeToNull(busProcSet);
        busFunction.setDCreateTime(new Date());
        //1。插入到function表中
        busFunctionMapper.insert(busFunction);
        /*QueryWrapper<BusFunction> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(busFunction);
        BusFunction queryBusFunction = this.getOne(queryWrapper);        //查询刚刚插入的那条数据的id
*/

        busProcSet.setIBusFunctionId(busFunction.getIId());

        busProcSet.setIBusModelId(busFunction.getIBusModelId());
        //2.插入到procset表中
        busProcSet.setIVersion(1);      //将版本设为0
        busProcSet.setIPageId(busFunction.getIPageId()) ;
        iBusProcSetService.save(busProcSet);   //插入到对应的数据
        QueryWrapper<BusProcSet> queryWrapperProc = new QueryWrapper<>();
        queryWrapperProc.setEntity(busProcSet);
        BusProcSet queryBusProcSet = iBusProcSetService.getOne(queryWrapperProc);      //查询出刚刚插入关联表的id值
        //3.更新procsetId到busFunction表中
        busFunction.setIProcSetId(queryBusProcSet.getIId());
        this.updateBudFunctionById(busFunction);

        //4.批量保存busFunctionView表中
        busFunctionView.forEach(entry -> {
            entry.setIBusFunctionId(busFunction.getIId());
        });
        iBusFunctionViewService.saveBatch(busFunctionView);
        //5.批量保存到busFunctionUnit
        busFunctionUnit.forEach(entry -> {
            entry.setIBusFunctionId(busFunction.getIId());
        });
        iBusFunctionUnitService.saveBatch(busFunctionUnit);

        //设置对应的字段含义，查询该业务所属的业务分类下的已配置业务，
        // 找到业务优先级最高的业务，复制含义配置，给新的业务功能。
        // 如果此项业务功能是第一个，则提示用户配置业务含义。
        //1、查询这个业务对应的model下的优先级最高的一条数据
        BusFunction bus2 = busFunctionMapper.queryFunByModel(busFunction.getIId()) ;
        if(bus2 == null ){
            result = "请配置对应的含义";
        }else{
            //2、插入数据到busdatail中
            int oldId =  bus2.getIId();
            int newId = busFunction.getIId() ;
            busPageDetailMapper.insertBusPageDetailByFunId(oldId,newId);
        }
        return result ;
    }

    /**
     * 更新function
     *
     * @param busFunction
     * @param busFunctionUnit
     * @param busProcSet
     * @param busFunctionView
     */
    @Override
    @Transactional
    @CacheEvict(value = CacheConstant.FUNCTION_CACHE, allEntries = true)
    public void updateFunction(BusFunction busFunction, List<BusFunctionUnit> busFunctionUnit, BusProcSet busProcSet, List<BusFunctionView> busFunctionView,String schema) {
        busFunction.setDUpdateTime(new Date());
        boolean updateProc = false ;        //是否修改proc标志
        BusFunction busFunctionBefore = busFunctionMapper.selectByIid(busFunction.getIId()) ;

        if(busFunction.getIPageId()!= busFunctionBefore.getIPageId()){
            updateProc = true ;
        }
        StringUtil stringUtil = new StringUtil();
        busProcSet.setIBusFunctionId(busFunction.getIId());
        busProcSet = (BusProcSet) stringUtil.changeToNull(busProcSet);
        busFunctionMapper.updateById(busFunction);
        if (busProcSet != null) {

            //更新流程按钮逻辑，如果通过当前数据能查询出对应的数据，这没有发生改变，这样就不用更新，如果没有数据的话，
            //就再插入一条数据,修改版本号且将数据id更新到对应的function中
            QueryWrapper<BusProcSet> busProcSetqueryWrapper = new QueryWrapper<>();
            busProcSetqueryWrapper.setEntity(busProcSet);
            BusProcSet res = iBusProcSetService.getOne(busProcSetqueryWrapper);
            if (res == null) {
                updateProc = true ;
            }

        }
        if(updateProc){
            if (busProcSet.getIVersion() == null) {
                busProcSet.setIVersion(0);      //如果没有版本号的话，就将版本号设为0
            }
            busProcSet.setIVersion(busProcSet.getIVersion() + 1);  //将版本号加一
            busProcSet.setIId(null);            //将之前的id置为空
            busProcSet.setIPageId(busFunction.getIPageId());            //将之前的id置为空
            //查询结果为空的话，说明没这条记录（已修改），就将版本号加1，将id置为空，重新插入一条记录
            iBusProcSetService.save(busProcSet);
            QueryWrapper<BusProcSet> procSetQueryWrapper = new QueryWrapper<>();
            procSetQueryWrapper.setEntity(busProcSet);
            busProcSet = iBusProcSetService.getOne(procSetQueryWrapper);
            busFunction.setIProcSetId(busProcSet.getIId());
            busFunctionMapper.updateById(busFunction);          //将新插入的数据id保存到function表中
        }


        /*if(busFunction.getIProcSetId() != null &&busProcSet.getIId() != null && busFunction.getIProcSetId() == busProcSet.getIId()){
            boolean res = iBusProcSetService.updateById(busProcSet);
        }else {
            QueryWrapper<BusProcSet> queryWrapperDel = new QueryWrapper<>();
            queryWrapperDel.eq("i_bus_function_id",busFunction.getIId());
            iBusProcSetService.remove(queryWrapperDel);
            busProcSet.setIBusFunctionId(busFunction.getIId()) ;
            busProcSet.setIBusModelId(busFunction.getIBusModelId());
            iBusProcSetService.save(busProcSet);
            QueryWrapper<BusProcSet> procSetQueryWrapper = new QueryWrapper<>();
            procSetQueryWrapper.setEntity(busProcSet);
            busProcSet = iBusProcSetService.getOne(procSetQueryWrapper);
            busFunction.setIProcSetId(busProcSet.getIId());
            busFunctionMapper.updateById(busFunction);
        }*/

        //更新机构表

       /* if(busFunctionUnit.getSUnitId() != null || busFunctionUnit.getSDeptId() != null){
            UpdateWrapper<BusFunctionUnit> updateWrapper= new UpdateWrapper<>() ;
            updateWrapper.eq("i_bus_function_id",busFunction.getIId()) ;
            boolean res = iBusFunctionUnitService.update(busFunctionUnit,updateWrapper) ;
        }*/
        QueryWrapper<BusFunctionUnit> queryWrapperUnit = new QueryWrapper<>();
        queryWrapperUnit.eq("i_bus_function_id", busFunction.getIId());
        iBusFunctionUnitService.remove(queryWrapperUnit);
        busFunctionUnit.forEach(entry -> {
            entry.setIBusFunctionId(busFunction.getIId());
            iBusFunctionUnitService.save(entry);
        });


        QueryWrapper<BusFunctionView> queryWrapperView = new QueryWrapper<>();
        queryWrapperView.eq("i_bus_function_id", busFunction.getIId());
        iBusFunctionViewService.remove(queryWrapperView);
        //更新权限表
        busFunctionView.forEach(entry -> {
            entry.setIBusFunctionId(busFunction.getIId());
            iBusFunctionViewService.save(entry);
        });
        /*if(busFunctionView.getIType() != null){
            UpdateWrapper<BusFunctionView> updateWrapper= new UpdateWrapper<>() ;
            updateWrapper.eq("i_bus_function_id",busFunction.getIId()) ;
            boolean res = iBusFunctionViewService.update(busFunctionView,updateWrapper) ;
        }*/

    }

    @Override
    public Map<String, Object> queryRoleAndDepartSer() {
        Map<String, Object> map = new HashMap<>();
        List<DictModel> departs = isysDictService.getDictByCode("sys_depart/depart_name/id");
        List<DictModel> rolelist = isysDictService.getDictByCode("sys_role/role_name/id");
        map.put("roleList", rolelist);
        map.put("departList", departs);
        return map;
    }

    @Override
    public List<BusFunction> queryByModelId(String modelId) {


        return busFunctionMapper.queryByModelIdDao(modelId);
    }

    @Override
    public List<BusFunction> getBusFunctionListByDepartId(String departId,String DBvalue) {
        List<BusFunction> busFunctionList = busFunctionMapper.getBusFunctionListByDepartId(departId, DBvalue);
        List<BusModel> busModels = new ArrayList<>();
        for (BusFunction busFunction : busFunctionList) {
            BusModel busModel = busModelMapper.getBusModelById(busFunction.getIBusModelId(),DBvalue);
            if (busModel.getIId() == busFunction.getIBusModelId()) {
                busFunction.setSBusdataTable(busModel.getSBusdataTable());
                continue;
            }
        }
        return busFunctionList;
    }

    /**
     * 获得编辑数据时的其它绑定数据
     *
     * @param functionId
     * @return
     */
    @Override
    public Map<String, Object> getEditInit(int functionId) {
        BusFunction function = new BusFunction();
        function.setIId(functionId);
        function = getById(functionId);
        //获取对应的数据
        Map<String, Object> map = new HashMap<>();
        //获取对应的proc
//        QueryWrapper<BusProcSet> wrapperProc = new QueryWrapper<>();
//        wrapperProc.eq("i_bus_function_id",functionId)
//                    .orderByDesc("i_id")
//                    .last("limit 1");
//        BusProcSet busProcSet = iBusProcSetService.getOne(wrapperProc);
        if (function.getIProcSetId() != null) {
            BusProcSet busProcSet = iBusProcSetService.getById(function.getIProcSetId());
            map.put("busProcSet", busProcSet);
            if (busProcSet != null) {
                if (busProcSet.getIProcButtonId() != null) {
                    OaProcButton oaProcButton = ioaProcButtonService.getById(busProcSet.getIProcButtonId());
                    if (oaProcButton != null) {
                        map.put("procButton", oaProcButton);
                    }
                }
                if (busProcSet.getIProcOpinionId() != null) {
                    OaProcOpinion oaProcOpinion = ioaProcOpinionService.queryById(busProcSet.getIProcOpinionId());
                    if (oaProcOpinion != null) {
                        map.put("procOpinion", oaProcOpinion);
                    }
                }
                if (busProcSet.getProcDefKey() != null && !("".equals(busProcSet.getProcDefKey()))) {
                    String keyName = iprocessManageService.queryActNameByKey(busProcSet.getProcDefKey());
                    if (keyName != null && !("".equals(keyName))) {
                        List<String> listStr = new ArrayList<>();
                        listStr.add(keyName);
                        listStr.add(busProcSet.getProcDefKey());
                        map.put("procKey", listStr);
                    }
                }
            }
        }

        //查询机构数据
        BusFunctionUnit busFunctionUnit = new BusFunctionUnit();
        busFunctionUnit.setIBusFunctionId(functionId);
        QueryWrapper<BusFunctionUnit> wrapperUnit = new QueryWrapper<>();
        wrapperUnit.setEntity(busFunctionUnit);
        List<BusFunctionUnit> unitList = iBusFunctionUnitService.list(wrapperUnit);

        //查询数据权限
        BusFunctionView busFunctionView = new BusFunctionView();
        busFunctionView.setIBusFunctionId(functionId);
        QueryWrapper<BusFunctionView> wrapperView = new QueryWrapper<>();
        wrapperView.setEntity(busFunctionView);
        List<BusFunctionView> viewList = iBusFunctionViewService.list(wrapperView);
        map.put("unitList", unitList);
        map.put("viewList", viewList);
        return map;
    }


    /**
     * 根据modelId查询出对应的functionList
     *
     * @param id
     * @return
     */
    @Override
    public IPage<BusFunction> getFunByModelId(String id) {
        List<BusFunction> functions = busFunctionMapper.getFunByModelIdDao(id);
        IPage<BusFunction> pageList = new Page<BusFunction>();
        pageList.setRecords(functions);
        return pageList;
    }

    @Override
    public BusFunction getOneByFunId(String functionId) {
        return busFunctionMapper.selectByIid(Integer.parseInt(functionId));
    }


    private void updateBudFunctionById(BusFunction queryBusFunction) {
        busFunctionMapper.updateBusFunctionByIdDao(queryBusFunction);
    }


}
