package com.cfcc.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.common.system.vo.DictModel;
import com.cfcc.modules.system.entity.SysDict;
import com.cfcc.modules.system.entity.SysDictItem;
import com.cfcc.modules.system.model.DuplicateCheckVo;
import com.cfcc.modules.system.model.TreeSelectModel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字典表 Mapper 接口
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
public interface SysDictMapper extends BaseMapper<SysDict> {

    /**
     * 重复检查SQL
     *
     * @return
     */
    public Long duplicateCheckCountSql(DuplicateCheckVo duplicateCheckVo);

    public Long duplicateCheckCountSqlNoDataId(DuplicateCheckVo duplicateCheckVo);

    public List<DictModel> queryDictItemsByCode(@Param("code") String code);

    public List<DictModel> queryTableDictItemsByCode(@Param("table") String table, @Param("text") String text, @Param("code") String code);

    public List<DictModel> queryTableDictItemsByCodeAndFilter(@Param("table") String table, @Param("text") String text, @Param("code") String code, @Param("filterSql") String filterSql);


    public String queryDictTextByKey(@Param("code") String code, @Param("key") String key);

    public String queryTableDictTextByKey(@Param("table") String table, @Param("text") String text, @Param("code") String code, @Param("key") String key);


    /**
     * 查询所有部门 作为字典信息 id -->value,departName -->text
     *
     * @return
     */
    public List<DictModel> queryAllDepartBackDictModel();

    /**
     * 查询所有用户  作为字典信息 username -->value,realname -->text
     *
     * @return
     */
    public List<DictModel> queryAllUserBackDictModel();

    /**
     * 通过关键字查询出字典表
     *
     * @param table
     * @param text
     * @param code
     * @param keyword
     * @return
     */
    public List<DictModel> queryTableDictItems(@Param("table") String table, @Param("text") String text, @Param("code") String code, @Param("keyword") String keyword);

    /**
     * 根据表名、显示字段名、存储字段名 查询树
     *
     * @param table
     * @param text
     * @param code
     * @param pid
     * @param hasChildField
     * @return
     */
    List<TreeSelectModel> queryTreeList(@Param("table") String table, @Param("text") String text, @Param("code") String code, @Param("pidField") String pidField, @Param("pid") String pid, @Param("hasChildField") String hasChildField);


    @Select("SELECT   " +
            "  d.id dictId,d.dict_code dictCode,d.dict_name dictName,   " +
            "  i.item_text dictItem,i.item_value dictVal,i.description description,GROUP_CONCAT(dp.s_unit_id) departIds   " +
            "  FROM   " +
            "  `sys_dict` d   " +
            "  LEFT JOIN sys_dict_item i ON d.id = i.dict_id  " +
            "   LEFT JOIN sys_dict_unit dp ON i.id = dp.s_dict_id  " +
            "where dict_code=#{dictKey} " +
            "GROUP BY " +
            " i.id,d.id")
    List<Map<String, Object>> getDictByKey(@Param("dictKey") String dictKey);

    String getDictByIdDao(String id);

    SysDictItem getDictItemByCodeDao(@Param("code")String code, @Param("value")String value);

    /**
     * 查询sql
     * feng
     * @param description
     * @return
     */
    @Select("${description}")
    List<DictModel> getSqlValueDao(@Param("description")String description);

    List<DictModel> getDescribeDictCodeDao(@Param("dictCode")String dictCode);

    @Select("SELECT   " +
            " i.id id, d.id dictId,d.dict_code dictCode,d.dict_name dictName,   " +
            "  i.item_text text,i.item_value value,i.description description,GROUP_CONCAT(dp.s_unit_id) departIds   " +
            "  FROM   " +
            "  `sys_dict` d   " +
            "  LEFT JOIN sys_dict_item i ON d.id = i.dict_id  " +
            "   LEFT JOIN sys_dict_unit dp ON i.id = dp.s_dict_id  " +
            "where dict_code=#{dictKey} " +
            "GROUP BY " +
            " i.id,d.id " +
            " order by i.sort_order")
    List<Map<String, Object>> getDictForSelDao(@Param("dictKey") String dictKey);

    String getDictIdByDictCode(@Param("dictCode") String dictId, @Param("DBvalue") String DBvalue);

    Long getDictByAllAndPage(@Param("sysDict") SysDict sysDict);

    List<SysDict> getDictByAll(@Param("sysDict") SysDict sysDict,@Param("start") Integer start,@Param("pageSize") Integer pageSize);

    List<SysDictItem> getEsIpAndHost(@Param("orgCode") String orgCode);
}
