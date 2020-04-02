package com.cfcc.modules.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cfcc.common.constant.CacheConstant;
import com.cfcc.common.constant.CommonConstant;
import com.cfcc.common.system.vo.DictModel;
import com.cfcc.common.util.SqlInjectionUtil;
import com.cfcc.modules.system.entity.SysDepart;
import com.cfcc.modules.system.entity.SysDict;
import com.cfcc.modules.system.entity.SysDictItem;
import com.cfcc.modules.system.mapper.SysDictItemMapper;
import com.cfcc.modules.system.mapper.SysDictMapper;
import com.cfcc.modules.system.model.TreeSelectModel;
import com.cfcc.modules.system.service.ISysDepartService;
import com.cfcc.modules.system.service.ISysDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 字典表 服务实现类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@Service
@Slf4j
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements ISysDictService {

    @Autowired
    private SysDictMapper sysDictMapper;
    @Autowired
    private ISysDepartService iSysDepartService;
    @Autowired
    private SysDictItemMapper sysDictItemMapper;

    /**
     * 通过查询指定code 获取字典
     *
     * @param code
     * @return
     */
    @Override
    @Cacheable(value = CacheConstant.DICT_CACHE, key = "#code")
    public List<DictModel> queryDictItemsByCode(String code) {
        log.info("无缓存dictCache的时候调用这里！");
        return sysDictMapper.queryDictItemsByCode(code);
    }

    /**
     * 通过查询指定code 获取字典值text
     *
     * @param code
     * @param key
     * @return
     */

    @Override
    @Cacheable(value = CacheConstant.DICT_CACHE)
    public String queryDictTextByKey(String code, String key, String orgSchema) {
        log.info("无缓存dictText的时候调用这里！");
        return sysDictMapper.queryDictTextByKey(code, key);
    }

    /**
     * 通过查询指定table的 text code 获取字典
     * dictTableCache采用redis缓存有效期10分钟
     *
     * @param table
     * @param text
     * @param code
     * @return
     */
    @Override
    //@Cacheable(value = "dictTableCache")
    public List<DictModel> queryTableDictItemsByCode(String table, String text, String code) {
        log.info("无缓存dictTableList的时候调用这里！");
        return sysDictMapper.queryTableDictItemsByCode(table, text, code);
    }

    @Override
    public List<DictModel> queryTableDictItemsByCodeAndFilter(String table, String text, String code, String filterSql) {
        log.info("无缓存dictTableList的时候调用这里！");
        return sysDictMapper.queryTableDictItemsByCodeAndFilter(table, text, code, filterSql);
    }

    /**
     * 通过查询指定table的 text code 获取字典值text
     * dictTableCache采用redis缓存有效期10分钟
     *
     * @param table
     * @param text
     * @param code
     * @param key
     * @return
     */
    @Override
    @Cacheable(value = "dictTableCache")
    public String queryTableDictTextByKey(String table, String text, String code, String key) {
        log.info("无缓存dictTable的时候调用这里！");
        return sysDictMapper.queryTableDictTextByKey(table, text, code, key);
    }

    /**
     * 根据字典类型id删除关联表中其对应的数据
     */
    @Override
    public boolean deleteByDictId(SysDict sysDict) {
        sysDict.setDelFlag(CommonConstant.DEL_FLAG_1);
        return this.updateById(sysDict);
    }

    @Override
    @Transactional
    public void saveMain(SysDict sysDict, List<SysDictItem> sysDictItemList) {

        sysDictMapper.insert(sysDict);
        if (sysDictItemList != null) {
            for (SysDictItem entity : sysDictItemList) {
                entity.setDictId(sysDict.getId());
                sysDictItemMapper.insert(entity);
            }
        }
    }

    @Override
    public List<DictModel> queryAllDepartBackDictModel() {
        return baseMapper.queryAllDepartBackDictModel();
    }

    @Override
    public List<DictModel> queryAllUserBackDictModel() {
        return baseMapper.queryAllUserBackDictModel();
    }

    @Override
    public List<DictModel> queryTableDictItems(String table, String text, String code, String keyword) {
        return baseMapper.queryTableDictItems(table, text, code, "%" + keyword + "%");
    }

    @Override
    public List<TreeSelectModel> queryTreeList(String table, String text, String code, String pidField, String pid, String hasChildField) {
        return baseMapper.queryTreeList(table, text, code, pidField, pid, hasChildField);
    }



    @Override
    public List<Map<String, Object>> getDictByKey(String dictKey) {
        return sysDictMapper.getDictByKey(dictKey);
    }

    /**
     * 查询数据字典约定，text为要显示的值，value为要存的值,
     *
     * 根据部门id查询出对应的机构id，将机构id与之权限匹配
     * feng
     * @param dictKey
     * @return
     */
    @Override
    public List<Map<String, Object>> getDictByKeySer(String dictKey,String deptId) {

        SysDepart unit = iSysDepartService.getUnitByDeptId(deptId);
        String unitId = unit.getId() ;
        List<Map<String, Object>> dictByKeyList = getDictForSel(dictKey);
        List<SysDictItem> result = new ArrayList<>();
        List<Map<String, Object>> list = dictByKeyList.stream().filter(map->{
            String departIds = (String)map.get("departIds");
            return(departIds == null || (departIds != null && departIds.contains(unitId))) ;
        }).collect(Collectors.toList());
        return list;

    }


    @Override
    public List<Map<String, Object>> getDictForSel(String dictKey) {

        return sysDictMapper.getDictForSelDao(dictKey);
    }

    @Override
    public List<DictModel> getDictByDictId(String id) {
        String dictCode = sysDictMapper.getDictByIdDao(id);

        return getDictByCode(dictCode) ;
    }


    /**
     * 根据字典code查询描述
     * @param dictCode
     * @return
     * @author feng
     */
    @Override
    public List<DictModel> getDescribeDictCode(String dictCode) {
        return sysDictMapper.getDescribeDictCodeDao(dictCode) ;
    }

    /**
     * 根据diotCode和value查出对应的数据
     * @param code
     * @param value
     * @return
     */
    @Override
    public SysDictItem getDictItemByCode(String code, String value) {
        return sysDictMapper.getDictItemByCodeDao(code,value);
    }
    /**
     * 通过dictCode 查询对应的列表数据，如果是sql的话，也可以查询出对应的数据
     * feng
     * @param dictCode
     * @return
     */
    @Override
    public List<DictModel> getDictByCode(String dictCode) {

        List<DictModel> ls = null;
        try {
            if (dictCode.indexOf("/") != -1) {
                //关联表字典（举例：sys_user,realname,id）
                String[] params = dictCode.split("/");
                if (params.length < 3) {
                    log.info("字典Code格式不正确！");
                    //result.error500("字典Code格式不正确！");
                    return null;
                }
                //SQL注入校验（只限制非法串改数据库）
                final String[] sqlInjCheck = {params[0], params[1], params[2]};
                SqlInjectionUtil.filterContent(sqlInjCheck);
                if (params.length == 4) {
                    //SQL注入校验（查询条件SQL 特殊check，此方法仅供此处使用）
                    SqlInjectionUtil.specialFilterContent(params[3]);
                    ls = queryTableDictItemsByCodeAndFilter(params[0], params[1], params[2], params[3]);
                } else if (params.length == 3) {
                    ls = queryTableDictItemsByCode(params[0], params[1], params[2]);
                } else {

                    return null;
                }
            } else {
                //字典表
                ls = queryDictItemsByCode(dictCode);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //("操作失败");
            return null;
        }

        return ls;
    }


    /**
     * 查询对应的sql数据字典
     *
     * @param description
     * @param userId
     * @param departId
     * @param description
     * @return
     */
    @Override
    public List<DictModel> getSqlValue(String description, String userId, String departId, String unitId) {
        String str[] = description.split("#") ;
        if(str.length > 1 ){
            description = str[0] ;
            for(int i = 1 ; i < str.length ; i ++){
                if(str[i].indexOf(" ") != -1){
                    String head = str[i].substring(0,str[i].indexOf(" ")) ;
                    if("userId".equals(head)){
                        str[i] = str[i].replace(head,"'"+userId+"'") ;
                    }else if("departId".equals(head)){
                        str[i] = str[i].replace(head,"'"+departId+"'") ;
                    }else if("unitId".equals(head)){
                        str[i] = str[i].replace(head,"'"+unitId+"'") ;
                    }
                }else{
                    if("userId".equals(str[i])){
                        str[i] = str[i].replace(str[i],"'"+userId+"'") ;
                    }else if("departId".equals(str[i])){
                        str[i] = str[i].replace(str[i],"'"+departId+"'") ;
                    }else if("unitId".equals(str[i])){
                        str[i] = str[i].replace(str[i],"'"+unitId+"'") ;
                    }
                }
                description += str[i] ;
            }
        }
        System.out.println("数据字典sql：" + description);
        return sysDictMapper.getSqlValueDao(description);
    }

    @Override
    public IPage<SysDict> getDictByAll(SysDict sysDict, Integer pageNo, Integer pageSize) {
        IPage<SysDict> pageList = new Page<SysDict>();
        Long total = sysDictMapper.getDictByAllAndPage(sysDict);
        List<SysDict> sysDictList = sysDictMapper.getDictByAll(sysDict,(pageNo - 1) * pageSize,pageSize);
        pageList.setRecords(sysDictList);
        pageList.setTotal(total);
        pageList.setSize(pageSize);
        pageList.setCurrent(pageNo);
        return pageList;
    }

    @Override
    public List<SysDictItem> getEsIpAndHost(String orgCode) {
        List<SysDictItem> dictItemList = sysDictMapper.getEsIpAndHost(orgCode);
        return dictItemList;
    }

    @Override
    public boolean deleteDictByDictId(String id) {
        return sysDictMapper.deleteDictByDictId(id);
    }


}