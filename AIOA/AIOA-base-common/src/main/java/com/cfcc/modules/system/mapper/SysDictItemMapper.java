package com.cfcc.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cfcc.modules.system.entity.SysDictItem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface SysDictItemMapper extends BaseMapper<SysDictItem> {
    @Select("SELECT * FROM SYS_DICT_ITEM WHERE DICT_ID = #{mainId}")
    public List<SysDictItem> selectItemsByMainId(String mainId);

    String getItemTextById(@Param("sDictId") String sDictId,@Param("value") Object value, @Param("DBvalue") String DBvalue);

    @Insert("<script>" +
            "insert  into  sys_dict_unit  (s_dict_id,s_unit_id) values " +
            "<foreach collection='ids' item='departId' separator=',' >" +
            "(#{dictId},#{departId})" +
            "</foreach>" +
            "</script>")
    void saveDictAndDeparts(@Param("dictId") String id, @Param("ids")List<String> departs);

    @Select("<script>" +
            "SELECT " +
            " i.id itemId, " +
            " d.id departId, " +
            " d.depart_name departName " +
            "FROM " +
            " sys_dict_item i " +
            "LEFT JOIN sys_dict_unit u ON i.id = u.s_dict_id " +
            "LEFT JOIN sys_depart d ON d.id = u.s_unit_id " +
            "  where u.s_dict_id in " +
            "  <foreach collection='list' index='index' item='item' open='(' separator=',' close=')'> " +
            "   #{item.id} " +
            "  </foreach>" +
            "</script>")
    List<Map<String, Object>> queryDeparts(@Param("list") List<SysDictItem> records);


    @Delete("DELETE  FROM `sys_dict_unit` where s_dict_id=#{id};")
    void deleteDepartAbout(String id);

    @Select("SELECT item_text FROM `sys_dict` d " +
            "LEFT JOIN sys_dict_item i on d.id=i.dict_id  " +
            "where d.dict_code=#{dictKey} and item_value=#{itemValue};")
    String getDictItemText(@Param("dictKey") String dictKey,@Param("itemValue") String itemValue);

    @Select("select d.item_text from sys_dict_item d where d.dict_id = " +
            "(select c.id from sys_dict c where c.dict_code = #{sDictId})  and d.item_value = #{itemValue};")
    String queryItemTextByDicIdAndValue(@Param("sDictId") String sDictId, @Param("itemValue") String itemValue);

    Boolean deleteDictItemByDictID(@Param("id") String id);
}
